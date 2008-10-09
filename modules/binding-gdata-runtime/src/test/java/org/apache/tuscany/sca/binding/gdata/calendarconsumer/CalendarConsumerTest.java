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
package org.apache.tuscany.sca.binding.gdata.calendarconsumer;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Feed;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.extensions.EventEntry;
import com.google.gdata.data.extensions.When;

//FIX-ME: the tests are executed in an incorrect order
public class CalendarConsumerTest {

    private static Node node;
    private Feed feed;
    private BaseEntry returnedEntry;
    private BaseEntry searchedEntry;
    private BaseEntry updatedEntry;
    private static CalendarConsumerImpl consumer;

    @BeforeClass
    public static void init() {
        String contribution = ContributionLocationHelper.getContributionLocation(CalendarConsumer.class);
        node = NodeFactory.newInstance().createNode(
                                                     "org/apache/tuscany/sca/binding/gdata/CalendarConsumer.composite", new Contribution("consumer", contribution));
        node.start();
        consumer = node.getService(CalendarConsumerImpl.class, "CalendarConsumer");
    }

    @AfterClass
    public static void close() {
        node.stop();
        node.destroy();
    }

    @Test
    public void getFeed() {
        System.out.println("getfeed");
        feed = (Feed) consumer.getFeed();
        assertNotNull(feed);
    }

    @Test
    public void post() {
        System.out.println("post");
        EventEntry entry = new EventEntry();

        entry.setTitle(new PlainTextConstruct("GSoC extra activity"));
        entry.setContent(new PlainTextConstruct("Reading the book Beautiful Code"));

        Person author = new Person("GSoC Student 2008", null, "gsocstudent2008@gmail.com");
        entry.getAuthors().add(author);

        DateTime startTime = DateTime.parseDateTime("2008-07-22T15:00:00-08:00");
        DateTime endTime = DateTime.parseDateTime("2008-07-22T17:00:00-08:00");
        When eventTimes = new When();
        eventTimes.setStartTime(startTime);
        eventTimes.setEndTime(endTime);
        entry.addTime(eventTimes);

        returnedEntry = consumer.post(entry);
        assertNotNull(returnedEntry);
    }

    @Test
    public void get() {
        System.out.println("get");
        try {
            searchedEntry = consumer.get(returnedEntry.getSelfLink().getHref());
            assertNotNull(searchedEntry);
        } catch (NotFoundException ex) {
            Logger.getLogger(CalendarConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void put() {
        System.out.println("put");
        try {
            searchedEntry.setTitle(new PlainTextConstruct("GSoC extra activity(opcional)"));
            updatedEntry = consumer.put(searchedEntry.getEditLink().getHref(), searchedEntry);
            assertNotNull(updatedEntry);
        } catch (NotFoundException ex) {
            Logger.getLogger(CalendarConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void delete() {
        System.out.println("delete");
        try {
            consumer.delete(updatedEntry.getEditLink().getHref());
        } catch (NotFoundException ex) {
            Logger.getLogger(CalendarConsumerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void query() {
        System.out.println("query");
        feed = (Feed) consumer.query("Students");
        assertNotNull(feed);
    }//    public void testCustomerCollection() throws Exception {
//
//        System.out.println(
//                "\n//--------------------------" +
//                "\n// Get the Feed" +
//                "\n//--------------------------\n");
//
//        Feed feed = (Feed) resourceCollection.getFeed();
//
//        System.out.println("Feed content - " + feed.getUpdated().toString() + ":\n");
//        for (Entry e : feed.getEntries()) {
//            System.out.println("# " + e.getTitle().getPlainText());
//        }
//
//        System.out.println(
//                "\n//--------------------------" +
//                "\n// Post a new Entry" +
//                "\n//--------------------------\n");
//
//        EventEntry entry = new EventEntry();
//
//        entry.setTitle(new PlainTextConstruct("GSoC extra activity"));
//        entry.setContent(new PlainTextConstruct("Reading the book Beautiful Code"));
//
//        Person author = new Person("GSoC Student 2008", null, "gsocstudent2008@gmail.com");
//        entry.getAuthors().add(author);
//
//        DateTime startTime = DateTime.parseDateTime("2008-06-19T15:00:00-08:00");
//        DateTime endTime = DateTime.parseDateTime("2008-06-19T17:00:00-08:00");
//        When eventTimes = new When();
//        eventTimes.setStartTime(startTime);
//        eventTimes.setEndTime(endTime);
//        entry.addTime(eventTimes);
//
//        BaseEntry returnedEntry = resourceCollection.post(entry);
//
//        System.out.println("# " + returnedEntry.getTitle().getPlainText());
//
//        System.out.println(
//                "\n//--------------------------" +
//                "\n// Get an Entry" +
//                "\n//--------------------------\n");
//
//        BaseEntry searchedEntry = resourceCollection.get(returnedEntry.getSelfLink().getHref());
//
//        System.out.println("# " + searchedEntry.getTitle().getPlainText());
//
//        System.out.println(
//                "\n//--------------------------" +
//                "\n// Update an Entry" +
//                "\n//--------------------------\n");
//
//        searchedEntry.setTitle(new PlainTextConstruct("GSoC extra activity(opcional)"));
//        BaseEntry updatedEntry = resourceCollection.put(searchedEntry.getEditLink().getHref(), searchedEntry);
//
//        System.out.println("# " + updatedEntry.getTitle().getPlainText());
//
//        System.out.println(
//                "\n//--------------------------" +
//                "\n// Delete an Entry" +
//                "\n//--------------------------\n");
//
//        resourceCollection.delete(updatedEntry.getEditLink().getHref());
//
//        System.out.println(
//                "\n//--------------------------" +
//                "\n// Execute a query" +
//                "\n//--------------------------\n");
//
//        feed = (Feed) resourceCollection.query("Students");
//
//        System.out.println("Feed content - " + feed.getUpdated().toString() + ":\n");
//        for (Entry e : feed.getEntries()) {
//            System.out.println("# " + e.getTitle().getPlainText());
//        }
//
//    }
}
