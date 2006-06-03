package org.apache.tuscany.core.integration.system.builder;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ModuleScopeContext;
import org.apache.tuscany.core.component.event.ModuleStart;
import org.apache.tuscany.core.component.event.ModuleStop;
import org.apache.tuscany.core.mock.factories.MockComponentFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.system.builder.SystemBindingBuilder;
import org.apache.tuscany.core.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.system.context.SystemCompositeComponent;
import org.apache.tuscany.core.system.context.SystemCompositeComponentImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.context.AtomicComponent;
import org.apache.tuscany.spi.context.Reference;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * Validates that system builders create autowired contexts from a set of model objects
 *
 * @version $$Rev$$ $$Date$$
 */
public class AutowireBuilderTestcase extends TestCase {
    private DeploymentContext deploymentContext;

    /**
     * Validates wiring from a component to a reference which is autowired to a component in the grandparent
     * composite
     */
    public void testComponentToReference() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder componentBuilder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeComponent grandParent = new SystemCompositeComponentImpl("grandparent", null, null);
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", grandParent, grandParent);

        ComponentDefinition<SystemImplementation> targetComponentDefinition = MockComponentFactory.createTarget();
        AtomicComponent targetComponentComponent = (AtomicComponent) componentBuilder.build(parent, targetComponentDefinition, deploymentContext);
        grandParent.register(targetComponentComponent);

        BoundReferenceDefinition<SystemBinding> targetReferenceDefinition = MockComponentFactory.createBoundReference();
        ComponentDefinition<SystemImplementation> sourceComponentDefinition = MockComponentFactory.createSourceWithTargetReference();


        AtomicComponent<?> sourceComponent = (AtomicComponent) componentBuilder.build(parent, sourceComponentDefinition, deploymentContext);
        parent.register(sourceComponent);

        Reference reference = (Reference) bindingBuilder.build(parent, targetReferenceDefinition, deploymentContext);
        parent.register(reference);

        connector.connect(sourceComponent);

        grandParent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Source source = (Source) parent.getChild("source").getService();
        assertNotNull(source);
        Target target = (Target) parent.getChild("target").getService();
        assertNotNull(target);
        assertSame(target, source.getTarget());
        assertSame(target, grandParent.getChild("target").getService());
        scope.onEvent(new ModuleStop(this, parent));
        grandParent.stop();
        scope.stop();
    }


    /**
     * Validates autowiring from a component to another component in the same composite
     */
    public void testComponentToComponent() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        SystemComponentBuilder componentBuilder = new SystemComponentBuilder();

        SystemCompositeComponent parent = new SystemCompositeComponentImpl(null, null, null);

        ComponentDefinition<SystemImplementation> targetComponentDefinition = MockComponentFactory.createTarget();
        AtomicComponent targetComponentComponent = (AtomicComponent) componentBuilder.build(parent, targetComponentDefinition, deploymentContext);
        parent.register(targetComponentComponent);
        ComponentDefinition<SystemImplementation> sourceComponentDefinition = MockComponentFactory.createSourceWithTargetAutowire();

        AtomicComponent sourceComponent = (AtomicComponent) componentBuilder.build(parent, sourceComponentDefinition, deploymentContext);
        parent.register(sourceComponent);

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

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = new DeploymentContext(null, null, null);

    }
}
