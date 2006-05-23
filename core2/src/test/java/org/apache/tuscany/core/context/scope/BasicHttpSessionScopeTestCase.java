package org.apache.tuscany.core.context.scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.WorkContextImpl;
import org.apache.tuscany.core.context.event.HttpSessionEnd;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.SessionScopeInitDestroyComponent;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.core.system.context.SystemAtomicContextImpl;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.context.WorkContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicHttpSessionScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private ObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        HttpSessionScopeContext scopeContext = new HttpSessionScopeContext(workContext);
        scopeContext.start();
        SystemAtomicContext atomicContext = createContext();
        atomicContext.setScopeContext(scopeContext);
        // start the request
        Object session = new Object();
        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session);
        SessionScopeInitDestroyComponent o1 = (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        SessionScopeInitDestroyComponent o2 = (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertSame(o1, o2);
        scopeContext.onEvent(new HttpSessionEnd(this, session));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testSessionIsolation() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        HttpSessionScopeContext scopeContext = new HttpSessionScopeContext(workContext);
        scopeContext.start();

        SystemAtomicContext atomicContext = createContext();
        atomicContext.setScopeContext(scopeContext);

        Object session1 = new Object();
        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session1);
        SessionScopeInitDestroyComponent o1 = (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());

        Object session2 = new Object();
        workContext.setIdentifier(HttpSessionScopeContext.HTTP_IDENTIFIER, session2);
        SessionScopeInitDestroyComponent o2 = (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertNotSame(o1, o2);

        scopeContext.onEvent(new HttpSessionEnd(this, session1));
        assertTrue(o1.isDestroyed());
        assertFalse(o2.isDestroyed());
        scopeContext.onEvent(new HttpSessionEnd(this, session2));
        assertTrue(o2.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<SessionScopeInitDestroyComponent>(SessionScopeInitDestroyComponent.class.getConstructor((Class[]) null), null);
        initInvoker = new MethodEventInvoker<Object>(SessionScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(SessionScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicContext createContext() {
        return new SystemAtomicContextImpl("foo", null, SessionScopeInitDestroyComponent.class, factory, false, initInvoker, destroyInvoker, null, null);
    }
}
