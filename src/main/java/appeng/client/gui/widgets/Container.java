package appeng.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.GuiWidget;
import appeng.client.gui.layout.Insets;
import appeng.client.gui.layout.LayoutManager;
import appeng.client.gui.style.WidgetStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Container extends GuiWidget {
    protected final List<GuiWidget> children = new ArrayList<>();

    protected WidgetStyle style;
    private LayoutManager layout;
    private Insets insets = new Insets(0, 0, 0, 0);

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        for (var child : children) {
            child.populateScreen(addWidget, bounds, screen);
        }
    }

    @Override
    public void updateBeforeRender() {
        for (var child : children) {
            child.updateBeforeRender();
        }
    }

    @Override
    public void tick() {
        for (var child : children) {
            child.tick();
        }
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        for (var child : children) {
            if (child.isVisible()) {
                child.drawBackgroundLayer(guiGraphics, bounds, mouse);
            }
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        for (var child : children) {
            if (child.isVisible()) {
                child.drawForegroundLayer(guiGraphics, bounds, mouse);
            }
        }
    }


    @Override
    public boolean wantsAllMouseDownEvents() {
        return children.stream().anyMatch(GuiWidget::wantsAllMouseDownEvents);
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        // Find "wants all event" children first
        for (var child : children) {
            if (child.isVisible()
                    && (child.wantsAllMouseDownEvents())
                    && child.onMouseDown(mousePos, button)) {
                return true;
            }
        }

        for (var child : children) {
            if (child.isVisible()
                    && !child.wantsAllMouseDownEvents()
                    && child.onMouseDown(mousePos, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean wantsAllMouseUpEvents() {
        return children.stream().anyMatch(GuiWidget::wantsAllMouseUpEvents);
    }

    @Override
    public boolean onMouseUp(Point mousePos, int button) {
        // Find "wants all event" children first
        for (var child : children) {
            if (child.isVisible()
                    && child.wantsAllMouseUpEvents()
                    && child.onMouseUp(mousePos, button)) {
                return true;
            }
        }

        for (var child : children) {
            if (child.isVisible()
                    && !child.wantsAllMouseUpEvents()
                    && child.onMouseUp(mousePos, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean wantsAllMouseWheelEvents() {
        return children.stream().anyMatch(GuiWidget::wantsAllMouseWheelEvents);
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        // Find "wants all event" children first
        for (var child : children) {
            if (child.isVisible()
                    && child.wantsAllMouseWheelEvents()
                    && child.onMouseWheel(mousePos, delta)) {
                return true;
            }
        }

        for (var child : children) {
            if (child.isVisible()
                    && !child.wantsAllMouseWheelEvents()
                    && child.getLayoutBounds().contains(mousePos.getX(), mousePos.getY())
                    && child.onMouseWheel(mousePos, delta)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseDrag(Point mousePos, int button) {
        for (var widget : children) {
            if (widget.isVisible() && widget.onMouseDrag(mousePos, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setStyle(WidgetStyle style) {
        this.style = style;
    }

    @Override
    public List<GuiWidget> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public void onVisibilityChanged() {
        super.onVisibilityChanged();

        for (var child : children) {
            child.onVisibilityChanged();
        }
    }

    public <T extends GuiWidget> T addChild(T child) {
        children.add(child);
        child.setParent(this);
        return child;
    }

    public void removeChild(GuiWidget child) {
        children.remove(child);
        if (child.getParent() == this) {
            child.setParent(null);
        }
    }

    @Override
    protected void doLayout() {
        if (layout != null) {
            layout.layoutContainer(this);
        }

        for (int i = 0; i < children.size(); i++) {
            var child = children.get(i);
            child.ensureLayout();
        }
    }

    public LayoutManager getLayout() {
        return layout;
    }

    public void setLayout(LayoutManager layout) {
        this.layout = layout;
    }

    public Insets getInsets() {
        return insets;
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }
}
