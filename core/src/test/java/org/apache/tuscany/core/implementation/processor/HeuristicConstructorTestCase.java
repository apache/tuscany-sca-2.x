package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.JavaServiceContract;

/**
 * @version $Rev$ $Date$
 */
public class HeuristicConstructorTestCase extends TestCase {

    private HeuristicPojoProcessor processor = new HeuristicPojoProcessor();

    /**
     * Verifies a single constructor is chosen with a parameter as the type
     */
    public void testSingleConstructorWithParam() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        JavaMappedProperty<String> prop = new JavaMappedProperty<String>();
        prop.setName("foo");
        prop.setJavaType(String.class);
        type.getProperties().put("foo", prop);
        processor.visitEnd(null, Foo1.class, type, null);
        assertNotNull(type.getConstructorDefinition().getConstructor());
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(0));
    }

    /**
     * Verifies a single constructor is chosen with a reference as the type
     */
    public void testSingleConstructorWithRef() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        JavaMappedReference ref = new JavaMappedReference();
        ref.setName("foo");
        ServiceContract contract = new JavaServiceContract(String.class);
        ref.setServiceContract(contract);
        type.getReferences().put("foo", ref);
        processor.visitEnd(null, Foo1.class, type, null);
        assertNotNull(type.getConstructorDefinition().getConstructor());
        assertEquals("foo", type.getConstructorDefinition().getInjectionNames().get(0));
    }

    /**
     * Verifies a single constructor is chosen with a property and a reference as the type
     */
    public void testSingleConstructorWithPropRef() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();

        JavaMappedProperty<String> prop = new JavaMappedProperty<String>();
        prop.setName("foo");
        prop.setJavaType(String.class);
        type.getProperties().put("foo", prop);

        JavaMappedReference ref = new JavaMappedReference();
        ref.setName("ref");
        ServiceContract contract = new JavaServiceContract(Foo1.class);
        ref.setServiceContract(contract);
        type.getReferences().put("ref", ref);
        processor.visitEnd(null, Foo2.class, type, null);
        assertNotNull(type.getConstructorDefinition().getConstructor());
        assertEquals(2, type.getConstructorDefinition().getInjectionNames().size());
    }


    public void testSingleConstructorUnresolvableParam() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        try {
            processor.visitEnd(null, Foo1.class, type, null);
            fail();
        } catch (UnresolvableConstructorParameterException e) {
            // expected
        }
    }

    public void testSingleConstructorAmbiguousRef() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        JavaMappedReference ref = new JavaMappedReference();
        ref.setName("ref");
        ServiceContract contract = new JavaServiceContract(Foo1.class);
        ref.setServiceContract(contract);
        type.getReferences().put("ref", ref);
        JavaMappedReference ref2 = new JavaMappedReference();
        ref2.setName("ref2");
        ref2.setServiceContract(contract);
        type.getReferences().put("ref2", ref2);
        try {
            processor.visitEnd(null, Foo4.class, type, null);
            fail();
        } catch (UnresolvableConstructorParameterException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testDefaultConstructor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(null, Foo3.class, type, null);
        assertNotNull(type.getConstructorDefinition().getConstructor());
    }

    public static class Foo1 {
        public Foo1(String val) {
        }
    }

    public static class Foo2 {
        public Foo2(String val, Foo1 ref) {
        }
    }

    public static class Foo3 {
    }

    public static class Foo4 {
        public Foo4(Foo1 ref) {
        }
    }

}
