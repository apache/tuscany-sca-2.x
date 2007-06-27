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
import java.util.Date;
import java.util.List;

import org.apache.tuscany.sca.demos.aggregator.types.ConfigType;
import org.apache.tuscany.sca.demos.aggregator.types.SourceType;
import org.apache.tuscany.sca.demos.aggregator.types.TypesFactory;
import org.apache.tuscany.sca.demos.aggregator.types.AlertsType;
import org.apache.tuscany.sca.demos.aggregator.types.AlertType;
import org.apache.tuscany.sca.demos.aggregator.types.impl.AlertsTypeImpl;

import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Reference;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

import org.apache.tuscany.sca.binding.feed.Collection;

/**
 * Read all new alerts from the specified sources
 *
 * @version $Rev$ $Date$
 */
@Service(AlertsService.class)
public class AlertsServiceImpl implements AlertsService {

    private RSSCheckerService    rssChecker;
    
    private AlertsSourcesService alertsSources;

    @Reference
    public void setRssChecker(RSSCheckerService rssChecker) {
        this.rssChecker = rssChecker;
    }
    
    @Reference
    public void alertsSources(AlertsSourcesService alertsSources) {
        this.alertsSources = alertsSources;
    }    
    
    DateFormat dateFormatter = DateFormat.getDateTimeInstance();
    
    /**
     * Return a structure holding all of the new alerts that have been found
     * 
     * @return the structure containing alerts 
     */
    public AlertsType getAllNewAlerts(String id)
    {
        System.err.println("getAllNewAlerts(" + id + ")");
        
        //TypesFactory factory    = TypesFactory.INSTANCE;
        //AlertsType returnAlerts = factory.createAlertsType();
        AlertsType returnAlerts = new AlertsTypeNonSDOImpl();
        List returnAlertList    = returnAlerts.getAlert();
        
        // get the date/time now so that we can update the 
        // alert source record so that next time we 
        // only get the latest alerts
        Date now = new Date();
        String nowString = dateFormatter.format(now);
              
        try {
            ConfigType alertSourceConfig = alertsSources.getAlertSources(id);
            
            for (Object source : alertSourceConfig.getSource()){
                SourceType sourceType = (SourceType)source;
                
                AlertsType alerts = null;
                
                if ( sourceType.getFeedType().equals("rss")){
                    alerts = rssChecker.getNewAlerts(sourceType.getFeedAddress(),
                                                     sourceType.getLastChecked());
                } else {
                    
                }
                
                // extend return list with any alerts we found
                for( Object alert : alerts.getAlert() ){         

                    // set the id on the alert so we know which source it
                    // came from 
                    ((AlertType)alert).setSourceId(sourceType.getId());
                    
                    // convert from SDO to POJO so that the 
                    // JSONRPC binding will work. It can't currently
                    // handle SDOs
                    AlertType newAlert = new AlertTypeNonSDOImpl();
    
                    newAlert.setSourceId(((AlertType)alert).getSourceId());
                    newAlert.setTitle(((AlertType)alert).getTitle());
                    newAlert.setSummary(((AlertType)alert).getSummary());                    
                    newAlert.setAddress(((AlertType)alert).getAddress());
                    newAlert.setDate(((AlertType)alert).getDate());
                    newAlert.setId(((AlertType)alert).getId());
                    newAlert.setUnread(((AlertType)alert).isUnread());                
                    
                    returnAlertList.add(newAlert);
                }
                
                // update the time last checked for this source
                sourceType.setLastChecked(nowString);
                //alertsSources.updateAlertSource(sourceType);
            }
        } catch(Exception ex) {
            System.err.println("Exception " + ex.toString());
        }
        
        return returnAlerts ;
        
    }
}
