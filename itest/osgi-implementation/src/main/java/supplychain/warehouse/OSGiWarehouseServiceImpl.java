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

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import supplychain.shipper.Shipper;


/**
 * This class implements the Warehouse service.
 */
public class OSGiWarehouseServiceImpl implements Warehouse {

    private OSGiWarehouseImpl warehouseImpl;
    private Shipper shipper;
    private int shipperNum;


    OSGiWarehouseServiceImpl(OSGiWarehouseImpl warehouseImpl, int shipperNum) {
        this.warehouseImpl = warehouseImpl;
        this.shipperNum = shipperNum;
    }
    
    
    protected void started(BundleContext bc) {
        try {
            ServiceReference[] refs = bc.getServiceReferences(Shipper.class.getName(), 
                    "(component.service.name=ShipperComponent" + shipperNum + "/Shipper)");
            if (refs != null && refs.length > 0) {
                shipper = (Shipper) bc.getService(refs[0]);
            }
        } catch (InvalidSyntaxException e) {
        }
    }

    public void fulfillOrder(String order) {
        System.out.println("OSGiWarehouseImpl.fulfillOrder : shipper is " + warehouseImpl.shipper);
            
        shipper.processShipment(order + ", fulfilled");
        
    }

}
