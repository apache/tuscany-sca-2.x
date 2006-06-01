package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * An inbound wire configured to use the {@link org.apache.tuscany.core.system.model.SystemBinding}. System
 * wires bind directly to their targets without proxying or interposing invocation chains.
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemInboundWireImpl<T> implements SystemInboundWire<T> {
    private String serviceName;
    private Class<T> businessInterface;
    private ComponentContext<?> componentContext;
    private SystemOutboundWire<T> wire;

    /**
     * Constructs a new inbound wire
     *
     * @param serviceName       the name of the service the inbound wire represents
     * @param businessInterface the service interface
     * @param target            the target context the wire is connected to
     */
    public SystemInboundWireImpl(String serviceName, Class<T> businessInterface, ComponentContext<?> target) {
        this.serviceName = serviceName;
        this.businessInterface = businessInterface;
        this.componentContext = target;
    }

    public SystemInboundWireImpl(String serviceName, Class<T> businessInterface) {
        this.serviceName = serviceName;
        this.businessInterface = businessInterface;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public T getTargetService() throws TargetException {
        if (wire != null) {
            return wire.getTargetService();
        }
        return (T) componentContext.getService(serviceName);
    }

    public Class<T> getBusinessInterface() {
        return businessInterface;
    }

    public void setBusinessInterface(Class<T> businessInterface) {
        this.businessInterface = businessInterface;
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }

    public Map<Method, InboundInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, InboundInvocationChain chain) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public boolean isOptimizable() {
        return true;  // system wires are always optimizable
    }

    public void setTargetWire(OutboundWire<T> wire) {
        assert(wire instanceof SystemOutboundWire): "wire must be a " + SystemOutboundWireImpl.class.getName();
        this.wire = (SystemOutboundWire<T>) wire;
    }

}
