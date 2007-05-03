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

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * This client program shows how to create an SCA runtime, start it,
 * locate a the Customer service component and invoke it.
 */
public class SupplyChainClientTestCase extends TestCase {

    private Customer customer;

    protected void setUp() throws Exception {
    	SCARuntime.start("supplychain.composite");

        ComponentContext context = SCARuntime.getComponentContext("CustomerComponent");
        ServiceReference<Customer> service = context.createSelfReference(Customer.class);
        customer = service.getService();
    }

    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }

    public void test() throws Exception {

        System.out.println("Main thread " + Thread.currentThread());
        customer.purchaseGoods();
        System.out.println("Sleeping ...");
        Thread.sleep(1000);
    }
}
