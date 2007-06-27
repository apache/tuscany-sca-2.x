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

package org.apache.tuscany.sca.implementation.data.companyFeed;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.tuscany.sca.binding.feed.EditableCollection;
import org.apache.tuscany.sca.binding.feed.NotFoundException;
import org.apache.tuscany.sca.implementation.data.DATA;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import commonj.sdo.DataObject;

public class CompanyFeed implements EditableCollection {
    
    @Reference
    protected DATA dataService;
    
    public Feed getFeed() {
        
        // Create a new Feed
        Feed feed = new Feed();
        feed.setTitle("Company Feed");
        Content subtitle = new Content(); 
        subtitle.setValue("A sample company feed");
        feed.setSubtitle(subtitle);
        Person author = new Person();
        author.setName("anonymous");
        feed.setAuthors(Collections.singletonList(author));
        Link link = new Link();
        link.setHref("http://incubator.apache.org/tuscany");
        feed.setAlternateLinks(Collections.singletonList(link));

        return feed;
    }

    public Entry get(String id) throws NotFoundException{
        
        DataObject data = dataService.get(id);        
        if(data == null) {
            throw new NotFoundException();
        }
        
        Entry entry = new Entry();
        entry.setId(id);
        entry.setTitle(data.getString("name"));
        List<Link> links = new ArrayList<Link>();
        Link link = new Link();
        link.setRel("edit");
        link.setHref("entry/" + id);
        links.add(link);
        entry.setOtherLinks(links);

        links = new ArrayList<Link>();
        link = new Link();
        link.setRel("alternate");
        link.setHref("entry/" + id);
        links.add(link);
        entry.setAlternateLinks(links);

        entry.setCreated(new Date());

        return entry;
    }

    public void delete(String id) throws NotFoundException {
    }

    public Entry post(Entry entry) {
        return null;
    }

    public Entry postMedia(String title, String slug, String contentType, InputStream media) {
        return null;
    }

    public Entry put(String id, Entry entry) throws NotFoundException {
        return null;
    }

    public Entry putMedia(String id, String contentType, InputStream media) throws NotFoundException {
        return null;
    }

}
