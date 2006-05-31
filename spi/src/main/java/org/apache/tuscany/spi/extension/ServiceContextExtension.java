package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.JDKInboundInvocationHandler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextExtension<T> extends AbstractContext<T> implements ServiceContext<T> {

    protected InboundWire<T> inboundWire;
    protected OutboundWire<T> outboundWire;
    private T target;

    public ServiceContextExtension(String name, CompositeContext parent) throws CoreRuntimeException {
        super(name, parent);
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire<T> wire) {
        target = null;
        inboundWire = wire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> outboundWire) {
        this.outboundWire = outboundWire;
    }

    public T getService() throws TargetException {
        if (target == null) {
            target = inboundWire.getTargetService();
        }
        return target;
    }

    public WireInvocationHandler getHandler() {
        return new JDKInboundInvocationHandler(inboundWire.getInvocationChains());
    }

    public Class<T> getInterface() {
        return inboundWire.getBusinessInterface();
    }

}
