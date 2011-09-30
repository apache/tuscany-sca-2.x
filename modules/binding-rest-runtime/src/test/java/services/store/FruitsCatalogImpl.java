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

package services.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
public class FruitsCatalogImpl implements Catalog {

    @Property
    public String currencyCode = "USD";

    @Reference
    public CurrencyConverter currencyConverter;

    private Map<String, Item> catalog = new HashMap<String, Item>();

    @Init
    public void init() {
        String currencySymbol = currencyConverter.getCurrencySymbol(currencyCode);
        catalog.put("Apple", new Item("Apple",  currencySymbol + currencyConverter.getConversion("USD", currencyCode, 2.99)));
        catalog.put("Orange", new Item("Orange", currencySymbol + currencyConverter.getConversion("USD", currencyCode, 3.55)));
        catalog.put("Pear", new Item("Pear", currencySymbol + currencyConverter.getConversion("USD", currencyCode, 1.55)));
    }

    public Items getItem() {
        Items items = new Items();
        items.setItems(new ArrayList<Item>(catalog.values()));
        return items;
    }

    public Item getItemById(String itemId, Date date) {
        return catalog.get(itemId);
    }

    public void addItem(Item item) {
        catalog.put(item.getName(),item);
    }

    public void updateItem(Item item) {
        if(catalog.get(item.getName()) != null) {
            catalog.put(item.getName(), item);
        }
    }

    public void deleteItem(String itemId) {
        if(catalog.get(itemId) != null) {
            catalog.remove(itemId);
        }
    }
}
