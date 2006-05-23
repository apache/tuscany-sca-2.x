package org.apache.tuscany.core.integration.system.builder;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.factories.MockComponentFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.system.builder.SystemBindingBuilder;
import org.apache.tuscany.core.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.spi.model.BoundReference;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ServiceContext;

/**
 * Validates that system builders and the default connector create properly wired contexts
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBuildersTestCase extends TestCase {

    /**
     * Validates building a wire from an atomic context to an atomic context
     */
    public void testAtomicWireBuild() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();

        SystemCompositeContext parent = new SystemCompositeContextImpl(null, null, null);

        Component<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
        Component<SystemImplementation> sourceComponent = MockComponentFactory.createSourceWithTargetReference();

        AtomicContext<?> sourceContext = (AtomicContext) builder.build(parent, sourceComponent);
        sourceContext.setScopeContext(scope);
        AtomicContext<?> targetContext = (AtomicContext) builder.build(parent, targetComponent);
        targetContext.setScopeContext(scope);

        parent.registerContext(sourceContext);
        parent.registerContext(targetContext);

        connector.connect(sourceContext);
        connector.connect(targetContext);
        parent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Source source = (Source) parent.getContext("source").getService();
        assertNotNull(source);
        Target target = (Target) parent.getContext("target").getService();
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

        SystemCompositeContext grandParent = new SystemCompositeContextImpl("grandparent", null, null);
        SystemCompositeContext parent = new SystemCompositeContextImpl("parent", grandParent, grandParent);

        // create a context in the grandparent that the reference will be autowired to
        Component<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
        AtomicContext targetComponentContext = (AtomicContext) builder.build(parent, targetComponent);
        targetComponentContext.setScopeContext(scope);
        grandParent.registerContext(targetComponentContext);

        BoundReference<SystemBinding> targetReference = MockComponentFactory.createBoundReference();
        Component<SystemImplementation> sourceComponent = MockComponentFactory.createSourceWithTargetReference();

        AtomicContext<?> sourceContext = (AtomicContext) builder.build(parent, sourceComponent);
        sourceContext.setScopeContext(scope);
        ReferenceContext targetContext = (ReferenceContext) bindingBuilder.build(parent, targetReference);

        parent.registerContext(sourceContext);
        parent.registerContext(targetContext);

        connector.connect(sourceContext);
        grandParent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Source source = (Source) parent.getContext("source").getService();
        assertNotNull(source);
        Target target = (Target) parent.getContext("target").getService();
        assertNotNull(target);
        assertSame(target, source.getTarget());
        scope.onEvent(new ModuleStop(this, parent));
        grandParent.stop();
        scope.stop();
    }


    /**
     * Validates building a wire from a service context to an atomic context
     */
    public void testServiceToAtomicWireBuild() throws Exception{
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeContext parent = new SystemCompositeContextImpl(null, null, null);

        BoundService<SystemBinding> service =  MockComponentFactory.createBoundService();
        Component<SystemImplementation> component = MockComponentFactory.createTarget();

        AtomicContext<?> sourceContext = (AtomicContext) builder.build(parent, component);
        sourceContext.setScopeContext(scope);
        ServiceContext<?> serviceContext = (ServiceContext) bindingBuilder.build(parent, service);

        parent.registerContext(sourceContext);
        parent.registerContext(serviceContext);

        connector.connect(sourceContext);
        connector.connect(serviceContext);
        parent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Target target = (Target) parent.getContext("service").getService();
        assertNotNull(target);
        Target target2 = (Target) parent.getContext("target").getService();
        assertNotNull(target);
        assertSame(target, target2);
        scope.onEvent(new ModuleStop(this, parent));
        parent.stop();
        scope.stop();

    }

    /**
     * Validates building a wire from a service context to a reference context
     */
    public void testServiceToReferenceWireBuild() throws Exception{
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeContext grandParent = new SystemCompositeContextImpl("grandparent", null, null);
        SystemCompositeContext parent = new SystemCompositeContextImpl("parent", grandParent, grandParent);

        // create a context in the grandparent that the reference will be autowired to
        Component<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
        AtomicContext targetComponentContext = (AtomicContext) builder.build(parent, targetComponent);
        targetComponentContext.setScopeContext(scope);
        grandParent.registerContext(targetComponentContext);

        BoundReference<SystemBinding> reference = MockComponentFactory.createBoundReference();
        BoundService<SystemBinding> service =  MockComponentFactory.createBoundService();

        ReferenceContext<?> referenceContext = (ReferenceContext) bindingBuilder.build(parent, reference);
        ServiceContext<?> serviceContext = (ServiceContext) bindingBuilder.build(parent, service);

        parent.registerContext(referenceContext);
        parent.registerContext(serviceContext);

        connector.connect(serviceContext);
        parent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Target target = (Target) parent.getContext("service").getService();
        assertNotNull(target);
        Target target2 = (Target) parent.getContext("target").getService();
        assertNotNull(target);
        assertSame(target, target2);
        scope.onEvent(new ModuleStop(this, parent));
        parent.stop();
        scope.stop();

    }


}
