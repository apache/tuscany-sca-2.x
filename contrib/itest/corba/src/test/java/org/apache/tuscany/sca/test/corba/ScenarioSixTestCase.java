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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.apache.tuscany.sca.host.corba.jse.DefaultCorbaHost;
import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct;
import org.apache.tuscany.sca.test.corba.generated.InnerUnion;
import org.apache.tuscany.sca.test.corba.generated.RichUnion;
import org.apache.tuscany.sca.test.corba.generated.ScenarioSix;
import org.apache.tuscany.sca.test.corba.generated.ScenarioSixHelper;
import org.apache.tuscany.sca.test.corba.types.ScenarioSixServant;
import org.apache.tuscany.sca.test.corba.types.TAnnotatedStruct;
import org.apache.tuscany.sca.test.corba.types.TInnerUnion;
import org.apache.tuscany.sca.test.corba.types.TRichUnion;
import org.apache.tuscany.sca.test.corba.types.TScenarioSix;
import org.apache.tuscany.sca.test.corba.types.TScenarioSixComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

/**
 * Tests using enhanced Java interfaces (annotations)
 * 
 * @version $Rev$ $Date$
 */
public class ScenarioSixTestCase {

    // note that those values are also used in resources/*.composite file
    private static int ORB_INITIAL_PORT = 5050;
    private static String SERVICE_NAME = "ScenarioSix";
    private static String TUSCANY_SERVICE_NAME = "ScenarioSixTuscany";

    private static SCADomain domain;

    private static TransientNameServer server;
    private static ORB orb;

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
            } catch (Throwable e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(SERVICE_NAME, "");
            NameComponent path[] = {nc};
            ScenarioSix scenarioSix = new ScenarioSixServant();
            ncRef.rebind(path, scenarioSix);
            // obtain domain
            domain = SCADomain.newInstance("ScenarioSix.composite");
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

    private boolean areArraysEqual(String[][] arr1, String[][] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                if (!arr1[i][j].equals(arr2[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    private String[][] getStringArray() {
        String[][] result = { {"Hello", "World"}, {"Hi", "Again"}};
        return result;
    }

    /**
     * Tests passing arrays. Tuscany acts as a client, servant object is served
     * in a traditional way
     */
    @Test
    public void test_arraysPassing_tuscanyAsClient() {
        try {
            TScenarioSix ref = domain.getService(TScenarioSixComponent.class, "ScenarioSix").getScenarioSix();
            String[][] arrayArg = getStringArray();
            String[][] arrayRes = ref.passStringArray(arrayArg);
            assertTrue(areArraysEqual(arrayArg, arrayRes));
            TAnnotatedStruct structArg = new TAnnotatedStruct();
            structArg.stringArray = getStringArray();
            TAnnotatedStruct structRes = ref.passAnnotatedStruct(structArg);
            assertTrue(areArraysEqual(structArg.stringArray, structRes.stringArray));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests passing arrays. Servant object is served by Tuscany and it is
     * accessed by traditional Corba client
     */
    @Test
    public void test_arraysPassing_tuscanyAsService() {
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(TUSCANY_SERVICE_NAME, "");
            NameComponent path[] = {nc};
            ScenarioSix ref = ScenarioSixHelper.narrow(ncRef.resolve(path));
            String[][] stringArg = getStringArray();
            String[][] stringRes = ref.passStringArray(stringArg);
            assertTrue(areArraysEqual(stringArg, stringRes));
            AnnotatedStruct structArg = new AnnotatedStruct();
            structArg.stringArray = getStringArray();
            AnnotatedStruct structRes = ref.passAnnotatedStruct(structArg);
            assertTrue(areArraysEqual(structArg.stringArray, structRes.stringArray));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests passing unions. Tuscany acts as a client, servant object is served
     * in a traditional way
     */
    @Test
    public void test_unionsPassing_tuscanyAsClient() {
        try {
            TScenarioSix ref = domain.getService(TScenarioSixComponent.class, "ScenarioSix").getScenarioSix();
            TRichUnion arg = new TRichUnion();
            TInnerUnion inner = new TInnerUnion();
            inner.setY(10f);
            arg.setIu(inner);
            TRichUnion result = ref.passRichUnion(arg);
            assertEquals(arg.getIu().getY(), result.getIu().getY(), 0.0f);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        try {
            TScenarioSix ref = domain.getService(TScenarioSixComponent.class, "ScenarioSix").getScenarioSix();
            TRichUnion arg = new TRichUnion();
            arg.setY(15f);
            TRichUnion result = ref.passRichUnion(arg);
            assertEquals(arg.getY(), result.getY(), 0.0f);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * Tests passing unions. Servant object is served by Tuscany and it is
     * accessed by traditional Corba client
     */
    @Test
    public void test_unionsPassing_tuscanyAsService() {
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(TUSCANY_SERVICE_NAME, "");
            NameComponent path[] = {nc};
            ScenarioSix ref = ScenarioSixHelper.narrow(ncRef.resolve(path));
            RichUnion arg = new RichUnion();
            InnerUnion inner = new InnerUnion();
            inner.y(20f);
            arg.iu(inner);
            RichUnion result = ref.passRichUnion(arg);
            assertEquals(arg.iu().y(), result.iu().y(), 0.0f);
            arg = new RichUnion();
            arg.y(15f);
            result = ref.passRichUnion(arg);
            assertEquals(arg.y(), result.y(), 0.0f);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
