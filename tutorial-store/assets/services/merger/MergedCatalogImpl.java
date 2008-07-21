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

package services.merger;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import services.Catalog;
import services.CurrencyConverter;
import services.Item;

public class MergedCatalogImpl implements Catalog {

    @Property
    public String currencyCode = "USD";
    
    @Reference
    public CurrencyConverter currencyConverter;
    
    @Reference
    public Catalog fruitsCatalog;
    
    @Reference
    public Catalog vegetablesCatalog;
    
    public Item[] get() {
        String currencySymbol = currencyConverter.getCurrencySymbol(currencyCode);
        
        Item[] fruits = fruitsCatalog.get();
        Item[] vegetables = vegetablesCatalog.get();
        
        Item[] catalog = new Item[fruits.length + vegetables.length];
        int i =0;
        for (Item item: fruits) {
            double price = Double.valueOf(item.getPrice().substring(1));  
            price = currencyConverter.getConversion("USD", currencyCode, price);
            catalog[i++] = new Item(item.getName(), currencySymbol + price);
        }
        
        for (Item item: vegetables) {
            double price = Double.valueOf(item.getPrice().substring(1));  
            price = currencyConverter.getConversion("USD", currencyCode, price);
            catalog[i++] = new Item(item.getName(), currencySymbol + price);
        }
        
        return catalog;
    }

}
