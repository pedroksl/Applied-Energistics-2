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

package appeng.client.gui;

import appeng.client.Point;
import appeng.client.gui.layout.LayoutElement;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.Container;
import appeng.client.gui.widgets.PanelBlitter;
import appeng.client.guidebook.document.LytSize;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class GuiWidget extends LayoutElement {
    @Nullable
    private String id;

    @Nullable
    private Container parent;

    @Nullable
    private GuiRoot root;

    private boolean visible = true;

    @Nullable
    public GuiRoot getRoot() {
        return root;
    }

    public void setRoot(@Nullable GuiRoot root) {
        if (this.root == root) {
            return;
        }

        var oldRoot = this.root;
        if (oldRoot != null) {
            // Disassociate from old root
            oldRoot.removeWidgetFromTree(this);
        }
        this.root = root;
        if (this.root != null) {
            this.root.addWidgetToTree(this);
        }
        invalidateLayout();

        // Do it for all children
        for (var child : getChildren()) {
            child.setRoot(root);
        }
    }

    public Collection<GuiWidget> getChildren() {
        return List.of();
    }


    @Override
    protected Collection<? extends LayoutElement> getLayoutChildren() {
        return getChildren();
    }

    public final boolean isVisibleIncludingParents() {
        for (var current = this; current != null; current = current.getParent()) {
            if (!current.isVisible()) {
                return false;
            }
        }
        return true;
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            onVisibilityChanged();
        }
    }

    @ApiStatus.OverrideOnly
    @MustBeInvokedByOverriders
    public void onVisibilityChanged() {
    }

    @Nullable
    public final Container getParent() {
        return parent;
    }

    public void setParent(@Nullable Container parent) {
        if (parent != this.parent) {
            var oldParent = this.parent;
            this.parent = parent;
            if (oldParent != null) {
                oldParent.removeChild(parent);
            }
        }
    }

    @Override
    protected LayoutElement getLayoutParent() {
        return parent;
    }

    /**
     * Allows the widget to add exclusion zones, which are used for managing space with other overlay mods such as JEI.
     *
     * @param exclusionZones The list to add additional exclusion zones to. Exclusion zones should be in window
     *                       coordinates.
     * @param screenBounds   The bounds of the current screen in window coordinates.
     */
    public void addExclusionZones(List<Rect2i> exclusionZones, Rect2i screenBounds) {
        Rect2i bounds = getLayoutBounds();
        if (bounds.getWidth() <= 0 || bounds.getHeight() <= 0) {
            return;
        }

        // Automatically add the bounds if they exceed the screen bounds
        if (bounds.getX() < 0
                || bounds.getY() < 0
                || bounds.getX() + bounds.getWidth() > screenBounds.getWidth()
                || bounds.getY() + bounds.getHeight() > screenBounds.getHeight()) {
            exclusionZones.add(new Rect2i(
                    screenBounds.getX() + bounds.getX(),
                    screenBounds.getY() + bounds.getY(),
                    bounds.getWidth(),
                    bounds.getHeight()));
        }
    }

    /**
     * Reinitializes a Vanilla screen and populates it with additional vanilla widgets.
     * <p/>
     * This is called initially when the screen is first shown, and called again everytime the screen is resized, as
     * Vanilla does it's positioning logic entirely in this method.
     *
     * @param bounds The bounding box of the screen in window coordinates.
     */
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
    }

    /**
     * Drive animations. This is called alongside each client tick, via {@link Screen#tick()}. This is called less often
     * than {@link #updateBeforeRender()}.
     */
    public void tick() {
    }

    /**
     * Perform layout directly before any rendering methods are called.
     */
    public void updateBeforeRender() {
    }

    /**
     * Draw this composite widget on the background layer of the screen.
     *
     * @param guiGraphics The current matrix stack. Is NOT transformed to the screen, but rather is at the origin of the
     *                    window.
     * @param bounds      The bounds of the current dialog in window coordinates.
     * @param mouse       The current mouse position relative to the dialogs origin.
     */
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
    }

    /**
     * Draw this composite widget on the foreground layer of the screen.
     *
     * @param guiGraphics The current matrix stack. Is transformed such that 0,0 is at the dialogs origin.
     * @param bounds      The bounds of the current dialog in dialog coordinates (x,y are 0).
     * @param mouse       The current mouse position relative to the dialogs origin.
     */
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
    }

    /**
     * Called when the player presses a mouse button on the screen.
     *
     * @param mousePos The current coordinate of the mouse cursor relative to the current dialogs origin.
     * @param button   The pressed button (0=left)
     * @return True to handle the event, false to pass it to other widgets.
     */
    public boolean onMouseDown(Point mousePos, int button) {
        return false;
    }

    /**
     * Override and return true to capture all mouse up events, even if the mouse is not over the widget.
     */
    public boolean wantsAllMouseDownEvents() {
        return false;
    }

    /**
     * Called when the player releases a mouse button on the screen.
     *
     * @param mousePos The current coordinate of the mouse cursor relative to the current dialogs origin.
     * @param button   The released button (0=left)
     * @return True to handle the event, false to pass it to other widgets.
     */
    public boolean onMouseUp(Point mousePos, int button) {
        return false;
    }

    /**
     * Override and return true to capture all mouse up events, even if the mouse is not over the widget.
     */
    public boolean wantsAllMouseUpEvents() {
        return false;
    }

    /**
     * Called when the player moves the mouse on the screen while holding a mouse button.
     *
     * @param mousePos The current coordinate of the mouse cursor relative to the current dialogs origin.
     * @param button   The held button (0=left)
     * @return True to handle the event, false to pass it to other widgets.
     */
    public boolean onMouseDrag(Point mousePos, int button) {
        return false;
    }

    /**
     * Called when the player moves the mousewheel.
     *
     * @param mousePos The current coordinate of the mouse cursor relative to the current dialogs origin.
     * @param delta    The mouse wheel movement.
     * @return True to handle the event, false to pass it to other widgets.
     */
    public boolean onMouseWheel(Point mousePos, double delta) {
        return false;
    }

    /**
     * Override and return true to capture all mouse wheel events, even if the mouse is not over the widget.
     */
    public boolean wantsAllMouseWheelEvents() {
        return false;
    }

    /**
     * Gets a tooltip at the given mouse position for this widget.
     *
     * @param mouseX The mouse x-coordinate relative to the screen.
     * @param mouseY The mouse y-coordinate relative to the screen.
     * @return Null if no tooltip is present, the tooltip otherwise.
     */
    @Nullable
    public Tooltip getTooltip(int mouseX, int mouseY) {
        return null;
    }

    /**
     * Is set by the {@link GuiRoot} when the widget is added, to the style configured for the
     * widget in the style json.
     */
    public void setStyle(WidgetStyle style) {
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    public boolean isFocused() {
        return false;
    }

    public void focus() {
    }

    public void blur() {
    }

    public void addPanels(PanelBlitter panelBlitter) {
        for (var child : getChildren()) {
            child.addPanels(panelBlitter);
        }
    }

    @Override
    public String toString() {
        if (id != null) {
            return getClass().getSimpleName() + "[#" + id + "]";
        } else {
            return super.toString();
        }
    }

}
