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

package calculator;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.host.corba.naming.TransientNameServer;
import org.apache.tuscany.sca.host.corba.naming.TransientNameService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

/**
 * This shows how to test the Calculator service component.
 */
public class CalculatorCORBAReferenceTestCase extends TestCase {

    private SCADomain scaDomain;
    private CalculatorService calculatorService;
    private TransientNameServer server;

    private void bindObject(String name, org.omg.CORBA.Object object) throws Exception {
        ORB orb = server.getORB();
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContext ncRef = NamingContextHelper.narrow(objRef);
        NameComponent nc = new NameComponent(name, "");
        NameComponent path[] = {nc};
        ncRef.rebind(path, object);
    }
    
    @BeforeClass
    protected void setUp() throws Exception {
        // create name service
        server = new TransientNameServer("localhost", 5080, TransientNameService.DEFAULT_SERVICE_NAME);
        Thread t = server.start();
        if (t == null) {
            Assert.fail("The naming server cannot be started");
        } else {
            // create CORBA object which will be accessible by SCA CORBA binding
            bindObject("CalculatorCORBAService", new CalculatorCORBAServant());
            scaDomain = SCADomain.newInstance("CalculatorCORBAReference.composite");
            calculatorService = scaDomain.getService(CalculatorService.class, "CalculatorServiceComponent");
        }
    }

    @AfterClass
    protected void tearDown() throws Exception {
        scaDomain.close();
        server.stop();
    }

    @Test
    public void testCalculator() throws Exception {
        assertEquals(5.0, calculatorService.add(3, 2));
        assertEquals(1.0, calculatorService.subtract(3, 2));
        assertEquals(6.0, calculatorService.multiply(3, 2));
        assertEquals(1.5, calculatorService.divide(3, 2));
    }
}
