package org.apache.tuscany.core.injection;

import java.lang.reflect.Field;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class FieldInjectorTestCase extends TestCase {

    private Field privateField;

    public void testIllegalAccess() throws Exception {
        FieldInjector<Foo> injector = new FieldInjector<Foo>(privateField, new SingletonObjectFactory<String>("foo"));
        try {
            injector.inject(new Foo());
            fail();
        } catch (AssertionError e) {
            //expected
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        privateField = Foo.class.getDeclaredField("hidden");
    }

    private class Foo {
        private String hidden;
    }
}
