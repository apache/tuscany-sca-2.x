package org.apache.tuscany.core.model.physical.instancefactory;

/**
 * Represents a field injection site.
 * 
 * @version $Revision$ $Date$
 *
 */
public class ConstructorSite implements Site {
    
    // Index of the site
    private int index;

    /**
     * Gets the index of the site.
     * @return Site index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index of the site.
     * @param index Index of the site.
     */
    public void setIndex(int name) {
        this.index = name;
    }

}
