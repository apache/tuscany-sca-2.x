package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.Resource;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ResourceProcessorTestCase extends TestCase {

    PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type;
    ResourceProcessor processor = new ResourceProcessor();

    public void testVisitField() throws Exception {
        Field field = Foo.class.getDeclaredField("bar");
        processor.visitField(null, field, type, null);
        Resource resource = type.getResources().get("bar");
        assertFalse(resource.isOptional());
        assertNull(resource.getMappedName());
        assertEquals(field.getType(), resource.getType());
    }

    public void testVisitMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar", Bar.class);
        processor.visitMethod(null, method, type, null);
        Resource resource = type.getResources().get("bar");
        assertFalse(resource.isOptional());
        assertNull(resource.getMappedName());
        assertEquals(method.getParameterTypes()[0], resource.getType());
    }

    public void testVisitNamedMethod() throws Exception {
        Method method = Foo.class.getMethod("setBar2", Bar.class);
        processor.visitMethod(null, method, type, null);
        Resource resource = type.getResources().get("someName");
        assertFalse(resource.isOptional());
        assertEquals("mapped", resource.getMappedName());
    }

    public void testVisitBadMethod() throws Exception {
        Method method = Foo.class.getMethod("setBad");
        try {
            processor.visitMethod(null, method, type, null);
            fail();
        } catch (IllegalResourceException e) {
            // expected
        }
    }

    public void testDuplicateResources() throws Exception {
        Field field = Foo.class.getDeclaredField("bar");
        processor.visitField(null, field, type, null);
        try {
            processor.visitField(null, field, type, null);
            fail();
        } catch (DuplicateResourceException e) {
            //expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
    }

    private class Foo {

        @org.osoa.sca.annotations.Resource
        protected Bar bar;

        @org.osoa.sca.annotations.Resource(optional = true)
        protected Bar barNotRequired;

        @org.osoa.sca.annotations.Resource
        public void setBar(Bar bar) {
        }

        @org.osoa.sca.annotations.Resource(name = "someName", mappedName = "mapped")
        public void setBar2(Bar bar) {
        }

        @org.osoa.sca.annotations.Resource
        public void setBad() {
        }

    }

    private interface Bar {

    }
}
