package org.apache.tuscany.core.wire.system;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

/**
 * The default implementation of a <code>WireFactory</code>
 *
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
@org.osoa.sca.annotations.Service(interfaces = {WireService.class})
public class WireServiceImpl implements WireService {

    private PolicyBuilderRegistry policyRegistry;

    public WireServiceImpl() {
    }

    public WireServiceImpl(PolicyBuilderRegistry registry) {
        this.policyRegistry = registry;
    }

    @Autowire
    public void setPolicyRegistry(PolicyBuilderRegistry policyRegistry) {
        this.policyRegistry = policyRegistry;
    }

    @Init(eager = true)
    public void init() {
    }

    public OutboundWire createReferenceWire(Reference reference) throws BuilderConfigException {
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

    public InboundWire createServiceWire(Service service) {
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


}
