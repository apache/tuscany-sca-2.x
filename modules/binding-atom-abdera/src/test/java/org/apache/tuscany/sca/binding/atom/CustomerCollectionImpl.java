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

package org.apache.tuscany.sca.binding.atom;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class CustomerCollectionImpl implements Collection {
    private final Abdera abdera = new Abdera();
    private Map<String, Entry> entries = new HashMap<String, Entry>();
    public Date lastModified = new Date();
    
    /**
     * Default constructor 
     */
    public CustomerCollectionImpl() {

    }

    public Entry post(Entry entry) {
        System.out.println(">>> CustomerCollectionImpl.post entry=" + entry.getTitle());

        if(!("Exception_Test".equalsIgnoreCase(entry.getTitle())))
        {
           String id = "urn:uuid:customer-" + UUID.randomUUID().toString();
           entry.setId(id);

           entry.addLink("" + id, "edit");
           entry.addLink("" + id, "alternate");
           Date now = new Date();
           entry.setUpdated(now);
           lastModified = now;
           entries.put(id, entry);

            System.out.println(">>> CustomerCollectionImpl.post return id=" + id);

            return entry;

        }
        else
        {
        	throw new IllegalArgumentException("Exception in Post method");
        }
    }

    public Entry get(String id) {
        System.out.println(">>> CustomerCollectionImpl.get id=" + id);
        return entries.get(id);
    }

    public void put(String id, Entry entry) throws NotFoundException {
        System.out.println(">>> CustomerCollectionImpl.put id=" + id + " entry=" + entry.getTitle());
        if(entries.containsKey(id)){
        	Date now = new Date();
        	entry.setUpdated(now);
        	lastModified = now;
            entries.put(id, entry);
        }
        else {
        	throw new NotFoundException();
        }
     }

    public void delete(String id) throws NotFoundException {
        System.out.println(">>> CustomerCollectionImpl.delete id=" + id);
        if(entries.containsKey(id)){
        	entries.remove(id);
        	lastModified = new Date();
        }
        else {
        	throw new NotFoundException();
		}
     }

    public Feed getFeed() {
        System.out.println(">>> CustomerCollectionImpl.getFeed");

        Feed feed = this.abdera.getFactory().newFeed();
        feed.setId("customers" + this.hashCode() ); // provide unique id for feed instance.
        feed.setTitle("customers");
        feed.setSubtitle("This is a sample feed");
        feed.setUpdated(lastModified);
        feed.addLink("");
        feed.addLink("", "self");

        for (Entry entry : entries.values()) {
            feed.addEntry(entry);
        }

        return feed;
    }

    public Feed query(String queryString) {
        System.out.println(">>> CustomerCollectionImpl.query collection " + queryString);
        return getFeed();
    }

    // This method used for testing.
    protected void testPut(String value) {
        String id = "urn:uuid:customer-" + UUID.randomUUID().toString();

        Entry entry = abdera.getFactory().newEntry();
        entry.setId(id);
        entry.setTitle("customer " + value);

        Content content = this.abdera.getFactory().newContent();
        content.setContentType(Content.Type.TEXT);
        content.setValue(value);

        entry.setContentElement(content);

        entry.addLink("" + id, "edit");
        entry.addLink("" + id, "alternate");

        entry.setUpdated(new Date());

        entries.put(id, entry);
        System.out.println(">>> id=" + id);
    }

}
