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

package com.tuscanyscatours.blog.feed.impl;

import java.util.List;

import org.apache.tuscany.sca.binding.rss.collection.NotFoundException;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.tuscanyscatours.blog.BlogPost;

/**
 * An RSS feed that implements the org.apache.tuscany.sca.binding.rss.collection.Collection
 * interface and uses the RSS APIs to construct the RSS feed.
 */
public class RSSBlogFeedImpl extends BaseBlogFeedImpl implements
    org.apache.tuscany.sca.binding.rss.collection.Collection {

    /**
     * Gets an RSS feed containing all the blog posts.
     * 
     * @return An RSS feed containing all the blog posts.
     */
    public SyndFeed getFeed() {
        // Create SCA Tours blog RSS feed
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle(FEED_TITLE);
        feed.setDescription(FEED_DESCRIPTION);
        feed.setAuthor(FEED_AUTHOR);

        // Get all blog posts and convert to RSS entries
        final List<BlogPost> blogEntries = getAllBlogPosts();
        for (BlogPost blogEntry : blogEntries) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setUri(nextBlogID());
            entry.setAuthor(blogEntry.getAuthor());
            entry.setTitle(blogEntry.getTitle());

            SyndContent content = new SyndContentImpl();
            content.setType("text");
            content.setValue(blogEntry.getContent());

            entry.setPublishedDate(blogEntry.getUpdated());
            entry.setLink(blogEntry.getLink());

            feed.getEntries().add(entry);
        }

        return feed;
    }

    /**
     * Query the feed.
     * 
     * @param query The query
     * @return Always returns null as method not implemented
     */
    public SyndFeed query(String query) {
        // Not implemented
        return null;
    }

    public SyndEntry get(String id) throws NotFoundException {
        // Not implemented
        return null;
    }

    public List<SyndEntry> getAll() throws NotFoundException {
        // Not implemented
        return null;
    }
}
