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

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;
import org.apache.tuscany.sca.demos.aggregator.types.AlertType;
import org.apache.tuscany.sca.demos.aggregator.types.AlertsType;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * Read all new alerts from the specified sources
 *
 * @version $Rev$ $Date$
 */
@Service(Collection.class)
public class AlertsFeedServiceImpl implements Collection {
  
    private AlertsService alerts;
    
    @Reference
    public void setAlerts(AlertsService alerts) {
        this.alerts = alerts;
    }    
    
    DateFormat dateFormatter = DateFormat.getDateTimeInstance();
    
    /**
     * Return the alerts as a feed.
     * 
     * @return the structure containing alerts 
     */    
    public org.apache.abdera.model.Feed getFeed() {
        
        // Create a new Feed
        Factory factory = Abdera.getNewFactory();
        Feed feed = factory.newFeed();
        feed.setTitle("Apache Tuscany Feed Aggregator");
        feed.setSubtitle("A sample showing an SCA application to aggregate various types of feeds");
        feed.addAuthor("Apache Tuscany");
        feed.addLink("http://tuscany.apache.org");
 
        // Aggregate entries from feed1 and feed2
        try {        
            AlertsType alerts = this.alerts.getAllNewAlerts("");
            
            for( Object alertObject : alerts.getAlert() ){         
                AlertType alert = ((AlertType)alertObject);
                Entry entry = factory.newEntry();
                entry.setTitle(alert.getTitle());
                //entry.(alert.getSummary());                    
                entry.addLink(alert.getAddress());
                entry.setPublished(dateFormatter.parse(alert.getDate()));
                        
                feed.addEntry(entry);
            }
        } catch(Exception ex) {
            System.err.println("Exception " + ex.toString());
        }
        
        return feed;
    }
    
    public Feed query(String queryString) {
        return getFeed();
    }

    public void delete(String id) throws NotFoundException {
    }

    public Entry get(String id) throws NotFoundException {
        return null;
    }

    public Entry post(Entry entry) {
        return null;
    }

    public void put(String id, Entry entry) throws NotFoundException {
    }

}
