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

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;

import com.tuscanyscatours.blog.BlogPost;

/**
 * Implementation of the blog feed that uses the Tuscany Data API so
 * that it is independent of any Feed APIs such as Atom and RSS.  
 */
public class GenericBlogFeedImpl extends BaseBlogFeedImpl {

    /**
     * Implementation of the getAll() method from the Tuscany API
     * that will return all of the blog posts as generic Tuscany
     * feed items.
     * 
     * @return All blog entries
     */
    public Entry<Object, Object>[] getAll() {
        final List<BlogPost> posts = getAllBlogPosts();

        final Entry<Object, Object>[] entries = new Entry[posts.size()];
        int i = 0;
        for (BlogPost post : posts) {
            entries[i++] = convertBlogPostToFeedItem(post);
        }

        return entries;
    }

    /**
     * Converts a blog post to a Tuscany API feed item.
     * 
     * @param post The blog post to convert
     * @return The blog post as a Tuscany API feed item
     */
    private Entry<Object, Object> convertBlogPostToFeedItem(BlogPost post) {
        // Convert Blog entry into an Item
        final Item item =
            new Item(post.getTitle(), post.getContent(), post.getLink(), post.getRelated(), post.getUpdated());

        // Add item to entry 
        final Entry<Object, Object> entry = new Entry<Object, Object>(nextBlogID(), item);

        return entry;
    }
}
