package appeng.client.gui.anchoring;

public record AnchorLine(AnchorLineTarget element, AnchorType type) {

    public static AnchorLine parentLeft() {
        return new AnchorLine(new AnchorLineTarget.Parent(), AnchorType.LEFT);
    }

    public static AnchorLine parentTop() {
        return new AnchorLine(new AnchorLineTarget.Parent(), AnchorType.TOP);
    }

    public static AnchorLine parentRight() {
        return new AnchorLine(new AnchorLineTarget.Parent(), AnchorType.RIGHT);
    }

    public static AnchorLine parentBottom() {
        return new AnchorLine(new AnchorLineTarget.Parent(), AnchorType.BOTTOM);
    }

    public static AnchorLine parentHorizontalCenter() {
        return new AnchorLine(new AnchorLineTarget.Parent(), AnchorType.HORIZONTAL_CENTER);
    }

    public static AnchorLine parentVerticalCenter() {
        return new AnchorLine(new AnchorLineTarget.Parent(), AnchorType.VERTICAL_CENTER);
    }

}
