package org.apache.tuscany.core.mock.factories;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.JavaServiceContract;
import org.apache.tuscany.spi.model.PojoComponentType;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemImplementation;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockComponentFactory {

    private MockComponentFactory() {
    }

    /**
     * Creates a component named "source" with a reference to target/Target
     */
    public static ComponentDefinition<SystemImplementation> createSourceWithTargetReference() {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType componentType = new PojoComponentType();
        componentType.setLifecycleScope(Scope.MODULE);
        ReferenceDefinition referenceDefinition = new ReferenceDefinition();
        referenceDefinition.setName("target");
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        referenceDefinition.setServiceContract(contract);
        componentType.add(referenceDefinition);
        try {
            componentType.addReferenceMember("target", SourceImpl.class.getMethod("setTarget", Target.class));
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
        impl.setComponentType(componentType);
        impl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<SystemImplementation> sourceComponentDefinition =
            new ComponentDefinition<SystemImplementation>(impl);
        sourceComponentDefinition.setName("source");

        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReference(referenceDefinition);
        referenceTarget.setReferenceName("target");
        try {
            referenceTarget.addTarget(new URI("target/Target"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        sourceComponentDefinition.add(referenceTarget);
        return sourceComponentDefinition;
    }

    /**
     * Creates a component named "source" with an autowire reference to {@link Target}
     */
    public static ComponentDefinition<SystemImplementation> createSourceWithTargetAutowire() {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType componentType = new PojoComponentType();
        componentType.setLifecycleScope(Scope.MODULE);
        ReferenceDefinition referenceDefinition = new ReferenceDefinition();
        referenceDefinition.setName("target");
        referenceDefinition.setAutowire(true);
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        referenceDefinition.setServiceContract(contract);
        componentType.add(referenceDefinition);
        try {
            componentType.addReferenceMember("target", SourceImpl.class.getMethod("setTarget", Target.class));
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
        impl.setComponentType(componentType);
        impl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<SystemImplementation> sourceComponentDefinition =
            new ComponentDefinition<SystemImplementation>(impl);
        sourceComponentDefinition.setName("source");

        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReference(referenceDefinition);
        referenceTarget.setReferenceName("target");
        sourceComponentDefinition.add(referenceTarget);
        return sourceComponentDefinition;
    }

    /**
     * Creates a component named "target" with a service named "Target"
     */
    public static ComponentDefinition<SystemImplementation> createTarget() {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType componentType = new PojoComponentType();
        componentType.setLifecycleScope(Scope.MODULE);
        ServiceDefinition targetServiceDefinition = new ServiceDefinition();
        targetServiceDefinition.setName("Target");
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        targetServiceDefinition.setServiceContract(contract);
        componentType.add(targetServiceDefinition);
        impl.setComponentType(componentType);
        impl.setImplementationClass(TargetImpl.class);
        ComponentDefinition<SystemImplementation> targetComponentDefinition =
            new ComponentDefinition<SystemImplementation>(impl);
        targetComponentDefinition.setName("target");
        return targetComponentDefinition;
    }


    public static BoundReferenceDefinition<SystemBinding> createBoundReference() {
        SystemBinding binding = new SystemBinding();
        BoundReferenceDefinition<SystemBinding> referenceDefinition = new BoundReferenceDefinition<SystemBinding>();
        referenceDefinition.setBinding(binding);
        referenceDefinition.setName("target");
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        referenceDefinition.setServiceContract(contract);
        return referenceDefinition;
    }

    /**
     * Creates a bound service with the name "service" that is configured to be wired to a target named "target/Target"
     */
    public static BoundServiceDefinition<SystemBinding> createBoundService() {
        SystemBinding binding = new SystemBinding();
        BoundServiceDefinition<SystemBinding> serviceDefinition = new BoundServiceDefinition<SystemBinding>();
        serviceDefinition.setBinding(binding);
        serviceDefinition.setName("serviceDefinition");
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaceClass(Target.class);
        serviceDefinition.setServiceContract(contract);
        try {
            serviceDefinition.setTarget(new URI("target/Target"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        return serviceDefinition;
    }


}
