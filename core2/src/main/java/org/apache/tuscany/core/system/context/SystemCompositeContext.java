package org.apache.tuscany.core.system.context;

import org.apache.tuscany.core.context.AutowireContext;

/**
 * Marker type for system composite contexts
 *
 * @version $Rev: 392199 $ $Date: 2006-04-06 23:32:29 -0700 (Thu, 06 Apr 2006) $
 */
public interface SystemCompositeContext<T> extends AutowireContext<T> {

    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap
     * code to create the initial configuration components.
     *
     * @param name     the name of the resulting component
     * @param service
     * @param instance the Object that will become the component's implementation
     * @throws ObjectRegistrationException
     */
    void registerJavaObject(String name, Class<?> service, Object instance) throws ObjectRegistrationException;
}
