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

import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gdata.client.Query;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;

public class GoogleWebAlbumServiceTestCase extends TestCase{

    private SCADomain scaDomainConsumer = null;
    private CustomerClient testService = null;    
    
    public GoogleWebAlbumServiceTestCase(){

    }
    
    @Before
    @Override
    public void setUp() throws Exception {
        System.out.println("Method Test Start-----------------------------------------------------------------------");
        
        //Initialize the GData client service (Reference Binding test)
        scaDomainConsumer = SCADomain.newInstance("org/apache/tuscany/sca/binding/gdata/ConsumerGoogleWebAlbum.composite");
        testService = scaDomainConsumer.getService(CustomerClient.class, "CustomerClient");  
    }

    @After
    @Override
    public void tearDown(){
        System.out.println("Method Test End------------------------------------------------------------------------");
        System.out.println("\n\n");
    }        
    
    @Test
    public void testClientGetFeed() throws Exception {
        Feed feed = testService.clientGetFeed();
        System.out.println("feed title: " + feed.getTitle().getPlainText());        
        assertEquals("flowers", feed.getTitle().getPlainText());
     }
    


    
    @Test
    public void testClientGetEntry() throws Exception {
        String entryID = "photoid/5233468700029715874";
        Entry contactEntry = testService.clientGetEntry(entryID);
        System.out.println("Entry ID: " + contactEntry.getId());
        assertTrue(contactEntry.getId().endsWith(entryID));
        System.out.println("------------------------------------------------------------\n\n");
    }
    
    
    @Test
    public void testClientQuery() throws Exception {
    	String feedUrlString = "http://picasaweb.google.com/data/feed/api/user/haibotuscany/album/flowers";
    	URL feedURL = new URL(feedUrlString);   
    	Query myQuery = new Query(feedURL);
        myQuery.setMaxResults(100);
        myQuery.setFullTextQuery("photo");    
        Feed resultFeed = testService.clientQuery(myQuery);        
        System.out.println("Query result feed title: " + resultFeed.getTitle().getPlainText());    
        System.out.println("Query result entry number: "+ resultFeed.getEntries().size());
        //assertEquals("gdata binding tuscany test", resultFeed.getTitle().getPlainText());
     }
    
    
    
    @Test
    public void testClientPut() throws Exception {  
        String entryID = "photoid/5233468700029715874";          
        String newBlogEntryTitle = "updatedTitle:dog";
        testService.clientPut(entryID, newBlogEntryTitle);      //update the title
        Thread.sleep(300);            
        Entry updatedEntry = testService.clientGetEntry(entryID);         
        assertEquals(newBlogEntryTitle, updatedEntry.getTitle().getPlainText());
    }
    
    
    @Test
    public void testClientDelete() throws Exception {
        
    	//Tested and it worked, but only once because we can not delete the same entry twice
    	String entryID = "photoid/5233468698153151618"; 
    	
    	//Delete this entry
    	//To test, change the entryID
        //testService.clientDelete(entryID);
    }    

}
