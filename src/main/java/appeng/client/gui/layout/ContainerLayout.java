package appeng.client.gui.layout;

import appeng.client.gui.GuiRoot;
import appeng.client.gui.GuiWidget;
import appeng.client.gui.anchoring.AnchorAxis;
import appeng.client.gui.anchoring.AnchorLine;
import appeng.client.gui.anchoring.AnchorLineTarget;
import net.minecraft.client.renderer.Rect2i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.OptionalInt;
import java.util.Set;

public final class ContainerLayout {
    private static final Logger LOG = LoggerFactory.getLogger(ContainerLayout.class);

    private ContainerLayout() {
    }

    public static void layout(Collection<? extends LayoutElement> widgets,
                              Rect2i screenBounds) {
        layout(AnchorAxis.X, widgets, screenBounds);
        layout(AnchorAxis.Y, widgets, screenBounds);

        // Mark layout as done for all widgets and layout children
        for (var layoutEl : widgets) {
            layoutEl.layoutInvalid = false;

            var layoutChildren = layoutEl.getLayoutChildren();
            if (!layoutChildren.isEmpty()) {
                layout(layoutEl.getLayoutChildren(), screenBounds);
            }
        }
    }

    private static void layout(AnchorAxis axis,
                               Collection<? extends LayoutElement> widgets,
                               Rect2i screenBounds) {

        Set<LayoutElement> openSet = Collections.newSetFromMap(new IdentityHashMap<>());
        openSet.addAll(widgets);

        while (!openSet.isEmpty()) {
            // Indicates that at least one widget in this iteration was laid out correctly
            boolean didWork = false;
            for (var it = openSet.iterator(); it.hasNext(); ) {
                var widget = it.next();

                if (widget.hasLayout()) {
                    it.remove();
                    continue;
                }

                var start = getWidgetStart(widget, axis);
                var size = getWidgetSize(widget, axis);
                var fixedSize = getWidgetFixedSize(widget, axis);
                if (fixedSize.isPresent()) {
                    setWidgetSize(widget, axis, size);
                } else {
                    // TODO Preferred size (images, text, etc.)
                }

                var anchors = widget.getAnchors();

                if (!anchors.isAnchored(axis)) {
                    didWork = true;
                    it.remove();
                    continue; // No anchor layout needed
                }

                var startAnchor = anchors.getAxisStart(axis);
                var centerAnchor = anchors.getAxisCenter(axis);
                var endAnchor = anchors.getAxisEnd(axis);

                // Center takes precedence and assumes the item has a preferred width
                if (centerAnchor != null) {
                    var targetPos = resolveAnchorTargetPos(screenBounds, widget, centerAnchor, openSet);
                    if (targetPos == null) {
                        // Unmet dependency
                        continue;
                    }
                    start = targetPos - size / 2;
                } else {
                    if (startAnchor != null) {
                        var targetPos = resolveAnchorTargetPos(screenBounds, widget, startAnchor, openSet);
                        if (targetPos == null) {
                            // Unmet dependency
                            continue;
                        }
                        start = targetPos;
                    }

                    if (endAnchor != null) {
                        var targetPos = resolveAnchorTargetPos(screenBounds, widget, endAnchor, openSet);
                        if (targetPos == null) {
                            // Unmet dependency
                            continue;
                        }
                        size = targetPos - start;
                    }
                }

                setWidgetStart(widget, axis, start);
                setWidgetSize(widget, axis, size);
                didWork = true;
                it.remove();
            }

            if (!didWork && !openSet.isEmpty()) {
                throw new IllegalStateException("Anchor cycle detected on axis " + axis + ": " + openSet);
            }
        }
    }

    private static Integer resolveAnchorTargetPos(Rect2i screenBounds, LayoutElement widget, AnchorLine line, Set<LayoutElement> openSet) {
        LayoutElement targetElement = null;
        var parent = widget.getLayoutParent();
        if (line.element() instanceof AnchorLineTarget.Parent) {
            targetElement = parent;
        } else if (line.element() instanceof AnchorLineTarget.SiblingId siblingId) {
            GuiRoot root = null;
            if (widget instanceof GuiWidget guiWidget) {
                root = guiWidget.getRoot();
            }
            if (root != null) {
                targetElement = root.getWidgetById(siblingId.id());
            }
            if (targetElement == null) {
                LOG.warn("Couldn't find sibling with id {} for {}", siblingId.id(), widget);
            }
        } else if (line.element() instanceof AnchorLineTarget.SiblingInstance siblingInstance) {
            targetElement = siblingInstance.widget();
        }

        // Validate for valid target element
        if (targetElement == widget) {
            LOG.warn("Cannot anchor {} to itself.", widget);
        } else if (targetElement != null && targetElement != parent && targetElement.getLayoutParent() != parent) {
            LOG.warn("Cannot anchor {} to non-sibling element {}", widget, targetElement);
        }

        if (targetElement != null && openSet.contains(targetElement)) {
            // The target element is still waiting for layout
            return null;
        }

        if (targetElement == null) {
            if (parent != null) {
                return switch (line.type()) {
                    case LEFT, TOP -> 0;
                    case RIGHT -> parent.width;
                    case BOTTOM -> parent.height;
                    case HORIZONTAL_CENTER -> parent.width / 2;
                    case VERTICAL_CENTER -> parent.height / 2;
                };
            } else {
                return switch (line.type()) {
                    case LEFT, TOP -> 0;
                    case RIGHT -> screenBounds.getWidth();
                    case BOTTOM -> screenBounds.getHeight();
                    case HORIZONTAL_CENTER -> screenBounds.getWidth() / 2;
                    case VERTICAL_CENTER -> screenBounds.getHeight() / 2;
                };
            }
        }

        // The target element has layout
        var targetBounds = targetElement.getLayoutBounds();
        return switch (line.type()) {
            case LEFT -> targetBounds.getX();
            case TOP -> targetBounds.getY();
            case RIGHT -> targetBounds.getX() + targetBounds.getWidth();
            case BOTTOM -> targetBounds.getY() + targetBounds.getHeight();
            case HORIZONTAL_CENTER -> targetBounds.getX() + targetBounds.getWidth() / 2;
            case VERTICAL_CENTER -> targetBounds.getY() + targetBounds.getHeight() / 2;
        };
    }

    private static int getWidgetStart(LayoutElement widget, AnchorAxis axis) {
        return switch (axis) {
            case X -> widget.x;
            case Y -> widget.y;
        };
    }

    private static int getWidgetSize(LayoutElement widget, AnchorAxis axis) {
        return switch (axis) {
            case X -> widget.width;
            case Y -> widget.height;
        };
    }

    private static OptionalInt getWidgetFixedSize(LayoutElement widget, AnchorAxis axis) {
        return switch (axis) {
            case X -> widget.getFixedWidth();
            case Y -> widget.getFixedHeight();
        };
    }

    private static void setWidgetStart(LayoutElement widget, AnchorAxis axis, int start) {
        switch (axis) {
            case X -> widget.x = start;
            case Y -> widget.y = start;
        }
    }

    private static void setWidgetSize(LayoutElement widget, AnchorAxis axis, int size) {
        switch (axis) {
            case X -> widget.width = size;
            case Y -> widget.height = size;
        }
    }

    private static int getBoundsStart(Rect2i bounds, AnchorAxis axis) {
        return switch (axis) {
            case X -> bounds.getX();
            case Y -> bounds.getY();
        };
    }

    private static int getBoundsEnd(Rect2i bounds, AnchorAxis axis) {
        return switch (axis) {
            case X -> bounds.getX() + bounds.getWidth();
            case Y -> bounds.getY() + bounds.getHeight();
        };
    }

}
