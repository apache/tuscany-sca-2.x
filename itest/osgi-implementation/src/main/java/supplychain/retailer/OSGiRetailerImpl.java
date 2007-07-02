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

/**
 * This class implements the Retailer service component.
 */
public class OSGiRetailerImpl extends OSGiBundleImpl {
    
    protected Warehouse warehouse;
    
    public OSGiRetailerImpl() {

        super(new String[]{"warehouse"}, 
                new String[]{"(component.service.name=WarehouseComponent1/Warehouse)"});
        
        Hashtable<String, Object> props1 = new Hashtable<String, Object>();
        props1.put("retailerName", "amazon.com");
        registerService(new OSGiRetailerServiceImpl(this, "amazon.com"),
                "supplychain.retailer.Retailer", props1);
        
        Hashtable<String, Object> props2 = new Hashtable<String, Object>();
        props2.put("retailerName", "play.com");
        registerService(
                new OSGiRetailerServiceImpl(this, "play.com"),
                "supplychain.retailer.Retailer", props2);
        
        Hashtable<String, Object> props3 = new Hashtable<String, Object>();
        props3.put("retailerName", "ebay.com");
        registerService(
                new OSGiRetailerServiceImpl(this, "ebay.com"),
                "supplychain.retailer.Retailer", props3);
    }
    
   
}
