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
package supplychain.shipper;


import org.osgi.service.component.ComponentContext;

import supplychain.customer.Customer;

/**
 * This class implements the Shipper service component.
 */
public class OSGiShipperComponentImpl implements Shipper {

    private Customer customer;
    private String shipperName;
    
    protected void setCustomer(Customer customer) {
    	this.customer = customer;
    }
    
    protected void unsetCustomer(Customer customer) {
    	this.customer = null;
    }
    
    public void processShipment(String order) {
    	System.out.println("OSGiShipperComponentImpl.processShipment, customer is " + customer);
        customer.notifyShipment(order + ", shipped (" + shipperName + ")");
    }
    
    
    protected void activate(ComponentContext context){
        System.out.println("Activated OSGiShipperComponentImpl bundle ");
        
        Object prop = context.getProperties().get("shipperName");
	    if (prop instanceof String[])
	    	shipperName = ((String [])prop)[0];
        
    }

    protected void deactivate(ComponentContext context){
        System.out.println("Deactivated OSGiShipperComponentImpl bundle ");
    }

	
}
