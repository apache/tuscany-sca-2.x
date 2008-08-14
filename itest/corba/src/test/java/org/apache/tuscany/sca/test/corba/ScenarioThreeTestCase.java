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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.host.corba.jse.DefaultCorbaHost;
import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.test.corba.types.TScenarioThree;
import org.apache.tuscany.sca.test.corba.types.TScenarioThreeComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 * Tests various mapping scenarios.
 */
public class ScenarioThreeTestCase {

    // note that those values are also used in resources/*.composite file
    private static int ORB_INITIAL_PORT = 5060;
    private static SCADomain domain;
    private static TransientNameServer server;

    /**
     * Sets up name service, creates and registers traditional CORBA service,
     * obtains SCADomain
     */
    @BeforeClass
    public static void setUp() {
        TestCorbaHost.setCorbaHost(new DefaultCorbaHost());
        try {
            try {
                server =
                    new TransientNameServer("localhost", ORB_INITIAL_PORT, TransientNameService.DEFAULT_SERVICE_NAME);
                Thread t = server.start();
                if (t == null) {
                    Assert.fail("The naming server cannot be started");
                }
            } catch (Throwable e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
            // obtain domain
            domain = SCADomain.newInstance("ScenarioThree.composite");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kills previously spawned name service.
     */
    @AfterClass
    public static void tearDown() {
        server.stop();
    }

    /**
     * Tests mapping for getters and setters
     */
    @Test
    public void test_getterSetter() {
        try {
            TScenarioThree ref =
                domain.getService(TScenarioThreeComponent.class, "ScenarioThreeReference").getScenarioThree();
            ref.getIntField();
            ref.setIntField(1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests mapping the same operation names but with different cases
     */
    @Test
    public void test_nameCase() {
        try {
            TScenarioThree ref =
                domain.getService(TScenarioThreeComponent.class, "ScenarioThreeReference").getScenarioThree();
            assertEquals(0, ref.caseDifferent());
            assertEquals(1, ref.CaseDifferent());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests mappings for operations with overloaded names
     */
    @Test
    public void test_overloadedNames() {
        try {
            TScenarioThree ref =
                domain.getService(TScenarioThreeComponent.class, "ScenarioThreeReference").getScenarioThree();
            ref.overloadedName();
            ref.overloadedName("");
            ref.overloadedName("", 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
