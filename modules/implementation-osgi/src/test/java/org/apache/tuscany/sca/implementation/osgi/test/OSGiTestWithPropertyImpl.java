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

package org.apache.tuscany.sca.implementation.osgi.test;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osoa.sca.annotations.Property;

/**
 * 
 * Test class - Implementation of an OSGi service
 *
 * @version $Rev$ $Date$
 */
public class OSGiTestWithPropertyImpl implements OSGiTestInterface, BundleActivator {
    
    @Property
    public double exchangeRate;
    
    private String currency;
    
    @Property
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String testService() throws Exception {
        
        if (exchangeRate != 2.0)
            throw new Exception("Property exchangeRate not set correctly, expected 2.0, got " + exchangeRate);
        if (!"USD".equals(currency))
            throw new Exception("Property currency not set correctly, expected USD, got " + currency);
        return OSGiTestWithPropertyImpl.class.getName();
        
    }

    public void start(BundleContext bc) throws Exception {
        
        bc.registerService(OSGiTestInterface.class.getName(), this, new Hashtable<String, Object>());
        
    }

    public void stop(BundleContext bc) throws Exception {
    }

    
}
