package org.apache.tuscany.core.integration.system.builder;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.factories.MockComponentFactory;
import org.apache.tuscany.core.system.builder.SystemBindingBuilder;
import org.apache.tuscany.core.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.system.context.SystemCompositeComponent;
import org.apache.tuscany.core.system.context.SystemCompositeComponentImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.spi.context.AtomicComponent;
import org.apache.tuscany.spi.context.Reference;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.Service;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;

/**
 * Validates that system builders and the default connector create properly wired contexts
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBuildersTestCase extends TestCase {
    private DeploymentContext deploymentContext;

    /**
     * Validates building a wire from an atomic context to an atomic context
     */
    public void testAtomicWireBuild() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();

        SystemCompositeComponent parent = new SystemCompositeComponentImpl(null, null, null);

        ComponentDefinition<SystemImplementation> targetComponentDefinition = MockComponentFactory.createTarget();
        ComponentDefinition<SystemImplementation> sourceComponentDefinition = MockComponentFactory.createSourceWithTargetReference();

        AtomicComponent<?> sourceComponent = (AtomicComponent) builder.build(parent, sourceComponentDefinition, deploymentContext);
        AtomicComponent<?> targetComponent = (AtomicComponent) builder.build(parent, targetComponentDefinition, deploymentContext);

        parent.register(sourceComponent);
        parent.register(targetComponent);

        connector.connect(sourceComponent);
        connector.connect(targetComponent);
        parent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Source source = (Source) parent.getChild("source").getService();
        assertNotNull(source);
        Target target = (Target) parent.getChild("target").getService();
        assertNotNull(target);
        assertSame(target, source.getTarget());
        scope.onEvent(new ModuleStop(this, parent));
        parent.stop();
        scope.stop();
    }

    /**
     * Validates building a wire from an atomic context to a reference context
     */
    public void testAtomicToReferenceWireBuild() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeComponent grandParent = new SystemCompositeComponentImpl("grandparent", null, null);
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", grandParent, grandParent);

        // create a context in the grandparent that the reference will be autowired to
        ComponentDefinition<SystemImplementation> targetComponentDefinition = MockComponentFactory.createTarget();
        AtomicComponent targetComponentComponent = (AtomicComponent) builder.build(parent, targetComponentDefinition, deploymentContext);
        grandParent.register(targetComponentComponent);

        BoundReferenceDefinition<SystemBinding> targetReferenceDefinition = MockComponentFactory.createBoundReference();
        ComponentDefinition<SystemImplementation> sourceComponentDefinition = MockComponentFactory.createSourceWithTargetReference();

        AtomicComponent<?> sourceComponent = (AtomicComponent) builder.build(parent, sourceComponentDefinition, deploymentContext);
        Reference reference = (Reference) bindingBuilder.build(parent, targetReferenceDefinition, deploymentContext);

        parent.register(sourceComponent);
        parent.register(reference);
        connector.connect(reference.getInboundWire(), reference.getOutboundWire(), true);
        connector.connect(sourceComponent);
        grandParent.register(parent);
        grandParent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Source source = (Source) parent.getChild("source").getService();
        assertNotNull(source);
        Target target = (Target) parent.getChild("target").getService();
        assertNotNull(target);
        assertSame(target, source.getTarget());
        scope.onEvent(new ModuleStop(this, parent));
        grandParent.stop();
        scope.stop();
    }


    /**
     * Validates building a wire from a service context to an atomic context
     */
    @SuppressWarnings("unchecked")
    public void testServiceToAtomicWireBuild() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        ConnectorImpl connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeComponent parent = new SystemCompositeComponentImpl(null, null, null);

        BoundServiceDefinition<SystemBinding> serviceDefinition = MockComponentFactory.createBoundService();
        ComponentDefinition<SystemImplementation> componentDefinition = MockComponentFactory.createTarget();

        AtomicComponent sourceComponent = (AtomicComponent) builder.build(parent, componentDefinition, deploymentContext);
        Service service = (Service) bindingBuilder.build(parent, serviceDefinition, deploymentContext);

        connector.connect(service.getInboundWire(), service.getOutboundWire(), true);
        parent.register(sourceComponent);
        parent.register(service);

        connector.connect(sourceComponent);
        String serviceName = service.getOutboundWire().getTargetName().getPortName();
        connector.connect(service.getOutboundWire(), sourceComponent.getInboundWire(serviceName), parent, true);
        parent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Target target = (Target) parent.getChild("serviceDefinition").getService();
        assertNotNull(target);
        Target target2 = (Target) parent.getChild("target").getService();
        assertNotNull(target);
        assertSame(target, target2);
        scope.onEvent(new ModuleStop(this, parent));
        parent.stop();
        scope.stop();

    }

    /**
     * Validates building a wire from a service context to a reference context
     */
    @SuppressWarnings("unchecked")
    public void testServiceToReferenceWireBuild() throws Exception {
//        WorkContext work = new WorkContextImpl();
//        ScopeContext scope = new ModuleScopeContext(work);
//        scope.start();
//
//        Connector connector = new ConnectorImpl();
//        SystemComponentBuilder builder = new SystemComponentBuilder();
//        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();
//
//        SystemCompositeComponent grandParent = new SystemCompositeComponentImpl("grandparent", null, null);
//        SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", grandParent, grandParent);
//
//        // create a context in the grandparent that the reference will be autowired to
//        ComponentDefinition<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
//        AtomicComponent targetComponentContext = (AtomicComponent) builder.build(parent, targetComponent, deploymentContext);
//        grandParent.registerContext(targetComponentContext);
//
//        BoundReferenceDefinition<SystemBinding> reference = MockComponentFactory.createBoundReference();
//        BoundServiceDefinition<SystemBinding> service = MockComponentFactory.createBoundService();
//
//        Reference referenceContext = (Reference) bindingBuilder.build(parent, reference, deploymentContext);
//        Service serviceContext = (Service) bindingBuilder.build(parent, service, deploymentContext);
//
//        parent.registerContext(referenceContext);
//        parent.registerContext(serviceContext);
//
//        connector.connect(serviceContext.getOutboundWire(), referenceContext.getInboundWire(),true);
//        grandParent.registerContext(parent);
//        grandParent.start();
//        scope.onEvent(new ModuleStart(this, parent));
//        Target target = (Target) parent.getContext("service").getService();
//        assertNotNull(target);
//        Target target2 = (Target) parent.getContext("target").getService();
//        assertNotNull(target);
//        assertSame(target, target2);
//        scope.onEvent(new ModuleStop(this, parent));
//        parent.stop();
//        scope.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
        ModuleScopeContext moduleScope = new ModuleScopeContext();
        moduleScope.start();
        deploymentContext = new DeploymentContext(null, null, moduleScope);

    }
}
