package org.apache.tuscany.core.integration.system.builder;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.MockComponentFactory;
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
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.context.ReferenceContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AutowireBuilderTestcase extends TestCase {

    public void testComponentToAutowireReference() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        BuilderRegistry registry = new BuilderRegistryImpl();
        SystemComponentBuilder componentBuilder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeContext grandParent = new SystemCompositeContextImpl();
        grandParent.setName("grandparent");
        SystemCompositeContext parent = new SystemCompositeContextImpl("parent",grandParent,grandParent);

        Component<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
        AtomicContext targetComponentContext = (AtomicContext) componentBuilder.build(parent, targetComponent);
        targetComponentContext.setScopeContext(scope);
        grandParent.registerContext(targetComponentContext);

        BoundReference<SystemBinding> targetReference = MockComponentFactory.createTargetReference();
        Component<SystemImplementation> sourceComponent = MockComponentFactory.createSourceWithTargetReference();


        AtomicContext sourceContext = (AtomicContext) componentBuilder.build(parent, sourceComponent);
        sourceContext.setScopeContext(scope);
        parent.registerContext(sourceContext);

        ReferenceContext targetContext = (ReferenceContext) bindingBuilder.build(parent, targetReference);
        parent.registerContext(targetContext);

        registry.connect(sourceContext, parent);
        //registry.connect(targetContext, parent);

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
}
