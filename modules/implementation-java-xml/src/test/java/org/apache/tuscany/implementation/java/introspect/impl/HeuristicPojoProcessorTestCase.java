/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.implementation.java.introspect.impl;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.implementation.java.impl.ConstructorDefinition;
import org.apache.tuscany.implementation.java.impl.JavaElement;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.introspect.ProcessingException;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.util.JavaXMLMapper;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

/**
 * Verfies component type information is properly introspected from an unadorned
 * POJO according to the SCA Java Client and Implementation Model Specification
 * 
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessorTestCase extends AbstractProcessorTest {

    private org.apache.tuscany.implementation.java.introspect.impl.HeuristicPojoProcessor processor;

    public HeuristicPojoProcessorTestCase() {
        DefaultJavaInterfaceIntrospector introspector = new DefaultJavaInterfaceIntrospector();
        processor = new org.apache.tuscany.implementation.java.introspect.impl.HeuristicPojoProcessor();
        processor.setInterfaceVisitorExtensionPoint(introspector);
    }

    private <T> void visitEnd(Class<T> clazz, JavaImplementationDefinition type) throws ProcessingException {
        for (Constructor<T> constructor : clazz.getConstructors()) {
            visitConstructor(constructor, type);
        }
        processor.visitEnd(clazz, type);
    }

    /**
     * Verifies a single service interface is computed when only one interface
     * is implemented
     */
    public void testSingleInterface() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<SingleInterfaceImpl> ctor = SingleInterfaceImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<SingleInterfaceImpl>(ctor));
        processor.visitEnd(SingleInterfaceImpl.class, type);
        assertEquals(1, type.getServices().size());
        assertTrue(ModelHelper.matches(ModelHelper.getService(type, PropertyInterface.class.getSimpleName()),
                                       PropertyInterface.class));
        assertTrue(type.getProperties().isEmpty());
        assertTrue(type.getReferences().isEmpty());
    }

    /**
     * Verifies property and reference setters are computed
     */
    public void testPropertyReference() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<SingleInterfaceWithPropertyReferenceImpl> ctor = SingleInterfaceWithPropertyReferenceImpl.class
            .getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<SingleInterfaceWithPropertyReferenceImpl>(ctor));
        processor.visitEnd(SingleInterfaceWithPropertyReferenceImpl.class, type);
        assertEquals(1, type.getServices().size());
        assertTrue(ModelHelper
            .matches(ModelHelper.getService(type, Interface1.class.getSimpleName()), Interface1.class));
        assertEquals(1, type.getProperties().size());
        org.apache.tuscany.assembly.Property prop = ModelHelper.getProperty(type, "property");
        assertNotNull(prop);
        assertEquals(ComplexProperty.class, type.getPropertyMembers().get("property").getType());
        assertEquals(1, type.getReferences().size());
        assertTrue(ModelHelper.matches(ModelHelper.getReference(type, "reference"), Ref.class));
    }

    /**
     * Verifies that a property setter is not introspected if an analogous
     * operation is in the service interface
     */
    public void testPropertySetterInInterface() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<SingleInterfaceImpl> ctor = SingleInterfaceImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<SingleInterfaceImpl>(ctor));
        processor.visitEnd(SingleInterfaceImpl.class, type);
        assertEquals(0, type.getProperties().size());
    }

    /**
     * Verifies that a reference setter is not introspected if an analogous
     * operation is in the service interface
     */
    public void testReferenceSetterInInterface() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<RefInterfaceImpl> ctor = RefInterfaceImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<RefInterfaceImpl>(ctor));
        processor.visitEnd(RefInterfaceImpl.class, type);
        assertEquals(0, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as
     * references according to spec rules
     */
    public void testReferenceCollectionType() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<ReferenceCollectionImpl> ctor = ReferenceCollectionImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<ReferenceCollectionImpl>(ctor));
        processor.visitEnd(ReferenceCollectionImpl.class, type);
        assertEquals(1, type.getProperties().size());
        assertEquals(3, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as
     * properties according to spec rules
     */
    public void testPropertyCollectionType() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<PropertyCollectionImpl> ctor = PropertyCollectionImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<PropertyCollectionImpl>(ctor));
        processor.visitEnd(PropertyCollectionImpl.class, type);
        assertEquals(0, type.getReferences().size());
        assertEquals(4, type.getProperties().size());
    }

    /**
     * Verifies references are calculated when the type marked with is
     * 
     * @Remotable
     */
    public void testRemotableRef() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<RemotableRefImpl> ctor = RemotableRefImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<RemotableRefImpl>(ctor));
        processor.visitEnd(RemotableRefImpl.class, type);
        assertEquals(2, type.getReferences().size());
        assertEquals(0, type.getProperties().size());
    }

    public void testParentInterface() throws ProcessingException, NoSuchMethodException {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<Child> ctor = Child.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<Child>(ctor));
        processor.visitEnd(Child.class, type);
        assertNotNull(ModelHelper.getService(type, Interface1.class.getSimpleName()));
    }

    /**
     * Verifies a service inteface is calculated when only props and refs are
     * given
     */
    public void testExcludedPropertyAndReference() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        org.apache.tuscany.assembly.Reference ref = factory.createReference();
        ref.setName("reference");
        type.getReferences().add(ref);
        type.getReferenceMembers().put("reference", new JavaElement("reference", Ref.class, null));
        org.apache.tuscany.assembly.Reference ref2 = factory.createReference();
        ref2.setName("reference2");
        type.getReferences().add(ref2);
        type.getReferenceMembers().put("reference2", new JavaElement("reference2", Ref.class, null));
        org.apache.tuscany.assembly.Property prop1 = factory.createProperty();
        prop1.setName("string1");
        type.getProperties().add(prop1);
        type.getPropertyMembers().put("string1", new JavaElement("string1", String.class, null));
        org.apache.tuscany.assembly.Property prop2 = factory.createProperty();
        prop2.setName("string2");
        type.getProperties().add(prop2);
        type.getPropertyMembers().put("string2", new JavaElement("string2", String.class, null));
        visitEnd(MockService.class, type);
        assertEquals(1, type.getServices().size());
    }

    public void testProtectedRemotableRefField() throws ProcessingException, NoSuchMethodException {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<ProtectedRemotableRefFieldImpl> ctor = ProtectedRemotableRefFieldImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<ProtectedRemotableRefFieldImpl>(ctor));
        processor.visitEnd(ProtectedRemotableRefFieldImpl.class, type);
        assertNotNull(ModelHelper.getReference(type, "otherRef"));
    }

    public void testProtectedRemotableRefMethod() throws ProcessingException, NoSuchMethodException {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<ProtectedRemotableRefMethodImpl> ctor = ProtectedRemotableRefMethodImpl.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<ProtectedRemotableRefMethodImpl>(ctor));
        processor.visitEnd(ProtectedRemotableRefMethodImpl.class, type);
        assertNotNull(ModelHelper.getReference(type, "otherRef"));
    }

    public void testSetDataTypes() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<PropertyIntTypeOnConstructor> ctor = PropertyIntTypeOnConstructor.class.getConstructor();
        type.setConstructorDefinition(new ConstructorDefinition<PropertyIntTypeOnConstructor>(ctor));
        processor.visitEnd(PropertyIntTypeOnConstructor.class, type);
        org.apache.tuscany.assembly.Property foo = ModelHelper.getProperty(type, "foo");
        assertEquals(int.class, type.getPropertyMembers().get("foo").getType());
        assertEquals(new QName(JavaXMLMapper.URI_2001_SCHEMA_XSD, "int"), foo.getXSDType());
    }

    private static class PropertyIntTypeOnConstructor {
        protected int foo;

        public PropertyIntTypeOnConstructor() {
        }

        public int getFoo() {
            return foo;
        }
    }

    private interface PropertyInterface {
        void setString1(String val);
    }

    private interface Interface1 {
    }

    private static class Parent implements Interface1 {

    }

    private static class Child extends Parent {
        public Child() {
        }

    }

    private static class SingleInterfaceImpl implements PropertyInterface {
        public SingleInterfaceImpl() {
        }

        public void setString1(String val) {
        }

    }

    private interface HeuristicServiceInterface {
        void fooOperation(String ref);

        void setInvalid1(); // No parameter

        void setInvalid2(String str, int i); // More than one parameter

        String setInvalid3(String str); // return should be void
    }

    public static class MockService implements PropertyInterface, RefInterface, HeuristicServiceInterface {

        @Property
        public void setString1(String val) {
        }

        @Property
        public void setString2(String val) {
        }

        @Reference
        public void setReference(Ref ref) {
        }

        @Reference
        public void setReference2(Ref ref) {
        }

        public void fooOperation(String ref) {

        }

        public void setInvalid1() {
        }

        public void setInvalid2(String str, int i) {
        }

        public String setInvalid3(String str) {
            return null;
        }

    }

    @Service
    private interface Ref {
    }

    private class ComplexProperty {
    }

    private interface RefInterface {
        void setReference(Ref ref);
    }

    private static class RefInterfaceImpl implements RefInterface {
        public RefInterfaceImpl() {
        }

        public void setReference(Ref ref) {
        }
    }

    private static class SingleInterfaceWithPropertyReferenceImpl implements Interface1 {
        public SingleInterfaceWithPropertyReferenceImpl() {
        }

        public void setReference(Ref ref) {
        }

        public void setProperty(ComplexProperty prop) {
        }
    }

    private static class ReferenceCollectionImpl implements Interface1 {
        public ReferenceCollectionImpl() {
        }

        public void setCollectionReference(Collection<Ref> ref) {
        }

        public void setNonGenericCollectionReference(Collection ref) {
            // [rfeng] By the SCA spec, this should be classified as property
        }

        public void setListReference(List<Ref> ref) {
        }

        public void setArrayReference(Ref[] ref) {
        }
    }

    private static class PropertyCollectionImpl implements Interface1 {
        public PropertyCollectionImpl() {
        }

        public void setCollectionProperty(Collection<ComplexProperty> prop) {
        }

        public void setCollectionProperty2(Collection<String> prop) {
        }

        public void setArrayProperty(ComplexProperty[] prop) {
        }

        public void setArrayProperty2(String[] prop) {
        }
    }

    @Remotable
    private interface RemotableRef {
    }

    private static class RemotableRefImpl implements Interface1 {
        protected RemotableRef otherRef;

        public RemotableRefImpl() {
        }

        public void setRef(RemotableRef ref) {

        }
    }

    private static class ProtectedRemotableRefFieldImpl implements Interface1 {
        protected RemotableRef otherRef;

        public ProtectedRemotableRefFieldImpl() {
        }

        public ProtectedRemotableRefFieldImpl(RemotableRef otherRef) {
            this.otherRef = otherRef;
        }

    }

    private static class ProtectedRemotableRefMethodImpl implements Interface1 {
        public ProtectedRemotableRefMethodImpl() {
        }

        protected void setOtherRef(RemotableRef otherRef) {
        }

    }

}
