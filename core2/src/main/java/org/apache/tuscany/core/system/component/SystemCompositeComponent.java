package org.apache.tuscany.core.system.component;

import org.apache.tuscany.core.component.AutowireComponent;

/**
 * Marker type for a specialized composite component. System composites are used by the runtime to manage system
 * components that offer services used by the runtime
 *
 * @version $Rev$ $Date$
 */
public interface SystemCompositeComponent<T> extends AutowireComponent<T> {

    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap code to
     * create the initial configuration components.
     *
     * @param name     the name of the resulting component
     * @param service  the service interface the component should expose
     * @param instance the Object that will become the component's implementation
     * @throws ObjectRegistrationException
     */
    <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance)
        throws ObjectRegistrationException;
}
