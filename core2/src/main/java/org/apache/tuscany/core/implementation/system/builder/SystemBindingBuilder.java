package org.apache.tuscany.core.implementation.system.builder;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.implementation.system.component.SystemReference;
import org.apache.tuscany.core.implementation.system.component.SystemReferenceImpl;
import org.apache.tuscany.core.implementation.system.component.SystemService;
import org.apache.tuscany.core.implementation.system.component.SystemServiceImpl;
import org.apache.tuscany.core.implementation.system.model.SystemBinding;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Creates {@link SystemService}s and {@link SystemReference}s by evaluating an assembly definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBindingBuilder implements BindingBuilder<SystemBinding> {

    public SystemService build(CompositeComponent parent,
                               BoundServiceDefinition<SystemBinding> boundServiceDefinition,
                               DeploymentContext deploymentContext) {
        Class<?> interfaze = boundServiceDefinition.getServiceContract().getInterfaceClass();
        QualifiedName targetName = new QualifiedName(boundServiceDefinition.getTarget().getPath());
        Component target = (Component) parent.getChild(targetName.getPartName());
        if (target == null) {
            throw new BuilderConfigException("Target not found: [" + targetName + ']');
        }
        SystemInboundWire<?> inboundWire =
            new SystemInboundWireImpl(boundServiceDefinition.getName(), interfaze, target);
        SystemOutboundWire<?> outboundWire =
            new SystemOutboundWireImpl(boundServiceDefinition.getName(), targetName, interfaze);
        SystemService service = new SystemServiceImpl(boundServiceDefinition.getName(), parent);
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
        return service;
    }

    public SystemReference build(CompositeComponent parent,
                                 BoundReferenceDefinition<SystemBinding> boundReferenceDefinition,
                                 DeploymentContext deploymentContext) {
        assert parent.getParent() instanceof AutowireComponent
            : "Grandparent not an instance of " + AutowireComponent.class.getName();
        AutowireComponent autowireComponent = (AutowireComponent) parent.getParent();
        Class<?> interfaze = boundReferenceDefinition.getServiceContract().getInterfaceClass();
        SystemReferenceImpl reference = new SystemReferenceImpl(boundReferenceDefinition.getName(), interfaze, parent);
        SystemInboundWire<?> inboundWire = new SystemInboundWireImpl(boundReferenceDefinition.getName(), interfaze);
        OutboundWire<?> outboundWire =
            new SystemOutboundAutowire(boundReferenceDefinition.getName(), interfaze, autowireComponent);
        reference.setInboundWire(inboundWire);
        reference.setOutboundWire(outboundWire);
        return reference;
    }
}
