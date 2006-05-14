package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireFactoryInitException;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

/**
 * Creates proxies that are returned to non-SCA clients using JDK dynamic proxy facilities and front a wire.
 * The proxies implement the business interface associated with the target service of the wire and are
 * typically returned by a locate operation.
 *
 * @version $Rev: 394431 $ $Date: 2006-04-15 21:27:44 -0700 (Sat, 15 Apr 2006) $
 */
public class JDKTargetWire<T> implements TargetWire<T> {

    private static final int UNINITIALIZED = 0;

    private static final int INITIALIZED = 1;

    private int state = UNINITIALIZED;

    private Class[] businessInterfaceArray;

    private Map<Method, TargetInvocationChain> methodToInvocationConfig;

    //private WireTargetConfiguration configuration;

    public void initialize() throws WireFactoryInitException {
        if (state != UNINITIALIZED) {
            throw new IllegalStateException("Wire factory in wrong state [" + state + "]");
        }
        if (invocationChains != null) {
            methodToInvocationConfig = new MethodHashMap<TargetInvocationChain>(invocationChains.size());
            for (Map.Entry<Method, TargetInvocationChain> entry : invocationChains.entrySet()) {
                Method method = entry.getKey();
                methodToInvocationConfig.put(method, entry.getValue());
            }
        }
        state = INITIALIZED;
    }

    @SuppressWarnings("unchecked")
    public T createProxy() {
        if (state != INITIALIZED) {
            throw new IllegalStateException("Proxy factory not INITIALIZED [" + state + "]");
        }
        WireInvocationHandler handler = new JDKInvocationHandler();
        handler.setConfiguration(methodToInvocationConfig);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), businessInterfaceArray, handler);
    }

    public void setBusinessInterface(Class interfaze) {
        businessInterfaceArray = new Class[]{interfaze};
    }

    @SuppressWarnings("unchecked")
    public Class<T> getBusinessInterface() {
        return businessInterfaceArray[0];
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public Class[] getImplementedInterfaces() {
        return businessInterfaceArray;
    }

    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    private Map<Method, TargetInvocationChain> invocationChains;

    public Map<Method, TargetInvocationChain> getInvocationChains() {
        return invocationChains;
    }

    public void setInvocationChains(Map<Method, TargetInvocationChain> chains) {
        this.invocationChains = chains;
    }

}
