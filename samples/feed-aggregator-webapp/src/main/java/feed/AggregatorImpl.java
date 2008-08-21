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
package feed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.parser.Parser;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * Implementation of an SCA component that aggregates several
 * Atom and RSS feeds.
 *
 * @version $Rev$ $Date$
 */
public class AggregatorImpl implements org.apache.tuscany.sca.binding.atom.collection.Collection {

    @Reference(required = false)
    public Collection atomFeed1;
    @Reference(required = false)
    public Collection atomFeed2;
    
    @Reference(required = false)
    public org.apache.tuscany.sca.binding.rss.collection.Collection rssFeed1;
    @Reference(required = false)
    public org.apache.tuscany.sca.binding.rss.collection.Collection rssFeed2;
    
    @Reference(required = false)
    public Sort sort;

    @Property
    public String feedTitle = "Aggregated Feed";
    @Property
    public String feedDescription = "Anonymous Aggregated Feed";
    @Property
    public String feedAuthor = "anonymous";
    
    public Feed getFeed() {
        
        // Create a new Feed
        Factory factory = Abdera.getNewFactory();
        Feed feed = factory.newFeed();
        feed.setTitle(feedTitle);
        feed.setSubtitle(feedDescription);
        Person author = factory.newAuthor();
        author.setName(feedAuthor);
        feed.addAuthor(author);
        feed.addLink("http://tuscany.apache.org", "alternate");

        // Aggregate entries from atomFeed1, atomFeed2, rssFeed1 and rssFeed2
        List<Entry> entries = new ArrayList<Entry>();
        if (atomFeed1 != null) {
            try {
                entries.addAll(atomFeed1.getFeed().getEntries());
            } catch (Exception e) {}
        }
        if (atomFeed2 != null) {
            try {
                entries.addAll(atomFeed2.getFeed().getEntries());
            } catch (Exception e) {}
        }
        if (rssFeed1 != null) {
            try {
                entries.addAll(atomFeed(rssFeed1.getFeed()).getEntries());
            } catch (Exception e) {}
        }
        if (rssFeed2 != null) {
            try {
                entries.addAll(atomFeed(rssFeed2.getFeed()).getEntries());
            } catch (Exception e) {}
        }

        // Sort entries by published date
        if (sort != null) {
            entries = sort.sort(entries);
        }
        
        // Add the entries to the new feed
        for (Entry entry: entries) {
            feed.addEntry(entry);
        }
        
        return feed;
    }

    public Feed query(String queryString) {
        Factory factory = Abdera.getNewFactory();
        Feed feed = factory.newFeed();
        feed.setTitle(feedTitle);
        feed.setSubtitle(feedDescription);
        Person author = factory.newAuthor();
        author.setName(feedAuthor);
        feed.addAuthor(author);
        feed.addLink("http://tuscany.apache.org", "alternate");
        
        Feed allFeed = getFeed();
        if (queryString.startsWith("title=")) {
            String title = queryString.substring(6);

            for (Entry entry: allFeed.getEntries()) {
                if (entry.getTitle().contains(title)) {
                    feed.addEntry(entry);
                }
            }
        }
        return feed;
    }

    public void delete(String id) throws NotFoundException {
    }

    public Entry get(String id) throws NotFoundException {
        return null;
    }

    public Entry post(Entry entry) {
        return null;
    }

    public void put(String id, Entry entry) throws NotFoundException {
    }

    /**
     * Convert a ROME feed to an Abdera feed.
     * 
     * @param romeFeed
     * @return
     */
    private static Feed atomFeed(SyndFeed syndFeed) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        syndFeed.setFeedType("atom_1.0");
        SyndFeedOutput syndOutput = new SyndFeedOutput();
        try {
            syndOutput.output(syndFeed, new OutputStreamWriter(bos));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Parser parser = Abdera.getNewParser();
        Document<Feed> document = parser.parse(new ByteArrayInputStream(bos.toByteArray()));
        
        return document.getRoot();
    }
}
