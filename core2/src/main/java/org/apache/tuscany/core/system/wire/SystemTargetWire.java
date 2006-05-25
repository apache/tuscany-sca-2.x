package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * The source side of a wire configured to use the {@link org.apache.tuscany.core.system.model.SystemBinding}
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemTargetWire<T> implements TargetWire<T> {
    private String serviceName;
    private Class<T> businessInterface;
    private ComponentContext<?> componentContext;

    public SystemTargetWire(String serviceName, Class<T> businessInterface, ComponentContext<?> target) {
        this.serviceName = serviceName;
        this.businessInterface = businessInterface;
        this.componentContext = target;
    }

    public SystemTargetWire(Class<T> businessInterface, ComponentContext<?> target) {
        this.businessInterface = businessInterface;
        this.componentContext = target;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @SuppressWarnings("unchecked")
    public T getTargetService() throws TargetException {
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

    public Map<Method, TargetInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, TargetInvocationChain chain) {
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

}
