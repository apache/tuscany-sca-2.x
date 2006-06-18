package org.apache.tuscany.spi.injection;

/**
 * Performs an invocation on an instance
 *
 * @version $Rev$ $Date$
 */
public interface EventInvoker<T> {

    /**
     * Performs the invocation on a given instance
     *
     * @throws ObjectCallbackException
     */
    void invokeEvent(T instance) throws ObjectCallbackException;
}
