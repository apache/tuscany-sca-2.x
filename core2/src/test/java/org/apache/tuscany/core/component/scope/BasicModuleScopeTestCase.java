package org.apache.tuscany.core.component.scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeInitDestroyComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicModuleScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private ObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContainer scopeContext = new ModuleScopeContainer(workContext);
        scopeContext.start();
        SystemAtomicComponent atomicContext = createContext(scopeContext);
        // start the request
        ModuleScopeInitDestroyComponent o1 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        ModuleScopeInitDestroyComponent o2 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertEquals(o1, o2);
        scopeContext.onEvent(new CompositeStop(this, null));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testModuleIsolation() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ModuleScopeContainer scopeContext = new ModuleScopeContainer(workContext);
        scopeContext.start();

        SystemAtomicComponent atomicContext = createContext(scopeContext);

        ModuleScopeInitDestroyComponent o1 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());

        ModuleScopeInitDestroyComponent o2 =
            (ModuleScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertSame(o1, o2);
        scopeContext.onEvent(new CompositeStop(this, null));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<ModuleScopeInitDestroyComponent>(
            ModuleScopeInitDestroyComponent.class.getConstructor((Class[]) null), null);
        initInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod(
            "init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(ModuleScopeInitDestroyComponent.class.getMethod(
            "destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicComponent createContext(ScopeContainer scopeContainer) {
        SystemAtomicComponentImpl context = new SystemAtomicComponentImpl("foo",
            null,
            scopeContainer,
            ModuleScopeInitDestroyComponent.class,
            factory,
            false,
            initInvoker,
            destroyInvoker,
            null,
            null);
        context.start();
        return context;
    }
}
