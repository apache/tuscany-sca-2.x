package org.apache.tuscany.core.injection;

import java.lang.reflect.Field;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class FieldInjectorTestCase extends TestCase {

    protected Field protectedField;

    public void testIllegalAccess() throws Exception {
        FieldInjector<Foo> injector = new FieldInjector<Foo>(protectedField, new SingletonObjectFactory<String>("foo"));
        Foo foo = new Foo();
        injector.inject(foo);
        assertEquals("foo", foo.hidden);
    }


    protected void setUp() throws Exception {
        super.setUp();
        protectedField = Foo.class.getDeclaredField("hidden");
    }

    private class Foo {
        private String hidden;
    }
}
