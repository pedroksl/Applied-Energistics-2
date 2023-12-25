package appeng.client.gui.layout;

import appeng.client.gui.widgets.Container;
import appeng.client.guidebook.document.LytSize;

public interface LayoutManager {
    /**
     * If the layout manager uses a per-component string,
     * adds the component {@code comp} to the layout,
     * associating it
     * with the string specified by {@code name}.
     *
     * @param name the string to be associated with the component
     * @param comp the component to be added
     */
    void addLayoutComponent(String name, LayoutElement comp);

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    void removeLayoutComponent(LayoutElement comp);

    /**
     * Calculates the preferred size dimensions for the specified
     * container, given the components it contains.
     *
     * @param  parent the container to be laid out
     * @return the preferred dimension for the container
     *
     * @see #minimumLayoutSize
     */
    LytSize preferredLayoutSize(Container parent);

    /**
     * Calculates the minimum size dimensions for the specified
     * container, given the components it contains.
     *
     * @param  parent the component to be laid out
     * @return the minimum dimension for the container
     *
     * @see #preferredLayoutSize
     */
    LytSize minimumLayoutSize(Container parent);

    /**
     * Lays out the specified container.
     * @param parent the container to be laid out
     */
    void layoutContainer(Container parent);

    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    void addLayoutComponent(LayoutElement comp, Object constraints);

    /**
     * Calculates the maximum size dimensions for the specified container,
     * given the components it contains.
     *
     * @see LayoutElement#getMaximumSize
     * @param  target the target container
     * @return the maximum size of the container
     */
    LytSize maximumLayoutSize(Container target);

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     *
     * @param  target the target container
     * @return the x-axis alignment preference
     */
    float getLayoutAlignmentX(Container target);

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     *
     * @param  target the target container
     * @return the y-axis alignment preference
     */
    float getLayoutAlignmentY(Container target);

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     * @param  target the target container
     */
    void invalidateLayout(Container target);
}
