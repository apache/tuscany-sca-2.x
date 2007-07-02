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
package supplychain.customer;

import java.util.ArrayList;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import supplychain.retailer.Retailer;

/**
 * This class implements the Customer service component.
 */
@Service(Customer.class)
@Scope("COMPOSITE")
public class JavaCustomerComponentImpl implements Customer {
    
    private Retailer retailer1;
	
    private Retailer retailer2;
    
    private Retailer retailer3;
    
    private static ArrayList<String> outstandingOrders = new ArrayList<String>();
    
    @Reference
    public void setRetailer1(Retailer retailer1) {
        this.retailer1 = retailer1;
    }
    
    @Reference
    public void setRetailer2(Retailer retailer2) {
        this.retailer2 = retailer2;
    }
    
    @Reference
    public void setRetailer3(Retailer retailer3) {
        this.retailer3 = retailer3;
    }
    
    public void purchaseBooks() {
    	System.out.println("JavaCustomerComponentImpl.purchaseBooks");
        outstandingOrders.add("Order, submitted (amazon.com), fulfilled, shipped (ParcelForce)");
        retailer1.submitOrder("Order");
    }
    
    public void purchaseGames() {
        System.out.println("JavaCustomerComponentImpl.purchaseGames");
        outstandingOrders.add("Order, submitted (play.com), fulfilled, shipped (ParcelForce)");
        
        retailer2.submitOrder("Order");
    }
    
    public void purchaseGoods() {
        retailer3.submitOrder("Order");
    }
    
    public void notifyShipment(String order) {
        outstandingOrders.remove(order);
        System.out.print("Work thread " + Thread.currentThread() + " - ");
        System.out.println(order);       
    }

    public boolean hasOutstandingOrders() {
        return outstandingOrders.size() != 0;
    }
    
    

}
