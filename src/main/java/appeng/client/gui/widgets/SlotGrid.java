package appeng.client.gui.widgets;

import appeng.menu.SlotSemantic;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Positions a range of {@linkplain net.minecraft.world.inventory.AbstractContainerMenu menu}
 * {@linkplain net.minecraft.world.inventory.Slot slots} based on their {@linkplain appeng.menu.SlotSemantic semantic}.
 */
public class SlotGrid extends Container {

    private final AbstractContainerMenu menu;

    private final SlotSemantic semantic;

    private MenuSlot.Background background;

    public SlotGrid(AbstractContainerMenu menu, SlotSemantic semantic) {
        this.semantic = semantic;
        this.menu = menu;
    }


}
