package org.apache.tuscany.spi.context;

/**
 *
 * @version $Rev: 393567 $ $Date: 2006-04-12 11:28:58 -0700 (Wed, 12 Apr 2006) $
 */
public interface WorkContext {

    public CompositeContext getCurrentModule();

    public void setCurrentModule(CompositeContext context);

    /**
     * Returns the unique key for the given identifier associated with the current request
     */
    public Object getIdentifier(Object type);

    /**
     * Sets the unique key for the given identifier associated with the current request
     */
    public void setIdentifier(Object type, Object identifier);

    /**
     * Clears the unique key for the given identifier associated with the current request
     */
    public void clearIdentifier(Object type);

    /**
     * Clears all identifiers associated with the current request
     */
    public void clearIdentifiers();

}
