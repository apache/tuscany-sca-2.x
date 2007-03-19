package org.apache.tuscany.core.model.physical.instancefactory;

import java.lang.annotation.ElementType;

/**
 * Represents a field injection site.
 * 
 * @version $Revision$ $Date$
 *
 */
public class MemberSite {
    
    // Element type
    private ElementType elementType;
    
    // Name of the site
    private String name;

    public MemberSite() {
    }

    public MemberSite(ElementType elementType, String name) {
        this.elementType = elementType;
        this.name = name;
    }

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

    /**
     * Gets the element type.
     * @return Element type.
     */
    public ElementType getElementType() {
        return elementType;
    }

    /**
     * Sets the element type.
     * @param elementType Element type.
     */
    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

}
