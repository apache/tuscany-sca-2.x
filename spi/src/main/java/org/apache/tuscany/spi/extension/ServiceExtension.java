package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The default implementation of an SCA service
 *
 * @version $Rev$ $Date$
 */
public class ServiceExtension<T> extends AbstractSCAObject<T> implements Service<T> {

    protected Class<T> interfaze;
    protected InboundWire<T> inboundWire;
    protected OutboundWire<T> outboundWire;
    protected WireService wireService;

    public ServiceExtension(String name, Class<T> interfaze, CompositeComponent parent, WireService wireService)
        throws CoreRuntimeException {
        super(name, parent);
        this.interfaze = interfaze;
        this.wireService = wireService;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public void setInboundWire(InboundWire<T> wire) {
        inboundWire = wire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> outboundWire) {
        this.outboundWire = outboundWire;
    }

    public T getServiceInstance() throws TargetException {
        return wireService.createProxy(inboundWire);
    }

    public WireInvocationHandler getHandler() {
        return wireService.createHandler(inboundWire);
    }

    public Class<T> getInterface() {
        return interfaze;
    }

}
