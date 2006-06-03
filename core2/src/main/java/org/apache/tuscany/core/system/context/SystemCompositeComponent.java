package org.apache.tuscany.core.system.context;

import org.apache.tuscany.core.context.AutowireComponent;

/**
 * Marker type for system composite contexts
 *
 * @version $Rev: 392199 $ $Date: 2006-04-06 23:32:29 -0700 (Thu, 06 Apr 2006) $
 */
public interface SystemCompositeComponent<T> extends AutowireComponent<T> {

    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap
     * code to create the initial configuration components.
     *
     * @param name     the name of the resulting component
     * @param service  the service interface the component should expose
     * @param instance the Object that will become the component's implementation
     * @throws ObjectRegistrationException
     */
    <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance) throws ObjectRegistrationException;
}
