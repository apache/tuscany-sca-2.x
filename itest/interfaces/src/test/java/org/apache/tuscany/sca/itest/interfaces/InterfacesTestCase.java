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

package org.apache.tuscany.sca.itest.interfaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class InterfacesTestCase {
    private static SCADomain domain;

    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("InterfacesTest.composite");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }

    @Test
    public void testLocalClient() {
        LocalServiceComponent service = domain.getService(LocalServiceComponent.class, "LocalServiceComponent");
        LocalClientComponent local = domain.getService(LocalClientComponent.class, "LocalClientComponent");

        try {
            ParameterObject po = new ParameterObject();
            assertEquals("AComponent", local.foo1(po));
            assertEquals("AComponent", po.field1);

            assertEquals("AAComponent", local.foo1("A"));

            assertEquals("AAComponent1", local.foo2("A", 1));
            assertEquals("AAComponent1", local.foo2(1, "A"));
        } catch (Exception e) {
            fail();
        }

        try {
            // test local callback
            local.callback("CallBack");
            Thread.sleep(100);
            assertEquals("CallBack", local.getCallbackValue());

            local.callModifyParameter();
            Thread.sleep(100);
            assertEquals("AComponent", service.getPO().field1);
        } catch (Exception e) {
            e.printStackTrace();
            fail("CallBack failed");
        }

        try {
            local.onewayMethod("OneWay");
            Thread.sleep(100);
            assertEquals("OneWay", local.getOnewayValue());
        } catch (Exception e) {
            fail("OneWay failed");
        }
    }

    @Test
    public void testRemoteClient() {
        RemoteServiceComponent service = domain.getService(RemoteServiceComponent.class, "RemoteServiceComponent");
        RemoteClientComponent remote = domain.getService(RemoteClientComponent.class, "RemoteClientComponent");

        try {
            // Test Pass By Value
            ParameterObject po = new ParameterObject("NotBComponent");
            assertEquals("BComponent", remote.foo1(po));
            assertEquals("NotBComponent", po.field1);

            assertEquals("BBComponent1", remote.foo2(1, "B"));

            // Test allowsPassByReference
            assertEquals("BComponent", remote.foo3(po));
            assertEquals("BComponent", po.field1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            remote.callback("CallBack");
            Thread.sleep(100);
            assertEquals("CallBack", remote.getCallbackValue());

            remote.callModifyParameter();
            Thread.sleep(100);
            assertEquals("CallBack", service.getPO().field1);
        } catch (Exception e) {
            fail("CallBack failed");
        }

        try {
            remote.onewayMethod("OneWay");
            Thread.sleep(100);
            assertEquals("OneWay", remote.getOnewayValue());
        } catch (Exception e) {
            fail("OneWay failed");
        }
    }

}
