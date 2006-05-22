package org.apache.tuscany.core.context.scope;

import junit.framework.TestCase;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.mock.context.MockCompositeContext;
import org.apache.tuscany.core.system.context.SystemAtomicContextImpl;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicModuleScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private ObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        CompositeContext currentModule = new MockCompositeContext(null, null);
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();
        SystemAtomicContext atomicContext = createContext();
        atomicContext.setScopeContext(scopeContext);
        // start the request
        workContext.setRemoteContext(currentModule);
        ModuleScopeInitDestroyComponent o1 = (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        ModuleScopeInitDestroyComponent o2 = (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertEquals(o1, o2);
        scopeContext.onEvent(new ModuleStop(this, currentModule));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testModuleIsolation() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        CompositeContext currentModule = new MockCompositeContext(null, null);
        ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
        scopeContext.start();

        SystemAtomicContext atomicContext = createContext();
        atomicContext.setScopeContext(scopeContext);
        SystemAtomicContext atomicContext2 = createContext();
        atomicContext2.setScopeContext(scopeContext);

        workContext.setRemoteContext(currentModule);
        ModuleScopeInitDestroyComponent o1 = (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());

        ModuleScopeInitDestroyComponent o2 = (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertSame(o1, o2);
        scopeContext.onEvent(new ModuleStop(this, currentModule));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<ModuleScopeInitDestroyComponent>(ModuleScopeInitDestroyComponent.class.getConstructor((Class[]) null), null);
        initInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicContext createContext() {
        return new SystemAtomicContextImpl("foo", null, ModuleScopeInitDestroyComponent.class,factory, false, initInvoker, destroyInvoker, null,null);
    }
}
