package org.apache.tuscany.core.system.context;

import org.apache.tuscany.core.system.wire.SystemInboundWire;
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Default implementation for service contexts configured with the {@link org.apache.tuscany.core.system.model.SystemBinding}
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemServiceContextImpl<T> extends AbstractContext<T> implements SystemServiceContext<T> {

    protected SystemInboundWire<T> inboundWire;
    protected SystemOutboundWire<T> outboundWire;

    public SystemServiceContextImpl(String name, CompositeContext parent) throws CoreRuntimeException {
        super(name, parent);
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire<T> wire) {
        assert(wire instanceof SystemInboundWire): "wire must be a " + SystemInboundWire.class.getName();
        this.inboundWire = (SystemInboundWire<T>) wire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> wire) {
        assert(wire instanceof SystemOutboundWire): "wire must be a " + SystemOutboundWire.class.getName();
        this.outboundWire = (SystemOutboundWire<T>) wire;
    }

    public Class<T> getInterface() {
        return inboundWire.getBusinessInterface();
    }

    public WireInvocationHandler getHandler() {
        // system services do not proxy
        throw new UnsupportedOperationException();
    }

    public T getService() throws TargetException {
        return inboundWire.getTargetService();
    }

}
