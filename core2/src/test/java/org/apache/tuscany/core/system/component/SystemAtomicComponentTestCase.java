package org.apache.tuscany.core.system.component;

import junit.framework.TestCase;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicComponentTestCase extends TestCase {

    EventInvoker<Object> initInvoker;

    public void testContextCreationAndInit() throws Exception {
        ObjectFactory<Foo> factory = new PojoObjectFactory<Foo>(Foo.class.getConstructor((Class[]) null), null);
        SystemAtomicComponent context = new SystemAtomicComponentImpl("foo", null, null, Foo.class, factory, false, initInvoker, null, null, null);
        Foo foo = (Foo) context.createInstance().getInstance();
        assertNotNull(foo);
        assertTrue(foo.initialized);
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
