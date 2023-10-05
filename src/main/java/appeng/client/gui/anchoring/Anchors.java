package appeng.client.gui.anchoring;

import org.jetbrains.annotations.Nullable;

public final class Anchors {
    @Nullable
    private AnchorLine left;
    private int leftMargin;
    @Nullable
    private AnchorLine top;
    private int topMargin;
    @Nullable
    private AnchorLine right;
    private int rightMargin;
    @Nullable
    private AnchorLine bottom;
    private int bottomMargin;
    @Nullable
    private AnchorLine horizontalCenter;
    private int horizontalCenterOffset;
    @Nullable
    private AnchorLine verticalCenter;
    private int verticalCenterOffset;

    public void fill(AnchorLineTarget target) {
        setLeft(new AnchorLine(target, AnchorType.LEFT));
        setTop(new AnchorLine(target, AnchorType.TOP));
        setRight(new AnchorLine(target, AnchorType.RIGHT));
        setBottom(new AnchorLine(target, AnchorType.BOTTOM));
    }

    public void fillParent() {
        fill(AnchorLineTarget.parent());
    }

    public AnchorLine getLeft() {
        return left;
    }

    public void setLeft(AnchorLine left) {
        this.left = left;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public AnchorLine getTop() {
        return top;
    }

    public void setTop(AnchorLine top) {
        this.top = top;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public AnchorLine getRight() {
        return right;
    }

    public void setRight(AnchorLine right) {
        this.right = right;
    }

    public int getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    public AnchorLine getBottom() {
        return bottom;
    }

    public void setBottom(AnchorLine bottom) {
        this.bottom = bottom;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public AnchorLine getHorizontalCenter() {
        return horizontalCenter;
    }

    public void setHorizontalCenter(AnchorLine horizontalCenter) {
        this.horizontalCenter = horizontalCenter;
    }

    public int getHorizontalCenterOffset() {
        return horizontalCenterOffset;
    }

    public void setHorizontalCenterOffset(int horizontalCenterOffset) {
        this.horizontalCenterOffset = horizontalCenterOffset;
    }

    public AnchorLine getVerticalCenter() {
        return verticalCenter;
    }

    public void setVerticalCenter(AnchorLine verticalCenter) {
        this.verticalCenter = verticalCenter;
    }

    public int getVerticalCenterOffset() {
        return verticalCenterOffset;
    }

    public void setVerticalCenterOffset(int verticalCenterOffset) {
        this.verticalCenterOffset = verticalCenterOffset;
    }

    public boolean isAnchored(AnchorAxis axis) {
        return getAxisStart(axis) != null || getAxisCenter(axis) != null || getAxisEnd(axis) != null;
    }

    public AnchorLine getAxisStart(AnchorAxis axis) {
        return switch (axis) {
            case X -> left;
            case Y -> top;
        };
    }

    public AnchorLine getAxisCenter(AnchorAxis axis) {
        return switch (axis) {
            case X -> horizontalCenter;
            case Y -> verticalCenter;
        };
    }

    public AnchorLine getAxisEnd(AnchorAxis axis) {
        return switch (axis) {
            case X -> right;
            case Y -> bottom;
        };
    }

    public int getAxisStartMargin(AnchorAxis axis) {
        return switch (axis) {
            case X -> leftMargin;
            case Y -> topMargin;
        };
    }

    public int getAxisCenterOffset(AnchorAxis axis) {
        return switch (axis) {
            case X -> horizontalCenterOffset;
            case Y -> verticalCenterOffset;
        };
    }

    public int getAxisEndMargin(AnchorAxis axis) {
        return switch (axis) {
            case X -> rightMargin;
            case Y -> bottomMargin;
        };
    }
}
