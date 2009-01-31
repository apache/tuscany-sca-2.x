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

import supplychain.OSGiBundleImpl;
import supplychain.customer.Customer;

/**
 * This class implements the Shipper service component.
 */
public class OSGiShipperImpl extends OSGiBundleImpl {
    
    protected Customer customer;

    
    public OSGiShipperImpl() {

        super(new String[]{"customer"}, null);
        
        Hashtable<String, Object> props1 = new Hashtable<String, Object>();
        props1.put("shipperName", "RoyalMail");
        registerService(new OSGiShipperServiceImpl(this, "RoyalMail"),
                "supplychain.shipper.Shipper", props1);
        
        Hashtable<String, Object> props2 = new Hashtable<String, Object>();
        props2.put("shipperName", "ParcelForce");
        registerService(
                new OSGiShipperServiceImpl(this, "ParcelForce"),
                "supplychain.shipper.Shipper", props2);
    }
    
    // Used only by service factories
    public OSGiShipperImpl(boolean ignore) {

        super(new String[]{"customer"}, null);
    }
   
}
