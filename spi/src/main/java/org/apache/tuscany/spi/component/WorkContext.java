package org.apache.tuscany.spi.component;

/**
 * Implementations track information associated with a request as it is processed by the runtime
 *
 * @version $Rev$ $Date$
 */
public interface WorkContext {

    /**
     * Returns the composite where a remote request came in
     */
    CompositeComponent getRemoteComponent();

    /**
     * Sets the composite where a remote request came in
     */
    void setRemoteComponent(CompositeComponent component);

    /**
     * Returns the unique key for the given identifier associated with the current request
     */
    Object getIdentifier(Object type);

    /**
     * Sets the unique key for the given identifier associated with the current request
     */
    void setIdentifier(Object type, Object identifier);

    /**
     * Clears the unique key for the given identifier associated with the current request
     */
    void clearIdentifier(Object type);

    /**
     * Clears all identifiers associated with the current request
     */
    void clearIdentifiers();

}
