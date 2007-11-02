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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.implementation.data.collection.Collection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;

public class ShoppingCartImpl implements Collection<String, String>, Total {
    
    private static Map<String, String> cart = new HashMap<String, String>();

    public Map<String, String> getAll() {
        return cart;
    }

    public String get(String key) throws NotFoundException {
        String item = cart.get(key);
        if (item == null) {
            throw new NotFoundException(key);
        } else {
            return item;
        }
    }

    public String post(String item) {
        String key = "cart-" + UUID.randomUUID().toString();
        cart.put(key, item);
        return key;
    }

    public String put(String key, String item) throws NotFoundException {
        if (!cart.containsKey(key)) {
            throw new NotFoundException(key);
        }
        cart.put(key, item);
        return item;
    }
    
    public void delete(String key) throws NotFoundException {
        if (key == null || key.equals("")) {
            cart.clear();
        } else {
            String item = cart.remove(key);
            if (item == null)
                throw new NotFoundException(key);
        }
    }

    public Map<String, String> query(String queryString) {
        // Implement queries later
        return null;
    }
    
    public String getTotal() {
        double total = 0;
        String currencySymbol = "";
        if (!cart.isEmpty()) {
            String item = cart.values().iterator().next();
            currencySymbol = item.substring(item.indexOf("-") + 2, item.indexOf("-") + 3);
        }
        for (String item : cart.values()) {
            total += Double.valueOf(item.substring(item.indexOf("-") + 3));
        }
        return currencySymbol + String.valueOf(total);
    }
}
