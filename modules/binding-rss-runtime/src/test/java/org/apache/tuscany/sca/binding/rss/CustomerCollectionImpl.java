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

package org.apache.tuscany.sca.binding.rss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.binding.rss.collection.Collection;
import org.apache.tuscany.sca.binding.rss.collection.NotFoundException;
import org.osoa.sca.annotations.Scope;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;

@Scope("COMPOSITE")
public class CustomerCollectionImpl implements Collection {

    private Map<String, SyndEntry> entries = new HashMap<String, SyndEntry>();

    public CustomerCollectionImpl() {

        for (int i = 0; i < 4; i++) {
            String id = "urn:uuid:customer-" + UUID.randomUUID().toString();

            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle("customer " + "Jane Doe_" + String.valueOf(i));
            entry.setUri(id);

            SyndContent content = new SyndContentImpl();
            content.setValue("Jane Doe_" + String.valueOf(i));
            content.setType("text");
            entry.setContents(Collections.singletonList(content));

            List<SyndLink> links = new ArrayList<SyndLink>();
            SyndLink link = new SyndLinkImpl();
            link.setRel("edit");
            link.setHref("" + id);
            links.add(link);
            entry.setLinks(links);

            links = new ArrayList<SyndLink>();
            link = new SyndLinkImpl();
            link.setRel("alternate");
            link.setHref("" + id);
            links.add(link);
            entry.setLinks(links);

            entry.setPublishedDate(new Date());

            entries.put(id, entry);
            System.out.println(">>> id=" + id);
        }
    }

    public SyndFeed getFeed() {
        System.out.println(">>> CustomerCollectionImpl.getFeed");

        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("customers");
        feed.setDescription("This is a sample feed");
        feed.getEntries().addAll(entries.values());
        return feed;
    }

    public SyndFeed query(String queryString) {
        System.out.println(">>> CustomerCollectionImpl.query");

        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("customers");
        feed.setDescription("This is a sample feed");
        feed.getEntries().addAll(entries.values());
        return feed;
    }
    
    /**
     * {@inheritDoc}
     */
    public SyndEntry get(String id) throws NotFoundException {
        final SyndEntry entry = entries.get(id);
        if (id == null) {
            throw new NotFoundException("No entry found with ID " + id);
        }

        return entry;
    }
}
