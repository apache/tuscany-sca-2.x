package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Method;

import org.osoa.sca.annotations.Init;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class InitProcessorTestCase extends TestCase {

    public void testInit() throws Exception {
        InitProcessor processor = new InitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = InitProcessorTestCase.Foo.class.getMethod("init");
        processor.visitMethod(null, method, type, null);
        assertNotNull(type.getInitMethod());
        assertEquals(50, type.getInitLevel());
    }

    public void testBadInit() throws Exception {
        InitProcessor processor = new InitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = InitProcessorTestCase.Bar.class.getMethod("badInit", String.class);
        try {
            processor.visitMethod(null, method, type, null);
            fail();
        } catch (IllegalInitException e) {
            // expected
        }
    }

    public void testTwoInit() throws Exception {
        InitProcessor processor = new InitProcessor();
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Method method = InitProcessorTestCase.Bar.class.getMethod("init");
        Method method2 = InitProcessorTestCase.Bar.class.getMethod("init2");
        processor.visitMethod(null, method, type, null);
        try {
            processor.visitMethod(null, method2, type, null);
            fail();
        } catch (DuplicateInitException e) {
            // expected
        }
    }


    private class Foo {
        @Init(eager = true)
        public void init() {
        }
    }


    private class Bar {
        @Init
        public void init() {
        }

        @Init
        public void init2() {
        }

        @Init
        public void badInit(String foo) {
        }


    }
}
