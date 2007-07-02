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
import util.OSGiTestUtil;

/**
 * OSGi test program - common code for versioned bundles
 */
public abstract class VersionedSupplyChainTestCase extends TestCase {
    
    private String compositeName;
    private SCADomain scaDomain;
    private Customer customer1;
    private Customer customer2;
    

    public VersionedSupplyChainTestCase(String compositeName) {
        super();
        this.compositeName = compositeName;
    }
    
    protected void setUp() throws Exception {
        
        OSGiTestUtil.setUpOSGiTestRutime();

        scaDomain = SCADomain.newInstance(compositeName);
        customer1 = scaDomain.getService(Customer.class, "CustomerComponent1");
        customer2 = scaDomain.getService(Customer.class, "CustomerComponent2");
    }
    
    protected void tearDown() throws Exception {
        scaDomain.close();
        
        OSGiTestUtil.shutdownOSGiRuntime();
    }
   
    public void test() throws Exception {
        
        System.out.println("Main thread " + Thread.currentThread());
        customer1.purchaseBooks();
        customer2.purchaseGames();
        long timeout = 5000L;
        while (timeout > 0) {
            if (customer1.hasOutstandingOrders()) {
                Thread.sleep(100);
                timeout -= 100;
            } else if (customer2.hasOutstandingOrders()) {
                Thread.sleep(100);
                timeout -= 100;
            } else
                break;
        }
        assertFalse(customer1.hasOutstandingOrders());
        assertFalse(customer2.hasOutstandingOrders());

        System.out.println("Test complete");
        
    }
}
