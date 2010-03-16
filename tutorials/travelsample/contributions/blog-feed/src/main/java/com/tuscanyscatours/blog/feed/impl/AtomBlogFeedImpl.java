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

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;

import com.tuscanyscatours.blog.BlogPost;

/**
 * An Atom feed that implements the org.apache.tuscany.sca.binding.atom.collection.Collection
 * interface and uses the Atom APIs to construct the Atom feed.
 */
public class AtomBlogFeedImpl extends BaseBlogFeedImpl implements
    org.apache.tuscany.sca.binding.atom.collection.Collection {

    /**
     * Gets an Atom feed containing all the blog posts.
     * 
     * @return An Atom feed containing all the blog posts.
     */
    public Feed getFeed() {
        // Create SCA Tours blog Atom feed
        final Factory factory = Abdera.getNewFactory();
        final Feed feed = factory.newFeed();
        feed.setTitle(FEED_TITLE);
        feed.setSubtitle(FEED_DESCRIPTION);
        feed.addAuthor(FEED_AUTHOR);

        // Get all blog posts and convert to Atom entries
        final List<BlogPost> blogEntries = getAllBlogPosts();
        for (BlogPost blogEntry : blogEntries) {
            final Entry entry = factory.newEntry();
            entry.setId(nextBlogID());
            entry.addAuthor(blogEntry.getAuthor());
            entry.setTitle(blogEntry.getTitle());
            entry.setContentAsHtml(blogEntry.getContent());
            entry.setUpdated(blogEntry.getUpdated());
            entry.addLink(blogEntry.getLink());
            feed.addEntry(entry);
        }

        return feed;
    }

    /**
     * Query the feed.
     * 
     * @param query The query
     * @return Always returns null as method not implemented
     */
    public Feed query(String query) {
        // Not implemented
        return null;
    }

    /**
     * Posts a new entry to the blog.
     * 
     * @param entry The new entry
     * @return Always returns null as method not implemented
     */
    public Entry post(Entry entry) {
        // Not implemented
        return null;
    }

    /**
     * Gets the specified entry from the blog.
     * 
     * @param id ID of the entry to get
     * @return Not used
     * @throws NotFoundException Always thrown as method not implemented
     */
    public Entry get(String id) throws NotFoundException {
        // Not implemented
        throw new NotFoundException("You are not allowed to update entries");
    }

    /**
     * Updates the specified entry on the blog.
     * 
     * @param id ID of the entry to update
     * @param entry The new entry
     * @throws NotFoundException Always thrown as method not implemented
     */
    public void put(String id, Entry entry) throws NotFoundException {
        // Not implemented
        throw new NotFoundException("You are not allowed to update entries");
    }

    /**
     * Deletes the specified entry from the blog.
     * 
     * @param id ID of the entry to delete
     * @throws NotFoundException Always thrown as method not implemented
     */
    public void delete(String id) throws NotFoundException {
        // Not implemented
        throw new NotFoundException("You are not allowed to delete entries");
    }
}
