package appeng.client.gui.implementations;

import appeng.client.gui.widgets.Container;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import appeng.api.client.AEKeyRendering;
import appeng.api.config.LockCraftingMode;
import appeng.api.stacks.AmountFormat;
import appeng.client.Point;
import appeng.client.gui.Icon;
import appeng.client.gui.Tooltip;
import appeng.core.localization.GuiText;
import appeng.core.localization.InGameTooltip;

import java.util.OptionalInt;

public class PatternProviderLockReason extends Container {
    private final PatternProviderScreen<?> screen;

    public PatternProviderLockReason(PatternProviderScreen<?> screen) {
        this.screen = screen;
    }

    @Override
    public OptionalInt getFixedWidth() {
        return OptionalInt.of(126);
    }

    @Override
    public OptionalInt getFixedHeight() {
        return OptionalInt.of(16);
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var menu = screen.getMenu();

        Icon icon;
        Component lockStatusText;
        if (menu.getCraftingLockedReason() == LockCraftingMode.NONE) {
            icon = Icon.UNLOCKED;
            lockStatusText = GuiText.CraftingLockIsUnlocked.text()
                    .withStyle(ChatFormatting.DARK_GREEN);
        } else {
            icon = Icon.LOCKED;
            lockStatusText = GuiText.CraftingLockIsLocked.text()
                    .withStyle(ChatFormatting.DARK_RED);
        }

        icon.getBlitter().dest(getLayoutBounds().getX(), getLayoutBounds().getY()).blit(guiGraphics);
        guiGraphics.drawString(Minecraft.getInstance().font, lockStatusText, getLayoutBounds().getX() + 15, getLayoutBounds().getY() + 5, -1, false);
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        var menu = screen.getMenu();
        var tooltip = switch (menu.getCraftingLockedReason()) {
            case NONE -> null;
            case LOCK_UNTIL_PULSE -> InGameTooltip.CraftingLockedUntilPulse.text();
            case LOCK_WHILE_HIGH -> InGameTooltip.CraftingLockedByRedstoneSignal.text();
            case LOCK_WHILE_LOW -> InGameTooltip.CraftingLockedByLackOfRedstoneSignal.text();
            case LOCK_UNTIL_RESULT -> {
                var stack = menu.getUnlockStack();
                Component stackName;
                Component stackAmount;
                if (stack != null) {
                    stackName = AEKeyRendering.getDisplayName(stack.what());
                    stackAmount = Component.literal(stack.what().formatAmount(stack.amount(), AmountFormat.FULL));
                } else {
                    stackName = Component.literal("ERROR");
                    stackAmount = Component.literal("ERROR");

                }
                yield InGameTooltip.CraftingLockedUntilResult.text(stackName, stackAmount);
            }
        };

        return tooltip != null ? new Tooltip(tooltip) : null;
    }
}
