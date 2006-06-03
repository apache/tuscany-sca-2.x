package org.apache.tuscany.spi.component;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The runtime instantiation of an SCA service configured for a binding.
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
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
