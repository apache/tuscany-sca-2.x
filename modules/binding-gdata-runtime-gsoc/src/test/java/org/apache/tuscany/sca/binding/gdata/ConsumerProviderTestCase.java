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

package org.apache.tuscany.sca.binding.gdata;

import junit.framework.TestCase;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;

public class ConsumerProviderTestCase extends TestCase {

    private SCADomain scaDomainProvider = null;
    private SCADomain scaDomainConsumer = null;
    private CustomerClient testService = null;

    @Before
    @Override
    public void setUp() throws Exception {
        System.out.println("Method Test Start-----------------------------------------------------------------------");

        // Setup the local GData servlet (Service Binding test)
        scaDomainProvider = SCADomain.newInstance("org/apache/tuscany/sca/binding/gdata/Provider.composite");
        System.out.println("[Debug Info] Provider.composite ready...");

        // Initialize the GData client service (Reference Binding test)
        scaDomainConsumer = SCADomain.newInstance("org/apache/tuscany/sca/binding/gdata/Consumer.composite");
        testService = scaDomainConsumer.getService(CustomerClient.class, "CustomerClient");
    }

    @After
    @Override
    public void tearDown() {
        scaDomainProvider.close();
        System.out.println("Method Test End------------------------------------------------------------------------");
        System.out.println("\n\n");
    }
    
        
    @Test
    public void testClientGetFeed() throws Exception {
        Feed feed = testService.clientGetFeed();
        System.out.println(feed.getTitle().getPlainText());
        assertNotNull(feed);
        // Given we are testing on the localhost providing feed, we know the
        // feed title is "Feedtitle(LocalHostServlet)"
        assertEquals("Feedtitle(LocalHostServlet)", feed.getTitle().getPlainText());
    }

    @Test
    public void testClientGetEntry() throws Exception {
        String entryID = "urn:uuid:customer-0";
        Entry entry = testService.clientGetEntry(entryID);
        System.out.println("entryID in testcase: " + entry.getId());
        assertEquals(entryID, entry.getId());
    }

    @Test
    public void testClientPost() throws Exception {
        Entry newEntry = new Entry();
        newEntry.setTitle(new PlainTextConstruct("NewEntry title by Post"));
        newEntry.setContent(new PlainTextConstruct("NewEntry Content by Post"));        
        Entry postedEntry = testService.clientPost(newEntry);        
        assertEquals("NewEntry title by Post", postedEntry.getTitle().getPlainText());
    }
    
  
    
    @Test
    public void testClientPut() throws Exception {
        String newTitleValue = "newTitleValueByPut";
        String entryID = "urn:uuid:customer-0";
        System.out.println("Before clientPut");
        testService.clientPut(entryID, newTitleValue);
        System.out.println("After clientPut");
        Entry updatedEntry = testService.clientGetEntry(entryID);
        System.out.println("title: "+ updatedEntry.getTitle().getPlainText());
        assertEquals(newTitleValue, updatedEntry.getTitle().getPlainText());
    }
    


    @Test
    public void testClientDelete() throws Exception {

        // We first create a new entry, then delete it

        // Post a new entry
        Entry newEntry = new Entry();
        newEntry.setTitle(new PlainTextConstruct("NewEntry title to be deleted"));
        newEntry.setContent(new PlainTextConstruct("NewEntry Content to be delted"));
        Entry confirmedNewEntry = testService.clientPost(newEntry);

        Thread.sleep(300);
       
        Feed feed00 = testService.clientGetFeed();
        int entryNum00 = feed00.getEntries().size(); // The number of entries
                                                        // before deleting
        System.out.println("entryNum00:" + entryNum00);
                
        // Delete this newly created entry
        String entryID = confirmedNewEntry.getId();
        Thread.sleep(300);
        
        System.out.println("confirmed entry ID: " + confirmedNewEntry.getId());
        System.out.println("confirmed entry title: " + confirmedNewEntry.getTitle().getPlainText());
        
        System.out.println("Before test clientDelete");
        testService.clientDelete(entryID);
        System.out.println("After test clientDelete");
        
        Feed feed01 = testService.clientGetFeed();
        int entryNum01 = feed01.getEntries().size();
        System.out.println("entryNum01:" + entryNum01); // The number of entries after deleting
        
        //assertEquals(1, entryNum00 - entryNum01);
    }
    

}
