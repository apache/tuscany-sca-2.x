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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

/**
 * Verifies component type information is properly introspected from an unadorned
 * POJO according to the SCA Java Client and Implementation Model Specification
 * 
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessorTestCase extends AbstractProcessorTest {

    private org.apache.tuscany.sca.implementation.java.introspect.impl.HeuristicPojoProcessor processor;
    private JavaImplementationFactory javaImplementationFactory;

    public HeuristicPojoProcessorTestCase() {
        processor = new HeuristicPojoProcessor(new DefaultAssemblyFactory(), new DefaultJavaInterfaceFactory());
        javaImplementationFactory = new DefaultJavaImplementationFactory();
    }

    private <T> void visitEnd(Class<T> clazz, JavaImplementation type) throws IntrospectionException {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            visitConstructor(constructor, type);
        }
        processor.visitEnd(clazz, type);
    }

    /**
     * Verifies a single service interface is computed when only one interface
     * is implemented
     */
    public void testSingleInterface() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<SingleInterfaceImpl> ctor = SingleInterfaceImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<SingleInterfaceImpl>(ctor));
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
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<SingleInterfaceWithPropertyReferenceImpl> ctor = SingleInterfaceWithPropertyReferenceImpl.class
            .getConstructor();
        type.setConstructor(new JavaConstructorImpl<SingleInterfaceWithPropertyReferenceImpl>(ctor));
        processor.visitEnd(SingleInterfaceWithPropertyReferenceImpl.class, type);
        assertEquals(1, type.getServices().size());
        assertTrue(ModelHelper
            .matches(ModelHelper.getService(type, Interface1.class.getSimpleName()), Interface1.class));
        assertEquals(1, type.getProperties().size());
        org.apache.tuscany.sca.assembly.Property prop = ModelHelper.getProperty(type, "property");
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
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<SingleInterfaceImpl> ctor = SingleInterfaceImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<SingleInterfaceImpl>(ctor));
        processor.visitEnd(SingleInterfaceImpl.class, type);
        assertEquals(0, type.getProperties().size());
    }

    /**
     * Verifies that a reference setter is not introspected if an analogous
     * operation is in the service interface
     */
    public void testReferenceSetterInInterface() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<RefInterfaceImpl> ctor = RefInterfaceImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<RefInterfaceImpl>(ctor));
        processor.visitEnd(RefInterfaceImpl.class, type);
        assertEquals(0, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as
     * references according to specification rules
     */
    public void testReferenceCollectionType() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<ReferenceCollectionImpl> ctor = ReferenceCollectionImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<ReferenceCollectionImpl>(ctor));
        processor.visitEnd(ReferenceCollectionImpl.class, type);
        assertEquals(1, type.getProperties().size());
        assertEquals(3, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as
     * properties according to specification rules
     */
    public void testPropertyCollectionType() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<PropertyCollectionImpl> ctor = PropertyCollectionImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<PropertyCollectionImpl>(ctor));
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
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<RemotableRefImpl> ctor = RemotableRefImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<RemotableRefImpl>(ctor));
        processor.visitEnd(RemotableRefImpl.class, type);
        assertEquals(2, type.getReferences().size());
        assertEquals(0, type.getProperties().size());
    }

    public void testParentInterface() throws IntrospectionException, NoSuchMethodException {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<Child> ctor = Child.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<Child>(ctor));
        processor.visitEnd(Child.class, type);
        assertNotNull(ModelHelper.getService(type, Interface1.class.getSimpleName()));
    }

    /**
     * Verifies a service interface is calculated when only props and refs are
     * given
     */
    public void testExcludedPropertyAndReference() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        org.apache.tuscany.sca.assembly.Reference ref = factory.createReference();
        ref.setName("reference");
        type.getReferences().add(ref);
        type.getReferenceMembers().put("reference", new JavaElementImpl("reference", Ref.class, null));
        org.apache.tuscany.sca.assembly.Reference ref2 = factory.createReference();
        ref2.setName("reference2");
        type.getReferences().add(ref2);
        type.getReferenceMembers().put("reference2", new JavaElementImpl("reference2", Ref.class, null));
        org.apache.tuscany.sca.assembly.Property prop1 = factory.createProperty();
        prop1.setName("string1");
        type.getProperties().add(prop1);
        type.getPropertyMembers().put("string1", new JavaElementImpl("string1", String.class, null));
        org.apache.tuscany.sca.assembly.Property prop2 = factory.createProperty();
        prop2.setName("string2");
        type.getProperties().add(prop2);
        type.getPropertyMembers().put("string2", new JavaElementImpl("string2", String.class, null));
        visitEnd(MockService.class, type);
        assertEquals(1, type.getServices().size());
    }

    public void testProtectedRemotableRefField() throws IntrospectionException, NoSuchMethodException {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<ProtectedRemotableRefFieldImpl> ctor = ProtectedRemotableRefFieldImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<ProtectedRemotableRefFieldImpl>(ctor));
        processor.visitEnd(ProtectedRemotableRefFieldImpl.class, type);
        assertNotNull(ModelHelper.getReference(type, "otherRef"));
    }

    public void testProtectedRemotableRefMethod() throws IntrospectionException, NoSuchMethodException {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<ProtectedRemotableRefMethodImpl> ctor = ProtectedRemotableRefMethodImpl.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<ProtectedRemotableRefMethodImpl>(ctor));
        processor.visitEnd(ProtectedRemotableRefMethodImpl.class, type);
        assertNotNull(ModelHelper.getReference(type, "otherRef"));
    }

    public void testSetDataTypes() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        Constructor<PropertyIntTypeOnConstructor> ctor = PropertyIntTypeOnConstructor.class.getConstructor();
        type.setConstructor(new JavaConstructorImpl<PropertyIntTypeOnConstructor>(ctor));
        processor.visitEnd(PropertyIntTypeOnConstructor.class, type);
        org.apache.tuscany.sca.assembly.Property foo = ModelHelper.getProperty(type, "foo");
        assertEquals(int.class, type.getPropertyMembers().get("foo").getType());
        assertEquals(new QName(JavaXMLMapper.URI_2001_SCHEMA_XSD, "int"), foo.getXSDType());
    }

    /**
     * Errata for Java Component Implementation Specification v1.0 corrects the algorithm for determining
     * references of an unannotated POJO (section 1.2.7).  This test makes sure that the earlier implementation
     * is corrected as per the errata.  A notable difference is that the interfaces annotated with @Service
     * no longer result in references.
     */
    public void testUpdatedRule() throws Exception {
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        visitEnd(SomeServiceImpl.class, type);
        assertEquals(12, type.getReferenceMembers().size());
        assertTrue(type.getReferenceMembers().containsKey("rri1"));
        assertTrue(type.getReferenceMembers().containsKey("rri2"));
        assertTrue(type.getReferenceMembers().containsKey("rri3"));
        assertTrue(type.getReferenceMembers().containsKey("rri4"));

        assertTrue(type.getReferenceMembers().containsKey("rria1"));
        assertTrue(type.getReferenceMembers().containsKey("rria2"));
        assertTrue(type.getReferenceMembers().containsKey("rria3"));
        assertTrue(type.getReferenceMembers().containsKey("rria4"));

        assertTrue(type.getReferenceMembers().containsKey("rric1"));
        assertTrue(type.getReferenceMembers().containsKey("rric2"));
        assertTrue(type.getReferenceMembers().containsKey("rric3"));
        assertTrue(type.getReferenceMembers().containsKey("rric4"));
        
        assertEquals(16, type.getPropertyMembers().size());
        assertTrue(type.getPropertyMembers().containsKey("pnri1"));
        assertTrue(type.getPropertyMembers().containsKey("pnri2"));
        assertTrue(type.getPropertyMembers().containsKey("pnri3"));
        assertTrue(type.getPropertyMembers().containsKey("pnri4"));

        assertTrue(type.getPropertyMembers().containsKey("pnria1"));
        assertTrue(type.getPropertyMembers().containsKey("pnria2"));
        assertTrue(type.getPropertyMembers().containsKey("pnria3"));
        assertTrue(type.getPropertyMembers().containsKey("pnria4"));

        assertTrue(type.getPropertyMembers().containsKey("pnric1"));
        assertTrue(type.getPropertyMembers().containsKey("pnric2"));
        assertTrue(type.getPropertyMembers().containsKey("pnric3"));
        assertTrue(type.getPropertyMembers().containsKey("pnric4"));
        
        assertTrue(type.getPropertyMembers().containsKey("gen1"));
        assertTrue(type.getPropertyMembers().containsKey("gen2"));
        assertTrue(type.getPropertyMembers().containsKey("gen3"));
        assertTrue(type.getPropertyMembers().containsKey("gen4"));
    }

    /**
     * Interfaces with "@WebService" annotation implemented by the class should result
     * in a Service in the same manner as an "@Remotable" annotation would.
     */
    public void testInterfaceWithWebServiceAnnotation() throws Exception{
        JavaImplementation type = javaImplementationFactory.createJavaImplementation();
        visitEnd(SomeWebServiceImpl.class, type);
        assertEquals(1, type.getServices().size());
        assertEquals("SomeWebService", type.getServices().get(0).getName());
    }
    
    @Remotable
    private interface ReferenceRemotableInterface {
        void operation1(String param1);
    }

    @Service
    private interface PropertyNonRemotableInterface {
        void operation1(String param1);
    }
    
    @Remotable
    private interface SomeService {
        void serviceOperation1();
    }

    private static class SomeServiceImpl implements SomeService {
        
        public SomeServiceImpl() {
        }
        
        // References - interface with @Remotable
        public void setRri1(ReferenceRemotableInterface rri) {
        }
        protected void setRri2(ReferenceRemotableInterface rri) {
        }
        public ReferenceRemotableInterface rri3;
        protected ReferenceRemotableInterface rri4;
        
        // References - array of interface with @Remotable
        public void setRria1(ReferenceRemotableInterface[] rri) {
        }
        protected void setRria2(ReferenceRemotableInterface[] rri) {
        }
        public ReferenceRemotableInterface[] rria3;
        protected ReferenceRemotableInterface[] rria4;

        // References - parametrized Collection of interface with @Remotable
        public void setRric1(Collection<ReferenceRemotableInterface> rri) {
        }
        protected void setRric2(Collection<ReferenceRemotableInterface> rri) {
        }
        public Collection<ReferenceRemotableInterface> rric3;
        protected Collection<ReferenceRemotableInterface> rric4;

        // Properties - interface with @Service and without @Remotable
        public void setPnri1(PropertyNonRemotableInterface arg) {
        }
        protected void setPnri2(PropertyNonRemotableInterface arg) {
        }
        public PropertyNonRemotableInterface pnri3;
        protected PropertyNonRemotableInterface pnri4;

        // Properties - array of interface with @Service and without @Remotable
        public void setPnria1(PropertyNonRemotableInterface[] arg) {
        }
        protected void setPnria2(PropertyNonRemotableInterface[] arg) {
        }
        public PropertyNonRemotableInterface[] pnria3;
        protected PropertyNonRemotableInterface[] pnria4;

        // Properties - parametrized Collection of interface with @Service and without @Remotable
        public void setPnric1(Collection<PropertyNonRemotableInterface> arg) {
        }
        protected void setPnric2(Collection<PropertyNonRemotableInterface> arg) {
        }
        public Collection<PropertyNonRemotableInterface> pnric3;
        protected Collection<PropertyNonRemotableInterface> pnric4;
        
        // Properties - Non-parametrized Collection
        public void setGen1(Collection arg) {
        }
        protected void setGen2(Collection arg) {
        }
        public Collection gen3;
        protected Collection gen4;
        
        public void serviceOperation1() {
        }
    }

    private static class PropertyIntTypeOnConstructor {
        protected int foo;

        public PropertyIntTypeOnConstructor() {
        }

        public int getFoo() {
            return foo;
        }
    }

    @Remotable
    private interface PropertyInterface {
        void setString1(String val);
    }

    @Remotable
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

    @Remotable
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
            // [rfeng] By the SCA specification, this should be classified as property
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

    @WebService
    private interface SomeWebService {
        void serviceOperation1();
    }
    
    @Service
    private static class SomeWebServiceImpl implements SomeWebService {
        public SomeWebServiceImpl() {
            
        }

        public void serviceOperation1() {
        }
    }

}
