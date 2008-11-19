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

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.osoa.sca.annotations.Reference;

public class CustomerClientImpl implements CustomerClient {

    protected final Abdera abdera = new Abdera();
    
    @Reference
    public Collection customerCollection;

    public void testCustomerCollection() throws Exception {

        Entry newEntry = newEntry("Sponge Bob");
        System.out.println(">>> post entry=" + newEntry.getTitle());
        newEntry = customerCollection.post(newEntry);
        System.out.println("<<< post id=" + newEntry.getId() + " entry=" + newEntry.getTitle());

        newEntry = newEntry("Jane Bond");
        System.out.println(">>> post entry=" + newEntry.getTitle());
        newEntry = customerCollection.post(newEntry);
        System.out.println("<<< post id=" + newEntry.getId() + " entry=" + newEntry.getTitle());

        System.out.println(">>> get id=" + newEntry.getId());
        Entry entry = customerCollection.get(newEntry.getId().toString());
        System.out.println("<<< get id=" + entry.getId() + " entry=" + entry.getTitle());

        System.out.println(">>> put id=" + newEntry.getId() + " entry=" + entry.getTitle());
        customerCollection.put(entry.getId().toString(), updateEntry(entry, "James Bond"));
        System.out.println("<<< put id=" + entry.getId() + " entry=" + entry.getTitle());

        System.out.println(">>> delete id=" + entry.getId());
        customerCollection.delete(entry.getId().toString());
        System.out.println("<<< delete id=" + entry.getId());

        System.out.println(">>> get collection");
        Feed feed = customerCollection.getFeed();
        System.out.println("<<< get collection");
        for (Object o : feed.getEntries()) {
            Entry e = (Entry)o;
            System.out.println("id = " + e.getId() + " entry = " + e.getTitle());
        }
    }

    public Collection getCustomerCollection() {
    	return customerCollection;
    }
    
    private Entry newEntry(String value) {

        Entry entry = this.abdera.newEntry();
        entry.setTitle("customer " + value);

        Content content = this.abdera.getFactory().newContent();
        content.setContentType(Content.Type.TEXT);
        content.setValue(value);
        
        entry.setContentElement(content);

        return entry;
    }

    private Entry updateEntry(Entry entry, String value) {

        entry.setTitle("customer " + value);

        Content content = this.abdera.getFactory().newContent();
        content.setContentType(Content.Type.TEXT);
        content.setValue(value);

        entry.setContentElement(content);

        return entry;
    }
}
