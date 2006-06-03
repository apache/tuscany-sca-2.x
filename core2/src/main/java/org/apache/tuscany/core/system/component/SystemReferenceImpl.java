package org.apache.tuscany.core.system.component;

import java.lang.reflect.Method;

import org.apache.tuscany.core.system.wire.SystemInboundWire;
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.system.component.SystemReference;
import org.apache.tuscany.spi.context.AbstractSCAObject;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Default implementation of a reference context configured with the {@link
 * org.apache.tuscany.core.system.model.SystemBinding}
 *
 * @version $Rev: 399991 $ $Date: 2006-05-04 23:44:07 -0700 (Thu, 04 May 2006) $
 */
public class SystemReferenceImpl<T> extends AbstractSCAObject<T> implements SystemReference<T> {

    protected SystemInboundWire<T> inboundWire;
    protected SystemOutboundWire<T> outboundWire;
    protected Class<T> referenceInterface;


    public SystemReferenceImpl(String name, Class<T> referenceInterface, CompositeComponent parent) {
        super(name, parent);
        assert (referenceInterface != null) : "ReferenceDefinition interface was null";
        this.referenceInterface = referenceInterface;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void setInboundWire(InboundWire<T> wire) {
        assert(wire instanceof SystemInboundWire): "wire must be a " + SystemInboundWire.class.getName();
        this.inboundWire = (SystemInboundWire<T>) wire;
    }

    public InboundWire<T> getInboundWire() {
        return inboundWire;
    }

    public OutboundWire<T> getOutboundWire() {
        return outboundWire;
    }

    public void setOutboundWire(OutboundWire<T> wire) {
        assert(wire instanceof SystemOutboundWire): "wire must be a " + SystemOutboundWire.class.getName();
        this.outboundWire = (SystemOutboundWire<T>) wire;
    }

    public Class<T> getInterface() {
        return referenceInterface;
    }

    public void setInterface(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public void prepare() {
        for (InboundInvocationChain chain : inboundWire.getInvocationChains().values()) {
            chain.setTargetInvoker(createTargetInvoker(outboundWire.getTargetName().getQualifiedName(), chain.getMethod()));
            chain.build();
        }
    }

    public T getService() throws TargetException {
        return referenceInterface.cast(inboundWire.getTargetService());
    }

    public WireInvocationHandler getHandler() throws TargetException {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

}
