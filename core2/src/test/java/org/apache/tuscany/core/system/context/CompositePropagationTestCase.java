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
import org.apache.tuscany.core.mock.MockContextFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
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

    public void testLifecyclePropagation() throws NoSuchMethodException {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();
        SystemCompositeContext parent = new SystemCompositeContextImpl("parent", null, null);
        SystemCompositeContext child1 = new SystemCompositeContextImpl("child1", null, null);
        SystemCompositeContext child2 = new SystemCompositeContextImpl("child2", null, null);
        child2.setParent(child1);
        child1.setParent(parent);
        parent.registerContext(child1);
        child1.registerContext(child2);
        parent.start();
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", SourceImpl.class);
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
        child2.setParent(child1);
        child1.setParent(parent);
        parent.registerContext(child1);
        child1.registerContext(child2);

        scopeContext.register(context);
        context.setScopeContext(scopeContext);
        child2.registerContext(context);

        parent.start();
        scopeContext.register(context);
        context.setScopeContext(scopeContext);
        AtomicContext ctx3 = (AtomicContext) child2.getContext("source");
        Source source3 = (Source) ctx3.getService();
        assertNotSame(source, source3);
        parent.stop();
        scopeContext.stop();

    }

    public void testEventPropagation() throws NoSuchMethodException {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();
        SystemCompositeContext parent = new SystemCompositeContextImpl("parent", null, null);
        SystemCompositeContext child1 = new SystemCompositeContextImpl("child1", null, null);
        SystemCompositeContext child2 = new SystemCompositeContextImpl("child2", null, null);
        child2.setParent(child1);
        child1.setParent(parent);
        parent.registerContext(child1);
        child1.registerContext(child2);
        parent.start();
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.add(ModuleScopeInitDestroyComponent.class);
        SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", interfaces,
                ModuleScopeInitDestroyComponent.class, false, initInvoker, destroyInvoker, null);
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
        parent.stop();
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        initInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
