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
import org.osoa.sca.model.Subsystem;

import org.apache.tuscany.model.assembly.sdo.AssemblyFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Subsystem</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class SubsystemTest extends TestCase {
    /**
     * The fixture for this Subsystem test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected Subsystem fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(SubsystemTest.class);
    }

    /**
     * Constructs a new Subsystem test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public SubsystemTest(String name) {
        super(name);
    }

    /**
     * Sets the fixture for this Subsystem test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected void setFixture(Subsystem fixture) {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Subsystem test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    private Subsystem getFixture() {
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
        setFixture(AssemblyFactory.eINSTANCE.createSubsystem());
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

} //SubsystemTest
