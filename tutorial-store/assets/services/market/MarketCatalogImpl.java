
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package services.market;

import java.util.Vector;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import services.Catalog;
import services.CurrencyConverter;
import services.Item;


public class MarketCatalogImpl implements Catalog {

    @Property
    public String currencyCode = "USD";
    
    @Reference
    public CurrencyConverter currencyConverter;
    
    @Reference(required=false)
    protected Catalog[] goodsCatalog;

    
    public Item[] get() {

        String currencySymbol = currencyConverter.getCurrencySymbol(currencyCode);
        Vector<Item> catalog = new Vector<Item>();

        for (int i = 0; i < goodsCatalog.length; i++) {
            Item[] items = goodsCatalog[i].get();

            for (Item item : items) {
                double price = Double.valueOf(item.getPrice().substring(1));
                price = currencyConverter.getConversion("USD", currencyCode, price);
                catalog.addElement(new Item(item.getName(), currencySymbol + price));
            }
        }

        Item[] catalogArray = new Item[catalog.size()];
        catalog.copyInto(catalogArray);

        return catalogArray;
    }

} 