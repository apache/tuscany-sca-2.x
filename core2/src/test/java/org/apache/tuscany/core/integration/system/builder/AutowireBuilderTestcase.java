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
import org.apache.tuscany.model.BoundReference;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * Validates that system builders create autowired contexts from a set of model objects
 *
 * @version $$Rev$$ $$Date$$
 */
public class AutowireBuilderTestcase extends TestCase {

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

        SystemCompositeContext grandParent = new SystemCompositeContextImpl("grandparent", null, null);
        SystemCompositeContext parent = new SystemCompositeContextImpl("parent", grandParent, grandParent);

        Component<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
        AtomicContext targetComponentContext = (AtomicContext) componentBuilder.build(parent, targetComponent);
        targetComponentContext.setScopeContext(scope);
        grandParent.registerContext(targetComponentContext);

        BoundReference<SystemBinding> targetReference = MockComponentFactory.createBoundReference();
        Component<SystemImplementation> sourceComponent = MockComponentFactory.createSourceWithTargetReference();


        AtomicContext<?> sourceContext = (AtomicContext) componentBuilder.build(parent, sourceComponent);
        sourceContext.setScopeContext(scope);
        parent.registerContext(sourceContext);

        ReferenceContext targetContext = (ReferenceContext) bindingBuilder.build(parent, targetReference);
        parent.registerContext(targetContext);

        connector.connect(sourceContext);

        grandParent.start();
        scope.onEvent(new ModuleStart(this, parent));
        Source source = (Source) parent.getContext("source").getService();
        assertNotNull(source);
        Target target = (Target) parent.getContext("target").getService();
        assertNotNull(target);
        assertSame(target, source.getTarget());
        assertSame(target, grandParent.getContext("target").getService());
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

        SystemCompositeContext parent = new SystemCompositeContextImpl(null, null, null);

        Component<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
        AtomicContext targetComponentContext = (AtomicContext) componentBuilder.build(parent, targetComponent);
        targetComponentContext.setScopeContext(scope);
        parent.registerContext(targetComponentContext);
        Component<SystemImplementation> sourceComponent = MockComponentFactory.createSourceWithTargetAutowire();

        AtomicContext sourceContext = (AtomicContext) componentBuilder.build(parent, sourceComponent);
        sourceContext.setScopeContext(scope);
        parent.registerContext(sourceContext);

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


}
