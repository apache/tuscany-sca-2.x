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

import org.apache.abdera.Abdera;
import org.apache.tuscany.sca.binding.gdata.collection.Collection;
import org.osoa.sca.annotations.Reference;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.Entry;
import com.google.gdata.data.PlainTextConstruct;

public class CustomerClientImpl implements CustomerClient {

    protected final Abdera abdera = new Abdera();

    @Reference
    public Collection resourceCollection;

    // Test Collection.getFeed()
    public void testGetFeed() throws Exception {
        // Get all the entries from the provider
        System.out.println("\n\n+++++++++++ get the feed from the provider service +++++++++++");
        com.google.gdata.data.Feed feed = resourceCollection.getFeed();
        System.out.println("\n\n\n!!!Fetched feed title:  " + feed.getTitle().getPlainText());
        int i = 0;
        for (Object o : feed.getEntries()) {
            com.google.gdata.data.Entry e = (com.google.gdata.data.Entry)o;
            System.out.print("Entry" + i + "\t");
            System.out.println(" id = " + e.getId() + "\t title = " + e.getTitle().getPlainText());
            i++;
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("\n\n\n");
    }

    // Test Collection.get(entryID)
    public void testGetEntry() throws Exception {

        // Get an existing entry based on its id
        System.out.println("+++++++++++ get an existing entry from the provider service +++++++++++");
        System.out.println(">>> get an entry based on its id");
        Entry entry = resourceCollection.get("urn:uuid:customer-0");
        System.out.println("\n\n\n!!! Entry retrieved with id=" + entry.getId()
            + " title="
            + entry.getTitle().getPlainText());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("\n\n\n");

    }

    // Test Collection.post(newEntry)
    public void testPost() throws Exception {

        // Put a new entry to the provider
        System.out.println("+++++++++++ post a new entry to the provider service +++++++++++");
        Entry newEntry = new Entry();
        newEntry.setTitle(new PlainTextConstruct("NewEntry title"));
        newEntry.setContent(new PlainTextConstruct("NewEntry Content"));
        System.out.println(">>> post a new entry:  title=" + newEntry.getTitle().getPlainText());
        Entry confirmedNewEntry = resourceCollection.post(newEntry);
        System.out.println("!!! New entry posted with id=" + confirmedNewEntry.getId()
            + " title="
            + confirmedNewEntry.getTitle());
        System.out.println("\n");
    }

    /*
     * public void testCustomerCollection() throws Exception { //Put a new entry
     * to the provider System.out.println("\n\n+++++++++++ post a new entry to
     * the provider service +++++++++++"); Entry newEntry =
     * newEntry("newtitle"); newEntry.addAuthor("newAuthor");
     * System.out.println(">>> post a new entry: title=" + newEntry.getTitle() + "
     * author=" + newEntry.getAuthor().getName()); newEntry =
     * resourceCollection.post(newEntry); System.out.println("!!! New entry
     * posted with id=" + newEntry.getId() + " title=" + newEntry.getTitle());
     * System.out.println("\n"); //Put a new entry to the provider
     * System.out.println("+++++++++++ post a new entry to the provider service
     * +++++++++++"); newEntry = newEntry("newtitleTemp");
     * newEntry.addAuthor("newAuthorTemp"); System.out.println(">>> post a new
     * entry: title=" + newEntry.getTitle() + " author=" +
     * newEntry.getAuthor().getName()); newEntry =
     * resourceCollection.post(newEntry); System.out.println("!!! New entry
     * posted with id=" + newEntry.getId() + " title=" + newEntry.getTitle());
     * System.out.println("\n"); //Get an existing entry based on its id
     * System.out.println("+++++++++++ get an existing entry from the provider
     * service +++++++++++"); System.out.println(">>> get an entry based on its
     * id"); Entry entry = resourceCollection.get(newEntry.getId().toString());
     * System.out.println("!!! Entry retrieved with id=" + entry.getId() + "
     * title=" + entry.getTitle()); System.out.println("\n"); //Update an
     * existing entry based on its id System.out.println("+++++++++++ update an
     * existing entry in the provider service +++++++++++");
     * System.out.println(">>> put id=" + entry.getId() + "
     * title=updatedTitle"); resourceCollection.put(entry.getId().toString(),
     * updateEntry(entry, "updatedTitle")); System.out.println("!!! Updated
     * entry with id=" + entry.getId() + " title=" + entry.getTitle());
     * System.out.println("\n"); System.out.println("+++++++++++ delete an
     * existing entry from the provider service +++++++++++");
     * System.out.println(">>> delete id=" + entry.getId());
     * resourceCollection.delete(entry.getId().toString());
     * System.out.println("!!! entry deleted"); //Get all the entries from the
     * provider System.out.println("\n\n+++++++++++ get all the entries from the
     * provider service +++++++++++"); Feed feed = resourceCollection.getFeed();
     * int i=0; for (Object o : feed.getEntries()) { Entry e = (Entry)o;
     * System.out.print("Entry" + i + "\t"); System.out.println(" id = " +
     * e.getId() + "\t title = " + e.getTitle()+ "\t author = " +
     * e.getAuthor().getName()); i++; } }
     */

    private Entry newEntry(String value) {
        /*
         * Entry entry = this.abdera.newEntry(); entry.setTitle(value); Content
         * content = this.abdera.getFactory().newContent();
         * content.setContentType(Content.Type.TEXT); content.setValue(value);
         * entry.setContentElement(content);
         */

        Entry entry = new Entry();
        entry.setTitle(new PlainTextConstruct(value));
        entry.setContent(new PlainTextConstruct(value));
        entry.setUpdated(DateTime.now());
        // entry.addHtmlLink("http://www.google.com", "languageType", "title");
        return entry;
    }

    private Entry updateEntry(Entry entry, String value) {
        /*
         * entry.setTitle(value); Content content =
         * this.abdera.getFactory().newContent();
         * content.setContentType(Content.Type.TEXT); content.setValue(value);
         * entry.setContentElement(content);
         */
        entry.setTitle(new PlainTextConstruct("Entry title: " + value));
        entry.setContent(new PlainTextConstruct(value));
        return entry;
    }

}
