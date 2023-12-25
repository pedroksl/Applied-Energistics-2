    /*
     * Copyright 2000-2009 JetBrains s.r.o.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    package appeng.client.gui.layout;


    import appeng.client.gui.widgets.Container;
    import appeng.client.guidebook.document.LytSize;


    public abstract class AbstractLayout implements LayoutManager {

        /**
         * Default value of HGAP property
         */
        public static final int DEFAULT_HGAP = 10;
        /**
         * Default value of VGAP property
         */
        public static final int DEFAULT_VGAP = 5;

        protected LayoutElement[] myComponents;
        protected GridConstraints[] myConstraints;
        /**
         * This is margin between container bounds and bounds of the
         * area where child components are laid out.
         */
        protected Insets myMargin;
        /**
         * Horizontal gap between columns. This parameter is used only by GridLayoutManager.
         */
        private int myHGap;
        /**
         * Vertical gap between rows. This parameter is used only by GridLayoutManager.
         */
        private int myVGap;
        private static final LayoutElement[] COMPONENT_EMPTY_ARRAY = new LayoutElement[0];

        public AbstractLayout() {
            myComponents = COMPONENT_EMPTY_ARRAY;
            myConstraints = GridConstraints.EMPTY_ARRAY;
            myMargin = new Insets(0, 0, 0, 0);
            myHGap = -1;
            myVGap = -1;
        }

        public final Insets getMargin() {
            return myMargin.copy();
        }

        /**
         * @return current own value of horizontal gap between columns. If horizontal
         * gap isn't defined then the method returns <code>-1</code>.
         */
        public final int getHGap() {
            return myHGap;
        }

        /**
         * @return horizontal gap (if it's defined in the layout) or traverses
         * the container hierarchy to find "inherited" HGAP property. Note, that
         * the method always return positive value.
         */
        protected static int getHGapImpl(Container container) {
            if (container == null) {
                throw new IllegalArgumentException("container cannot be null");
            }
            while (container != null) {
                if (container.getLayout() instanceof AbstractLayout) {
                    final AbstractLayout layout = (AbstractLayout) container.getLayout();
                    if (layout.getHGap() != -1) {
                        return layout.getHGap();
                    }
                }
                container = container.getParent();
            }
            return DEFAULT_HGAP;
        }

        /**
         * @param hGap new horizontal gap. If <code>hGap</code> is <code>-1</code>
         *             then own gap is not defined and it should be inherited from parent container.
         * @throws IllegalArgumentException if <code>hGap</code> is less
         *                                  then <code>-1</code>
         */
        public final void setHGap(final int hGap) {
            if (hGap < -1) {
                throw new IllegalArgumentException("wrong hGap: " + hGap);
            }
            myHGap = hGap;
        }

        /**
         * @return current own value of vertical gap between rows. If vertical
         * gap isn't defined then the method returns <code>-1</code>.
         */
        public final int getVGap() {
            return myVGap;
        }

        /**
         * @return horizontal gap (if it's defined in the layout) or traverses
         * the container hierarchy to find "inherited" HGAP property. Note, that
         * the method always return positive value.
         */
        protected static int getVGapImpl(Container container) {
            if (container == null) {
                throw new IllegalArgumentException("container cannot be null");
            }
            while (container != null) {
                if (container.getLayout() instanceof AbstractLayout) {
                    final AbstractLayout layout = (AbstractLayout) container.getLayout();
                    if (layout.getVGap() != -1) {
                        return layout.getVGap();
                    }
                }
                container = container.getParent();
            }
            return DEFAULT_VGAP;
        }

        /**
         * Sets new vertical gap between rows
         *
         * @param vGap new vertical gap. If <code>vGap</code> is <code>-1</code>
         *             then own gap is not defined and it should be inherited from parent container.
         * @throws IllegalArgumentException if <code>vGap</code> is less
         *                                  then <code>-1</code>
         */
        public final void setVGap(final int vGap) {
            if (vGap < -1) {
                throw new IllegalArgumentException("wrong vGap: " + vGap);
            }
            myVGap = vGap;
        }

        public final void setMargin(final Insets margin) {
            if (margin == null) {
                throw new IllegalArgumentException("margin cannot be null");
            }
            myMargin = margin.copy();
        }

        final int getComponentCount() {
            return myComponents.length;
        }

        final LayoutElement getComponent(final int index) {
            return myComponents[index];
        }

        final GridConstraints getConstraints(final int index) {
            return myConstraints[index];
        }

        public void addLayoutComponent(final LayoutElement comp, final Object constraints) {
            if (!(constraints instanceof GridConstraints)) {
                throw new IllegalArgumentException("constraints: " + constraints);
            }

            final LayoutElement[] newComponents = new LayoutElement[myComponents.length + 1];
            System.arraycopy(myComponents, 0, newComponents, 0, myComponents.length);
            newComponents[myComponents.length] = comp;
            myComponents = newComponents;

            final GridConstraints[] newConstraints = new GridConstraints[myConstraints.length + 1];
            System.arraycopy(myConstraints, 0, newConstraints, 0, myConstraints.length);
            newConstraints[myConstraints.length] = (GridConstraints) ((GridConstraints) constraints).clone();
            myConstraints = newConstraints;
        }

        public final void addLayoutComponent(final String name, final LayoutElement comp) {
            throw new UnsupportedOperationException();
        }

        public final void removeLayoutComponent(final LayoutElement comp) {
            final int i = getComponentIndex(comp);
            if (i == -1) {
                throw new IllegalArgumentException("component was not added: " + comp);
            }

            if (myComponents.length == 1) {
                myComponents = COMPONENT_EMPTY_ARRAY;
            } else {
                final LayoutElement[] newComponents = new LayoutElement[myComponents.length - 1];
                System.arraycopy(myComponents, 0, newComponents, 0, i);
                System.arraycopy(myComponents, i + 1, newComponents, i, myComponents.length - i - 1);
                myComponents = newComponents;
            }

            if (myConstraints.length == 1) {
                myConstraints = GridConstraints.EMPTY_ARRAY;
            } else {
                final GridConstraints[] newConstraints = new GridConstraints[myConstraints.length - 1];
                System.arraycopy(myConstraints, 0, newConstraints, 0, i);
                System.arraycopy(myConstraints, i + 1, newConstraints, i, myConstraints.length - i - 1);
                myConstraints = newConstraints;
            }
        }

        public GridConstraints getConstraintsForComponent(LayoutElement comp) {
            final int i = getComponentIndex(comp);
            if (i == -1) {
                throw new IllegalArgumentException("component was not added: " + comp);
            }

            return myConstraints[i];
        }

        private int getComponentIndex(final LayoutElement comp) {
            for (int i = 0; i < myComponents.length; i++) {
                final LayoutElement component = myComponents[i];
                if (component == comp) {
                    return i;
                }
            }
            return -1;
        }

        public final float getLayoutAlignmentX(final Container container) {
            return 0.5f;
        }

        public final float getLayoutAlignmentY(final Container container) {
            return 0.5f;
        }

        public abstract LytSize maximumLayoutSize(Container target);

        public abstract void invalidateLayout(Container target);

        public abstract LytSize preferredLayoutSize(Container parent);

        public abstract LytSize minimumLayoutSize(Container parent);

        public abstract void layoutContainer(Container parent);

    }
