package it.nexbit.cuba.lookuppreview.web.fragments;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.actions.picker.OpenAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.datagrid.ContainerDataGridItems;
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.icons.Icons;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataComponents;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UiController("lupreview_ItemPreviewFragment")
@UiDescriptor("item-preview-fragment.xml")
public class ItemPreviewFragment extends ScreenFragment {

    @Inject
    protected DataComponents dataComponents;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ViewRepository viewRepository;
    @Inject
    protected UiComponentsGenerator uiComponentsGenerator;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected Icons icons;
    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected Messages messages;
    @Inject
    protected Actions actions;

    @Inject
    protected ScrollBoxLayout scrollBox;
    @Inject
    protected Label<String> panelCaption;
    @Inject
    protected PopupButton addBtn;
    @Inject
    protected CheckBox selectAllCheckBox;
    @Inject
    protected Button removeBtn;
    @Inject
    protected Button editBtn;
    @Inject
    protected HBoxLayout editActions;

    protected GridLayout fieldsGrid;
    protected InstanceContainer itemDc;
    protected View previewView;

    protected boolean isEditing;

    protected List<String> visibleProperties;
    protected int totalCheckedProperties;

    private int nextRowIndex;

    @Subscribe
    protected void onInit(InitEvent event) {
        panelCaption.setValue(messages.getMainMessage("lupreview.noItemCaption"));
    }

    protected void loadVisibleProperties() {
        // TODO: load visibleProperties list from persistent storage
//        if (visibleProperties == null) {
            visibleProperties = previewView.getProperties().stream()
                .map(ViewProperty::getName).collect(Collectors.toList());
//        }
    }

    protected void initAddBtn() {
        addBtn.removeAllActions();
        for (ViewProperty property : previewView.getProperties()) {
            if (!visibleProperties.contains(property.getName())) {
                addBtn.addAction(new AddPropertyAction(property.getName()));
            }
        }
        if (addBtn.getActions().isEmpty()) {
            addBtn.setEnabled(false);
        } else {
            addBtn.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    public void setItem(Entity item) {
        if (itemDc == null && item != null) {
            MetaClass metaClass = item.getMetaClass();

            itemDc = dataComponents.createInstanceContainer(item.getClass());
            getScreenData().registerContainer("previewItemDc", itemDc);
            previewView = viewRepository.getView(metaClass, "preview");
            itemDc.setView(previewView);

            loadVisibleProperties();

            createComponents(item);
        }

        if (item == null) {
            panelCaption.setValue(messages.getMainMessage("lupreview.noItemCaption"));
            fieldsGrid.setVisible(isEditing());
            editBtn.setVisible(false);
        } else {
            panelCaption.setValue(messages.formatMainMessage("lupreview.itemCaption", metadataTools.getInstanceName(item)));
            fieldsGrid.setVisible(true);
            editBtn.setVisible(!isEditing());

            item = dataManager.reload(item, previewView);
        }

        itemDc.setItem(item);
    }

    @SuppressWarnings("unchecked")
    private void createComponents(Entity item) {
        // create a Grid container
        fieldsGrid = uiComponents.create(GridLayout.class);
        fieldsGrid.setId("fieldsGrid");
        fieldsGrid.setWidthFull();
        fieldsGrid.setSpacing(true);
        fieldsGrid.setMargin(true, false, false, false);
        fieldsGrid.setStyleName("item-preview-fields-grid");
        fieldsGrid.setColumns(3);
        fieldsGrid.setColumnExpandRatio(1, 1.0F);

        nextRowIndex = 0;
        for (String property : visibleProperties) {

            ComponentGenerationContext context = new ComponentGenerationContext(itemDc.getEntityMetaClass(), property);
            // TODO: set a custom container component instead of Form? (we're NOT using Form for its limited flexibility)
            context.setComponentClass(Form.class);
            context.setValueSource(new ContainerValueSource<>(itemDc, property));

            Component propertyComponent = uiComponentsGenerator.generate(context);

            setupPropertyComponent(propertyComponent, item, property);

            createFieldsGridRow(propertyComponent);
        }

        initAddBtn();

        scrollBox.removeAll();
        scrollBox.add(fieldsGrid);

    }

    @SuppressWarnings("unchecked")
    protected void setupPropertyComponent(Component propertyComponent, Entity item, String propertyName) {
        MetaClass itemMetaClass = item.getMetaClass();

        ((Component.HasCaption) propertyComponent).setCaption(
                messageTools.getPropertyCaption(itemMetaClass, propertyName)
        );

        if (propertyComponent instanceof DataGrid) {
            DataGrid dg = (DataGrid) propertyComponent;

            MetaClass propMetaClass = itemMetaClass.getPropertyNN(propertyName).getRange().asClass();
            MetaProperty inverseProp = itemMetaClass.getPropertyNN(propertyName).getInverse();

            CollectionContainer propertyContainer =
                    dataComponents.createCollectionContainer(propMetaClass.getJavaClass());
            View previewView = viewRepository.findView(propMetaClass, "preview");
            propertyContainer.setView(previewView != null ? previewView : viewRepository.getView(propMetaClass, View.BASE));
            getScreenData().registerContainer("lupreview_" + propertyName + "Dc", propertyContainer);

            assert inverseProp != null;
            CollectionLoader loader = dataComponents.createCollectionLoader();
            loader.setQuery("select e from " + propMetaClass.getName() + " e " +
                    "where e." + inverseProp.getName() + ".id = :parentId");
            loader.setParameter("parentId", item.getId());
            loader.setView(propertyContainer.getView());
            loader.setContainer(propertyContainer);
            loader.getView().getProperties().stream().map(ViewProperty::getName).forEach(invPropName -> {
                dg.addColumn(invPropName, metadataTools.resolveMetaPropertyPathNN(propMetaClass, invPropName));
            });
            loader.load();

            dg.setItems(new ContainerDataGridItems(propertyContainer));
        }
    }

    protected void createFieldsGridRow(Component propertyComponent) {
        Preconditions.checkNotNullArgument(propertyComponent);

        if (nextRowIndex == fieldsGrid.getRows())
            fieldsGrid.setRows(nextRowIndex + 1);

        Label iconLabel = uiComponents.create(Label.class);
        iconLabel.setIcon(icons.get(CubaIcon.ARROWS));
        iconLabel.setAlignment(Component.Alignment.MIDDLE_CENTER);
        iconLabel.setStyleName("item-preview-fields-grid__move-icon");
        iconLabel.setVisible(isEditing());
        fieldsGrid.add(iconLabel, 0, nextRowIndex);

        if (propertyComponent instanceof Field) {
            ((Field) propertyComponent).setEditable(false);
        }
        if (propertyComponent instanceof PickerField) {
            ((PickerField) propertyComponent).addAction(actions.create(OpenAction.class));
        }
        propertyComponent.setWidthFull();
        propertyComponent.setStyleName("item-preview-fields-grid__field");
        fieldsGrid.add(propertyComponent, 1, nextRowIndex);

        CheckBox checkBox = uiComponents.create(CheckBox.class);
        checkBox.setAlignment(Component.Alignment.MIDDLE_RIGHT);
        checkBox.setVisible(isEditing());
        checkBox.setStyleName("item-preview-fields-grid__remove-check-box");
        checkBox.addValueChangeListener(event -> {
            if (event.isUserOriginated()) {
                //noinspection ConstantConditions
                totalCheckedProperties += event.getValue() ? 1 : -1;
                if (totalCheckedProperties == visibleProperties.size())
                    selectAllCheckBox.setValue(true);
                else
                    selectAllCheckBox.setValue(false);

                if (totalCheckedProperties > 0)
                    removeBtn.setEnabled(true);
                else
                    removeBtn.setEnabled(false);
            }
        });
        fieldsGrid.add(checkBox, 2, nextRowIndex);

        nextRowIndex++;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        if (editing != isEditing) {
            isEditing = editing;
            editBtn.setVisible(!editing);
            addBtn.setVisible(editing);
            removeBtn.setVisible(editing);
            selectAllCheckBox.setVisible(editing);
            editActions.setVisible(editing);

            // hide/show edit controls in grid rows
            for (int i = 0; i < visibleProperties.size(); i++) {
                fieldsGrid.getComponentNN(0, i).setVisible(editing);
                fieldsGrid.getComponentNN(2, i).setVisible(editing);
            }

            if (itemDc.getItemOrNull() == null && !editing) {
                panelCaption.setValue(messages.getMainMessage("lupreview.noItemCaption"));
                fieldsGrid.setVisible(false);
                editBtn.setVisible(false);
            }
        }
    }

    @Subscribe("selectAllCheckBox")
    protected void onSelectAllCheckBoxValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        if (!event.isUserOriginated()) return;

        for (int i = 0; i < visibleProperties.size(); i++) {
            CheckBox checkBox = (CheckBox) fieldsGrid.getComponentNN(2, i);
            checkBox.setValue(selectAllCheckBox.isChecked());
        }
        totalCheckedProperties = selectAllCheckBox.isChecked() ? visibleProperties.size() : 0;
        removeBtn.setEnabled(selectAllCheckBox.isChecked());
    }

    @Subscribe("editBtn")
    protected void onEditClick(Button.ClickEvent event) {
        setEditing(true);
    }

    @Subscribe("saveBtn")
    protected void onSaveBtnClick(Button.ClickEvent event) {
        setEditing(false);
    }

    @Subscribe("cancelBtn")
    protected void onCancelBtnClick(Button.ClickEvent event) {
        setEditing(false);

        loadVisibleProperties();
        createComponents(itemDc.getItem());
    }

    @Subscribe("removeBtn")
    protected void onRemoveBtnClick(Button.ClickEvent event) {
        List<String> toRemove = new ArrayList<>();

        for (int i = 0; i < visibleProperties.size(); i++) {
            CheckBox checkBox = (CheckBox) fieldsGrid.getComponentNN(2, i);
            if (checkBox.getValue()) {
                toRemove.add(visibleProperties.get(i));
            }
        }
        visibleProperties.removeAll(toRemove);
        if (visibleProperties.isEmpty()) selectAllCheckBox.setValue(false);
        removeBtn.setEnabled(false);

        createComponents(itemDc.getItem());
    }

    protected class AddPropertyAction extends AbstractAction {

        protected final String propertyName;

        AddPropertyAction(String propertyName) {
            super(propertyName);
            this.propertyName = propertyName;
        }

        @Override
        protected String getDefaultCaption() {
            return messageTools.getPropertyCaption(itemDc.getEntityMetaClass(), propertyName);
        }

        @Override
        public void actionPerform(Component component) {
            visibleProperties.add(propertyName);
            createComponents(itemDc.getItem());
        }
    }
}