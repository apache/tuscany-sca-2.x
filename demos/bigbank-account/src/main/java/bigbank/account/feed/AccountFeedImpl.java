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

import java.io.InputStream;
import java.util.Collections;

import org.apache.tuscany.sca.binding.feed.Collection;
import org.apache.tuscany.sca.binding.feed.NotFoundException;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import bigbank.account.AccountService;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

/**
 * @version $$Rev$$ $$Date$$
 */

@Service(Collection.class)
public class AccountFeedImpl implements Collection {

    @Reference
    protected AccountService accountService;
    
    @SuppressWarnings("unchecked")
    public com.sun.syndication.feed.atom.Feed getFeed() {
        
        // Create a new Feed
        Feed feed = new Feed();
        feed.setId("accounts");
        feed.setTitle("Account Report Feed");
        Content subtitle = new Content();
        subtitle.setValue("This is a sample feed");
        feed.setSubtitle(subtitle);
        Link link = new Link();
        link.setHref("http://incubator.apache.org/tuscany");
        feed.getAlternateLinks().add(link);

        // Add the Account report entry 
        Entry entry = get("1234");
        feed.getEntries().add(entry);

        return feed;
    }

    public void delete(String id) {
    }

    public Entry get(String id) {

        // Get the account report for the specified customer ID
        double balance = accountService.getAccountReport(id); 
        String value = Double.toString(balance);
        
        Entry entry = new Entry();
        entry.setId("account-" + id);
        entry.setTitle("Account Report Entry");
        Content summary = new Content();
        summary.setType(Content.HTML);
        summary.setValue("This is your account report: <b>" + value + "</b>");
        entry.setSummary(summary);
        Content content = new Content();
        content.setValue(value);
        entry.setContents(Collections.singletonList(content));
        return entry;
    }

    public Entry post(Entry entry) {
        return null;
    }

    public Entry put(String id, Entry entry) {
        return null;
    }

    public Entry postMedia(String title, String slug, String contentType, InputStream media) {
        return null;
    }
    
    public Entry putMedia(String id, String contentType, InputStream media) throws NotFoundException {
        return null;
    }
}
