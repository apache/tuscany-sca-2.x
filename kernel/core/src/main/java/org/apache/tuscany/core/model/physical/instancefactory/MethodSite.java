package org.apache.tuscany.core.model.physical.instancefactory;

/**
 * Represents a method injection site.
 * 
 * @version $Revision$ $Date$
 *
 */
public class MethodSite implements Site {
    
    // Name of the site
    private String name;

    /**
     * Gets the name of the site.
     * @return Site name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the site.
     * @param name Name of the site.
     */
    public void setName(String name) {
        this.name = name;
    }

}
