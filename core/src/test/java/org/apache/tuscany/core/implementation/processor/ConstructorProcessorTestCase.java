package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorProcessorTestCase extends TestCase {

    public void testDuplicateConstructor() throws Exception {
        ConstructorProcessor processor = new ConstructorProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor1 = BadFoo.class.getConstructor(String.class);
        Constructor ctor2 = BadFoo.class.getConstructor(String.class, String.class);
        processor.visitConstructor(null, ctor1, type, null);
        try {
            processor.visitConstructor(null, ctor2, type, null);
            fail();
        } catch (DuplicateConstructorException e) {
            // expected
        }
    }


    private static class BadFoo {

        @org.osoa.sca.annotations.Constructor("foo")
        public BadFoo(String foo) {

        }

        @org.osoa.sca.annotations.Constructor({"foo", "bar"})
        public BadFoo(String foo, String bar) {

        }

    }


}
