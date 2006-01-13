package org.apache.tuscany.core.injection;

/**
 * A no-op invoker
 *
 * @version $Rev$ $Date$
 */
public final class NullEventInvoker<T> implements EventInvoker<T> {
    public static final EventInvoker<?> NULL_INVOKER = new NullEventInvoker();

    public void invokeEvent(T instance) {
        // does nothing
    }
}
