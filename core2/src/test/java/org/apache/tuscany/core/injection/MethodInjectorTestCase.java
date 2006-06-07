package org.apache.tuscany.core.injection;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class MethodInjectorTestCase extends MockObjectTestCase {
    private Method fooMethod;
    private Method privateMethod;
    private Method exceptionMethod;

    public void testIllegalArgument() throws Exception {
        ObjectFactory<Object> factory = new SingletonObjectFactory<Object>(new Object());
        MethodInjector<Foo> injector = new MethodInjector<Foo>(fooMethod, factory);
        try {
            injector.inject(new Foo());
            fail();
        } catch (ObjectCreationException e) {
            // expected
        }
    }

    public void testIllegalAccess() throws Exception {
        ObjectFactory<Object> factory = new SingletonObjectFactory<Object>("foo");
        MethodInjector<Foo> injector = new MethodInjector<Foo>(privateMethod, factory);
        try {
            injector.inject(new Foo());
            fail();
        } catch (AssertionError e) {
            // expected
        }
    }

    public void testException() throws Exception {
        ObjectFactory<Object> factory = new SingletonObjectFactory<Object>("foo");
        MethodInjector<Foo> injector = new MethodInjector<Foo>(exceptionMethod, factory);
        try {
            injector.inject(new Foo());
            fail();
        } catch (RuntimeException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        fooMethod = Foo.class.getMethod("foo", String.class);
        privateMethod = Foo.class.getDeclaredMethod("hidden", String.class);
        exceptionMethod = Foo.class.getDeclaredMethod("exception", String.class);

    }

    private class Foo {

        public void foo(String bar) {
        }

        private void hidden(String bar){}

        public void exception(String bar){
            throw new RuntimeException();
        }

    }
}