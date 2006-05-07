package org.apache.tuscany.core.injection;

/**
 * Performs an invocation on an instance
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
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
