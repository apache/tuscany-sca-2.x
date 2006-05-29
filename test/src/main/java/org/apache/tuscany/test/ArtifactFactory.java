package org.apache.tuscany.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.ReferenceInvocationChainImpl;
import org.apache.tuscany.core.wire.ServiceInvocationChainImpl;
import org.apache.tuscany.core.wire.jdk.JDKReferenceWire;
import org.apache.tuscany.core.wire.jdk.JDKServiceWire;
import org.apache.tuscany.spi.wire.ReferenceInvocationChain;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;
import org.apache.tuscany.spi.wire.ServiceWire;

/**
 * A factory for creating runtime artifacts to facilitate testing without directly instantiating core
 * implementation classes
 *
 * @version $$Rev$$ $$Date$$
 */
public class ArtifactFactory {

    private ArtifactFactory() {
    }

    /**
     * Creates a target wire. After a wire is returned, client code must call
     * {@link #completeTargetWire(org.apache.tuscany.spi.wire.ServiceWire<T>)}. These two methods have been separated
     * to allow wires to be decorated with interceptors or handlers prior to their completion
     *
     * @param serviceName the service name associated with the wire
     * @param interfaze the interface associated with the wire
     */
    public static <T> ServiceWire<T> createTargetWire(String serviceName, Class<T> interfaze) {
        ServiceWire<T> wire = new JDKServiceWire<T>();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(createTargetInvocationChains(interfaze));
        return wire;
    }

    /**
     * Finalizes the target wire
     */
    public static <T> void completeTargetWire(ServiceWire<T> wire) {
        for (ServiceInvocationChain chain : wire.getInvocationChains().values()) {
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
        }
    }

    /**
     * Creates a source wire
     * @param refName the reference name the wire is associated with on the client
     * @param interfaze the interface associated with the wire
     */
    public static <T> ReferenceWire<T> createReferenceWire(String refName, Class<T> interfaze) {
        ReferenceWire<T> wire = new JDKReferenceWire<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createSourceInvocationChains(interfaze));
        wire.setBusinessInterface(interfaze);
        return wire;
    }

    private static Map<Method, ReferenceInvocationChain> createSourceInvocationChains(Class<?> interfaze) {
        Map<Method, ReferenceInvocationChain> invocations = new HashMap<Method, ReferenceInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            ReferenceInvocationChain chain = new ReferenceInvocationChainImpl(method);
            invocations.put(method, chain);
        }
        return invocations;
    }

    private static Map<Method, ServiceInvocationChain> createTargetInvocationChains(Class<?> interfaze) {
        Map<Method, ServiceInvocationChain> invocations = new MethodHashMap<ServiceInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            ServiceInvocationChain chain = new ServiceInvocationChainImpl(method);
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
            invocations.put(method, chain);
        }
        return invocations;
    }


}
