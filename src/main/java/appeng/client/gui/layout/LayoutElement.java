package appeng.client.gui.layout;

import appeng.client.gui.anchoring.AnchorAxis;
import appeng.client.gui.anchoring.AnchorLine;
import appeng.client.gui.anchoring.AnchorLineTarget;
import appeng.client.gui.anchoring.Anchors;
import appeng.client.guidebook.document.LytSize;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.OptionalInt;

public abstract class LayoutElement {

    // Current position relative to parent, result of layout
    protected int x;

    protected int y;

    protected int width;

    protected int height;

    // Layout constraints
    private int minWidth;
    private int maxWidth;
    private int minHeight;
    private int maxHeight;
    private int preferredWidth;
    private int preferredHeight;

    private LayoutRoot root;

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
        if (!this.layoutInvalid) {
            this.layoutInvalid = true;
            if (root != null) {
                root.elementsToUpdate.add(this);
            }
        }
    }

    public final void ensureLayout() {
        updateLayout();
    }

    protected final void updateLayout() {
        if (layoutInvalid) {
            layoutInvalid = false;

            doLayout();
        }
    }

    protected void doLayout() {
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

    public int getImplicitWidth() {
        return 0;
    }

    public int getImplicitHeight() {
        return 0;
    }

    public final int getPreferredWidth() {
        return preferredWidth;
    }

    public final int getPreferredHeight() {
        return preferredHeight;
    }

    public final int getMinWidth() {
        return minWidth;
    }

    public final int getMaxWidth() {
        return maxWidth;
    }

    public final int getMinHeight() {
        return minHeight;
    }

    public final int getMaxHeight() {
        return maxHeight;
    }

    public final void setMinWidth(int minWidth) {
        minWidth = Math.max(0, minWidth);
        if (minWidth != this.minWidth) {
            this.minWidth = minWidth;
            invalidateLayout();
        }
    }

    public final void setMaxWidth(int maxWidth) {
        maxWidth = Math.max(0, maxWidth);
        if (maxWidth != this.maxWidth) {
            this.maxWidth = maxWidth;
            invalidateLayout();
        }
    }

    public final void setMinHeight(int minHeight) {
        minHeight = Math.max(0, minHeight);
        if (minHeight != this.minHeight) {
            this.minHeight = minHeight;
            invalidateLayout();
        }
    }

    public final void setMaxHeight(int maxHeight) {
        maxHeight = Math.max(0, maxHeight);
        if (maxHeight != this.maxHeight) {
            this.maxHeight = maxHeight;
            invalidateLayout();
        }
    }

    public final void setPreferredWidth(int preferredWidth) {
        preferredWidth = Math.max(0, preferredWidth);
        if (preferredWidth != this.preferredWidth) {
            this.preferredWidth = preferredWidth;
            invalidateLayout();
        }
    }

    public final void setPreferredHeight(int preferredHeight) {
        preferredHeight = Math.max(0, preferredHeight);
        if (preferredHeight != this.preferredHeight) {
            this.preferredHeight = preferredHeight;
            invalidateLayout();
        }
    }

    public abstract boolean isVisible();

    public LytSize getSize() {
        return new LytSize(width, height);
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public LytSize getMinimumSize() {
        return new LytSize(minWidth, minHeight);
    }

    public LytSize getPreferredSize() {
        return getMinimumSize();
    }
}
