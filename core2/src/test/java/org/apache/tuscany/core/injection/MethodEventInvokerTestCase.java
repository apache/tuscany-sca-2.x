package org.apache.tuscany.core.injection;

import java.lang.reflect.Method;

import org.jmock.MockObjectTestCase;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;

/**
 * @version $Rev$ $Date$
 */
public class MethodEventInvokerTestCase extends MockObjectTestCase {
    private Method privateMethod;
    private Method exceptionMethod;

    public void testIllegalAccess() throws Exception {
        MethodEventInvoker<MethodEventInvokerTestCase.Foo> injector = new MethodEventInvoker<Foo>(privateMethod);
        try {
            injector.invokeEvent(new Foo());
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }

    public void testException() throws Exception {
        MethodEventInvoker<MethodEventInvokerTestCase.Foo> injector = new MethodEventInvoker<Foo>(exceptionMethod);
        try {
            injector.invokeEvent(new Foo());
            fail();
        } catch (RuntimeException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        privateMethod = MethodEventInvokerTestCase.Foo.class.getDeclaredMethod("hidden");
        exceptionMethod = MethodEventInvokerTestCase.Foo.class.getDeclaredMethod("exception");

    }

    private class Foo {

        public void foo() {
        }

        private void hidden(){}

        public void exception(){
            throw new RuntimeException();
        }

    }
}
