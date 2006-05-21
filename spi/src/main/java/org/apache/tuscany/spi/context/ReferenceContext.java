package org.apache.tuscany.spi.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Manages a reference configured for a binding
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public interface ReferenceContext<T> extends Context<T> {

    /**
     * Returns the service interface configured for the reference
     */
    public Class<T> getInterface();

    /**
     * Returns the handler responsible for flowing a request through the reference
     *
     * @throws TargetException
     *
     */
    public InvocationHandler getHandler() throws TargetException;

    public TargetWire<T> getTargetWire();

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to a service
     * contained by the context
     *
     * @param serviceName the name of the service
     * @param operation the operation to invoke
     */
    TargetInvoker createTargetInvoker(String serviceName, Method operation);

    
}
