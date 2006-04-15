package org.apache.tuscany.core.injection;

/**
 * Performs an wire on an instance
 *
 * @version $Rev$ $Date$
 * @see MethodEventInvoker
 */
public interface EventInvoker<T> {

    /**
     * Performs the wire on a given instance
     *
     * @throws ObjectCallbackException
     */
    void invokeEvent(T instance) throws ObjectCallbackException;
}
