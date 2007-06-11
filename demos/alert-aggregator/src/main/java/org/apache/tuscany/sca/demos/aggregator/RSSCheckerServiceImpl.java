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

import java.net.URL;
import java.util.Date;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.tuscany.sca.demos.aggregator.types.AlertType;
import org.apache.tuscany.sca.demos.aggregator.types.AlertsType;
import org.apache.tuscany.sca.demos.aggregator.types.TypesFactory;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * The interface for the rss checker service
 */
public class RSSCheckerServiceImpl implements RSSCheckerService {

    public AlertsType getNewAlerts(String rssaddress, String lastchecktimestamp){
        // Create the list of alerts to return
        TypesFactory factory    = TypesFactory.INSTANCE;
        AlertsType returnAlerts = factory.createAlertsType();
        List returnAlertList    = returnAlerts.getAlert();
        
        try {
            // Turn the date into something we can process. 
            DateFormat dateFormatter = DateFormat.getDateTimeInstance();
            Date timestamp = dateFormatter.parse(lastchecktimestamp);
            
            // get the feed data from the supplied address            
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(rssaddress)));
            //System.out.println(feed);
            
            // check all the items to see if we have seen them before
            List entries = feed.getEntries();
            for(Object entry: entries){
                SyndEntry syndEntry = (SyndEntry)entry;             
                
                if (syndEntry.getPublishedDate().after(timestamp)){
                    AlertType newAlert = factory.createAlertType();
                    
                    newAlert.setTitle(syndEntry.getTitle());
                 //   newAlert.setSummary("<![CDATA[" + 
                //                        syndEntry.getDescription().getValue() +
                //                        "]]>");
                    newAlert.setSummary("");                    
                    newAlert.setAddress(syndEntry.getLink());
                    newAlert.setDate(dateFormatter.format(syndEntry.getPublishedDate()));
                    newAlert.setId(rssaddress);
                    newAlert.setUnread(true);
                    
                    returnAlertList.add(newAlert);
                }
            }
            
        } catch(Exception ex) {
            System.err.println("Exception " + ex.toString());
        }

        return returnAlerts;
    }

}
