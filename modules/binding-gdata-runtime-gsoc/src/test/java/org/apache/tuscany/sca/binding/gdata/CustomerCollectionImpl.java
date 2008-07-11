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

package org.apache.tuscany.sca.binding.gdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.sca.binding.gdata.collection.Collection;
import org.osoa.sca.annotations.Scope;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;

@Scope("COMPOSITE")
public class CustomerCollectionImpl implements Collection {

    // private final Abdera abdera = new Abdera();
    private Map<String, Entry> entries = new HashMap<String, Entry>();

    /**
     * Default constructor that initializes collection with couple customer
     * entries
     */
    public CustomerCollectionImpl() {

        for (int i = 0; i < 4; i++) {
            // id is supposed to be generated in a random way, but for the
            // purpose of testing
            // I just make them as static ids
            // String id = "urn:uuid:customer-" + UUID.randomUUID().toString();

            String id = "urn:uuid:customer-" + String.valueOf(i);

            Entry entry = new Entry();
            entry.setId(id);
            entry.setTitle(new PlainTextConstruct("EntryTitle_" + i));
            entry.setContent(new PlainTextConstruct("content_" + i));
            entry.setUpdated(DateTime.now());

            // FIXME: The following three lines of code need to be fixed to add
            // HTML links.

            // entry.addHtmlLink(""+id, "application/atom+xml", "title");
            // entry.addHtmlLink(""+id, "", "edit");
            // entry.addHtmlLink(""+id, "", "alternate");

            /*
             * Entry entry = abdera.getFactory().newEntry(); entry.setId(id);
             * entry.setTitle("title_" + String.valueOf(i));
             * entry.addAuthor("author_" + String.valueOf(i)); entry.addLink("" +
             * id, "edit"); entry.addLink("" + id, "alternate");
             * entry.setUpdated(new Date());
             */

            entries.put(id, entry);
            System.out.println(">>> id=" + id);
        }
    }

    public Entry post(Entry entry) {
        System.out.println(">>> ResourceCollectionImpl.post entry=" + entry.getTitle());

        String id = "urn:uuid:customer-" + UUID.randomUUID().toString();
        entry.setId(id);
        /*
         * entry.addLink("" + id, "edit"); entry.addLink("" + id, "alternate");
         * entry.setUpdated(new Date());
         */
        entry.setUpdated(DateTime.now());
        // entry.addHtmlLink("http://www.google.com", "languageType", "edit");
        // entry.addHtmlLink("http://www.google.com", "languageType",
        // "alternate");

        entries.put(id, entry);
        System.out.println(">>> ResourceCollectionImpl.post return id=" + id);
        return entry;
    }

    public Entry get(String id) {
        System.out.println(">>> ResourceCollectionImpl.get id=" + id);
        return entries.get(id);
    }

    public void put(String id, Entry entry) {
        System.out.println(">>> ResourceCollectionImpl.put id=" + id + " entry=" + entry.getTitle());

        // entry.setUpdated(new Date());
        entry.setUpdated(DateTime.now());

        entries.put(id, entry);
    }

    public void delete(String id) {
        System.out.println(">>> ResourceCollectionImpl.delete id=" + id);
        entries.remove(id);
    }

    @SuppressWarnings("unchecked")
    public Feed getFeed() {
        System.out.println(">>> ResourceCollectionImpl.get collection");

        /*
         * Feed feed = this.abdera.getFactory().newFeed();
         * feed.setTitle("customers"); feed.setSubtitle("This is a sample
         * feed"); feed.setUpdated(new Date()); feed.addLink("");
         * feed.addLink("", "self");
         */

        Feed feed = new Feed();
        feed.setTitle(new PlainTextConstruct("Feedtitle"));
        feed.setSubtitle(new PlainTextConstruct("Subtitle: This is a sample feed"));
        feed.setUpdated(DateTime.now());

        // FIXME: The following two lines of code need to be fixed
        // feed.addHtmlLink("", "", "");
        // feed.addHtmlLink("", "languageType", "self");

        ArrayList<Entry> entryList = new ArrayList<Entry>();
        for (Entry entry : entries.values()) {
            entryList.add(entry);
        }
        feed.setEntries(entryList);

        return feed;
    }

    public Feed query(String queryString) {
        System.out.println(">>> ResourceCollectionImpl.query collection " + queryString);
        return getFeed();
    }

}
