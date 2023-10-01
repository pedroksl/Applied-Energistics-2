package appeng.client.gui.me.items;

import appeng.client.gui.GuiRoot;
import appeng.client.gui.widgets.Container;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.client.Point;
import appeng.client.gui.GuiWidget;
import appeng.menu.me.items.PatternEncodingTermMenu;

import java.util.OptionalInt;

public abstract class EncodingModePanel extends Container {
    protected final PatternEncodingTermScreen<?> screen;
    protected final PatternEncodingTermMenu menu;
    protected final GuiRoot widgets;

    public EncodingModePanel(PatternEncodingTermScreen<?> screen, GuiRoot widgets) {
        this.screen = screen;
        this.menu = screen.getMenu();
        this.widgets = widgets;
    }

    abstract ItemStack getTabIconItem();

    abstract Component getTabTooltip();

    @Override
    public OptionalInt getFixedWidth() {
        return OptionalInt.of(126);
    }

    @Override
    public OptionalInt getFixedHeight() {
        return OptionalInt.of(68);
    }
}
