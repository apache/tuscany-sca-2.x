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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.tuscanyscatours.blog.BlogPost;

/**
 * Base class for all blog feeds that provides the common methods
 * that are shared by all the blog feed implementations.
 */
public abstract class BaseBlogFeedImpl {

    /**
     * Title of the blog.feed.
     */
    protected static final String FEED_TITLE = "Tuscany SCA Tours Blog Feed";

    /**
     * Description of the blog feed.
     */
    protected static final String FEED_DESCRIPTION = "Feed contianing the latest blog posts from Tuscany SCA Tours";

    /**
     * Author of the blog feed.
     */
    protected static final String FEED_AUTHOR = "SCA Tours CEO";

    /**
     * Used to generate unique IDs for the blog entries.
     */
    protected static final AtomicInteger ID_GEN = new AtomicInteger();

    /**
     * Generates the next blog entry ID.
     * 
     * @return Next blog entry ID
     */
    protected String nextBlogID() {
        return Integer.toString(ID_GEN.incrementAndGet());
    }

    /**
     * Retrieves a list of all blog posts.
     * 
     * @return A list of all blog posts.
     */
    public List<BlogPost> getAllBlogPosts() {
        // Note: To keep things simple, we will just hard code a sample post.
        // A proper implementation would load all blog posts from some resource
        // such as files or a database.
        List<BlogPost> blogEntries = new ArrayList<BlogPost>();

        // Create a sample entry
        final BlogPost samplePost =
            new BlogPost(
                         FEED_AUTHOR,
                         "Apache Tuscany in Action book features SCA Tours",
                         "We are famous as SCA Tours has been featured in the Apache Tuscany in Action book published by Manning",
                         new Date(), "http://www.manning.com/laws/", null);

        // Add sample post to the list of posts
        blogEntries.add(samplePost);

        return blogEntries;
    }
}
