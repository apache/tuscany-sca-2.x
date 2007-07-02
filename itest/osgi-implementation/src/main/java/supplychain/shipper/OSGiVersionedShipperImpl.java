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


import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import supplychain.OSGiBundleImpl;
import supplychain.customer.Customer;

/**
 * This class implements the Shipper service component.
 */
public class OSGiVersionedShipperImpl extends OSGiBundleImpl {
    
    protected Customer customer;
    
    private int version;

    
    public OSGiVersionedShipperImpl() {

        super(new String[]{"customer"}, null);
    }
    
    protected void started(BundleContext bc) {
        
        String jarFile = bc.getBundle().getLocation();
        if (jarFile.endsWith("1.jar")) version = 1;
        else if (jarFile.endsWith("2.jar")) version = 2;
        
        
        Hashtable<String, Object> props1 = new Hashtable<String, Object>();
        props1.put("shipperName", "RoyalMail");
        bc.registerService("supplychain.shipper.Shipper", 
                new OSGiVersionedShipperServiceImpl(this, "RoyalMail", version),
                props1);
        
        Hashtable<String, Object> props2 = new Hashtable<String, Object>();
        props2.put("shipperName", "ParcelForce");
        bc.registerService("supplychain.shipper.Shipper", 
                new OSGiVersionedShipperServiceImpl(this, "ParcelForce", version),
                props2);
        
        try {
            ServiceReference[] refs = bc.getServiceReferences(Customer.class.getName(), 
                    "(component.service.name=CustomerComponent" + version + ".CustomerService)");
            if (refs != null && refs.length > 0) {
                customer = (Customer) bc.getService(refs[0]);
            }
        } catch (InvalidSyntaxException e) {
        }
    }
    
   
}
