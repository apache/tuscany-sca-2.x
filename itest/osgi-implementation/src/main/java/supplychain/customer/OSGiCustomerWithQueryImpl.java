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

import supplychain.OSGiBundleImpl;
import supplychain.retailer.Retailer;
import supplychain.retailer.RetailerQuery;

/**
 * This class implements the Customer service component.
 */
public class OSGiCustomerWithQueryImpl extends OSGiBundleImpl implements Customer {
    
    private static ArrayList<String> outstandingOrders = new ArrayList<String>();
    
    private Retailer retailer;
    private RetailerQuery retailerQuery;
    
    public OSGiCustomerWithQueryImpl() {
        super(  new String[]{"retailer", "retailerQuery"},
                new String[]{"(retailerName=amazon.com)",
                             "(retailerName=amazon.com)"
                });
        registerService(this, "supplychain.customer.Customer", null);
                
    }
   
    public void purchaseBooks() {
    	System.out.println("OSGiCustomerWithQueryImpl.purchaseBooks");
        outstandingOrders.add("Order, submitted (amazon.com), fulfilled, shipped (RoyalMail)");
        
        if (retailerQuery.isAvailable("Order"))
            retailer.submitOrder("Order");
        
    }
    
    public void purchaseGames() {
        System.out.println("OSGiCustomerWithQueryImpl.purchaseGames");
        outstandingOrders.add("Order, submitted (amazon.com), fulfilled, shipped (RoyalMail)");
        
        if (retailerQuery.isAvailable("Order"))
            retailer.submitOrder("Order");
        
    }
    
    public void purchaseGoods() {
        if (retailerQuery.isAvailable("Order"))
            retailer.submitOrder("Order");
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
