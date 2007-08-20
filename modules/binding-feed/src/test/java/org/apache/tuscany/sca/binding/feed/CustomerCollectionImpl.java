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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.binding.feed.collection.Collection;
import org.osoa.sca.annotations.Scope;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

@Scope("COMPOSITE")
public class CustomerCollectionImpl implements Collection {

    private Map<String, Entry> entries = new HashMap<String, Entry>();

    public CustomerCollectionImpl() {

        for (int i = 0; i < 4; i++) {
            String id = "urn:uuid:customer-" + UUID.randomUUID().toString();

            Entry entry = new Entry();
            entry.setTitle("customer " + "Jane Doe_" + String.valueOf(i));
            entry.setId(id);

            Content content = new Content();
            content.setValue("Jane Doe_" + String.valueOf(i));
            content.setType(Content.TEXT);
            entry.setContents(Collections.singletonList(content));

            List<Link> links = new ArrayList<Link>();
            Link link = new Link();
            link.setRel("edit");
            link.setHref("" + id);
            links.add(link);
            entry.setOtherLinks(links);

            links = new ArrayList<Link>();
            link = new Link();
            link.setRel("alternate");
            link.setHref("" + id);
            links.add(link);
            entry.setAlternateLinks(links);

            entry.setCreated(new Date());

            entries.put(id, entry);
            System.out.println(">>> id=" + id);
        }
    }

    public Entry post(Entry entry) {
        System.out.println(">>> ResourceCollectionImpl.post entry=" + entry.getTitle());

        String id = "urn:uuid:customer-" + UUID.randomUUID().toString();
        entry.setId(id);

        List<Link> links = new ArrayList<Link>();
        Link link = new Link();
        link.setRel("edit");
        link.setHref("" + id);
        links.add(link);
        entry.setOtherLinks(links);

        links = new ArrayList<Link>();
        link = new Link();
        link.setRel("alternate");
        link.setHref("" + id);
        links.add(link);
        entry.setAlternateLinks(links);

        entry.setCreated(new Date());

        entries.put(id, entry);
        System.out.println(">>> ResourceCollectionImpl.post return id=" + id);

        return entry;
    }

    public Entry get(String id) {
        System.out.println(">>> ResourceCollectionImpl.get id=" + id);
        return entries.get(id);
    }

    public Entry put(String id, Entry entry) {
        System.out.println(">>> ResourceCollectionImpl.put id=" + id + " entry=" + entry.getTitle());

        entry.setUpdated(new Date());
        entries.put(id, entry);
        return entry;
    }

    public void delete(String id) {
        System.out.println(">>> ResourceCollectionImpl.delete id=" + id);
        entries.remove(id);
    }

    @SuppressWarnings("unchecked")
    public Feed getFeed() {
        System.out.println(">>> ResourceCollectionImpl.get collection");

        Feed feed = new Feed();
        feed.setTitle("customers");
        Content subtitle = new Content();
        subtitle.setValue("This is a sample feed");
        feed.setSubtitle(subtitle);
        feed.getEntries().addAll(entries.values());
        return feed;
    }

}
