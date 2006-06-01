package org.apache.tuscany.spi.wire;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface RuntimeWire<T> {

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
     * Returns true if the wire and all of its interceptors can be optimized
     */
    boolean isOptimizable();

}
