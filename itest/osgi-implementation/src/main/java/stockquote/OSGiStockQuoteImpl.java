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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * 
 * Stock quote with configurable properties.
 *
 */
public class OSGiStockQuoteImpl implements StockQuote, BundleActivator {
    
    public String pid;
    
    public double exchangeRate;
    
    private String currency;
    
    private BundleContext bundleContext;
    
    public OSGiStockQuoteImpl() {     
        this.pid = "stockQuote";
    }
    
    protected OSGiStockQuoteImpl(BundleContext bc) {
        this.bundleContext = bc;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
     
    private void checkProperties() throws Exception {
               
        if (exchangeRate == 2.0 && !"USD".equals(currency)) {
            throw new RuntimeException("Property exchangeRate not set correctly, exchangeRate= " + 
                    exchangeRate + " currency=" + currency);
        }
        
        if (exchangeRate == 1.48 && !"EURO".equals(currency)) {
            throw new RuntimeException("Property exchangeRate not set correctly, exchangeRate= " + 
                    exchangeRate + " currency=" + currency);
        }
        
        ServiceReference configAdminReference = bundleContext.getServiceReference("org.osgi.service.cm.ConfigurationAdmin");
        if (configAdminReference != null && pid != null ) {
            
            ConfigurationAdmin cm = (ConfigurationAdmin)bundleContext.getService(configAdminReference);
            
            Configuration config = cm.getConfiguration("stockQuote", null);
            
            Dictionary props = config.getProperties();
            
            if (exchangeRate != (double)(Double)props.get("exchangeRate")) {
                throw new Exception("Config Property exchangeRate not set correctly, expected " 
                        + props.get("exchangeRate") + " got " + exchangeRate);
            }
            
            if (!currency.equals(props.get("currency"))) {
                throw new Exception("Config Property currency not set correctly, expected " 
                        + props.get("currency") + " got " + currency);
            }
        }
        
    }
    
    public double getQuote(String ticker) throws Exception {
        
        checkProperties();
       
        return 52.81 * exchangeRate;
        
    }

    public void start(BundleContext bc) throws Exception {
        
        bundleContext = bc;
        
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put("service.pid", "stockQuote");
        
        bc.registerService(StockQuote.class.getName(), this, props);
        
    }

    public void stop(BundleContext bc) throws Exception {
    }

    
}
