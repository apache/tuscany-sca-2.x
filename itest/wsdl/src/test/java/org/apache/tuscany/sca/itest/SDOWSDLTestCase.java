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

package org.apache.tuscany.sca.itest;

import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCADomain;

import bigbank.account.services.accountdata.AccountDataService;

import com.bigbank.account.AccountFactory;
import com.bigbank.account.CustomerProfileData;

/**
 * Tests all the combinations of wiring services, components, and references
 * which use either interface.java or interface.wsdl.
 * 
 * The tests use a service (1) wired to a components (2) wired to another 
 * component (3) wired to a reference (4). Each of those uses either 
 * interface.java (a) or interface.wsdl (b). This results in 16 different
 * combinations 1a2a3a4a thru 1b2b3b4b.
 */
public class SDOWSDLTestCase extends TestCase {

    private SCADomain domain;
    
    public void testClient1a2a3a4a() throws RemoteException  {
        
        doit("Client1a2a3a4a");
        doit("Client1a2a3a4b");
        doit("Client1a2a3b4a");
        doit("Client1a2a3b4b");
        doit("Client1a2b3a4a");
        doit("Client1a2b3a4b");
        doit("Client1a2b3b4a");
        doit("Client1a2b3b4b");
        doit("Client1b2a3a4a");
        doit("Client1b2a3a4b");
        doit("Client1b2a3b4a");
        doit("Client1b2a3b4b");

// TODO: TUSCANY-1135, service using wsdl wired to component using wsdl fails  
//        doit("Client1b2b3a4a");
//        doit("Client1b2b3a4b");
//        doit("Client1b2b3b4a");
//        doit("Client1b2b3b4b");
    }

    private void doit(String compName) throws RemoteException {
        AccountDataService client = domain.getService(AccountDataService.class, compName);
        CustomerProfileData dataIn = AccountFactory.INSTANCE.createCustomerProfileData();
        dataIn.setAddress("home");
        dataIn.setEmail("petra@home");
        dataIn.setFirstName("petra");
        dataIn.setId(1);
        dataIn.setLastName("A");
        dataIn.setLoginID("petra");
        dataIn.setPassword("ant");
        
        CustomerProfileData dataOut = client.createAccount(dataIn , false, false);
        
        assertEquals(dataIn.getAddress(), dataOut.getAddress());
        assertEquals(dataIn.getEmail(), dataOut.getEmail());
        assertEquals(dataIn.getFirstName(), dataOut.getFirstName());
        assertEquals(dataIn.getId(), dataOut.getId());
        assertEquals(dataIn.getLastName(), dataOut.getLastName());
        assertEquals(dataIn.getLoginID(), dataOut.getLoginID());
        assertEquals(dataIn.getPassword(), dataOut.getPassword());
    }

    protected void setUp() throws Exception {
    	domain = SCADomain.newInstance("SDOWSDLTest.composite");
    }

    protected void tearDown() throws Exception {
    	domain.close();
    }

}
