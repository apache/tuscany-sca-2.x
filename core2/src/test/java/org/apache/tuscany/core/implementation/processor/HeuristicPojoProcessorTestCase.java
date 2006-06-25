package org.apache.tuscany.core.implementation.processor;

import java.util.Collection;
import java.util.List;

import org.osoa.sca.annotations.Service;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;

/**
 * Verfies component type information is properly introspected from an unadorned POJO according to the SCA Java Client
 * and Implementation Model Specification
 *
 * @version $Rev$ $Date$
 */
public class HeuristicPojoProcessorTestCase extends TestCase {

    private HeuristicPojoProcessor processor = new HeuristicPojoProcessor();

    /**
     * Verifies a single service interface is computed when only one interface is implemented
     */
    public void testSingleInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(SingleInterfaceImpl.class, type, null);
        assertEquals(1, type.getServices().size());
        assertEquals(PropertyInterface.class,
            type.getServices().get("HeuristicPojoProcessorTestCase$PropertyInterface")
                .getServiceContract().getInterfaceClass());
        assertTrue(type.getProperties().isEmpty());
        assertTrue(type.getReferences().isEmpty());
    }

    /**
     * Verifies property and reference setters are computed
     */
    public void testPropertyReference() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(SingleInterfaceWithPropertyReferenceImpl.class, type, null);
        assertEquals(1, type.getServices().size());
        assertEquals(Interface1.class,
            type.getServices().get("HeuristicPojoProcessorTestCase$Interface1")
                .getServiceContract().getInterfaceClass());
        assertEquals(1, type.getProperties().size());
        assertEquals(ComplexProperty.class, type.getProperties().get("property").getJavaType());
        assertEquals(1, type.getReferences().size());
        assertEquals(Ref.class, type.getReferences().get("reference").getServiceContract().getInterfaceClass());
    }

    /**
     * Verifies that a property setter is not introspected if an analogous operation is in the service interface
     */
    public void testPropertySetterInInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(SingleInterfaceImpl.class, type, null);
        assertEquals(0, type.getProperties().size());
    }

    /**
     * Verifies that a reference setter is not introspected if an analogous operation is in the service interface
     */
    public void testReferenceSetterInInterface() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(RefInterfaceImpl.class, type, null);
        assertEquals(0, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as references according to spec rules
     */
    public void testReferenceCollectionType() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(ReferenceCollectionImpl.class, type, null);
        assertEquals(0, type.getProperties().size());
        assertEquals(4, type.getReferences().size());
    }

    /**
     * Verifies collection generic types or array types are introspected as properties according to spec rules
     */
    public void testPropertyCollectionType() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        processor.visitEnd(PropertyCollectionImpl.class, type, null);
        assertEquals(0, type.getReferences().size());
        assertEquals(4, type.getProperties().size());
    }


    private interface PropertyInterface {
        void setString1(String val);
    }

    private interface Interface1 {
    }

    private class SingleInterfaceImpl implements PropertyInterface {
        public void setString1(String val) {
        }
    }

    private class ServiceImpl implements PropertyInterface, Interface1 {
        public void setString1(String val) {
        }

        public void setString2(String val) {
        }

        public void setProperty(String val) {
        }

        public void setReference(Ref ref) {
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

    private class RefInterfaceImpl implements RefInterface {
        public void setReference(Ref ref) {
        }
    }

    private class SingleInterfaceWithPropertyReferenceImpl implements Interface1 {
        public void setReference(Ref ref) {
        }

        public void setProperty(ComplexProperty prop) {
        }
    }

    private class ReferenceCollectionImpl implements Interface1 {
        public void setCollectionReference(Collection<Ref> ref) {
        }

        public void setNonGenericCollectionReference(Collection ref) {
        }

        public void setListReference(List<Ref> ref) {
        }

        public void setArrayReference(Ref[] ref) {
        }
    }

    private class PropertyCollectionImpl implements Interface1 {
        public void setCollectionProperty(Collection<ComplexProperty> prop) {
        }

        public void setCollectionProperty2(Collection<String> prop) {
        }

        public void setArrayProperty(ComplexProperty[] prop) {
        }

        public void setArrayProperty2(String[] prop) {
        }
    }

//    public void test() throws Exception{
//        Method m = ReferenceCollectionImpl.class.getMethod("setCollectionReference", Collection.class);
//        Method m2 = ReferenceCollectionImpl.class.getMethod("setNonGenericCollectionReference", Collection.class);
//        Type[] type = m.getGenericParameterTypes();
//        Type[] type2 = m2.getGenericParameterTypes();
//        Type t = ((ParameterizedType)type[0]).getRawType();
//        Type t2 = ((ParameterizedType)type[0]).getActualTypeArguments()[0];
//        System.out.println("");
//    }
//
}
