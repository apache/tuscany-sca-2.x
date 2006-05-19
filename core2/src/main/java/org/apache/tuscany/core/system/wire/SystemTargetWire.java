package org.apache.tuscany.core.system.wire;

import java.util.Map;
import java.util.Collections;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.common.ObjectFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemTargetWire implements TargetWire {
    private String serviceName;
    private Class businessInterface;
    private ObjectFactory factory;

    public SystemTargetWire(String serviceName, Class businessInterface, ObjectFactory factory) {
        this.serviceName = serviceName;
        this.businessInterface = businessInterface;
        this.factory = factory;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

     public Object createProxy() throws ProxyCreationException {
         return factory.getInstance();
    }

    public Class getBusinessInterface() {
        return businessInterface;
    }


    public void setBusinessInterface(Class businessInterface) {
        this.businessInterface = businessInterface;
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }

    public Map getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, TargetInvocationChain chain) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChain(Method method, SourceInvocationChain chains) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }


}
