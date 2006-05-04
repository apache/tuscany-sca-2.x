package org.apache.tuscany.core.system.context;

import junit.framework.TestCase;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.context.scope.MockScopeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.AtomicContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicContextTestCase extends TestCase {

    EventInvoker<Object> initInvoker;

    public void testContextCreationAndInit() throws Exception {
        ObjectFactory<Foo> factory = new PojoObjectFactory<Foo>(Foo.class.getConstructor((Class[]) null), null, null);
        ScopeContext<AtomicContext> scopeContext = new MockScopeContext();
        SystemAtomicContext context = new SystemAtomicContext("foo",factory, false, initInvoker, null);
        context.setScopeContext(scopeContext);
        Foo instance = (Foo) context.getInstance(null);
        assertNotNull(instance);
        assertTrue(instance.initialized);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initInvoker = new MethodEventInvoker<Object>(Foo.class.getMethod("init"));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static class Foo {

        private boolean initialized;

        public void init() {
            initialized = true;
        }

    }

}
