package org.apache.tuscany.core.bootstrap;

import junit.framework.TestCase;

public class ExtensionRegistryImplTestCase extends TestCase {
    private ExtensionRegistry registry;

    protected void setUp() throws Exception {
        super.setUp();
        registry = new ExtensionRegistryImpl();
    }

    public void testRegistry() {
        MyService service = new MyServiceImpl();
        registry.addExtension(MyService.class, service);
        assertSame(service, registry.getExtension(MyService.class));
        assertEquals(1, registry.getExtensions(MyService.class).size());
        MyService service2 = new MyServiceImpl();
        registry.addExtension(MyService.class, service2);
        assertSame(service, registry.getExtension(MyService.class));
        assertEquals(2, registry.getExtensions(MyService.class).size());
        registry.removeExtension(MyService.class, service);
        assertSame(service2, registry.getExtension(MyService.class));
        assertEquals(1, registry.getExtensions(MyService.class).size());
        registry.removeExtensionPoint(MyService.class);
        assertNull(registry.getExtension(MyService.class));
    }

    private static interface MyService {
        void doSomething();
    }

    private static class MyServiceImpl implements MyService {

        public void doSomething() {
        }

    }

}
