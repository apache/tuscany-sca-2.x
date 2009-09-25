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

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.abdera.model.Link;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for using an Atom feed that does not implement
 * the Collections interface but does have a getAll() method.
 */
public class AtomFeedNonCollectionTest {
    /**
     * Used to generate unique IDs for the feed entries.
     */
    private static final AtomicInteger ID_GEN = new AtomicInteger();

    /**
     * Data used for creating test feed entries.
     */
    private static final String[] FEED_ENTRY_TITLES = {"Apache Tuscany", "Apache"};

    /**
     * Data used for creating test feed entries.
     */
    private static final String[] FEED_ENTRY_CONTENT = {"This is Apache Tuscany", "This is Apache"};

    /**
     * Data used for creating test feed entries.
     */
    private static final String[] FEED_ENTRY_LINK = {"http://tuscany.apache.org", "http://www.apache.org"};

    /**
     * The number of test feed entries.
     */
    private static final int FEED_ENTRY_COUNT = FEED_ENTRY_TITLES.length;

    private static SCADomain scaConsumerDomain;
    private static SCADomain scaProviderDomain;
    private static CustomerClient testService;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println(">>>AtomFeedNonCollectionTest.init entry");
        scaProviderDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/ProviderNonCollection.composite");
        scaConsumerDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/atom/Consumer.composite");
        testService = scaConsumerDomain.getService(CustomerClient.class, "CustomerClient");
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println(">>>AtomFeedNonCollectionTest.destroy entry");
        if (scaConsumerDomain != null) {
            scaConsumerDomain.close();
        }
        if (scaProviderDomain != null) {
            scaProviderDomain.close();
        }
    }

    /**
     * Make sure everything has been initialised correctly.
     */
    @Before
    public void testPrelim() {
        Assert.assertNotNull(scaProviderDomain);
        Assert.assertNotNull(scaConsumerDomain);
        Assert.assertNotNull(testService);

        // Add some entries to the Atom feed
        final Entry<Object, Object>[] testEntries = new Entry[FEED_ENTRY_COUNT];
        for (int i = 0; i < FEED_ENTRY_COUNT; i++) {
            testEntries[i] = createFeedEntry(FEED_ENTRY_TITLES[i], FEED_ENTRY_CONTENT[i], FEED_ENTRY_LINK[i]);
        }
        CustomerNonCollectionImpl.entries = testEntries;
    }

    /**
     * Post test cleanup
     */
    @After
    public void testPostCleanup()
    {
        // Clear any old feed entries that have been added by previous tests
        CustomerNonCollectionImpl.entries = null;
    }

    /**
     * Test that we can retrieve entries from a feed that does not implement
     * the Collection interface.
     */
    @Test
    public void testThatCanGetFeedEntriesFromNonCollectionImplementation() {
        // Get the entries from the feed
        final Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);
        final List<org.apache.abdera.model.Entry> entries = resourceCollection.getFeed().getEntries();

        // Validate the feed entries
        Assert.assertNotNull(entries);
        Assert.assertEquals(FEED_ENTRY_COUNT, entries.size());
        for (int i = 0; i < FEED_ENTRY_COUNT; i++) {
            final org.apache.abdera.model.Entry entry = entries.get(i);
            Assert.assertEquals(FEED_ENTRY_TITLES[i], entry.getTitle());
            Assert.assertEquals(FEED_ENTRY_CONTENT[i], entry.getContent());
            final List<Link> links = entry.getLinks();
            Assert.assertNotNull(links);
            Assert.assertEquals(1, links.size());
            final Link link = links.get(0);
            final String linkStr = link.getHref().toString();
            Assert.assertEquals(FEED_ENTRY_LINK[i], linkStr);
        }
    }

    /**
     * Creates a feed entry.
     * 
     * @param title Title for the feed entry
     * @param content Contents of the feed entry
     * @param link Link for the feed entry
     * @return A new feed entry.
     */
    private Entry<Object, Object> createFeedEntry(String title, String content, String link) {
        final Item item = new Item(title, content, link, null, new Date());
        final Entry<Object, Object> entry = new Entry<Object, Object>(nextFeedID(), item);
        return entry;
    }

    /**
     * Generates the feed entry ID.
     * 
     * @return Next feed entry ID
     */
    private String nextFeedID() {
        return Integer.toString(ID_GEN.incrementAndGet());
    }

    /**
     * Tests that the title of the feed can be set by the title
     * attribute on the binding.atom
     */
    @Test
    public void testThatFeedTitleSet() {
        final String expectedFeedTitle = "Atom binding Non Collection";

        // Get the title of the feed
        final Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);
        final String feedTitle = resourceCollection.getFeed().getTitle();

        // Validate the title
        Assert.assertEquals(expectedFeedTitle, feedTitle);
    }

    /**
     * Tests that the description of the feed can be set by the description
     * attribute on the binding.atom
     */
    @Test
    public void testThatFeedDescriptionSet() {
        final String expectedFeedDescription = "Feed used for unit testing";

        // Get the description of the feed
        final Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);
        final String feedDescription = resourceCollection.getFeed().getSubtitle();

        // Validate the description
        Assert.assertEquals(expectedFeedDescription, feedDescription);
    }
}
