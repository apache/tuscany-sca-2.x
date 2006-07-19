package org.apache.tuscany.spi.component;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The runtime instantiation of an SCA service configured with a binding.
 *
 * @version $Rev$ $Date$
 */
public interface Service<T> extends SCAObject<T> {

    /**
     * Returns the handler responsible for flowing a request through the service
     *
     * @throws TargetException
     */
    WireInvocationHandler getHandler() throws TargetException;

    /**
     * Returns the service interface configured for the service
     */
    Class<T> getInterface();

    InboundWire<T> getInboundWire();

    void setInboundWire(InboundWire<T> wire);

    OutboundWire<T> getOutboundWire();

    void setOutboundWire(OutboundWire<T> wire);


}
