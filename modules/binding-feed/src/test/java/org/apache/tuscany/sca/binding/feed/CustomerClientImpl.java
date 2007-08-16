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

package org.apache.tuscany.sca.binding.feed;

import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;

public class CustomerClientImpl implements CustomerClient {

    @Reference
    public Collection resourceCollection;

    public void testCustomerCollection() throws Exception {

        Entry newEntry = newEntry("Sponge Bob");
        System.out.println(">>> post entry=" + newEntry.getTitle());
        newEntry = resourceCollection.post(newEntry);
        System.out.println("<<< post id=" + newEntry.getId() + " entry=" + newEntry.getTitle());

        newEntry = newEntry("Jane Bond");
        System.out.println(">>> post entry=" + newEntry.getTitle());
        newEntry = resourceCollection.post(newEntry);
        System.out.println("<<< post id=" + newEntry.getId() + " entry=" + newEntry.getTitle());

        System.out.println(">>> get id=" + newEntry.getId());
        Entry entry = resourceCollection.get(newEntry.getId());
        System.out.println("<<< get id=" + entry.getId() + " entry=" + entry.getTitle());

        System.out.println(">>> put id=" + newEntry.getId() + " entry=" + entry.getTitle());
        entry = resourceCollection.put(entry.getId(), updateEntry(entry, "James Bond"));
        System.out.println("<<< put id=" + entry.getId() + " entry=" + entry.getTitle());

        System.out.println(">>> delete id=" + entry.getId());
        resourceCollection.delete(entry.getId());
        System.out.println("<<< delete id=" + entry.getId());

        System.out.println(">>> get collection");
        Feed feed = resourceCollection.getFeed();
        System.out.println("<<< get collection");
        for (Object o : feed.getEntries()) {
            Entry e = (Entry)o;
            System.out.println("id = " + e.getId() + " entry = " + e.getTitle());
        }
    }

    private Entry newEntry(String value) {

        Entry entry = new Entry();
        entry.setTitle("customer " + value);

        Content content = new Content();
        content.setValue(value);
        content.setType(Content.TEXT);
        List<Object> list = new ArrayList<Object>();
        list.add(content);
        entry.setContents(list);

        return entry;
    }

    private Entry updateEntry(Entry entry, String value) {

        entry.setTitle("customer " + value);
        Content content = new Content();
        content.setValue(value);
        content.setType(Content.TEXT);
        List<Object> list = new ArrayList<Object>();
        list.add(content);
        entry.setContents(list);

        return entry;
    }
}
