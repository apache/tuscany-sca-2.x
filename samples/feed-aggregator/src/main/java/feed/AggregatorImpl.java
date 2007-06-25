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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.sca.binding.feed.ResourceCollection;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;

/**
 * Implementation of an SCA component that aggregates several
 * Atom and RSS feeds.
 *
 * @version $Rev$ $Date$
 */
public class AggregatorImpl implements ResourceCollection {

    @Reference
    public ResourceCollection feed1;
    @Reference
    public ResourceCollection feed2;
    @Reference(required = false)
    public Sort sort;

    @Property
    public String feedTitle = "Aggregated Feed";
    @Property
    public String feedDescription = "Anonymous Aggregated Feed";
    @Property
    public String feedAuthor = "anonymous";

    @SuppressWarnings("unchecked")
    public com.sun.syndication.feed.atom.Feed getCollection() {
        
        // Create a new Feed
        com.sun.syndication.feed.atom.Feed feed = new com.sun.syndication.feed.atom.Feed();
        feed.setTitle(feedTitle);
        Content subtitle = new Content();
        subtitle.setValue(feedDescription);
        feed.setSubtitle(subtitle);
        Person author = new Person();
        author.setName(feedAuthor);
        feed.setAuthors(Collections.singletonList(author));
        Link link = new Link();
        link.setHref("http://incubator.apache.org/tuscany");
        feed.getAlternateLinks().add(link);

        // Aggregate entries from feed1 and feed2
        List<Entry> entries = new ArrayList<Entry>();
        entries.addAll(feed1.getCollection().getEntries());
        entries.addAll(feed2.getCollection().getEntries());

        // Sort entries by published date
        if (sort != null)
            feed.setEntries(sort.sort(entries));
        else
            feed.setEntries(entries);
        return feed;
    }
}
