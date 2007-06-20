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
package bigbank.account.feed;

import org.apache.tuscany.sca.binding.feed.Feed;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import bigbank.account.AccountService;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * @version $$Rev$$ $$Date$$
 */

@Service(Feed.class)
public class AccountFeedImpl implements Feed {

    @Reference
    protected AccountService accountService;
    
    @SuppressWarnings("unchecked")
    public SyndFeed get(String uri) {
        
        // Get the account report for the specified customer ID
        String customerID = uri.substring(uri.lastIndexOf('/')+1);
        double balance = accountService.getAccountReport(customerID); 
        String value = Double.toString(balance);
        
        // Create a new Feed
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("Account Report Feed");
        feed.setDescription("A sample Account Report feed");
        feed.setAuthor("anonymous");
        feed.setLink(uri);
        
        SyndEntry entry = new SyndEntryImpl();
        entry.setAuthor("anonymous");
        SyndContent content = new SyndContentImpl();
        content.setValue(value);
        entry.setDescription(content);
        feed.getEntries().add(entry);

        return feed;
    }
}
