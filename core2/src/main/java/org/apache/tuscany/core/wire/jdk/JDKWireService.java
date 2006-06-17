package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Proxy;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.RuntimeWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public class JDKWireService implements WireService {

    //private PolicyBuilderRegistry policyRegistry;

    public JDKWireService() {
    }

    public JDKWireService(PolicyBuilderRegistry policyRegistry) {
        //this.policyRegistry = policyRegistry;
    }

    @Autowire
    public void setPolicyRegistry(PolicyBuilderRegistry policyRegistry) {
        //this.policyRegistry = policyRegistry;
    }

    @Init(eager = true)
    public void init() {
    }


    public <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException {
        assert wire != null : "WireDefinition was null";
        if (wire instanceof InboundWire) {
            InboundWire<T> inbound = (InboundWire<T>) wire;
            JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(inbound.getInvocationChains());
            Class<T> interfaze = inbound.getBusinessInterface();
            return interfaze.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaze}, handler));
        } else if (wire instanceof OutboundWire) {
            OutboundWire<T> inbound = (OutboundWire<T>) wire;
            JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(inbound.getInvocationChains());
            Class<T> interfaze = inbound.getBusinessInterface();
            return interfaze.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaze}, handler));
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public WireInvocationHandler createHandler(RuntimeWire<?> wire) {
        assert wire != null : "WireDefinition was null";
        if (wire instanceof InboundWire) {
            InboundWire<?> inbound = (InboundWire) wire;
            return new JDKInboundInvocationHandler(inbound.getInvocationChains());
        } else if (wire instanceof OutboundWire) {
            OutboundWire<?> inbound = (OutboundWire) wire;
            return new JDKOutboundInvocationHandler(inbound.getInvocationChains());
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    /*
    public OutboundWire createReferenceWire(ReferenceDefinition reference) throws BuilderConfigException {
        String name = reference.getName();
        Class interfaze = reference.getServiceContract().getInterfaceClass();
        OutboundWire<?> wire = new OutboundWireImpl();
        wire.setBusinessInterface(interfaze);
        wire.setReferenceName(name);

        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
        for (Method method : javaMethods) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(method);
            wire.addInvocationChain(method, chain);
        }
        if (policyRegistry != null) {
            // invoke policy builders
            policyRegistry.buildSource(reference, wire);
        }
        return wire;
    }

    public InboundWire createServiceWire(ServiceDefinition service) {
        String name = service.getName();
        Class interfaze = service.getServiceContract().getInterfaceClass();
        InboundWire<?> wire = new InboundWireImpl();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(name);

        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
        for (Method method : javaMethods) {
            InboundInvocationChain chain = new InboundInvocationChainImpl(method);
            wire.addInvocationChain(method, chain);
        }
        if (policyRegistry != null) {
            // invoke policy builders
            policyRegistry.buildTarget(service, wire);
        }
        return wire;
    }
    */
}
