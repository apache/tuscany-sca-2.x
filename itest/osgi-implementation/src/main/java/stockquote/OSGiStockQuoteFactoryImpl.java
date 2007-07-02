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

package stockquote;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 * OSGi service factory where service has configurable properties
 * Only property injection is tested. ConfigurationAdmin is not used.
 *
 */
public class OSGiStockQuoteFactoryImpl implements BundleActivator, ServiceFactory {
    
   
    private BundleContext bundleContext;
    

    public void start(BundleContext bc) throws Exception {
        
        bundleContext = bc;
        
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        bc.registerService(StockQuote.class.getName(), this, props);
        
    }

    public void stop(BundleContext bc) throws Exception {
    }


    public Object getService(Bundle bundle, ServiceRegistration registration) {
        return new OSGiStockQuoteImpl(bundleContext);
    }


    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
        
    }

    
}
