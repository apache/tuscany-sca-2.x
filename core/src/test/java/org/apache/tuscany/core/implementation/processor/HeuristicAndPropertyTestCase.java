package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Constructor;

import org.osoa.sca.annotations.Property;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.ConstructorDefinition;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class HeuristicAndPropertyTestCase extends TestCase {

    private PropertyProcessor propertyProcessor = new PropertyProcessor();
    private HeuristicPojoProcessor heuristicProcessor = new HeuristicPojoProcessor();

    /**
     * Verifies the property and heuristic processors don't collide
     */
    @SuppressWarnings("unchecked")
    public void testPropertyProcessorWithHeuristicProcessor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        Constructor ctor = Foo.class.getConstructor(String.class);
        type.setConstructorDefinition(new ConstructorDefinition(ctor));
        propertyProcessor.visitConstructor(null, ctor, type, null);
        heuristicProcessor.visitEnd(null, Foo.class, type, null);
        assertEquals(1, type.getProperties().size());
        assertNotNull(type.getProperties().get("foo"));
    }


    public static class Foo {
        public Foo(@Property(name = "foo") String prop) {
        }
    }

}
