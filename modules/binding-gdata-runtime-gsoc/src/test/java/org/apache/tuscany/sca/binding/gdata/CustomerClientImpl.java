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

import com.google.gdata.client.Query;
import com.google.gdata.data.Feed;
import com.google.gdata.data.Entry;
import com.google.gdata.data.PlainTextConstruct;

import org.apache.tuscany.sca.binding.gdata.collection.Collection;
import org.apache.tuscany.sca.binding.gdata.collection.NotFoundException;
import org.osoa.sca.annotations.Reference;

public class CustomerClientImpl implements CustomerClient {

    @Reference
    public Collection resourceCollection;

    // Call Collection.getFeed()
    public Feed clientGetFeed() throws Exception {
        // Get all the entries from the provider, return in a single feed
        System.out.println(">>> get the feed from the provider service");
        Feed feed = resourceCollection.getFeed();
        System.out.println("\n\n!!! Fetched feed title:  " + feed.getTitle().getPlainText());
        int i = 0;
        for (Object o : feed.getEntries()) {
            com.google.gdata.data.Entry e = (com.google.gdata.data.Entry)o;
            System.out.print("Entry" + i + "\t");
            System.out.println(" id = " + e.getId() + "\t title = " + e.getTitle().getPlainText());
            i++;
        }
        return feed;
    }
        
    
    // Call Collection.get(entryID)
    public Entry clientGetEntry(String entryID) throws Exception {
        // Get an existing entry based on its id
        System.out.println(">>> get an existing entry from the provider service");
        Entry entry = resourceCollection.get(entryID);
        System.out.println("\n\n!!! Entry retrieved with id=" + entry.getId()
            + " title="
            + entry.getTitle().getPlainText());
        return entry;
    }

       
    // Call Collection.post(newEntry)
    public Entry clientPost(Entry newEntry) throws Exception {
        // Put a new entry to the provider
        System.out.println(">>> post a new entry to the provider service");
        Entry confirmedNewEntry = resourceCollection.post(newEntry);
        System.out.println("!!! New entry posted with id=" + confirmedNewEntry.getId()
            + " title="
            + confirmedNewEntry.getTitle().getPlainText());        
        System.out.println("\n");
        return confirmedNewEntry;
    }

    
    // Call Collection.delete(newEntry)
    public void clientDelete(String entryID) throws Exception {
        // Put a new entry to the provider
        System.out.println(">>> delete an existing entry from the provider service");
        System.out.println(">>> delete id=" + "urn:uuid:customer-1");
        resourceCollection.delete(entryID);
        System.out.println("!!! entry with id" + entryID);
        System.out.println("\n");
    }
    
    
    
    // Call Collection.put(entry, updatedTitle)
    public void clientPut(String entryID, String newTitle) throws Exception {

        System.out.println("clientPut");
        // Put a new entry to the provider
        System.out.println(">>> put id=" + entryID + " title=" + newTitle);
        Entry entry = resourceCollection.get(entryID);
        
        //change the title of this entry
        entry.setTitle(new PlainTextConstruct(newTitle));
        resourceCollection.put(entryID, entry);
        System.out.println("!!! Updated entry with id=" + entry.getId() + " title=" + entry.getTitle());
        System.out.println("\n");
    }

    

    // Call Collection.getFeed()
    public Feed clientQuery(Query query) throws Exception {
        // Get all the entries from the provider, return in a single feed
        System.out.println(">>> query the service");
        Feed feed = resourceCollection.query(query);
        System.out.println("\n\n!!! Query result feed title:  " + feed.getTitle().getPlainText());
        int i = 0;
        for (Object o : feed.getEntries()) {
            com.google.gdata.data.Entry e = (com.google.gdata.data.Entry)o;
            System.out.print("Entry" + i + "\t");
            System.out.println(" id = " + e.getId() + "\t title = " + e.getTitle().getPlainText());
            i++;
        }
        return feed;
    }
   
}
