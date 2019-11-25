package it.nexbit.cuba.lookuppreview.gui.components.factories;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesTools;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentGenerationContext;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.factories.AbstractComponentGenerationStrategy;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;

@org.springframework.stereotype.Component(PreviewComponentGenerationStrategy.NAME)
public class PreviewComponentGenerationStrategy extends AbstractComponentGenerationStrategy {
    public static final String NAME = "lupreview_PreviewComponentGenerationStrategy";

    public PreviewComponentGenerationStrategy(Messages messages, DynamicAttributesTools dynamicAttributesTools) {
        super(messages, dynamicAttributesTools);
    }

    @Inject
    public void setUiComponents(UiComponents uiComponents) {
        this.uiComponents = uiComponents;
    }

    @Nullable
    @Override
    public Component createComponent(ComponentGenerationContext context) {
        MetaClass metaClass = context.getMetaClass();
        MetaPropertyPath mpp = resolveMetaPropertyPath(metaClass, context.getProperty());

        if (mpp == null) {
            return null;
        }

        Range mppRange = mpp.getRange();
        Component resultComponent = null;
        if (mppRange.isClass() &&
                (mppRange.getCardinality() == Range.Cardinality.ONE_TO_MANY ||
                        mppRange.getCardinality() == Range.Cardinality.MANY_TO_MANY)) {

            resultComponent = createDataGridComponent(context, mpp);
        }

        return resultComponent;
    }

    protected Component createDataGridComponent(ComponentGenerationContext context, MetaPropertyPath mpp) {
        MetaProperty metaProperty = mpp.getMetaProperty();
        Class<?> javaType = metaProperty.getJavaType();

        if (Collection.class.isAssignableFrom(javaType)) {
            DataGrid dataGrid = uiComponents.create(DataGrid.class);
            dataGrid.setHeight("150px");
            return dataGrid;
        }

        return null;
    }

}
