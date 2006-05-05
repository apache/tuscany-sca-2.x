package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.spi.wire.TargetInvocationConfiguration;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.spi.wire.WireFactoryInitException;
import org.apache.tuscany.spi.wire.WireTargetConfiguration;

/**
 * Creates proxies that are returned to non-SCA clients using JDK dynamic proxy facilities and front a wire.
 * The proxies implement the business interface associated with the target service of the wire and are
 * typically returned by a locate operation.
 *
 * @version $Rev: 394431 $ $Date: 2006-04-15 21:27:44 -0700 (Sat, 15 Apr 2006) $
 */
public class JDKTargetWireFactory<T> implements TargetWireFactory<T> {

    private static final int UNINITIALIZED = 0;

    private static final int INITIALIZED = 1;

    private int state = UNINITIALIZED;

    private Class[] businessInterfaceArray;

    private Map<Method, TargetInvocationConfiguration> methodToInvocationConfig;

    private WireTargetConfiguration configuration;

    public void initialize() throws WireFactoryInitException {
        if (state != UNINITIALIZED) {
            throw new IllegalStateException("Proxy factory in wrong state [" + state + "]");
        }
        Map<Method, TargetInvocationConfiguration> invocationConfigs = configuration.getInvocationConfigurations();
        methodToInvocationConfig = new MethodHashMap<TargetInvocationConfiguration>(invocationConfigs.size());
        for (Map.Entry<Method, TargetInvocationConfiguration> entry : invocationConfigs.entrySet()) {
            Method method = entry.getKey();
            methodToInvocationConfig.put(method, entry.getValue());
        }
        state = INITIALIZED;
    }

    @SuppressWarnings("unchecked")
    public T createProxy() {
        if (state != INITIALIZED) {
            throw new IllegalStateException("Proxy factory not INITIALIZED [" + state + "]");
        }
        InvocationHandler handler = new JDKInvocationHandler(methodToInvocationConfig);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), businessInterfaceArray, handler);
    }

    public WireTargetConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WireTargetConfiguration config) {
        configuration = config;
    }

    public void setBusinessInterface(Class interfaze) {
        businessInterfaceArray = new Class[]{interfaze};
    }

    public T getBusinessInterface() {
        return (T) businessInterfaceArray[0];
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public Class[] getImplementatedInterfaces() {
        return businessInterfaceArray;
    }

}
