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
import org.osoa.sca.model.PropertyValues;

import org.apache.tuscany.model.assembly.sdo.AssemblyFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Property Values</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class PropertyValuesTest extends TestCase {
    /**
     * The fixture for this Property Values test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected PropertyValues fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(PropertyValuesTest.class);
    }

    /**
     * Constructs a new Property Values test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public PropertyValuesTest(String name) {
        super(name);
    }

    /**
     * Sets the fixture for this Property Values test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected void setFixture(PropertyValues fixture) {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Property Values test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private PropertyValues getFixture() {
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
        setFixture(AssemblyFactory.eINSTANCE.createPropertyValues());
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

} //PropertyValuesTest
