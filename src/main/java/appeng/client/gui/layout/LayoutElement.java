package appeng.client.gui.layout;

import appeng.client.gui.anchoring.AnchorAxis;
import appeng.client.gui.anchoring.AnchorLine;
import appeng.client.gui.anchoring.AnchorLineTarget;
import appeng.client.gui.anchoring.Anchors;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.OptionalInt;

public abstract class LayoutElement {

    protected int x;

    protected int y;

    protected int width;

    protected int height;

    private final Anchors anchors = new Anchors();

    protected abstract LayoutElement getLayoutParent();

    protected abstract Collection<? extends LayoutElement> getLayoutChildren();

    public OptionalInt getFixedWidth() {
        return OptionalInt.empty();
    }

    public OptionalInt getFixedHeight() {
        return OptionalInt.empty();
    }

    boolean layoutInvalid = true;

    public final Anchors getAnchors() {
        return anchors;
    }

    protected final void invalidateLayout() {
        this.layoutInvalid = true;
    }

    /**
     * @return The area occupied by this widget relative to its parent.
     */
    public final Rect2i getLayoutBounds() {
        return new Rect2i(x, y, width, height);
    }

    /**
     * @return The area occupied by this widget relative to the window.
     */
    public final Rect2i getScreenBounds() {
        var x = 0;
        var y = 0;
        for (var current = this; current != null; current = current.getLayoutParent()) {
            x += current.x;
            y += current.y;
        }
        return new Rect2i(x, y, width, height);
    }

    protected void onLayoutUpdated(Rect2i layoutBounds) {
    }

    public boolean hasLayout() {
        return !layoutInvalid;
    }

    /**
     * Convenience method to set {@linkplain #setWidth width} and {@linkplain #setHeight height} simultaneously.
     */
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        // Ignore if widget has fixed width
        if (getFixedWidth().isPresent()) {
            return;
        }
        if (this.width != width) {
            this.width = width;
            invalidateLayout();
            invalidateChildrenDependentOnParentSize(AnchorAxis.X);
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        // Ignore if widget has fixed height
        if (getFixedHeight().isPresent()) {
            return;
        }
        if (this.height != height) {
            this.height = height;
            invalidateLayout();
            invalidateChildrenDependentOnParentSize(AnchorAxis.Y);
        }
    }

    private void invalidateChildrenDependentOnParentSize(AnchorAxis axis) {
        // Invalidate any child depending on the parents height
        for (var child : getLayoutChildren()) {
            if (child.isLayoutDependentOnParentSize(axis)) {
                child.invalidateLayout();
            }
        }
    }

    /**
     * @return True if this element is in any way anchored to the parent.
     */
    boolean isLayoutDependentOnParentSize(AnchorAxis axis) {
        return refersToParent(anchors.getAxisCenter(axis))
                || refersToParent(anchors.getAxisEnd(axis));
    }

    private static boolean refersToParent(@Nullable AnchorLine line) {
        return line != null && line.element() instanceof AnchorLineTarget.Parent;
    }
}
