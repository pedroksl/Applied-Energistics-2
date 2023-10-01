package appeng.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.style.Blitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

import java.util.OptionalInt;

/**
 * Renders a simple panel with a background an no interactivity.
 */
public class BackgroundPanel extends Container {
    private final Blitter background;

    public BackgroundPanel(Blitter background) {
        this.background = background;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        background.dest(this.bounds.getX(), this.bounds.getY()).blit(guiGraphics);
    }

    @Override
    public OptionalInt getFixedWidth() {
        return OptionalInt.of(background.getSrcWidth());
    }

    @Override
    public OptionalInt getFixedHeight() {
        return OptionalInt.of(background.getSrcHeight());
    }
}
