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

package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class ShoppingCartImpl implements Cart {
    
    private Map<String, Item> cart;
    
    @Init
    public void init() {
        cart = new HashMap<String, Item>();
    }

    public Entry<String, Item>[] getAll() {
        Entry<String, Item>[] entries = new Entry[cart.size()];
        int i = 0;
        for (Map.Entry<String, Item> e: cart.entrySet()) {
            entries[i++] = new Entry<String, Item>(e.getKey(), e.getValue());
        }
        return entries;
    }

    public Item get(String key) throws NotFoundException {
        Item item = cart.get(key);
        if (item == null) {
            throw new NotFoundException(key);
        } else {
            return item;
        }
    }

    public String post(String key, Item item) {
        if (key == null) {
            key ="cart-" + UUID.randomUUID().toString();
        }
        cart.put(key, item);
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        if (!cart.containsKey(key)) {
            throw new NotFoundException(key);
        }
        cart.put(key, item);
    }
    
    public void delete(String key) throws NotFoundException {
        if (key == null || key.equals("")) {
            cart.clear();
        } else {
            Item item = cart.remove(key);
            if (item == null)
                throw new NotFoundException(key);
        }
    }

    public Entry<String, Item>[] query(String queryString) {
        List<Entry<String, Item>> entries = new ArrayList<Entry<String,Item>>();
        if (queryString.startsWith("name=")) {
            String name = queryString.substring(5);
            for (Map.Entry<String, Item> e: cart.entrySet()) {
                Item item = e.getValue();
                if (item.getName().equals(name)) {
                    entries.add(new Entry<String, Item>(e.getKey(), e.getValue()));
                }
            }
        }
        return entries.toArray(new Entry[entries.size()]);
    }
    
}
