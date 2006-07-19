package org.apache.tuscany.spi.extension;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceTestCase extends TestCase {

    public void testScope() throws Exception {
        TestReference ref = new TestReference(null, null, null);
        assertEquals(Scope.COMPOSITE, ref.getScope());

    }

    public void testSetGetInterface() throws Exception {
        TestReference<TestReference> ref = new TestReference<TestReference>(null, null, null);
        ref.setInterface(TestReference.class);
        assertEquals(TestReference.class, ref.getInterface());

    }


    public void testPrepare() throws Exception {
        TestReference ref = new TestReference(null, null, null);
        try {
            ref.prepare();
            fail();
        } catch (AssertionError e) {
            //expected
        }

    }

    private class TestReference<T> extends ReferenceExtension<T> {
        public TestReference(String name, CompositeComponent parent, WireService wireService) {
            super(name, parent, wireService);
        }

        public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
            return null;
        }
    }
}
