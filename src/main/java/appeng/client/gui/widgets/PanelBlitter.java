package appeng.client.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;

import appeng.client.gui.style.Blitter;

public class PanelBlitter {

    private static final int BORDER = 4;

    private static final int SIZE = 256;
    private static final int TILED_SIZE = SIZE - 2 * BORDER;
    private static final Blitter FULL = Blitter.texture("guis/background.png", 2 * SIZE, SIZE);

    // Top-Left, Top-Right, Right-Bottom, Bottom-Left
    private static final Blitter[] OUTER_CORNERS = {
            FULL.copy().src(0, 0, BORDER, BORDER),
            FULL.copy().src(SIZE - BORDER, 0, BORDER, BORDER),
            FULL.copy().src(SIZE - BORDER, SIZE - BORDER, BORDER, BORDER),
            FULL.copy().src(0, SIZE - BORDER, BORDER, BORDER),
    };
    private static final Blitter[] INNER_CORNERS = {
            FULL.copy().src(SIZE, 0, BORDER, BORDER),
            FULL.copy().src(SIZE + BORDER, 0, BORDER, BORDER),
            FULL.copy().src(SIZE + BORDER, BORDER, BORDER, BORDER),
            FULL.copy().src(SIZE, BORDER, BORDER, BORDER),
    };

    // Top, Right, Bottom, Left
    private static final Blitter[] EDGES = new Blitter[] {
            FULL.copy().src(BORDER, 0, TILED_SIZE, BORDER),
            FULL.copy().src(SIZE - BORDER, BORDER, BORDER, TILED_SIZE),
            FULL.copy().src(BORDER, SIZE - BORDER, TILED_SIZE, BORDER),
            FULL.copy().src(0, BORDER, BORDER, TILED_SIZE)
    };

    private static final Blitter CENTER = FULL.copy().src(BORDER, BORDER, TILED_SIZE, TILED_SIZE);

    private final List<Rectangle> rects = new ArrayList<>();

    private final List<Rectangle> processedRects = new ArrayList<>();

    public PanelBlitter() {
    }

    public void addBounds(int x, int y, int width, int height) {
        rects.add(new Rectangle(x, y, width, height));
        processedRects.clear();
    }

    public void blit(GuiGraphics graphics, int xOffset, int yOffset) {

        // Update processed rectangles lazily
        if (processedRects.size() != rects.size()) {
            processedRects.clear();
            for (var rect : rects) {
                processedRects.add(rect.copy());
            }
            // Merge/Split Edges with other rectangles
            for (var rect : processedRects) {
                for (var otherRect : processedRects) {
                    if (rect == otherRect) {
                        continue;
                    }

                    // Split/eliminate left edges that touch the other rectangle
                    mergeEdges(rect, otherRect, 0);
                    mergeEdges(rect, otherRect, 1);
                    mergeEdges(rect, otherRect, 2);
                    mergeEdges(rect, otherRect, 3);
                }
            }
        }

        for (var rect : processedRects) {
            var outerTop = rect.outerTop();
            var outerRight = rect.outerRight();
            var outerBottom = rect.outerBottom();
            var outerLeft = rect.outerLeft();
            var innerTop = rect.topEdges.isEmpty() ? outerTop : outerTop + BORDER;
            var innerRight = rect.rightEdges.isEmpty() ? outerRight : outerRight - BORDER;
            var innerBottom = rect.bottomEdges.isEmpty() ? outerBottom : outerBottom - BORDER;
            var innerLeft = rect.leftEdges.isEmpty() ? outerLeft : outerLeft + BORDER;

            for (int side = 0; side < 4; side++) {
                var edges = rect.getEdgesForSide(side);

                for (var edge : edges) {
                    int el, et, eb, er;
                    switch (side) {
                        case 0 -> {
                            el = Math.max(innerLeft, outerLeft + edge.start);
                            er = Math.min(innerRight, outerLeft + edge.end);
                            et = outerTop;
                            eb = innerTop;
                        }
                        case 1 -> {
                            el = innerRight;
                            er = outerRight;
                            et = Math.max(innerTop, outerTop + edge.start);
                            eb = Math.min(innerBottom, outerTop + edge.end);
                        }
                        case 2 -> {
                            el = Math.max(innerLeft, outerLeft + edge.start);
                            er = Math.min(innerRight, outerLeft + edge.end);
                            et = innerBottom;
                            eb = outerBottom;
                        }
                        case 3 -> {
                            el = outerLeft;
                            er = innerLeft;
                            et = Math.max(innerTop, outerTop + edge.start);
                            eb = Math.min(innerBottom, outerTop + edge.end);
                        }
                        default -> throw new IndexOutOfBoundsException("side");
                    }

                    renderEdge(graphics, edge.style, side, xOffset + el, yOffset + et, xOffset + er, yOffset + eb);
                }
            }

            for (int i = 0; i < rect.corners.length; i++) {
                var cornerStyle = rect.corners[i];
                var blitter = cornerStyle.blitter;
                if (blitter != null) {
                    switch (i) {
                        case 0 -> blitter.dest(xOffset + rect.x, yOffset + rect.y, BORDER, BORDER).blit(graphics);
                        case 1 -> blitter.dest(xOffset + rect.outerRight() - BORDER, yOffset + rect.y, BORDER, BORDER)
                                .blit(graphics);
                        case 2 -> blitter.dest(xOffset + rect.outerRight() - BORDER,
                                yOffset + rect.outerBottom() - BORDER, BORDER, BORDER).blit(graphics);
                        case 3 -> blitter.dest(xOffset + rect.x, yOffset + rect.outerBottom() - BORDER, BORDER, BORDER)
                                .blit(graphics);
                    }
                }
            }

            CENTER.dest(xOffset + innerLeft, yOffset + innerTop, innerRight - innerLeft, innerBottom - innerTop)
                    .blit(graphics);
        }
    }

    private void renderEdge(GuiGraphics graphics, EdgeStyle style, int side, int left, int top, int right, int bottom) {
        if (right <= left || bottom <= top) {
            return;
        }

        var innerStartCorner = CornerStyle.NONE;
        var innerEndCorner = CornerStyle.NONE;
        switch (side) {
            case 0 -> {
                innerStartCorner = CornerStyle.INNER_BOTTOM_RIGHT;
                innerEndCorner = CornerStyle.INNER_BOTTOM_LEFT;
            }
            case 1 -> {
                innerStartCorner = CornerStyle.INNER_BOTTOM_LEFT;
                innerEndCorner = CornerStyle.INNER_TOP_LEFT;
            }
            case 2 -> {
                innerStartCorner = CornerStyle.INNER_TOP_RIGHT;
                innerEndCorner = CornerStyle.INNER_TOP_LEFT;
            }
            case 3 -> {
                innerStartCorner = CornerStyle.INNER_BOTTOM_RIGHT;
                innerEndCorner = CornerStyle.INNER_TOP_RIGHT;
            }
            default -> throw new IndexOutOfBoundsException("side");
        }

        if (style == EdgeStyle.NORMAL) {
            var edgeBlitter = EDGES[side];
            edgeBlitter.dest(left, top, right - left, bottom - top).blit(graphics);
        } else {
            if (style == EdgeStyle.INNER_FILL_NO_START) {
                innerStartCorner = CornerStyle.NONE;
            } else if (style == EdgeStyle.INNER_FILL_NO_END) {
                innerEndCorner = CornerStyle.NONE;
            }

            if (side == 1 || side == 3) {
                // Vertical
                if (innerStartCorner != CornerStyle.NONE) {
                    innerStartCorner.blitter.dest(left, top).blit(graphics);
                    top += BORDER;
                }
                if (innerEndCorner != CornerStyle.NONE) {
                    innerEndCorner.blitter.dest(left, bottom - BORDER).blit(graphics);
                    bottom -= BORDER;
                }
            } else {
                // Horizontal
                if (innerStartCorner != CornerStyle.NONE) {
                    innerStartCorner.blitter.dest(left, top).blit(graphics);
                    left += BORDER;
                }
                if (innerEndCorner != CornerStyle.NONE) {
                    innerEndCorner.blitter.dest(right - BORDER, top).blit(graphics);
                    right -= BORDER;
                }
            }
            if (right - left > 0 && bottom - top > 0) {
                CENTER.dest(left, top, right - left, bottom - top).blit(graphics);
            }
        }
    }

    private static void mergeEdges(Rectangle rect, Rectangle otherRect, int side) {
        var edges = rect.getEdgesForSide(side);

        if (edges.isEmpty()) {
            return;
        }
        // Determine if the two rects touch on the given side
        var touching = switch (side) {
            case 0 -> rect.outerTop() == otherRect.outerBottom();
            case 1 -> rect.outerRight() == otherRect.outerLeft();
            case 2 -> rect.outerBottom() == otherRect.outerTop();
            case 3 -> rect.outerLeft() == otherRect.outerRight();
            default -> throw new IllegalStateException("Unexpected value: " + side);
        };
        if (!touching) {
            return;
        }

        var ourStart = switch (side) {
            case 0, 2 -> rect.x;
            case 1, 3 -> rect.y;
            default -> throw new IllegalStateException("Unexpected value: " + side);
        };
        var otherStart = switch (side) {
            case 0, 2 -> otherRect.outerLeft();
            case 1, 3 -> otherRect.outerTop();
            default -> throw new IllegalStateException("Unexpected value: " + side);
        };
        var otherEnd = switch (side) {
            case 0, 2 -> otherRect.outerRight();
            case 1, 3 -> otherRect.outerBottom();
            default -> throw new IllegalStateException("Unexpected value: " + side);
        };

        var tempEdges = new ArrayList<Edge>();
        for (var ourEdge : edges) {
            var edgeStart = ourStart + ourEdge.start;
            var edgeEnd = ourStart + ourEdge.end;

            // Determine overlap
            if (edgeStart < otherEnd && edgeEnd > otherStart) {
                // Add split edge for top part that starts before the other rectangle does
                var overhangStart = otherStart - edgeStart;
                if (overhangStart > 0) {
                    tempEdges.add(new Edge(edgeStart - ourStart, edgeStart - ourStart + overhangStart));
                }
                // Add split edge for bottom part that starts before the other rectangle does
                var overhangEnd = edgeEnd - otherEnd;
                if (overhangEnd > 0) {
                    tempEdges.add(new Edge(edgeEnd - ourStart - overhangEnd, edgeEnd));
                }

                var startCorner = switch (side) {
                    case 0 -> 0;
                    case 1 -> 1;
                    case 2 -> 3;
                    case 3 -> 0;
                    default -> throw new IllegalStateException("Unexpected value: " + side);
                };
                var endCorner = switch (side) {
                    case 0 -> 1;
                    case 1 -> 2;
                    case 2 -> 2;
                    case 3 -> 3;
                    default -> throw new IllegalStateException("Unexpected value: " + side);
                };

                // Add a fill only if the edge hasn't been entirely eliminated
                if (overhangStart > 0 || overhangEnd > 0) {
                    var fillType = EdgeStyle.INNER_FILL;
                    if (overhangStart <= 0) {
                        fillType = EdgeStyle.INNER_FILL_NO_START;
                    } else if (overhangEnd <= 0) {
                        fillType = EdgeStyle.INNER_FILL_NO_END;
                    }

                    tempEdges.add(
                            new Edge(fillType, edgeStart - ourStart + overhangStart, edgeEnd - ourStart - overhangEnd));
                    if (overhangStart <= 0) {
                        rect.corners[startCorner] = switch (side) {
                            case 0, 2 -> CornerStyle.LEFT_BORDER;
                            case 1, 3 -> CornerStyle.TOP_BORDER;
                            default -> throw new IllegalStateException("Unexpected value: " + side);
                        };
                    }
                    if (overhangEnd <= 0) {
                        rect.corners[endCorner] = switch (side) {
                            case 0, 2 -> CornerStyle.RIGHT_BORDER;
                            case 1, 3 -> CornerStyle.BOTTOM_BORDER;
                            default -> throw new IllegalStateException("Unexpected value: " + side);
                        };
                    }
                } else {
                    // If the edge has been eliminated, also eliminate the corners
                    rect.corners[startCorner] = CornerStyle.NONE;
                    rect.corners[endCorner] = CornerStyle.NONE;
                }
            } else {
                tempEdges.add(ourEdge);
            }
        }
        edges.clear();
        edges.addAll(tempEdges);
    }

    private record Edge(EdgeStyle style, int start, int end) {
        public Edge(int start, int end) {
            this(EdgeStyle.NORMAL, start, end);
        }
    }

    private static final class Rectangle {
        CornerStyle[] corners = new CornerStyle[] {
                CornerStyle.OUTER_TOP_LEFT,
                CornerStyle.OUTER_TOP_RIGHT,
                CornerStyle.OUTER_BOTTOM_RIGHT,
                CornerStyle.OUTER_BOTTOM_LEFT,
        };
        private final List<Edge> leftEdges = new ArrayList<>();
        private final List<Edge> topEdges = new ArrayList<>();
        private final List<Edge> rightEdges = new ArrayList<>();
        private final List<Edge> bottomEdges = new ArrayList<>();
        int x;
        int y;
        int width;
        int height;

        public Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.leftEdges.add(new Edge(0, height));
            this.topEdges.add(new Edge(0, width));
            this.rightEdges.add(new Edge(0, height));
            this.bottomEdges.add(new Edge(0, width));
        }

        public int outerLeft() {
            return x;
        }

        public int outerTop() {
            return y;
        }

        public int outerRight() {
            return x + width;
        }

        public int outerBottom() {
            return y + height;
        }

        public List<Edge> getEdgesForSide(int side) {
            return switch (side) {
                case 0 -> topEdges;
                case 1 -> rightEdges;
                case 2 -> bottomEdges;
                case 3 -> leftEdges;
                default -> throw new IllegalStateException("Unexpected value: " + side);
            };
        }

        public Rectangle copy() {
            var result = new Rectangle(
                    x, y, width, height);
            System.arraycopy(corners, 0, result.corners, 0, result.corners.length);
            for (int i = 0; i < 4; i++) {
                result.getEdgesForSide(i).clear();
                result.getEdgesForSide(i).addAll(getEdgesForSide(i));
            }
            return result;
        }

    }

    private enum EdgeStyle {
        NORMAL,
        INNER_FILL,
        INNER_FILL_NO_START,
        INNER_FILL_NO_END
    }

    private enum CornerStyle {
        NONE(null),
        OUTER_TOP_LEFT(OUTER_CORNERS[0]),
        OUTER_TOP_RIGHT(OUTER_CORNERS[1]),
        OUTER_BOTTOM_RIGHT(OUTER_CORNERS[2]),
        OUTER_BOTTOM_LEFT(OUTER_CORNERS[3]),
        TOP_BORDER(EDGES[0]),
        RIGHT_BORDER(EDGES[1]),
        BOTTOM_BORDER(EDGES[2]),
        LEFT_BORDER(EDGES[3]),
        INNER_TOP_LEFT(INNER_CORNERS[0]),
        INNER_TOP_RIGHT(INNER_CORNERS[1]),
        INNER_BOTTOM_RIGHT(INNER_CORNERS[2]),
        INNER_BOTTOM_LEFT(INNER_CORNERS[3]);

        private final Blitter blitter;

        CornerStyle(Blitter blitter) {
            this.blitter = blitter;
        }
    }

}
