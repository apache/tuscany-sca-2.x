/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.config.impl;

import java.util.List;

import junit.framework.TestCase;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.config.processor.ProcessorUtils;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;

/**
 * @version $$Rev$$ $$Date$$
 */
public class CoreAnnotationsProcessingTestCase extends TestCase {

    private ComponentTypeIntrospector introspector;
    private AssemblyFactory factory;

    public void testServiceBasicProcessing() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(TestComponentImpl.class, type);
        assertEquals(1, type.getServices().size());
        ServiceContract contract = type.getServices().get(0).getServiceContract();
        assertEquals(TestComponent.class, contract.getInterface());
        assertEquals(Scope.MODULE, contract.getScope());
    }

    public void testServiceNameSet() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(TestComponentImpl.class, type);
        assertEquals(1, type.getServices().size());
        Service service = type.getServices().get(0);
        assertEquals(JavaIntrospectionHelper.getBaseName(TestComponent.class), service.getName());
    }

    /**
     * Tests the case where a class implements one interface not marked as with <code>Remotable</code>
     */
    public void testSingleServiceProcessing() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(TestLocalComponentImpl.class, type);
        assertEquals(1, type.getServices().size());
        ServiceContract contract = type.getServices().get(0).getServiceContract();
        assertEquals(TestLocalComponent.class, contract.getInterface());
        assertEquals(Scope.MODULE, contract.getScope());
    }

    /**
     * Tests the case where an implementation specifies a service interface of its parent as opposed to the
     * single interface it directly implements
     */
    public void testInteraceHierarchyServiceProcessing() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(SuperFooImpl.class, type);
        assertEquals(1, type.getServices().size());
        ServiceContract contract = type.getServices().get(0).getServiceContract();
        assertEquals(SuperSuperFoo.class, contract.getInterface());
    }

    /**
     * Tests the case where a class implements two interfaces, with one specified using <code>@Service</code>
     * and one marked with <code>@Remotable</code>
     */
    public void testMutlipleServiceProcessing() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(TestMultipleInterfacesComponentImpl.class, type);
        assertEquals(2, type.getServices().size());
        for (Service service : type.getServices()) {
            if (!service.getServiceContract().equals(TestComponent.class) &&
                    service.getServiceContract().equals(TestLocalComponent.class)) {
                fail("Expected multiple interfaces not found");
            }
        }
    }

    /**
     * Test case when an class implements two non-Remotable interfaces and does not specify one with
     * <code>@Service</code>
     */
    public void testNonServiceProcessing() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(TestNonServiceInterfacesImpl.class, type);
        assertEquals(1, type.getServices().size());
        ServiceContract contract = type.getServices().get(0).getServiceContract();
        assertEquals(TestNonServiceInterfacesImpl.class, contract.getInterface());
        assertEquals(Scope.MODULE, contract.getScope());
    }

    /**
     * Tests the case where a class implements two non-Remotable interfaces, with one specified using
     * <code>@Service</code>
     */
    public void testNonServiceSpecifiedProcessing() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(TestNonServiceSpecifiedImpl.class, type);
        assertEquals(1, type.getServices().size());
        ServiceContract contract = type.getServices().get(0).getServiceContract();
        assertEquals(TestNonServiceInterface.class, contract.getInterface());
        assertEquals(Scope.MODULE, contract.getScope());
    }

    /**
     * Tests the case where a component's scope is specified by its superclass
     */
    public void testParentScopeEvaluation() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(ScopeTestComponent.class, type);
        assertEquals(1, type.getServices().size());
        ServiceContract contract = type.getServices().get(0).getServiceContract();
        assertEquals(Scope.MODULE, contract.getScope());
    }

    /**
     * FIXME JFM - temporarily disabled until non-annotated properties are fixed public void
     * testPropertyProcessing() throws Exception { ComponentType type = factory.createComponentType();
     * introspector.introspect(TestComponentImpl.class, type); List<Property>properties =
     * type.getProperties(); assertEquals(3, properties.size()); for (Property property : properties) { if
     * (!property.getName().equals("foo") && !property.getName().equals("fooRequired") &&
     * !property.getName().equals("baz")) { fail("Property names not handled properly"); } if
     * (property.getName().equals("fooRequired")) { assertTrue(property.isRequired()); } else {
     * assertFalse(property.isRequired()); } } } *
     */

    public void testReferenceProcessing() throws Exception {
        ComponentType type = factory.createComponentType();
        introspector.introspect(TestComponentImpl.class, type);
        List<Reference>references = type.getReferences();
        assertEquals(5, references.size());
        for (Reference reference : references) {
            if (reference.getName().equals("setBarRequired")) {
                assertTrue(reference.getMultiplicity() == Multiplicity.ONE_N);
            } else if (reference.getName().equals("setBar")) {
                assertTrue(reference.getMultiplicity() == Multiplicity.ZERO_N);
            } else if (reference.getName().equals("bazRefeference")) {
                assertTrue(reference.getMultiplicity() == Multiplicity.ZERO_ONE);
            } else if (reference.getName().equals("wombat")) {
                assertTrue(reference.getMultiplicity() == Multiplicity.ONE_ONE);
            } else if (reference.getName().equals("bar")) {
                assertTrue(reference.getMultiplicity() == Multiplicity.ZERO_ONE);
            } else {
                fail("Reference names not handled properly");
            }
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        factory = new SystemAssemblyFactoryImpl();
        introspector = ProcessorUtils.createCoreIntrospector(factory);
    }

}
