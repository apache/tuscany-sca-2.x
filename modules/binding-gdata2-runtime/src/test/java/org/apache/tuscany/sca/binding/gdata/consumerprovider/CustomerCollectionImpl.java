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
package org.apache.tuscany.sca.binding.gdata.consumerprovider;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import org.apache.tuscany.sca.binding.gdata.collection.Collection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class CustomerCollectionImpl implements Collection {

    private Map<String, BaseEntry> entries;

    public CustomerCollectionImpl() {
        entries = new HashMap<String, BaseEntry>();

        BaseEntry entry = new Entry();
        entry.setId("urn:uuid:customer-0");
        entry.setTitle(new PlainTextConstruct("An exampling entry - GSoC"));

        entries.put(entry.getId(), entry);
    }

    public BaseFeed<? extends BaseFeed, ? extends BaseEntry> getFeed() {
        System.out.println(">>> CustomerCollectionImpl.getFeed");

        BaseFeed feed = new Feed();
        feed.setTitle(new PlainTextConstruct("Customers Feed"));
        feed.setSubtitle(new PlainTextConstruct("This is a sample feed"));
        feed.setUpdated(new DateTime());

        //FIX-ME
        //feed.addHtmlLink("", "", ""); //feed.addLink("");
        //feed.addHtmlLink("", "self", ""); //feed.addLink("", "self");       
        feed.addHtmlLink("http://localhost:8086/customer", "", "");

        feed.setEntries(new ArrayList(entries.values()));

        return feed;
    }

    public BaseFeed<? extends BaseFeed, ? extends BaseEntry> query(String queryString) {
        System.out.println(">>> CustomerCollectionImpl.query collection " + queryString);
        return getFeed();
    }

    public BaseEntry post(BaseEntry entry) {
        System.out.println(">>> CustomerCollectionImpl.post entry=" + entry.getTitle().getPlainText());

        String id = "urn:uuid:customer-" + UUID.randomUUID().toString();
        entry.setId(id);

        //FIX-ME
        entry.addHtmlLink("" + id, "edit", "");
        entry.addHtmlLink("" + id, "alternate", "");

        DateTime dateTime = new DateTime(new Date(), TimeZone.getTimeZone("America/Los_Angeles"));
        entry.setUpdated(dateTime);

        entries.put(id, entry);

        System.out.println(">>> CustomerCollectionImpl.post return id=" + id);

        return entry;
    }

    public BaseEntry get(String id) throws NotFoundException {
        System.out.println(">>> CustomerCollectionImpl.get id=" + id);

        return entries.get(id);
    }

    public BaseEntry put(String id, BaseEntry entry) throws NotFoundException {
        System.out.println(">>> CustomerCollectionImpl.put id=" + id + " entry=" + entry.getTitle());

        DateTime dateTime = new DateTime(new Date(), TimeZone.getTimeZone("America/Los_Angeles"));
        entry.setUpdated(dateTime);

        entries.put(id, entry);

        return entries.get(id);
    }

    public void delete(String id) throws NotFoundException {
        System.out.println(">>> CustomerCollectionImpl.delete id=" + id);
        entries.remove(id);
    }
}
