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

package org.apache.tuscany.sca.binding.atom.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.data.collection.Collection;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

public class AggregatorImpl implements Aggregator {

    @Reference(required = false)
    public Collection<String, Item> atomFeed1;
    
    @Reference(required = false)
    public Collection<String, Item> atomFeed2;

    @Reference(required = false)
    public Sort sort;

    @Property
    public String feedTitle = "Aggregated Feed";
    @Property
    public String feedDescription = "Anonymous Aggregated Feed";
    @Property
    public String feedAuthor = "anonymous";

    public Item get(String id) {

        try {
            if (atomFeed1.get(id) != null)
                return atomFeed1.get(id);
        } catch (NotFoundException ex) {
            ex.printStackTrace();
        }

        try {
            if (atomFeed2.get(id) != null)
                return atomFeed2.get(id);
        } catch (NotFoundException ex) {
            ex.printStackTrace();
        }

        return null;

    }

    public Entry<String, Item>[] getAll() {

        // Aggregate entries from atomFeed1, atomFeed2, rssFeed1 and rssFeed2
        List<Entry> entries = new ArrayList<Entry>();
        if (atomFeed1 != null) {
            try {
                for (Entry entry : atomFeed1.getAll()) {
                    entries.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (atomFeed2 != null) {
            try {
                for (Entry entry : atomFeed2.getAll()) {
                    entries.add(entry);
                }
            } catch (Exception e) {
            }
        }

        // Sort entries by published date
        if (sort != null) {
            entries = sort.sort(entries);
        }

        return entries.toArray(new Entry[entries.size()]);

    }

    public String post(String key, Item entry) {
        return null;
    }

    public void put(String id, Item entry) {
    }

    public Entry<String, Item>[] query(String queryString) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        Entry<String, Item>[] allFeed = getAll();
        if (queryString.startsWith("title=")) {
            String title = queryString.substring(6);

            for (Entry<String, Item> entry : allFeed) {
                if (entry.getData().getTitle().contains(title)) {
                    entries.add(entry);
                }
            }
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public void delete(String id) {

    }
}
