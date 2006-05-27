package org.apache.tuscany.spi.context;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Provides a runtime context for application artifacts configured as components
 *
 * @version $$Rev$$ $$Date$$
 */
public interface ComponentContext<T> extends Context<T> {

    /**
     * Returns a service associated with the given name
     *
     * @throws TargetException if an error occurs retrieving the service instance
     */
    Object getService(String name) throws TargetException;


    /**
     * Returns the service interfaces implemented by the context
     */
    List<Class<?>> getServiceInterfaces();


    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service
     * contained by the context
     *
     * @param serviceName the name of the service
     * @param operation   the operation to invoke
     */
    TargetInvoker createTargetInvoker(String serviceName, Method operation);


}
