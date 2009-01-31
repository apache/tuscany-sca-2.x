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
import org.apache.tuscany.sca.test.corba.generated.ScenarioTwo;
import org.apache.tuscany.sca.test.corba.generated.ScenarioTwoHelper;
import org.apache.tuscany.sca.test.corba.types.ScenarioTwoServant;
import org.apache.tuscany.sca.test.corba.types.TScenarioTwo;
import org.apache.tuscany.sca.test.corba.types.TScenarioTwoComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

/**
 * @version $Rev$ $Date$
 * Tests attribute get/set mapping using cooperation between traditional CORBA
 * and Tuscany CORBA
 */
public class ScenarioTwoTestCase {

    // note that those values are also used in resources/*.composite file
    private static int ORB_INITIAL_PORT = 5060;

    private static SCADomain domain;

    private static TransientNameServer server;
    private static ORB orb;
    private static String TUSCANY_SERVICE_NAME = "ScenarioTwo";
    private static String GENERATED_SERVICE_NAME = "ScenarioTwoGenerated";

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
                orb = server.getORB();
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContext ncRef = NamingContextHelper.narrow(objRef);
                NameComponent nc = new NameComponent(GENERATED_SERVICE_NAME, "");
                NameComponent path[] = {nc};
                ScenarioTwo scenarioTwo = new ScenarioTwoServant();
                ncRef.rebind(path, scenarioTwo);
            } catch (Throwable e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
            // obtain domain
            domain = SCADomain.newInstance("ScenarioTwo.composite");
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
     * Tests using objects attribute (which is server in traditional way) by
     * Tuscany CORBA binding
     */
    @Test
    public void test_tuscanyGetSetAttribute() {
        try {
            TScenarioTwo ref = domain.getService(TScenarioTwoComponent.class, "ScenarioTwo").getScenarioTwo();
            String strVal = "Whatever";
            ref.setStringField(strVal);
            assertEquals(strVal, ref.getStringField());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests using objects attribute (which is served by Tuscany) in traditional
     * way (by idlj generated code)
     */
    @Test
    public void test_getneratedGetSetAttribute() {
        try {
            orb = server.getORB();
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(TUSCANY_SERVICE_NAME, "");
            NameComponent path[] = {nc};
            ScenarioTwo st = ScenarioTwoHelper.narrow(ncRef.resolve(path));
            st.stringField("");
            assertEquals("", st.stringField());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
