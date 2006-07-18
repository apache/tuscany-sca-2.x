/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.processor;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;

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


    public void testSingleConstructorResolvableParam() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(null, Foo5.class, type, null);
        assertEquals(String.class, type.getProperties().get("string").getJavaType());
    }

    public void testSingleConstructorResolvableRef() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(null, Foo6.class, type, null);
        assertEquals(Ref.class,
            type.getReferences().get("heuristicconstructortestcase$ref").getServiceContract().getInterfaceClass());
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
        } catch (AmbiguousConstructorException e) {
            // expected
        }
    }

    public void testConstructorPropertyAnnotatedParamsOnly() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(null, Foo7.class, type, null);
        assertNotNull(type.getProperties().get("myProp"));
    }

    public void testConstructorReferenceAnnotatedParamsOnly() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(null, Foo8.class, type, null);
        assertNotNull(type.getReferences().get("myRef"));
    }

    public void testConstructorAutowireAnnotatedParamsOnly() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(null, Foo9.class, type, null);
        assertNotNull(type.getReferences().get("myAutowire"));
    }

    @SuppressWarnings("unchecked")
    public void testDefaultConstructor() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(null, Foo3.class, type, null);
        assertNotNull(type.getConstructorDefinition().getConstructor());
    }

    public void testMultipleConstructors() throws Exception {
    //    throw new UnsupportedOperationException("Finish heuristic multiple constructors - Foo10");
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

    public static class Prop {

    }

    @Remotable
    public static interface Ref {

    }

    public static class Foo5 {
        public Foo5(String val) {
        }
    }

    public static class Foo6 {
        public Foo6(Ref ref) {
        }
    }

    public static class Foo7 {
        public Foo7(@Property(name = "myProp") String prop) {
        }
    }


    public static class Foo8 {
        public Foo8(@Reference(name = "myRef") String ref) {
        }
    }

    public static class Foo9 {
        public Foo9(@Autowire(name = "myAutowire") String autowire) {
        }
    }

    public static class Foo10 {

        public Foo10() {
        }

        public Foo10(String prop) {
        }

        public Foo10(@Property(name = "prop1") String prop1, @Property(name = "prop2") String prop2) {

        }
    }


}
