package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AbstractSCAObject;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.context.Service;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The default implementation of an SCA service 
 *
 * @version $Rev: 399161 $ $Date: 2006-05-02 23:09:37 -0700 (Tue, 02 May 2006) $
 */
public class ServiceExtension<T> extends AbstractSCAObject<T> implements Service<T> {

    protected InboundWire<T> inboundWire;
    protected OutboundWire<T> outboundWire;
    protected WireService wireService;

    public ServiceExtension(String name, CompositeComponent parent, WireService wireService) throws CoreRuntimeException {
        super(name, parent);
        this.wireService = wireService;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire<T> wire) {
        //target = null;
        inboundWire = wire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> outboundWire) {
        this.outboundWire = outboundWire;
    }

    public T getService() throws TargetException {
        return wireService.createProxy(inboundWire);
//        if (target == null) {
//            target = inboundWire.getTargetService();
//        }
//        return target;
    }

    public WireInvocationHandler getHandler() {
        return wireService.createHandler(inboundWire);
    }

    public Class<T> getInterface() {
        return inboundWire.getBusinessInterface();
    }

}
