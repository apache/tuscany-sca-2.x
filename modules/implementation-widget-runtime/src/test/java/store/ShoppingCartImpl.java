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

package store;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;

/**
 * Implementation of a Shopping Cart.
 *
 * @version $Rev$ $Date$
 */
public class ShoppingCartImpl implements Collection {

    private static Map<String, Entry> cart = new HashMap<String, Entry>();

    public Feed getFeed() {
        Feed feed = Abdera.getNewFactory().newFeed();
        feed.setTitle("shopping cart");
        feed.setSubtitle("Total : " + getTotal());
        
        for (Entry entry : cart.values()) {
            feed.addEntry(entry);
        }
        return feed;
    }
    
    public Feed query(String queryString) {
        if (queryString.startsWith("name=")) {
            String name = queryString.substring(5);

            Feed feed = Abdera.getNewFactory().newFeed();
            feed.setTitle("shopping cart");
            feed.setSubtitle("Total : " + getTotal());
            
            for (Entry entry : cart.values()) {
                if (entry.getTitle().contains(name)) {
                    feed.addEntry(entry);
                }
            }
            return feed;
            
        } else {
            return getFeed();
        }
    }

    public Entry get(String id) throws NotFoundException {
        return cart.get(id);
    }

    public Entry post(Entry entry) {
        System.out.println("post" + entry);
        String id = "cart-" + UUID.randomUUID().toString();
        entry.setId(id);

        entry.addLink(id, "edit");
        entry.addLink(id, "alternate");
        
        entry.setUpdated(new Date());

        cart.put(id, entry);
        return entry;
    }

    public void put(String id, Entry entry) throws NotFoundException {
        entry.setUpdated(new Date());
        cart.put(id, entry);
    }

    public void delete(String id) throws NotFoundException {
        if (id.equals(""))
            cart.clear();
        else
            cart.remove(id);
    }

    private String getTotal() {
        float total = 0;
        String currencySymbol = "";
        if (!cart.isEmpty()) {
            String item = ((Entry)cart.values().iterator().next()).getContent();
            // Select first symbol after dash.
            currencySymbol = item.substring(item.indexOf("-") + 2, item.indexOf("-") + 3);
        }
        for (Entry entry : cart.values()) {
            String item = entry.getContent();
            
            int index = item.length()-1;
            char digit;
            while ((digit = item.charAt(index)) == '.' || Character.isDigit(digit)) {
                index--;
            }
            
            total += Float.valueOf(item.substring(index));
        }
        return currencySymbol + String.valueOf(total);
    }
}
