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

import org.osgi.service.component.ComponentContext;

/**
 * Declarative Stock quote with configurable properties.
 */
public class OSGiStockQuoteComponentImpl implements StockQuote {
    
    public double exchangeRate;
    
    private String currency;
    
    
    public double configExchangeRate;
    public String configCurrency;
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public double getQuote(String ticker) throws Exception {
        
        if (exchangeRate == 2.0 && !"USD".equals(currency)) {
            throw new RuntimeException("Property exchangeRate not set correctly, exchangeRate= " + 
                    exchangeRate + " currency=" + currency);
        }
        if (exchangeRate == 1.48 && !"EURO".equals(currency)) {
            throw new RuntimeException("Property exchangeRate not set correctly, exchangeRate= " + 
                    exchangeRate + " currency=" + currency);
        }
        
        if (configExchangeRate == 2.0 && !"USD".equals(configCurrency)) {
            throw new RuntimeException("ConfigAdmin Property exchangeRate not set correctly, exchangeRate= " + 
                    configExchangeRate + " currency=" + configCurrency);
        }
        if (configExchangeRate == 1.48 && !"EURO".equals(configCurrency)) {
            throw new RuntimeException("Property configExchangeRate not set correctly, exchangeRate= " + 
                    configExchangeRate + " currency=" + configCurrency);
        }
        
        return 52.81 * exchangeRate;
        
    }
    
    protected void activate(ComponentContext context){
        System.out.println("Activated OSGiStockQuoteComponentImpl bundle ");
        Dictionary props = context.getProperties();
        Object prop = props.get("currency");
        if (prop instanceof String[]&& ((String [])prop).length > 0)
            configCurrency = ((String [])prop)[0];
        prop = props.get("exchangeRate");
        if (prop instanceof Double[]&& ((Double [])prop).length > 0)
            configExchangeRate = (double)((Double [])prop)[0];
    }

    protected void deactivate(ComponentContext context){
        System.out.println("Deactivated OSGiStockQuoteComponentImpl bundle ");
    }

    

}
