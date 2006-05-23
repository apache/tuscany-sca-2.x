package org.apache.tuscany.core.context.scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.event.RequestEnd;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.RequestScopeInitDestroyComponent;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.context.SystemAtomicContextImpl;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicRequestScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private ObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        RequestScopeContext scopeContext = new RequestScopeContext(null);
        scopeContext.start();
        SystemAtomicContext atomicContext = createContext();
        atomicContext.setScopeContext(scopeContext);
        // start the request
        RequestScopeInitDestroyComponent o1 = (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        RequestScopeInitDestroyComponent o2 = (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertSame(o1, o2);
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testRequestIsolation() throws Exception {
        RequestScopeContext scopeContext = new RequestScopeContext(null);
        scopeContext.start();

        SystemAtomicContext atomicContext = createContext();
        atomicContext.setScopeContext(scopeContext);

        RequestScopeInitDestroyComponent o1 = (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());

        RequestScopeInitDestroyComponent o2 = (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertNotSame(o1, o2);
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o2.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<RequestScopeInitDestroyComponent>(RequestScopeInitDestroyComponent.class.getConstructor((Class[]) null), null);
        initInvoker = new MethodEventInvoker<Object>(RequestScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(RequestScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicContext createContext() {
        return new SystemAtomicContextImpl("foo", null, RequestScopeInitDestroyComponent.class, factory, false, initInvoker, destroyInvoker, null, null);
    }
}
