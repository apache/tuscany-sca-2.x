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
package supplychain.factory;

import supplychain.SupplyChainTestCase;

/**
 * OSGi test program - declarative with scopes other than composites which use OSGi service factories
 */
public class FactoryTestCase extends SupplyChainTestCase {
    /**
     * This constant defines the time period (in milliseconds) for which we are prepared to wait for
     * the @OneWay notifyShipment() callback to run. 
     */
    private static final long MAX_WAIT_TIME_FOR_CALLBACK = 10000;

    public FactoryTestCase() {
        super("factory-test.composite", "factory");
    }
    
    protected FactoryTestCase(String compositeName, String contributionLocation) {
        super(compositeName, contributionLocation);
    }
    
    
    @Override
    public void test() throws Exception {
        
        System.out.println("Main thread " + Thread.currentThread());
        customer.purchaseBooks();
        waitForOrderShipmentNotification();                // TUSCANY-2198 notifyShipment() callback is @OneWay 
        assertFalse(customer.hasOutstandingOrders());
        
        customer.purchaseGames();       
        waitForOrderShipmentNotification();                // TUSCANY-2198 notifyShipment() callback is @OneWay 
        assertFalse(customer.hasOutstandingOrders());

        Thread.sleep(2000);
        System.out.println("Test complete");
        
    }

    /**
     * Since the notifyShipment() callback on the Customer is @OneWay, we need to allow
     * some time for it to complete as it is runs asynchronously.
     *
     * This is for TUSCANY-2198
     */
    private void waitForOrderShipmentNotification() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (customer.hasOutstandingOrders()
                && System.currentTimeMillis() - startTime < MAX_WAIT_TIME_FOR_CALLBACK) {
            Thread.sleep(100);
        }
    }
}
