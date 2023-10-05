package appeng.client.gui.anchoring;

import appeng.client.gui.GuiWidget;

public sealed interface AnchorLineTarget {
    static AnchorLineTarget parent() {
        return new Parent();
    }

    static AnchorLineTarget siblingId(String id) {
        return new SiblingId(id);
    }

    static AnchorLineTarget sibling(GuiWidget widget) {
        return new SiblingInstance(widget);
    }

    record Parent() implements AnchorLineTarget {
    }

    record SiblingId(String id) implements AnchorLineTarget {
    }

    record SiblingInstance(GuiWidget widget) implements AnchorLineTarget {
    }
}
