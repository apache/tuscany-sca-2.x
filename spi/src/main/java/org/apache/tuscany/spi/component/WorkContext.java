package org.apache.tuscany.spi.component;

/**
 * Implementations track information associated with a request as it is processed by the runtime
 *
 * @version $Rev: 393567 $ $Date: 2006-04-12 11:28:58 -0700 (Wed, 12 Apr 2006) $
 */
public interface WorkContext {

    /**
     * Returns the composite where a remote request came in
     */
    public CompositeComponent getRemoteComponent();

    /**
     * Sets the composite where a remote request came in
     */
    public void setRemoteComponent(CompositeComponent component);

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
