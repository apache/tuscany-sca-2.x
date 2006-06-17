package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.system.component.SystemReference;
import org.apache.tuscany.core.system.component.SystemReferenceImpl;
import org.apache.tuscany.core.system.component.SystemService;
import org.apache.tuscany.core.system.component.SystemServiceImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.wire.SystemInboundWire;
import org.apache.tuscany.core.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.core.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BindingBuilder;
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
