package appeng.client.gui.widgets;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;

public class PlayerInventoryPanel extends Container {
    private final AEBaseMenu menu;

    public PlayerInventoryPanel(AEBaseMenu menu) {
        this.menu = menu;
        var inventory = addChild(new SlotGrid(menu, SlotSemantics.PLAYER_INVENTORY));
        var hotbar = addChild(new SlotGrid(menu, SlotSemantics.PLAYER_HOTBAR));
    }
}
