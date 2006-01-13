package org.apache.tuscany.core.injection;

/**
 * Performs an invocation on an instance
 *
 * @version $Rev$ $Date$
 * @see MethodEventInvoker
 */
public interface EventInvoker<T> {

    /**
     * Performs the invocation on a given instance
     *
     * @throws ObjectCallbackException
     */
    void invokeEvent(T instance) throws ObjectCallbackException;
}
