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
package supplychain.warehouse;


import java.util.Hashtable;

import org.osgi.framework.BundleContext;

import supplychain.OSGiBundleImpl;
import supplychain.shipper.Shipper;

/**
 * This class implements the Warehouse service component.
 */

public class OSGiWarehouseImpl extends OSGiBundleImpl implements Warehouse {
    
    protected Shipper shipper;
    private OSGiWarehouseServiceImpl warehouseService1;
    private OSGiWarehouseServiceImpl warehouseService2;
    
    public OSGiWarehouseImpl() {
    	super(new String[]{"shipper"}, null);
        
        Hashtable<String, Object> props1 = new Hashtable<String, Object>();
        props1.put("component.name", "WarehouseComponent1");
        warehouseService1 = new OSGiWarehouseServiceImpl(this, 2);
        registerService(warehouseService1,
                "supplychain.warehouse.Warehouse", props1);
        
        Hashtable<String, Object> props2 = new Hashtable<String, Object>();
        props2.put("component.name", "WarehouseComponent2");
        warehouseService2 = new OSGiWarehouseServiceImpl(this, 1);
        registerService(warehouseService2,
                "supplychain.warehouse.Warehouse", props2);
    }
    
    protected void started(BundleContext bc) {
        warehouseService1.started(bc);
        warehouseService2.started(bc);
    }
    
    public void fulfillOrder(String order) {
    	System.out.println("OSGiWarehouseImpl.fulfillOrder : shipper is " + shipper);
        	
        shipper.processShipment(order + ", fulfilled");
    	
    }
    
   
}
