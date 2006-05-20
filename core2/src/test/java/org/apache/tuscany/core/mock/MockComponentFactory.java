package org.apache.tuscany.core.mock;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.model.PojoComponentType;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.model.BoundReference;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.JavaServiceContract;
import org.apache.tuscany.model.Reference;
import org.apache.tuscany.model.ReferenceTarget;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.model.ServiceContract;
import org.apache.tuscany.model.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockComponentFactory {

    /**
     * Creates a component named "source" with a reference to target/Target
     */
    public static Component<SystemImplementation> createSourceWithTargetReference() {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType componentType = new PojoComponentType();
        componentType.setLifecycleScope(Scope.MODULE);
        Reference reference = new Reference();
        reference.setName("target");
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaze(Target.class);
        reference.setServiceContract(contract);
        componentType.add(reference);
        try {
            componentType.addReferenceMember("target", SourceImpl.class.getMethod("setTarget", Target.class));
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
        impl.setComponentType(componentType);
        impl.setImplementationClass(SourceImpl.class);
        Component<SystemImplementation> sourceComponent = new Component<SystemImplementation>(impl);
        sourceComponent.setName("source");

        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReference(reference);
        referenceTarget.setReferenceName("target");
        try {
            referenceTarget.addTarget(new URI("target/Target"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        sourceComponent.add(referenceTarget);
        return sourceComponent;
    }

    /**
     * Creates a component named "target" with a service named "Target"
     */
    public static Component<SystemImplementation> createTarget() {
        SystemImplementation impl = new SystemImplementation();
        PojoComponentType componentType = new PojoComponentType();
        componentType.setLifecycleScope(Scope.MODULE);
        Service targetService = new Service();
        targetService.setName("Target");
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaze(Target.class);
        targetService.setServiceContract(contract);
        componentType.add(targetService);
        impl.setComponentType(componentType);
        impl.setImplementationClass(TargetImpl.class);
        Component<SystemImplementation> targetComponent = new Component<SystemImplementation>(impl);
        targetComponent.setName("target");
        return targetComponent;
    }


    public static BoundReference<SystemBinding> createTargetReference() {
        SystemBinding binding = new SystemBinding();
        BoundReference<SystemBinding> reference = new BoundReference<SystemBinding>();
        reference.setBinding(binding);
        reference.setName("target");
        ServiceContract contract = new JavaServiceContract();
        contract.setInterfaze(Target.class);
        reference.setServiceContract(contract);
        return reference;
    }
}
