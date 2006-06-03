package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.system.component.SystemReferenceImpl;
import org.apache.tuscany.core.system.component.SystemService;
import org.apache.tuscany.core.system.component.SystemServiceImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.core.system.wire.SystemInboundWire;
import org.apache.tuscany.core.system.wire.SystemOutboundWire;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.context.Component;
import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.spi.context.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemBindingBuilder implements BindingBuilder<SystemBinding> {

    public SCAObject build(CompositeComponent parent, BoundServiceDefinition<SystemBinding> boundServiceDefinition, DeploymentContext deploymentContext) {
        Class<?> interfaze = boundServiceDefinition.getServiceContract().getInterfaceClass();
        QualifiedName targetName = new QualifiedName(boundServiceDefinition.getTarget().getPath());
        Component target = (Component) parent.getChild(targetName.getPartName());
        SystemInboundWire<?> inboundWire = new SystemInboundWireImpl(boundServiceDefinition.getName(), interfaze, target);
        SystemOutboundWire<?> outboundWire = new SystemOutboundWireImpl(boundServiceDefinition.getName(), targetName, interfaze);
        SystemService context = new SystemServiceImpl(boundServiceDefinition.getName(), parent);
        context.setInboundWire(inboundWire);
        context.setOutboundWire(outboundWire);
        return context;
    }

    public SCAObject build(CompositeComponent parent, BoundReferenceDefinition<SystemBinding> boundReferenceDefinition, DeploymentContext deploymentContext) {
        assert(parent.getParent() instanceof AutowireComponent):"Grandparent not an instance of " + AutowireComponent.class.getName();
        AutowireComponent autowireContext = (AutowireComponent) parent.getParent();
        Class<?> interfaze = boundReferenceDefinition.getServiceContract().getInterfaceClass();
        SystemReferenceImpl ctx = new SystemReferenceImpl(boundReferenceDefinition.getName(), interfaze, parent);
        SystemInboundWire<?> inboundWire = new SystemInboundWireImpl(boundReferenceDefinition.getName(), interfaze);
        OutboundWire<?> outboundWire = new SystemOutboundAutowire(boundReferenceDefinition.getName(), interfaze, autowireContext);
        ctx.setInboundWire(inboundWire);
        ctx.setOutboundWire(outboundWire);
        return ctx;
    }
}
