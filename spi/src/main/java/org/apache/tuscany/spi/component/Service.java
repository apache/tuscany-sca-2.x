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
    public WireInvocationHandler getHandler() throws TargetException;

    /**
     * Returns the service interface configured for the service
     */
    public Class<T> getInterface();

    public InboundWire<T> getInboundWire();

    public void setInboundWire(InboundWire<T> wire);

    public OutboundWire<T> getOutboundWire();

    public void setOutboundWire(OutboundWire<T> wire);


}
