package org.apache.tuscany.spi.component;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Manages an SCA reference configured for a binding
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public interface Reference<T> extends SCAObject<T> {

    /**
     * Returns the service interface configured for the reference
     */
    public Class<T> getInterface();

    /**
     * Returns the handler responsible for flowing a request through the reference
     *
     * @throws TargetException
     */
    public WireInvocationHandler getHandler() throws TargetException;

    public InboundWire<T> getInboundWire();

    public OutboundWire<T> getOutboundWire();

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to the target
     * service of the reference
     *
     * @param serviceName the name of the service
     * @param operation   the operation to invoke
     */
    TargetInvoker createTargetInvoker(String serviceName, Method operation);


}
