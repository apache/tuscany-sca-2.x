package org.apache.tuscany.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.SourceInvocationChainImpl;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.core.wire.jdk.JDKSourceWire;
import org.apache.tuscany.core.wire.jdk.JDKTargetWire;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;

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
     * {@link #completeTargetWire(org.apache.tuscany.spi.wire.TargetWire<T>)}. These two methods have been separated
     * to allow wires to be decorated with interceptors or handlers prior to their completion
     *
     * @param serviceName the service name associated with the wire
     * @param interfaze the interface associated with the wire
     */
    public static <T> TargetWire<T> createTargetWire(String serviceName, Class<T> interfaze) {
        TargetWire<T> wire = new JDKTargetWire<T>();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(createTargetInvocationChains(interfaze));
        return wire;
    }

    /**
     * Finalizes the target wire
     */
    public static <T> void completeTargetWire(TargetWire<T> wire) {
        for (TargetInvocationChain chain : wire.getInvocationChains().values()) {
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
        }
    }

    /**
     * Creates a source wire
     * @param refName the reference name the wire is associated with on the client
     * @param interfaze the interface associated with the wire
     */
    public static <T> SourceWire<T> createSourceWire(String refName, Class<T> interfaze) {
        SourceWire<T> wire = new JDKSourceWire<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createSourceInvocationChains(interfaze));
        wire.setBusinessInterface(interfaze);
        return wire;
    }

    private static Map<Method, SourceInvocationChain> createSourceInvocationChains(Class<?> interfaze) {
        Map<Method, SourceInvocationChain> invocations = new HashMap<Method, SourceInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            SourceInvocationChain chain = new SourceInvocationChainImpl(method);
            invocations.put(method, chain);
        }
        return invocations;
    }

    private static Map<Method, TargetInvocationChain> createTargetInvocationChains(Class<?> interfaze) {
        Map<Method, TargetInvocationChain> invocations = new MethodHashMap<TargetInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            TargetInvocationChain chain = new TargetInvocationChainImpl(method);
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
            invocations.put(method, chain);
        }
        return invocations;
    }


}
