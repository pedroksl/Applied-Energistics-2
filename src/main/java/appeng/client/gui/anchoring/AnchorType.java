package appeng.client.gui.anchoring;

public enum AnchorType {
    LEFT(AnchorAxis.X),
    TOP(AnchorAxis.Y),
    RIGHT(AnchorAxis.X),
    BOTTOM(AnchorAxis.Y),
    HORIZONTAL_CENTER(AnchorAxis.X),
    VERTICAL_CENTER(AnchorAxis.Y);

    private final AnchorAxis axis;

    AnchorType(AnchorAxis axis) {
        this.axis = axis;
    }
}

