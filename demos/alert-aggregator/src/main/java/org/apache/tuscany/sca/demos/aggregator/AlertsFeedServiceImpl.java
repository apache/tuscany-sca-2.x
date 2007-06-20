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

package org.apache.tuscany.sca.demos.aggregator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.binding.feed.Feed;
import org.apache.tuscany.sca.demos.aggregator.types.AlertType;
import org.apache.tuscany.sca.demos.aggregator.types.AlertsType;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * Read all new alerts from the specified sources
 *
 * @version $Rev$ $Date$
 */
@Service(Feed.class)
public class AlertsFeedServiceImpl implements Feed {
  
    private AlertsService alerts;
    
    @Reference
    public void alerts(AlertsService alerts) {
        this.alerts = alerts;
    }    
    
    DateFormat dateFormatter = DateFormat.getDateTimeInstance();
    
    /**
     * Return the alerts as a feed. Used by binding.feed
     * 
     * @return the structure containing alerts 
     */    
    public SyndFeed get(String uri) {
        
        // Create a new Feed
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("Apache Tuscant Feed Aggregator");
        feed.setDescription("A sample showing an SCA application to aggregate various types of feeds");
        feed.setAuthor("Apache Tuscany");
        feed.setLink(uri);
 
        // Aggregate entries from feed1 and feed2
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        
        try {        
            AlertsType alerts = this.alerts.getAllNewAlerts("");
            
            for( Object alertObject : alerts.getAlert() ){         
                AlertType alert = ((AlertType)alertObject);
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(alert.getTitle());
                //entry.(alert.getSummary());                    
                entry.setLink(alert.getAddress());
                entry.setPublishedDate(dateFormatter.parse(alert.getDate()));
                        
                entries.add(entry);
            }
        } catch(Exception ex) {
            System.err.println("Exception " + ex.toString());
        }
        
        feed.setEntries(entries);
        return feed;
    }    
}
