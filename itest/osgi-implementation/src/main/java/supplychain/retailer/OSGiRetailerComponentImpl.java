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
package supplychain.retailer;

import org.osgi.service.component.ComponentContext;

import supplychain.warehouse.Warehouse;

/**
 * This class implements the Customer service component.
 */
public class OSGiRetailerComponentImpl implements Retailer {
    
    private Warehouse warehouse;
    private String retailerName;
    
    
    protected void setWarehouse(Warehouse warehouse) {
    	this.warehouse = warehouse;
    }
    
    protected void unsetWarehouse(Warehouse warehouse) {
    	this.warehouse = null;
    }
    
    public void submitOrder(String order) {
    	
    	System.out.println("OSGiRetailerComponentImpl.submitOrder , warehouse is " + warehouse);
        warehouse.fulfillOrder(order + ", submitted (" + retailerName + ")");
        
    }

    protected void activate(ComponentContext context){
        System.out.println("Activated OSGiRetailerComponentImpl bundle ");
        
        Object prop = context.getProperties().get("retailerName");
	    if (prop instanceof String[])
	        retailerName = ((String [])prop)[0];
        
        
    }

    protected void deactivate(ComponentContext context){
        System.out.println("Deactivated OSGiRetailerComponentImpl bundle ");
    }

	
}
