package org.apache.tuscany.core.implementation.processor;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceProcessorTestCase extends TestCase {

    PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
        new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
    ReferenceProcessor processor = new ReferenceProcessor();

    public void testMethodAnnotation() throws Exception {
        processor.visitMethod(null, ReferenceProcessorTestCase.Foo.class.getMethod("setFoo", Ref.class), type, null);
        JavaMappedReference reference = type.getReferences().get("foo");
        assertNotNull(reference);
        ServiceContract contract = reference.getServiceContract();
        assertEquals(Ref.class, contract.getInterfaceClass());
        assertEquals("ReferenceProcessorTestCase$Ref", contract.getInterfaceName());
    }

    public void testMethodRequired() throws Exception {
        processor
            .visitMethod(null, ReferenceProcessorTestCase.Foo.class.getMethod("setFooRequired", Ref.class), type, null);
        JavaMappedReference prop = type.getReferences().get("fooRequired");
        assertNotNull(prop);
        assertTrue(prop.isRequired());
    }

    public void testMethodName() throws Exception {
        processor
            .visitMethod(null, ReferenceProcessorTestCase.Foo.class.getMethod("setBarMethod", Ref.class), type, null);
        assertNotNull(type.getReferences().get("bar"));
    }

    public void testFieldAnnotation() throws Exception {
        processor.visitField(null, ReferenceProcessorTestCase.Foo.class.getDeclaredField("baz"), type, null);
        JavaMappedReference reference = type.getReferences().get("baz");
        assertNotNull(reference);
        ServiceContract contract = reference.getServiceContract();
        assertEquals(Ref.class, contract.getInterfaceClass());
        assertEquals("ReferenceProcessorTestCase$Ref", contract.getInterfaceName());
    }

    public void testFieldRequired() throws Exception {
        processor.visitField(null, ReferenceProcessorTestCase.Foo.class.getDeclaredField("bazRequired"), type, null);
        JavaMappedReference prop = type.getReferences().get("bazRequired");
        assertNotNull(prop);
        assertTrue(prop.isRequired());
    }

    public void testFieldName() throws Exception {
        processor.visitField(null, ReferenceProcessorTestCase.Foo.class.getDeclaredField("bazField"), type, null);
        assertNotNull(type.getReferences().get("theBaz"));
    }

    public void testDuplicateFields() throws Exception {
        processor.visitField(null, ReferenceProcessorTestCase.Bar.class.getDeclaredField("dup"), type, null);
        try {
            processor.visitField(null, ReferenceProcessorTestCase.Bar.class.getDeclaredField("baz"), type, null);
            fail();
        } catch (DuplicateReferenceException e) {
            //expected
        }
    }

    public void testDuplicateMethods() throws Exception {
        processor.visitMethod(null, ReferenceProcessorTestCase.Bar.class.getMethod("dupMethod", Ref.class), type, null);
        try {
            processor
                .visitMethod(null, ReferenceProcessorTestCase.Bar.class.getMethod("dupSomeMethod", Ref.class), type,
                    null);
            fail();
        } catch (DuplicateReferenceException e) {
            //expected
        }
    }

    public void testInvalidProperty() throws Exception {
        try {
            processor.visitMethod(null, ReferenceProcessorTestCase.Bar.class.getMethod("badMethod"), type, null);
            fail();
        } catch (IllegalReferenceException e) {
            //expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        type = new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor = new ReferenceProcessor();
    }

    private interface Ref {
    }

    private class Foo {

        @Reference
        protected Ref baz;
        @Reference(required = true)
        protected Ref bazRequired;
        @Reference(name = "theBaz")
        protected Ref bazField;


        @Reference
        public void setFoo(Ref ref) {
        }

        @Reference(required = true)
        public void setFooRequired(Ref ref) {
        }

        @Reference(name = "bar")
        public void setBarMethod(Ref ref) {
        }

    }


    private class Bar {

        @Reference
        protected Ref dup;

        @Reference(name = "dup")
        protected Ref baz;

        @Reference
        public void dupMethod(Ref s) {
        }

        @Reference(name = "dupMethod")
        public void dupSomeMethod(Ref s) {
        }

        @Reference
        public void badMethod() {
        }


    }
}
