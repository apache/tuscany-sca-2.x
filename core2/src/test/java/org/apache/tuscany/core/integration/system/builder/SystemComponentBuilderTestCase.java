package org.apache.tuscany.core.integration.system.builder;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.Connector;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.MockComponentFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.system.context.SystemCompositeContext;
import org.apache.tuscany.core.system.context.SystemCompositeContextImpl;
import org.apache.tuscany.core.system.model.SystemImplementation;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemComponentBuilderTestCase extends TestCase {

    public void testComponentWire() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContext scope = new ModuleScopeContext(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();

        SystemCompositeContext parent = new SystemCompositeContextImpl();

        Component<SystemImplementation> targetComponent = MockComponentFactory.createTarget();
        Component<SystemImplementation> sourceComponent = MockComponentFactory.createSourceWithTargetReference();

        AtomicContext sourceContext = (AtomicContext) builder.build(parent, sourceComponent);
        sourceContext.setScopeContext(scope);
        AtomicContext targetContext = (AtomicContext) builder.build(parent, targetComponent);
        targetContext.setScopeContext(scope);

        parent.registerContext(sourceContext);
        parent.registerContext(targetContext);

        connector.connect(sourceContext, parent);
        connector.connect(targetContext, parent);
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
