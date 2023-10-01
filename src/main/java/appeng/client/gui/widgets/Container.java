package appeng.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.GuiWidget;
import appeng.client.gui.style.WidgetStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Container extends GuiWidget {
    protected final List<GuiWidget> children = new ArrayList<>();

    protected Rect2i bounds = new Rect2i(0, 0, 0, 0);

    protected WidgetStyle style;

    protected boolean visible;

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Rect2i getLayoutBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rect2i bounds) {
        this.bounds = bounds;
    }

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
}
