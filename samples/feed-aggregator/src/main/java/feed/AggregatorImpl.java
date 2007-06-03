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
import java.util.List;

import org.apache.tuscany.sca.binding.feed.Feed;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * Implementation of an SCA component that aggregates several
 * Atom and RSS feeds.
 *
 * @version $Rev$ $Date$
 */
public class AggregatorImpl implements Feed {

    @Reference
    public Feed feed1;
    @Reference
    public Feed feed2;
    @Reference(required = false)
    public Sort sort;

    @Property
    public String feedTitle = "Aggregated Feed";
    @Property
    public String feedDescription = "Anonymous Aggregated Feed";
    @Property
    public String feedAuthor = "anonymous";
    @Property
    public String feedLink = "http://incubator.apache.org/tuscany";

    @SuppressWarnings("unchecked")
    public SyndFeed get() {
        
        // Create a new Feed
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle(feedTitle);
        feed.setDescription(feedDescription);
        feed.setAuthor(feedAuthor);
        feed.setLink(feedLink);

        // Aggregate entries from feed1 and feed2
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        entries.addAll(feed1.get().getEntries());
        entries.addAll(feed2.get().getEntries());

        // Sort entries by published date
        if (sort != null)
            feed.setEntries(sort.sort(entries));
        else
            feed.setEntries(entries);
        return feed;
    }
}
