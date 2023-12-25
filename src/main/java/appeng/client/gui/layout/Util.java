// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package appeng.client.gui.layout;

import appeng.client.guidebook.document.LytSize;

import java.util.ArrayList;

public final class Util {
    private static final LytSize MAX_SIZE = new LytSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final int DEFAULT_INDENT = 10;

    public static LytSize getMinimumSize(final LayoutElement component, final GridConstraints constraints, final boolean addIndent) {
        try {
            var size = getSize(constraints.myMinimumSize, component.getMinimumSize());
            if (addIndent) {
                size.width += DEFAULT_INDENT * constraints.getIndent();
            }
            return size.toLytSize();
        }
        catch (NullPointerException npe) { //IDEA-80722
            return LytSize.empty();
        }
    }

    public static LytSize getMaximumSize(final GridConstraints constraints, final boolean addIndent) {
        try {
            //[anton] we use only our property for maximum size.
            // JButton reports that its max size = pref size, so it is impossible to make a column of same sized buttons.
            // Probably there are other bad cases...
            var size = getSize(constraints.myMaximumSize, MAX_SIZE);
            if (addIndent && size.width < MAX_SIZE.width()) {
                size.width += DEFAULT_INDENT * constraints.getIndent();
            }
            return size.toLytSize();
        }
        catch (NullPointerException e) {//IDEA-80722
            return new LytSize(0, 0);
        }
    }

    public static LytSize getPreferredSize(final LayoutElement component, final GridConstraints constraints, final boolean addIndent) {
        try {
            var size = getSize(constraints.myPreferredSize, component.getPreferredSize());
            if (addIndent) {
                size.width += DEFAULT_INDENT * constraints.getIndent();
            }
            return size.toLytSize();
        }
        catch (NullPointerException e) {//IDEA-80722
            return new LytSize(0, 0);
        }
    }

    private static MutableSize getSize(final LytSize overridenSize, final LytSize ownSize) {
        final int overridenWidth = overridenSize.width() >= 0 ? overridenSize.width() : ownSize.width();
        final int overridenHeight = overridenSize.height() >= 0 ? overridenSize.height() : ownSize.height();
        return new MutableSize(overridenWidth, overridenHeight);
    }

    static void adjustSize(final LayoutElement component, final GridConstraints constraints, final MutableSize size) {
        final LytSize minimumSize = getMinimumSize(component, constraints, false);
        final LytSize maximumSize = getMaximumSize(constraints, false);

        size.width = Math.max(size.width, minimumSize.width());
        size.height = Math.max(size.height, minimumSize.height());

        size.width = Math.min(size.width, maximumSize.width());
        size.height = Math.min(size.height, maximumSize.height());
    }

    /**
     * @param eliminated output parameter; will be filled indices (Integers) of eliminated cells. May be null.
     * @return cellCount
     */
    public static int eliminate(final int[] cellIndices, final int[] spans, final ArrayList eliminated) {
        final int size = cellIndices.length;
        if (size != spans.length) {
            throw new IllegalArgumentException("size mismatch: " + size + ", " + spans.length);
        }
        if (eliminated != null && eliminated.size() != 0) {
            throw new IllegalArgumentException("eliminated must be empty");
        }

        int cellCount = 0;
        for (int i = 0; i < size; i++) {
            cellCount = Math.max(cellCount, cellIndices[i] + spans[i]);
        }

        for (int cell = cellCount - 1; cell >= 0; cell--) {
            // check if we should eliminate cell

            boolean starts = false;
            boolean ends = false;

            for (int i = 0; i < size; i++) {
                if (cellIndices[i] == cell) {
                    starts = true;
                }
                if (cellIndices[i] + spans[i] - 1 == cell) {
                    ends = true;
                }
            }

            if (starts && ends) {
                continue;
            }

            if (eliminated != null) {
                eliminated.add(Integer.valueOf(cell));
            }

            // eliminate cell
            for (int i = 0; i < size; i++) {
                final boolean decreaseSpan = cellIndices[i] <= cell && cell < cellIndices[i] + spans[i];
                final boolean decreaseIndex = cellIndices[i] > cell;

                if (decreaseSpan) {
                    spans[i]--;
                }

                if (decreaseIndex) {
                    cellIndices[i]--;
                }
            }

            cellCount--;
        }

        return cellCount;
    }
}