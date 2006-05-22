package org.apache.tuscany.core.system.context;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.scope.ModuleScopeContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.factories.MockContextFactory;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * Tests registering arbirarily deep child composite contexts
 *
 * @version $Rev$ $Date$
 */
public class CompositePropagationTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private SystemCompositeContext parent;
    private SystemCompositeContext child1;
    private SystemCompositeContext child2;
    private ModuleScopeContext scopeContext;

    public void testLifecyclePropagation() throws NoSuchMethodException {
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", SourceImpl.class);

        parent.start();
        scopeContext.register(context);
        context.setScopeContext(scopeContext);
        child2.registerContext(context);
        scopeContext.publish(new ModuleStart(this, parent));

        AtomicContext ctx = (AtomicContext) child2.getContext("source");
        Source source = (Source) ctx.getService();
        assertNotNull(source);

        CompositeContext composite1 = (CompositeContext) parent.getContext("child1");
        CompositeContext composite2 = (CompositeContext) composite1.getContext("child2");
        AtomicContext ctx2 = (AtomicContext) composite2.getContext("source");
        Source source2 = (Source) ctx2.getService();
        assertSame(source, source2);

        scopeContext.onEvent(new ModuleStop(this, parent));
        parent.stop();
        scopeContext.stop();

        //restart
        scopeContext.start();
        parent.start();

        AtomicContext ctx3 = (AtomicContext) child2.getContext("source");
        Source source3 = (Source) ctx3.getService();
        assertNotSame(source, source3);
    }

    public void testEventPropagation() throws NoSuchMethodException {
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(ModuleScopeInitDestroyComponent.class);
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", interfaces,
                ModuleScopeInitDestroyComponent.class, false, initInvoker, destroyInvoker, null, null);

        parent.start();
        scopeContext.register(context);
        context.setScopeContext(scopeContext);
        child2.registerContext(context);
        scopeContext.publish(new ModuleStart(this, parent));

        AtomicContext ctx = (AtomicContext) child2.getContext("source");
        ModuleScopeInitDestroyComponent source = (ModuleScopeInitDestroyComponent) ctx.getService();
        assertNotNull(source);
        assertTrue(source.isInitialized());

        scopeContext.onEvent(new ModuleStop(this, parent));
        assertTrue(source.isDestroyed());
    }

    protected void setUp() throws Exception {
        super.setUp();
        initInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod("init"));
        destroyInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod("destroy"));

        WorkContext workContext = new WorkContextImpl();
        scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        parent = new SystemCompositeContextImpl("parent", null, null);
        child1 = new SystemCompositeContextImpl("child1", parent, null);
        child2 = new SystemCompositeContextImpl("child2", child1, null);
        child1.registerContext(child2);
        parent.registerContext(child1);
    }

    protected void tearDown() throws Exception {
        parent.stop();
        scopeContext.stop();
        super.tearDown();
    }

}
