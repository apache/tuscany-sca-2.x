package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.ServiceInvocationHandler;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * The default implementation of an service context
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceContextExtension<T> extends AbstractContext<T> implements ServiceContext<T> {

    protected InboundWire<T> inboundWire;
    private T target;

    public ServiceContextExtension(String name, InboundWire<T> wire, CompositeContext parent) throws CoreRuntimeException {
        super(name, parent);
        this.inboundWire = wire;
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

    public T getService() throws TargetException {
        if (target == null) {
            target = inboundWire.getTargetService();
        }
        return target;
    }

    public WireInvocationHandler getHandler() {
        return new ServiceInvocationHandler(inboundWire.getInvocationChains());
    }

    public Class<T> getInterface() {
        return inboundWire.getBusinessInterface();
    }

}
