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

package com.tuscanyscatours.feedlogger.impl;

import java.util.List;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.tuscanyscatours.feedlogger.FeedLogger;

public class FeedLoggerImpl implements FeedLogger {

    /**
     * Reference to the SCA Tours Blog Atom feed.
     */
    @Reference
    public org.apache.tuscany.sca.binding.atom.collection.Collection scaToursBlogAtom;

    /**
     * Reference to the SCA Tours Blog RSS feed.
     */
    @Reference
    public org.apache.tuscany.sca.binding.rss.collection.Collection scaToursBlogRSS;

    /**
     * Reference to the BBC News RSS feed.
     */
    @Reference
    public org.apache.tuscany.sca.binding.rss.collection.Collection bbcNews;

    /**
     * {@inheritDoc}
     */
    public void logFeeds(int maxEntriesPerFeed) {
        System.out.println("Logging SCA Tours Blog Atom feed:");
        logAtomFeed(scaToursBlogAtom, maxEntriesPerFeed);

        System.out.println("Logging SCA Tours Blog RSS feed:");
        logRSSFeed(scaToursBlogRSS, maxEntriesPerFeed);

        System.out.println("Logging BBC News feed:");
        logRSSFeed(bbcNews, maxEntriesPerFeed);
    }

    /**
     * Logs up to maxEntries entries from the specified feed.
     * 
     * @param maxEntries The maximum number of entries to log per feed 
     */
    private void logAtomFeed(org.apache.tuscany.sca.binding.atom.collection.Collection atomFeed, int maxEntries) {
        final Feed feed = atomFeed.getFeed();
        System.out.println("Feed: " + feed.getTitle());
        final List<Entry> entries = feed.getEntries();

        for (int i = 0; i < entries.size() && i < maxEntries; i++) {
            Entry entry = entries.get(i);
            System.out.println("Entry: " + entry.getTitle());
        }
        System.out.println();
    }

    /**
     * Logs up to maxEntries entries from the specified feed.
     * 
     * @param maxEntries The maximum number of entries to log
     */
    private void logRSSFeed(org.apache.tuscany.sca.binding.rss.collection.Collection rssFeed, int maxEntries) {
        SyndFeed feed = rssFeed.getFeed();
        System.out.println("Feed: " + feed.getTitle());

        List<SyndEntry> entries = feed.getEntries();
        for (int i = 0; i < entries.size() && i < maxEntries; i++) {
            SyndEntry entry = entries.get(i);
            System.out.println("Entry: " + entry.getTitle());
        }
        System.out.println();
    }
}
