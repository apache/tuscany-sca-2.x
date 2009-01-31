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
import java.util.Hashtable;

import org.osgi.framework.BundleContext;

import supplychain.OSGiBundleImpl;
import supplychain.retailer.Retailer;

/**
 * This class implements the Customer service component.
 */
public class OSGiVersionedCustomerImpl extends OSGiBundleImpl implements Customer {
    
    private static ArrayList<String> outstandingOrders = new ArrayList<String>();
    
    private Retailer retailer1;
    private Retailer retailer2;
    private Retailer retailer3;
    private int version;
    
    public OSGiVersionedCustomerImpl() {
        super(  new String[]{"retailer1", "retailer2", "retailer3"},
                new String[]{"(retailerName=amazon.com)", 
                             "(retailerName=play.com)",
                             "(retailerName=ebay.com)"
                             });
               
    }
    
    protected void started(BundleContext bc)  {
        
        String jarFile = bc.getBundle().getLocation();
        if (jarFile.endsWith("1.jar")) version = 1;
        else if (jarFile.endsWith("2.jar")) version = 2;
        else version = 3;
        
        Hashtable<String, Object> props1 = new Hashtable<String, Object>();
        props1.put("component.name", "CustomerComponent" + version);
        bc.registerService("supplychain.customer.Customer", this, props1);
               
    }
   
    public void purchaseBooks() {
    	System.out.println("OSGiVersionedCustomerImpl.purchaseBooks , retailer is " + retailer1);
        outstandingOrders.add("Order, submitted (amazon.com v" + version + 
                "), fulfilled, shipped (ParcelForce)");
        
        retailer1.submitOrder("Order");
        
    }
    
    public void purchaseGames() {
        System.out.println("OSGiCustomerImpl.purchaseGames");
        outstandingOrders.add("Order, submitted (play.com v" + version + 
                "), fulfilled, shipped (ParcelForce)");
        
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
