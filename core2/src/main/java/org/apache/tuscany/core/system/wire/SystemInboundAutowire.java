package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.RuntimeWire;
import org.apache.tuscany.spi.QualifiedName;

/**
 * The target side of an wire configured to autowire
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemInboundAutowire<T> implements InboundWire<T> {

    private Class<T> businessInterface;
    private AutowireContext<?> context;

    public SystemInboundAutowire(Class<T> businessInterface, AutowireContext context) {
        this.businessInterface = businessInterface;
        this.context = context;
    }

    public Class<T> getBusinessInterface() {
        return businessInterface;
    }

    public void setBusinessInterface(Class<T> businessInterface) {
        this.businessInterface = businessInterface;
    }

    public String getServiceName() {
        return null;
    }

    public void setServiceName(String serviceName) {
    }

    public T getTargetService() throws TargetException {
        return context.resolveInstance(businessInterface);
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

    public QualifiedName getTargetName() {
        throw new UnsupportedOperationException();
    }

    public void setTargetName(QualifiedName name) {
        throw new UnsupportedOperationException();
    }

    public boolean isOptimizable() {
        return true;  // system wires are always optimizable
    }

    public void setTargetWire(RuntimeWire<T> wire) {
        throw new UnsupportedOperationException(); // FIXME not implemented
    }

    public void setTargetWire(OutboundWire<T> wire) {
        throw new UnsupportedOperationException(); // FIXME not implemented
    }
}