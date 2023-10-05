package appeng.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.style.Blitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

import java.util.OptionalInt;

/**
 * Renders a simple fixed-size image and no interactivity.
 */
public class Image extends Container {
    private final Blitter image;

    public Image(Blitter image) {
        this.image = image;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        image.dest(this.getLayoutBounds().getX(), this.getLayoutBounds().getY()).blit(guiGraphics);
    }

    @Override
    public OptionalInt getFixedWidth() {
        return OptionalInt.of(image.getSrcWidth());
    }

    @Override
    public OptionalInt getFixedHeight() {
        return OptionalInt.of(image.getSrcHeight());
    }
}
