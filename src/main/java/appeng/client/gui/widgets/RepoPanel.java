package appeng.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.common.Repo;
import appeng.core.AppEng;
import appeng.menu.me.common.GridInventoryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RepoPanel extends Container {
    private final Repo repo;
    private final Scrollbar scrollbar;

    private List<LayoutElement> layout = new ArrayList<>();

    private int innerHeight = 0;

    protected AEBaseScreen<?> screen;

    public RepoPanel(Repo repo) {
        this.repo = repo;
        this.scrollbar = new Scrollbar();
        repo.addUpdateViewListener(this::invalidateLayout);
        children.add(scrollbar);
    }

    private int availableWidth() {
        return getLayoutBounds().getWidth() - scrollbar.getLayoutBounds().getWidth();
    }

//    private void invalidateLayout() {
////        scrollbar.setHeight(this.rows * SLOT_SIZE - 2);
////        int totalRows = (this.repo.size() + getSlotsPerRow() - 1) / getSlotsPerRow();
////        if (repo.hasPinnedRow()) {
////            totalRows++;
////        }
////        scrollbar.setRange(0, totalRows - this.rows, Math.max(1, this.rows / 6));
//        layoutInvalid = true;
//    }

    protected void onLayoutUpdated(Rect2i bounds) {
        layout.clear();

        var availableWidth = availableWidth();

        var slots = new Slots(repo.getView(), availableWidth);
        layout.add(slots);

        innerHeight = layout.stream().mapToInt(le -> le.height).sum();

        // Update the scrollbar accordingly
//  TODO  scrollbar.setPosition(bounds.getX() + bounds.getWidth() - scrollbar.getLayoutBounds().getWidth(), bounds.getY());
        scrollbar.setHeight(bounds.getHeight());
        var vertOverflow = Math.max(0, innerHeight - bounds.getHeight());
        scrollbar.setRange(0, vertOverflow, vertOverflow / 10);
    }

    @Override
    public void updateBeforeRender() {
//        updateLayout();

        super.updateBeforeRender();
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var left = bounds.getX() + this.getLayoutBounds().getX();
        var top = bounds.getY() + this.getLayoutBounds().getY();
        var right = bounds.getX() + this.getLayoutBounds().getX() + this.getLayoutBounds().getWidth();
        var bottom = bounds.getY() + this.getLayoutBounds().getY() + this.getLayoutBounds().getHeight();

        guiGraphics.enableScissor(left, top, right, bottom);

        // Draw slots
        var curY = top - scrollbar.getCurrentScroll();
        for (var layoutElement : layout) {
            layoutElement.drawBackground(guiGraphics, left, curY, mouse);

            curY += layoutElement.height;
        }

        super.drawBackgroundLayer(guiGraphics, bounds, mouse);

        guiGraphics.disableScissor();
    }

    @Override
    public void
    drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var left = bounds.getX() + this.getLayoutBounds().getX();
        var top = bounds.getY() + this.getLayoutBounds().getY();
        var right = bounds.getX() + this.getLayoutBounds().getX() + this.getLayoutBounds().getWidth();
        var bottom = bounds.getY() + this.getLayoutBounds().getY() + this.getLayoutBounds().getHeight();

        guiGraphics.enableScissor(
                screen.getGuiLeft() + left,
                screen.getGuiTop() + top,
                screen.getGuiLeft() + right,
                screen.getGuiTop() + bottom
        );

        var curY = top - scrollbar.getCurrentScroll();
        for (var layoutElement : layout) {
            layoutElement.drawForeground(guiGraphics, left, curY, mouse);

            curY += layoutElement.height;
        }

        super.drawForegroundLayer(guiGraphics, bounds, mouse);

        guiGraphics.disableScissor();
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        super.populateScreen(addWidget, bounds, screen);
        this.screen = screen;
    }

    abstract static class LayoutElement {
        int height;

        void drawBackground(GuiGraphics guiGraphics, int x, int y, Point mouse) {
        }

        void drawForeground(GuiGraphics guiGraphics, int x, int y, Point mouse) {
        }
    }

    static class Group extends LayoutElement {
        private Component title;
        private boolean expanded;
        private Slots slots;
    }

    static class Slots extends LayoutElement {
        private List<GridInventoryEntry> entries;
        private final int rows;
        private final int cols;

        public Slots(List<GridInventoryEntry> entries, int availableWidth) {
            this.entries = List.copyOf(entries);

            this.cols = availableWidth / 18;
            this.rows = (entries.size() + this.cols - 1) / this.cols;
            this.height = this.rows * 18;
        }

        @Override
        void drawBackground(GuiGraphics guiGraphics, int x, int y, Point mouse) {
            forEachSlot(x, y, (idx, slotX, slotY) -> {
                guiGraphics.blit(
                        AppEng.makeId("ae2guide/gui/slot_light.png"),
                        slotX, slotY,
                        0, 0, 0, 18, 18, 18, 18
                );
            });
        }

        @Override
        void drawForeground(GuiGraphics guiGraphics, int x, int y, Point mouse) {
            forEachSlot(x, y, (idx, slotX, slotY) -> {
                if (idx < entries.size()) {
                    var entry = entries.get(idx);
                    var stack = entry.getWhat().wrapForDisplayOrFilter();
                    guiGraphics.renderItem(null, Minecraft.getInstance().level, stack, slotX + 1, slotY + 1, 0, 1);
                }
            });
        }

        private void forEachSlot(int x, int y, SlotConsumer consumer) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    var idx = row * cols + col;
                    consumer.consume(idx, x + col * 18, y + row * 18);
                }
            }
        }

        @FunctionalInterface
        private interface SlotConsumer {
            void consume(int idx, int slotX, int slotY);
        }
    }
}
