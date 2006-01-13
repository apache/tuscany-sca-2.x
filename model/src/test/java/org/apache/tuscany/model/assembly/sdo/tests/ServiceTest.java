/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.sdo.tests;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.osoa.sca.model.Service;

import org.apache.tuscany.model.assembly.sdo.AssemblyFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Service</b></em>'.
 * <!-- end-user-doc -->
 * <p/>
 * The following features are tested:
 * <ul>
 * <li>{@link org.apache.tuscany.model.assembly.sdo.Service#getInterface() <em>Interface</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ServiceTest extends TestCase {
    /**
     * The fixture for this Service test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected Service fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(ServiceTest.class);
    }

    /**
     * Constructs a new Service test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public ServiceTest(String name) {
        super(name);
    }

    /**
     * Sets the fixture for this Service test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected void setFixture(Service fixture) {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Service test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private Service getFixture() {
        return fixture;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        setFixture(AssemblyFactory.eINSTANCE.createService());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        setFixture(null);
    }

    /**
     * Tests the '{@link org.apache.tuscany.model.assembly.sdo.Service#getInterface() <em>Interface</em>}' feature getter.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @see org.apache.tuscany.model.assembly.sdo.Service#getInterface()
     */
    public void testGetInterface() {
        // TODO: implement this feature getter test method
        // Ensure that you remove @generated or mark it @generated NOT
        fail();
    }

    /**
     * Tests the '{@link org.apache.tuscany.model.assembly.sdo.Service#setInterface(org.apache.tuscany.model.assembly.sdo.Interface) <em>Interface</em>}' feature setter.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.apache.tuscany.model.assembly.sdo.Service#setInterface(org.apache.tuscany.model.assembly.sdo.Interface)
     * @generated
     */
    public void testSetInterface() {
        // TODO: implement this feature setter test method
        // Ensure that you remove @generated or mark it @generated NOT
        fail();
	}

} //ServiceTest
