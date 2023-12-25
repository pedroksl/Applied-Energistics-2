package appeng.client.gui.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Root container for {@link LayoutElement}
 */
public class LayoutRoot {

    /**
     * The layout of these items has been invalidated and they're waiting to be updated.
     */
    final List<LayoutElement> elementsToUpdate = new ArrayList<>();

}
