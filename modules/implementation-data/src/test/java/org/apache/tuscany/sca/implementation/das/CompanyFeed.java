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

package org.apache.tuscany.sca.implementation.das;

import org.apache.tuscany.sca.binding.feed.Feed;
import org.apache.tuscany.sca.implementation.data.DATA;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

import commonj.sdo.DataObject;

public class CompanyFeed implements Feed {
    
    @Reference
    protected DATA dataService;

    protected String parseId(String uri) {
        int separator = uri.lastIndexOf("/");
        return uri.substring(separator + 1);
    }
    
    @SuppressWarnings("unchecked")
    public SyndFeed get(String uri) {
        String id = parseId(uri);
        
        DataObject data = dataService.get(id);        
        if(data == null) {
            //FIXME: how to handle errors here ?
        }
        
        // Create a new Feed
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("Company Feed");
        feed.setDescription("A sample company feed");
        feed.setAuthor("anonymous");
        feed.setLink(uri);
        
        //loop torugh all the results returned
        SyndEntry entry = new SyndEntryImpl();
        entry.setUri(uri);
        entry.setAuthor("anonymous");
        entry.setTitle(data.getString("name"));
        
        SyndContent content = new SyndContentImpl();
        content.setValue(data.getString("name"));
        entry.setDescription(content);
        feed.getEntries().add(entry);

        return feed;
    }

}
