package org.apache.tuscany.core.system.context;

import javax.naming.ConfigurationException;

import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.spi.context.DuplicateNameException;
import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class SystemObjectRegistrationTestCase extends TestCase {
    private SystemCompositeContext<?> systemContext;

    public void testRegistration() throws Exception {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        MockComponent resolvedInstance = (MockComponent)systemContext.getContext("foo").getService();
        assertSame(instance, resolvedInstance);
    }

    public void testDuplicateRegistration() throws ConfigurationException {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        try {
            systemContext.registerJavaObject("foo", MockComponent.class, instance);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
    }

    public void testAutowireToObject()  {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        assertSame(instance, systemContext.resolveInstance(MockComponent.class));
        assertNull(systemContext.resolveExternalInstance(MockComponent.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
        systemContext = new SystemCompositeContextImpl(null, null, null);
        systemContext.start();
        systemContext.publish(new ModuleStart(this,null));
    }

    protected void tearDown() throws Exception {
        systemContext.publish(new ModuleStop(this,null));
        systemContext.stop();
        super.tearDown();
    }

    private static class MockComponent {
        public String hello(String message) {
            return message;
        }
    }
}
