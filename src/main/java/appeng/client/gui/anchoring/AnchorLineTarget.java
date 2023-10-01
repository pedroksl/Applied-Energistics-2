package appeng.client.gui.anchoring;

import appeng.client.gui.GuiWidget;

public sealed interface AnchorLineTarget {
    record Parent() implements AnchorLineTarget {
    }

    record SiblingId(String id) implements AnchorLineTarget {
    }

    record SiblingInstance(GuiWidget widget) implements AnchorLineTarget {
    }
}
