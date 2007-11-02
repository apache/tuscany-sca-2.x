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

package services.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.binding.feed.collection.Collection;
import org.apache.tuscany.sca.binding.feed.collection.NotFoundException;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

public class ShoppingCartTableImpl implements Collection {
    
    private static Map<String, String> cart = new HashMap<String, String>();

    public Feed getFeed() {
        Feed feed = new Feed();
        feed.setTitle("shopping cart");
        for (Map.Entry<String, String> item: getAllData().entrySet()) {
            feed.getEntries().add(createEntry(item.getKey(), item.getValue()));
        }
        return feed;
    }
    
    private Entry createEntry(String key, String item) {
        Entry entry = new Entry();
        entry.setId(key);
        entry.setTitle("cart-item");

        Content content = new Content();
        content.setType(Content.TEXT);
        content.setValue(item);
        List contents = new ArrayList();
        contents.add(content);
        entry.setContents(contents);

        Link link = new Link();
        link.setRel("edit");
        link.setHref(key);
        entry.getOtherLinks().add(link);
        link = new Link();
        link.setRel("alternate");
        link.setHref(key);
        entry.getAlternateLinks().add(link);

        entry.setCreated(new Date());

        return entry;
    }

    public Entry get(String id) throws NotFoundException {
        return createEntry(id, cart.get(id));
    }

    public Entry post(Entry entry) {
        System.out.println("post" + entry);
        String item = ((Content)entry.getContents().get(0)).getValue();
        String key = postData(item);
        return createEntry(key, item);
    }

    public Entry put(String id, Entry entry) throws NotFoundException {
        String item = ((Content)entry.getContents().get(0)).getValue();
        item = putData(id, item);
        return createEntry(id, item);
    }

    public void delete(String id) throws NotFoundException {
        deleteData(id);
    }

    private Map<String, String> getAllData() {
        return cart;
    }

    private String getData(String key) throws NotFoundException {
        return cart.get(key);
    }

    private String postData(String item) {
        String key = "cart-" + UUID.randomUUID().toString();
        cart.put(key, item);
        return key;
    }

    private String putData(String key, String item) throws NotFoundException {
        cart.put(key, item);
        return item;
    }
    
    private void deleteData(String key) throws NotFoundException {
        if (key == null || key.equals(""))
            cart.clear();
        else
            cart.remove(key);
    }

    private Map<String, String> queryData(String queryString) {
        return getAllData();
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
