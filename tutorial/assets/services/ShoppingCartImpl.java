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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.binding.feed.collection.Collection;
import org.apache.tuscany.sca.binding.feed.collection.NotFoundException;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

public class ShoppingCartImpl implements Collection {

    private static Map<String, Entry> cart = new HashMap<String, Entry>();

    public Feed getFeed() {
        Feed feed = new Feed();
        feed.setTitle("shopping cart");
        Content subtitle = new Content();
        subtitle.setValue("Total : " + getTotal());
        feed.setSubtitle(subtitle);
        feed.getEntries().addAll(cart.values());
        return feed;
    }

    public Entry get(String id) throws NotFoundException {
        return cart.get(id);
    }

    public Entry post(Entry entry) {
        System.out.println("post" + entry);
        String id = "cart-" + UUID.randomUUID().toString();
        entry.setId(id);

        Link link = new Link();
        link.setRel("edit");
        link.setHref(id);
        entry.getOtherLinks().add(link);
        link = new Link();
        link.setRel("alternate");
        link.setHref(id);
        entry.getAlternateLinks().add(link);

        entry.setCreated(new Date());

        cart.put(id, entry);
        return entry;
    }

    public Entry put(String id, Entry entry) throws NotFoundException {
        entry.setUpdated(new Date());
        cart.put(id, entry);
        return entry;
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
            String item = ((Content)cart.values().iterator().next().getContents().get(0)).getValue();
            currencySymbol = item.substring(item.indexOf("-") + 2, item.indexOf("-") + 3);
        }
        for (Entry entry : cart.values()) {
            String item = ((Content)entry.getContents().get(0)).getValue();
            total += Float.valueOf(item.substring(item.indexOf("-") + 3));
        }
        return currencySymbol + String.valueOf(total);
    }
}
