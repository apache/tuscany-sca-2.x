package org.apache.tuscany.spi.wire;

import org.apache.tuscany.spi.component.TargetException;

/**
 * The base wire type used to connect references and services
 *
 * @version $$Rev$$ $$Date$$
 */
public interface RuntimeWire<T> {

    /**
     * Returns the non-proxied target instance for this wire
     */
    T getTargetService() throws TargetException;

    /**
     * Sets the primary interface type generated proxies implement
     */
    void setBusinessInterface(Class<T> interfaze);

    /**
     * Returns the primary interface type implemented by generated proxies
     */
    Class<T> getBusinessInterface();

    /**
     * Adds an interface type generated proxies implement
     */
    void addInterface(Class<?> claz);

    /**
     * Returns an array of all interfaces implemented by generated proxies
     */
    Class[] getImplementedInterfaces();

    /**
     * Returns true if the wire and all of its interceptors and handlers can be optimized
     */
    boolean isOptimizable();

}
