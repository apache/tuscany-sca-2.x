package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;

import org.osoa.sca.annotations.Property;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ConstructorDefinition;

/**
 * Verifies the constructor processor works when parameters are marked with custom extension annotations
 *
 * @version $Rev$ $Date$
 */
public class ConstructorProcessorExtensibilityTestCase extends TestCase {
    private ConstructorProcessor processor = new ConstructorProcessor();

    public void testProcessFirst() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor1 = Foo.class.getConstructor(String.class, String.class);
        processor.visitConstructor(null, ctor1, type, null);
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(0));
    }

    /**
     * Verifies the constructor processor can be called after another processor has evaluated the constructor and found
     * an annotation
     *
     * @throws Exception
     */
    public void testProcessLast() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor1 = Foo.class.getConstructor(String.class, String.class);
        ConstructorDefinition<Foo> definition = new ConstructorDefinition<Foo>(ctor1);
        definition.getInjectionNames().add("");
        definition.getInjectionNames().add("mybar");
        type.setConstructorDefinition(definition);
        processor.visitConstructor(null, ctor1, type, null);
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(0));
    }


    private @interface Bar {

    }

    private static class Foo {
        @org.osoa.sca.annotations.Constructor
        public Foo(@Property(name = "foo") String foo, @Bar String bar) {

        }
    }


}
