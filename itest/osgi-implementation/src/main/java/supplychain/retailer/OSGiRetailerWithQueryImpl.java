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


import java.util.Hashtable;

import supplychain.OSGiBundleImpl;
import supplychain.warehouse.Warehouse;
import supplychain.warehouse.WarehouseQuery;

/**
 * This class implements the Retailer service component with query.
 */
public class OSGiRetailerWithQueryImpl extends OSGiBundleImpl implements Retailer, RetailerQuery {
    
    private Warehouse warehouse;
    private WarehouseQuery warehouseQuery;
    
    public OSGiRetailerWithQueryImpl() {

        super(new String[]{"warehouse", "warehouseQuery"}, null);
        
        Hashtable<String, Object> props1 = new Hashtable<String, Object>();
        props1.put("retailerName", "amazon.com");
        registerService(this,
                "supplychain.retailer.Retailer", props1);
        
        registerService(this,
                "supplychain.retailer.RetailerQuery", props1);
        
    }
    
    public void submitOrder(String order) {

        System.out.println("Retailer.submitOrder, warehouse is " + warehouse);
        warehouse.fulfillOrder(order + ", submitted ("
                + "amazon.com" + ")");

    }

    public boolean isAvailable(String order) {
        return warehouseQuery.isAvailable(order);
    }

    
   
}
