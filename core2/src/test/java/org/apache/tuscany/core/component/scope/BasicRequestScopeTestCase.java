package org.apache.tuscany.core.component.scope;

import junit.framework.TestCase;
import org.apache.tuscany.spi.component.PojoConfiguration;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.spi.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.spi.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.RequestScopeInitDestroyComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.ScopeContainer;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicRequestScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private ObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        RequestScopeContainer scopeContext = new RequestScopeContainer(null);
        scopeContext.start();
        SystemAtomicComponent atomicContext = createContext(scopeContext);
        // start the request
        RequestScopeInitDestroyComponent o1 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        RequestScopeInitDestroyComponent o2 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertSame(o1, o2);
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testRequestIsolation() throws Exception {
        RequestScopeContainer scopeContext = new RequestScopeContainer(null);
        scopeContext.start();

        SystemAtomicComponent atomicContext = createContext(scopeContext);

        RequestScopeInitDestroyComponent o1 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o1.isDestroyed());

        RequestScopeInitDestroyComponent o2 =
            (RequestScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertNotSame(o1, o2);
        scopeContext.onEvent(new RequestEnd(this));
        assertTrue(o2.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<RequestScopeInitDestroyComponent>(
            RequestScopeInitDestroyComponent.class.getConstructor((Class[]) null), null);
        initInvoker = new MethodEventInvoker<Object>(
            RequestScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(
            RequestScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicComponent createContext(ScopeContainer scopeContainer) {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scopeContainer);
        configuration.addServiceInterface(RequestScopeInitDestroyComponent.class);
        configuration.setObjectFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl("foo", configuration);
        scopeContainer.register(component);
        return component;
    }
}
