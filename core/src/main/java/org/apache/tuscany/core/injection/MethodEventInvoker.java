package org.apache.tuscany.core.injection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Performs an invocation on a method of a given instance
 *
 * @version $Rev$ $Date$
 */
public class MethodEventInvoker<T> implements EventInvoker<T> {
    private final Method method;

    //----------------------------------
    // Constructors
    //----------------------------------

    /**
     * Intantiates an  invoker for the given method
     */
    public MethodEventInvoker(Method method) {
        this.method = method;
    }

    //----------------------------------
    // Methods
    //----------------------------------

    public void invokeEvent(T instance) throws ObjectCallbackException {
        try {
            method.invoke(instance, (Object[]) null);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Method is not accessible [" + method + "]");
        } catch (InvocationTargetException e) {
            throw new ObjectCallbackException("Exception thrown by callback method [" + method + "]", e);
        }
    }

}
