package it.nexbit.cuba.lookuppreview.web.mixins;

import com.google.common.base.Strings;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.Fragment;
import com.haulmont.cuba.gui.components.LookupComponent;
import com.haulmont.cuba.gui.components.SplitPanel;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.StandardLookup;
import com.haulmont.cuba.gui.screen.Subscribe;
import it.nexbit.cuba.lookuppreview.web.fragments.ItemPreviewFragment;

public interface HasSelectedItemPreview {

    @Subscribe
    default void initSelectedItemPreview(Screen.InitEvent event) {

        StandardLookup thisScreen = (StandardLookup) event.getSource();

        // get the preview frame component (MANDATORY)
        ItemPreviewFragment itemPreviewFragment = thisScreen.getWindow().getComponents().stream()
                .filter(component -> component instanceof Fragment)
                .filter(component -> ((Fragment) component).getFrameOwner() instanceof ItemPreviewFragment)
                .map(component -> (ItemPreviewFragment) ((Fragment) component).getFrameOwner())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No ItemPreviewFragment found in the screen XML"));

        // get the SplitPanel component (MANDATORY)
        SplitPanel splitPanel = (SplitPanel) thisScreen.getWindow().getComponents().stream()
                .filter(component -> component instanceof SplitPanel)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No SplitPanel found in the screen XML"));

        LookupComponent<Entity> lookupComponent = Utils.getLookupComponent(thisScreen);
        ((LookupComponent.LookupSelectionChangeNotifier<Entity>) lookupComponent).addLookupValueChangeListener(e -> {
            // avoid setting the item if the side panel is closed
            if ((splitPanel.isSplitPositionReversed() && splitPanel.getSplitPosition() < 0.1F) ||
                    (!splitPanel.isSplitPositionReversed() && splitPanel.getSplitPosition() > 99.9F)) {
                return;
            }

            Entity selectedEntity = lookupComponent.getLookupSelectedItems().isEmpty()
                    ? null
                    : lookupComponent.getLookupSelectedItems().iterator().next();
            itemPreviewFragment.setItem(selectedEntity);
        });
    }

    final class Utils {

        @SuppressWarnings("unchecked")
        static LookupComponent<Entity> getLookupComponent(StandardLookup screen) {
            com.haulmont.cuba.gui.screen.LookupComponent annotation =
                    screen.getClass().getAnnotation(com.haulmont.cuba.gui.screen.LookupComponent.class);
            if (annotation == null || Strings.isNullOrEmpty(annotation.value())) {
                throw new IllegalStateException(
                        String.format("StandardLookup %s does not declare @LookupComponent", screen.getClass())
                );
            }
            return (LookupComponent<Entity>) screen.getWindow().getComponentNN(annotation.value());
        }

    }
}
