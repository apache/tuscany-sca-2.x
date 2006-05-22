package org.apache.tuscany.core.system.context;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemCompositeContextResolutionTestCase extends TestCase {

    public void testComponentResolution() throws NoSuchMethodException {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();
        SystemCompositeContext composite = new SystemCompositeContextImpl("foo", null, null);
        composite.start();
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", SourceImpl.class);
        scopeContext.register(context);
        context.setScopeContext(scopeContext);
        composite.registerContext(context);
        scopeContext.publish(new ModuleStart(this, composite));
        AtomicContext ctx = (AtomicContext) composite.getContext("source");
        Source source = (Source) ctx.getService();
        assertNotNull(source);
        scopeContext.publish(new ModuleStop(this, composite));
        composite.stop();
        scopeContext.stop();
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
