package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Proxy;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.WorkContext;
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

    private WorkContext context;
    //private PolicyBuilderRegistry policyRegistry;

    public JDKWireService() {
    }

    @Constructor({"workContext", "policyregisty"})
    public JDKWireService(@Autowire WorkContext context, @Autowire PolicyBuilderRegistry policyRegistry) {
        this.context = context;
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
            ClassLoader cl = interfaze.getClassLoader();
            return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
        } else if (wire instanceof OutboundWire) {
            OutboundWire<T> outbound = (OutboundWire<T>) wire;
            JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(outbound);
            Class<T> interfaze = outbound.getBusinessInterface();
            ClassLoader cl = interfaze.getClassLoader();
            return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public <T> T createCallbackProxy(Class<T> interfaze) throws ProxyCreationException {
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(context);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public WireInvocationHandler createHandler(RuntimeWire<?> wire) {
        assert wire != null : "WireDefinition was null";
        if (wire instanceof InboundWire) {
            InboundWire<?> inbound = (InboundWire) wire;
            return new JDKInboundInvocationHandler(inbound.getInvocationChains());
        } else if (wire instanceof OutboundWire) {
            OutboundWire<?> outbound = (OutboundWire) wire;
            return new JDKOutboundInvocationHandler(outbound);
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public WireInvocationHandler createCallbackHandler() {
        return new JDKCallbackInvocationHandler(context);
    }


}
