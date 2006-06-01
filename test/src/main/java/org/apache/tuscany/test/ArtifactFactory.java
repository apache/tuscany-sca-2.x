package org.apache.tuscany.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * A factory for creating runtime artifacts to facilitate testing without directly instantiating core
 * implementation classes
 *
 * @version $$Rev$$ $$Date$$
 */
public class ArtifactFactory {

    private ArtifactFactory() {
    }

    public static Connector createConnector(){
        return new ConnectorImpl();
    }

    public static WireService createWireService(){
        return new JDKWireService();
    }
    
    /**
     * Creates an inbound wire. After a wire is returned, client code must call {@link
     * #terminateWire(org.apache.tuscany.spi.wire.InboundWire<T>)}. These two methods have been separated
     * to allow wires to be decorated with interceptors or handlers prior to their completion
     *
     * @param serviceName the service name associated with the wire
     * @param interfaze   the interface associated with the wire
     */
    public static <T> InboundWire<T> createInboundWire(String serviceName, Class<T> interfaze) {
        InboundWire<T> wire = new InboundWireImpl<T>();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(serviceName);
        wire.addInvocationChains(createInboundChains(interfaze));
        return wire;
    }

    /**
     * Creates an outbound wire. After a wire is returned, client code must call {@link
     * #terminateWire(org.apache.tuscany.spi.wire.OutboundWire<T>)}. These two methods have been separated
     * to allow wires to be decorated with interceptors or handlers prior to their completion
     *
     * @param refName   the reference name the wire is associated with on the client
     * @param interfaze the interface associated with the wire
     */
    public static <T> OutboundWire<T> createOutboundWire(String refName, Class<T> interfaze) {
        OutboundWire<T> wire = new OutboundWireImpl<T>();
        wire.setReferenceName(refName);
        wire.addInvocationChains(createOutboundChains(interfaze));
        wire.setBusinessInterface(interfaze);
        return wire;
    }


    /**
     * Finalizes the target wire
     */
    public static <T> void terminateWire(InboundWire<T> wire) {
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
        }
    }

    public static <T> void terminateWire(OutboundWire<T> wire) {
        for (OutboundInvocationChain chain : wire.getInvocationChains().values()) {
            // add tail interceptor
            chain.addInterceptor(new InvokerInterceptor());
        }
    }

    private static Map<Method, OutboundInvocationChain> createOutboundChains(Class<?> interfaze) {
        Map<Method, OutboundInvocationChain> invocations = new HashMap<Method, OutboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(method);
            invocations.put(method, chain);
        }
        return invocations;
    }

    private static Map<Method, InboundInvocationChain> createInboundChains(Class<?> interfaze) {
        Map<Method, InboundInvocationChain> invocations = new MethodHashMap<InboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(method);
            // add tail interceptor
            //chain.addInterceptor(new InvokerInterceptor());
            invocations.put(method, chain);
        }
        return invocations;
    }


}
