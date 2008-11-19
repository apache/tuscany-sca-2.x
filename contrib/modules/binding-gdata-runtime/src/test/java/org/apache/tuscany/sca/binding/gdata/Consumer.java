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

import org.apache.tuscany.sca.host.embedded.SCADomain;

import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;

public class Consumer {

    public static void main(String[] args) throws Exception {

        //Initialize the GData client service (Reference Binding test)
        SCADomain scaDomain = SCADomain.newInstance("org/apache/tuscany/sca/binding/gdata/ConsumerGoogleBlogger.composite");
        CustomerClient testService = scaDomain.getService(CustomerClient.class, "CustomerClient");          
        
        
        Feed feed = testService.clientGetFeed();
        
        System.out.println("#Entries(Before post): "+ testService.clientGetFeed().getEntries().size());
        
        /*
        String entryID = "tag:blogger.com,1999:blog-4520949313432095990.post-973462497533349425";
        Entry entry = testService.clientGetEntry(entryID);
        System.out.println("Entry id: " + entry.getId());
        */
        
        Entry myEntry = new Entry();
        myEntry.setTitle(new PlainTextConstruct("titleByConsumer2"));
        myEntry.setContent(new PlainTextConstruct("contentByConsmer2"));
        testService.clientPost(myEntry);
        
        System.out.println("#Entries(After post): "+ testService.clientGetFeed().getEntries().size());
        
        String entryID = "tag:blogger.com,1999:blog-4520949313432095990.post-973462497533349425";
        Entry entry = testService.clientGetEntry(entryID);
        System.out.println("Entry id: " + entry.getId());

       
    }
}
