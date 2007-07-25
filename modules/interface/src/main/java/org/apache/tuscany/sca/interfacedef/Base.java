package org.apache.tuscany.sca.interfacedef;
/**
 * Base interface for all interface model objects.
 * 
 * @version $Rev$ $Date$
 */
public interface Base {

    /**
     * Returns true if the model element is unresolved.
     * 
     * @return true if the model element is unresolved.
     */
    boolean isUnresolved();

    /**
     * Sets whether the model element is unresolved.
     * 
     * @param unresolved whether the model element is unresolved
     */
    void setUnresolved(boolean unresolved);

}
