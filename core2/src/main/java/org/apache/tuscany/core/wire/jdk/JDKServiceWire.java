package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;
import org.apache.tuscany.spi.wire.ServiceInvocationHandler;
import org.apache.tuscany.spi.wire.ServiceWire;

/**
 * Creates proxies that are returned to non-SCA clients using JDK dynamic proxy facilities and front a wire.
 * The proxies implement the business interface associated with the target service of the wire and are
 * typically returned by a locate operation.
 *
 * @version $Rev: 394431 $ $Date: 2006-04-15 21:27:44 -0700 (Sat, 15 Apr 2006) $
 */
public class JDKServiceWire<T> implements ServiceWire<T> {

    private Class[] businessInterfaces;
    private Map<Method, ServiceInvocationChain> invocationChains = new MethodHashMap<ServiceInvocationChain>();
    private String serviceName;

    @SuppressWarnings("unchecked")
    public T getTargetService() throws TargetException {
        ServiceInvocationHandler handler = new ServiceInvocationHandler(invocationChains);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), businessInterfaces, handler);
    }

    public void setBusinessInterface(Class interfaze) {
        businessInterfaces = new Class[]{interfaze};
    }

    @SuppressWarnings("unchecked")
    public Class<T> getBusinessInterface() {
        return businessInterfaces[0];
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public Class[] getImplementedInterfaces() {
        return businessInterfaces;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<Method, ServiceInvocationChain> getInvocationChains() {
        return invocationChains;
    }

    public void addInvocationChains(Map<Method, ServiceInvocationChain> chains) {
        invocationChains.putAll(chains);
    }

    public void addInvocationChain(Method method, ServiceInvocationChain chain) {
        invocationChains.put(method, chain);
    }

    public void setTargetWire(ServiceWire<T> wire) {
        throw new UnsupportedOperationException("not yet implemented"); // FIXME

    }

    public boolean isOptimizable() {
        for (ServiceInvocationChain chain : invocationChains.values()) {
            if (chain.getTargetInvoker() != null && !chain.getTargetInvoker().isOptimizable()) {
                return false;
            }
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
            if (chain.getRequestHandlers() != null && !chain.getRequestHandlers().isEmpty()) {
                if (chain.getRequestHandlers() != null) {
                    for (MessageHandler handler : chain.getRequestHandlers()) {
                        if (!handler.isOptimizable()) {
                            return false;
                        }
                    }
                }
            }
            if (chain.getResponseHandlers() != null && !chain.getResponseHandlers().isEmpty()) {
                if (chain.getResponseHandlers() != null) {
                    for (MessageHandler handler : chain.getResponseHandlers()) {
                        if (!handler.isOptimizable()) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

}
