package org.apache.tuscany.spi.component;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Manages an SCA reference configured with a binding
 *
 * @version $Rev$ $Date$
 */
public interface Reference<T> extends SCAObject<T> {

    /**
     * Returns the service interface configured for the reference
     */
    Class<T> getInterface();

    /**
     * Returns the handler responsible for flowing a request through the reference
     *
     * @throws TargetException
     */
    WireInvocationHandler getHandler() throws TargetException;

    InboundWire<T> getInboundWire();

    OutboundWire<T> getOutboundWire();

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which dispatches to the target service of
     * the reference
     *
     * @param operation the operation to invoke
     */
    TargetInvoker createTargetInvoker(Method operation);

    /**
     * Callback to create a {@link org.apache.tuscany.spi.wire.TargetInvoker} which issues a non-blocking dispatch
     *
     * @param operation the operation to invoke
     * @param wire      the outbound wire of the invocation source, used for callbacks
     */
    TargetInvoker createAsyncTargetInvoker(Method operation, OutboundWire wire);

}
