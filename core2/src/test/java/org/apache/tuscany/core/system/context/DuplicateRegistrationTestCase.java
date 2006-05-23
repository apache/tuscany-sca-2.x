package org.apache.tuscany.core.system.context;

import junit.framework.TestCase;
import org.apache.tuscany.core.context.DuplicateNameException;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.mock.factories.MockContextFactory;

/**
 * @version $Rev$ $Date$
 */
public class DuplicateRegistrationTestCase extends TestCase {

    public void testDuplicateRegistration() throws Exception {
        SystemCompositeContext systemContext = new SystemCompositeContextImpl(null, null, null);
        systemContext.start();
        systemContext.publish(new ModuleStart(this, null));
        SystemAtomicContext context1 = MockContextFactory.createSystemAtomicContext("foo", MockComponent.class);
        SystemAtomicContext context2 = MockContextFactory.createSystemAtomicContext("foo", MockComponent.class);
        systemContext.registerContext(context1);
        try {
            systemContext.registerContext(context2);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
        systemContext.publish(new ModuleStop(this, null));
        systemContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static class MockComponent {
        public String hello(String message) {
            return message;
        }
    }
}
