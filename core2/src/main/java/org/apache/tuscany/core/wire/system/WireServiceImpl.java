package org.apache.tuscany.core.wire.system;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.wire.SourceInvocationChainImpl;
import org.apache.tuscany.core.wire.TargetInvocationChainImpl;
import org.apache.tuscany.spi.model.Reference;
import org.apache.tuscany.spi.model.Service;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireFactoryService;
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

    private WireFactoryService wireFactoryService;
    private PolicyBuilderRegistry policyRegistry;


    public WireServiceImpl() {

    }

    public WireServiceImpl(WireFactoryService wireFactoryService) {
        this.wireFactoryService = wireFactoryService;
    }

    public WireServiceImpl(WireFactoryService wireFactoryService, PolicyBuilderRegistry registry) {
        this.wireFactoryService = wireFactoryService;
        this.policyRegistry = registry;
    }

    @Autowire
    public void setWireFactoryService(WireFactoryService service) {
        this.wireFactoryService = service;
    }


    @Autowire
    public void setPolicyRegistry(PolicyBuilderRegistry policyRegistry) {
        this.policyRegistry = policyRegistry;
    }

    @Init(eager = true)
    public void init() {
    }

    public SourceWire createSourceWire(Reference reference) throws BuilderConfigException {
        String name = reference.getName();
        Class interfaze = reference.getServiceContract().getInterfaceClass();
        SourceWire<?> wire = wireFactoryService.createSourceWire();
        wire.setBusinessInterface(interfaze);
        wire.setReferenceName(name);

        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
        for (Method method : javaMethods) {
            SourceInvocationChain chain = new SourceInvocationChainImpl(method);
            wire.addInvocationChain(method, chain);
        }
        if (policyRegistry != null) {
            // invoke policy builders
            policyRegistry.buildSource(reference, wire);
        }
        return wire;
    }

    public TargetWire createTargetWire(Service service) {
        String name = service.getName();
        Class interfaze = service.getServiceContract().getInterfaceClass();
        TargetWire<?> wire = wireFactoryService.createTargetWire();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(name);

        Set<Method> javaMethods = JavaIntrospectionHelper.getAllUniqueMethods(interfaze);
        for (Method method : javaMethods) {
            TargetInvocationChain chain = new TargetInvocationChainImpl(method);
            wire.addInvocationChain(method, chain);
        }
        if (policyRegistry != null) {
            // invoke policy builders
            policyRegistry.buildTarget(service, wire);
        }
        return wire;
    }


}
