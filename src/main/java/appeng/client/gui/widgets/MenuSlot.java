package appeng.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.GuiWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Corresponds to a slot in the {@link net.minecraft.world.inventory.AbstractContainerMenu} associated with the
 * current {@link net.minecraft.client.gui.screens.inventory.ContainerScreen}. Slots are identified through
 * their index, since that is also what Minecraft itself uses for synchronization of slots between
 * server and client.
 */
public class MenuSlot extends GuiWidget {

    private final int slotIndex;
    private Background background;

    // This is initially null
    @Nullable
    private Slot slot;

    public MenuSlot(int slotIndex) {
        this.slotIndex = slotIndex;
        setBackground(Background.DEFAULT);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        this.slot = screen.getMenu().getSlot(slotIndex);
    }

    @Override
    protected void onLayoutUpdated(Rect2i layoutBounds) {
        positionSlot();
    }

    @Override
    public void onVisibilityChanged() {
        super.onVisibilityChanged();

        positionSlot();
    }

    private void positionSlot() {
        if (this.slot != null) {
            if (isVisibleIncludingParents()) {
                var slotOrigin = background.getSlotPos(width, height);
                this.slot.x = slotOrigin.getX();
                this.slot.y = slotOrigin.getY();
            } else {
                this.slot.x = -9999;
                this.slot.y = -9999;
            }
        }
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        if (background != this.background) {
            this.background = background;
            setPreferredWidth(background.preferredWidth);
            setPreferredHeight(background.preferredHeight);
        }
    }

    public enum Background {
        NONE(16, 16) {
            @Override
            Point getSlotPos(int width, int height) {
                return Point.ZERO;
            }
        },
        DEFAULT(18, 18) {
            @Override
            Point getSlotPos(int width, int height) {
                return new Point(1, 1);
            }
        };

        private final int preferredWidth;
        private final int preferredHeight;

        Background(int preferredWidth, int preferredHeight) {
            this.preferredWidth = preferredWidth;
            this.preferredHeight = preferredHeight;
        }

        abstract Point getSlotPos(int width, int height);
    }

}
