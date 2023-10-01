/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.client.gui.implementations;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;

import appeng.client.gui.GuiRoot;
import appeng.client.gui.widgets.TabButton;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.SwitchGuisPacket;
import appeng.menu.ISubMenu;

/**
 * Utility class for sub-screens of other menus that allow returning to the primary menu UI.
 */
public final class AESubScreen {
    private AESubScreen() {
    }

    public static TabButton addBackButton(ISubMenu subMenu, String id, GuiRoot widgets) {
        return addBackButton(subMenu, id, widgets, null);
    }

    public static TabButton addBackButton(ISubMenu subMenu, String id, GuiRoot widgets,
                                          @Nullable Component label) {
        var icon = subMenu.getHost().getMainMenuIcon();
        if (label == null) {
            label = icon.getHoverName();
        }
        TabButton button = new TabButton(icon, label,
                btn -> {
                    goBack();
                });
        widgets.add(id, button);
        return button;
    }

    public static void goBack() {
        NetworkHandler.instance().sendToServer(SwitchGuisPacket.returnToParentMenu());
    }

}
