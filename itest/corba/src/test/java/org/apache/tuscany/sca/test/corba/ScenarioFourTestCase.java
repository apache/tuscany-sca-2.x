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

package org.apache.tuscany.sca.test.corba;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.test.corba.scenariofour.ScenarioFourFactory;
import org.apache.tuscany.sca.test.corba.scenariofour.ScenarioFourSdo;
import org.apache.tuscany.sca.test.corba.types.ScenarioFour;
import org.apache.tuscany.sca.test.corba.types.ScenarioFourComponent;
import org.apache.tuscany.sca.test.corba.types.ScenarioFourException;
import org.apache.tuscany.sca.test.corba.types.ScenarioFourStruct;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests SCA default binding over CORBA binding
 */
public class ScenarioFourTestCase {

    // note that those values are also used in resources/*.composite file
    private static SCADomain domain;
    private static ScenarioFourComponent scenarioFourComponent;
    private static ScenarioFour scenarioFour;

    /**
     * Initial configuration
     */
    @BeforeClass
    public static void setUp() {
        try {
            // obtain domain
            domain = SCADomain.newInstance("ScenarioFour.composite");
            scenarioFourComponent = domain.getService(ScenarioFourComponent.class, "ScenarioFour");
            scenarioFour = scenarioFourComponent.getScenarioFour();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test cleanup
     */
    @AfterClass
    public static void tearDown() {
        // do nothing
    }

    /**
     * General tests for passing JAXB objects
     */
    @Test
    public void test_generalJAXB() {
        try {
            ScenarioFourStruct input = new ScenarioFourStruct();
            input.field1 = "Test";
            input.field2 = 1;
            input.field3 = new double[1];
            ScenarioFourStruct output = scenarioFour.setStruct(input);
            assertTrue(input.equals(output));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    /**
     * Test for JAXB exceptions
     */
    @Test
    public void test_exceptionsJAXB() {
        try {
            scenarioFour.exceptionTest();
            fail();
        } catch (ScenarioFourException e) {
            assertTrue(ScenarioFourException.DEFAULT_CONTENT.equals(e.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * General test for passing SDO objects
     */
    @Test
    public void test_generalSDO() {
        try {
            ScenarioFourSdo scenarioFourSdo = ScenarioFourFactory.INSTANCE.createScenarioFourSdo();
            scenarioFourSdo.setMessage("Test1");
            scenarioFourSdo.setSymbol("Test2");
            ScenarioFourSdo result = scenarioFour.passScenarioFourStruct(scenarioFourSdo);
            assertTrue(scenarioFourSdo.getMessage().equals(result.getMessage()) && scenarioFourSdo.getSymbol().equals(result.getSymbol()));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * Tests reusing local name server with multiple bindings 
     */
    @Test
    public void test_nameServerReuse() {
        try {
            ScenarioFour scenarioFour = domain.getService(ScenarioFourComponent.class, "ScenarioFourReuse").getScenarioFour();
            ScenarioFourStruct struct = new ScenarioFourStruct();
            scenarioFour.setStruct(struct);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
