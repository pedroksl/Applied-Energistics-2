package appeng.client.gui;

import appeng.client.Point;
import appeng.client.gui.widgets.IResizableWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;

import java.util.function.Consumer;

public class VanillaWidget extends GuiWidget {
    private final AbstractWidget widget;

    public VanillaWidget(AbstractWidget widget) {
        this.widget = widget;
    }

    @Override
    public boolean isFocused() {
        return widget.isFocused();
    }

    @Override
    public void focus() {
        widget.setFocused(true);
    }

    @Override
    public void blur() {
        widget.setFocused(false);
    }

    @Override
    public void updateBeforeRender() {
        var bounds = getLayoutBounds();
        if (widget instanceof IResizableWidget resizableWidget) {
            resizableWidget.move(new Point(bounds.getX(), bounds.getY()));
        } else {
            widget.setX(bounds.getX());
            widget.setY(bounds.getY());
        }
        widget.setWidth(bounds.getWidth());
        widget.height = bounds.getHeight();
    }

    @Override
    public void onVisibilityChanged() {
        super.onVisibilityChanged();

        widget.visible = isVisible();
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        if (isFocused()) {
            blur(); // Minecraft already cleared focus on the screen
        }

        // Position the widget
//        TODO WidgetStyle widgetStyle = style.getWidget(entry.getKey());
//        TODO var newBounds = widgetStyle.resolveBounds(bounds, widget.getWidth(), widget.getHeight());
//        TODO widget.setBounds(newBounds);

        addWidget.accept(widget);
    }
}
