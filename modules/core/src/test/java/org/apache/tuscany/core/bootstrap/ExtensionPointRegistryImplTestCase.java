package org.apache.tuscany.core.bootstrap;

import junit.framework.TestCase;

public class ExtensionPointRegistryImplTestCase extends TestCase {
    private ExtensionPointRegistry registry;

    protected void setUp() throws Exception {
        super.setUp();
        registry = new ExtensionPointRegistryImpl();
    }

    public void testRegistry() {
        MyRegistry service = new MyREgistryImpl();
        registry.addExtensionPoint(MyRegistry.class, service);
        assertSame(service, registry.getExtensionPoint(MyRegistry.class));
        registry.removeExtensionPoint(MyRegistry.class);
        assertNull(registry.getExtensionPoint(MyRegistry.class));
    }

    private static interface MyRegistry {
        void doSomething();
    }

    private static class MyREgistryImpl implements MyRegistry {

        public void doSomething() {
        }

    }

}
