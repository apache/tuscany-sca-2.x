package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.Lifecycle;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
 * @version $Rev$ $Date$
 */
public class InstanceWrapperTestCase extends MockObjectTestCase {



    public void testExceptionInit() throws Exception {
        AtomicComponent component = getComponent();
        InstanceWrapper wrapper = new InstanceWrapperImpl(component, new Object());
        try {
            wrapper.start();
            fail();
        } catch (SomeException e) {
            // expected
        }
        assertEquals(Lifecycle.ERROR,wrapper.getLifecycleState());
    }

    public void testNonStart() throws Exception {
        Mock mock = mock(AtomicComponent.class);
        AtomicComponent comp = (AtomicComponent) mock.proxy();  // class-level one has an expects
        InstanceWrapper wrapper = new InstanceWrapperImpl(comp, new Object());
        try {
            wrapper.getInstance();
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private AtomicComponent getComponent() throws Exception {
        // do not use setUp() since we do not need this in all testcases
        Mock mock = mock(AtomicComponent.class);
        mock.expects(once()).method("init").will(new Stub() {
            public Object invoke(Invocation invocation) throws Throwable {
                throw new SomeException();
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return stringBuffer.append("bad init");
            }
        });
       return (AtomicComponent) mock.proxy();
    }

    private class SomeException extends RuntimeException {
    }
}
