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
package supplychain;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;


import supplychain.customer.Customer;

/**
 * This client program shows how to create an SCA runtime, start it, locate a simple HelloWorld service component and
 * invoke it.
 */
public class SupplyChainClientTestCase extends TestCase {

	private SCADomain scaDomain;
    private Customer customer;

    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("supplychain.composite");
        customer = scaDomain.getService(Customer.class, "CustomerComponent");
    }
    
    protected void tearDown() throws Exception {
        scaDomain.close();
    }


    public void test() throws Exception {
    	
    	
        System.out.println("In SupplyChainClientTestCase.test: Calling customer.purchaseGoods, customer is " + customer);
        
        customer.purchaseGoods();
        
        System.out.println("Sleeping ...");
        Thread.sleep(2000);
        System.out.println("Test complete");
        
    }
    
    
}
