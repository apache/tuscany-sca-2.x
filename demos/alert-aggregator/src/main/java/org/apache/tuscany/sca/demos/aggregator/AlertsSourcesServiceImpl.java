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

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

import org.apache.tuscany.sca.demos.aggregator.types.ConfigType;
import org.apache.tuscany.sca.demos.aggregator.types.SourceType;
import org.apache.tuscany.sca.demos.aggregator.types.TypesFactory;
import org.apache.tuscany.sca.demos.aggregator.types.impl.SourceTypeImpl;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

/**
 * Retrieve and manage alert sources
 *
 * @version $Rev$ $Date$
 */
@Service(AlertsSourcesService.class)
@Scope("COMPOSITE")
public class AlertsSourcesServiceImpl implements AlertsSourcesService {
    
    ConfigType alertSources;
    
    /**
     * Constructor reads the configuration to provide
     * the initial list of alert sources
     */
    public AlertsSourcesServiceImpl(){
        System.err.println("AlertsSourcesServiceImpl()");
        try {
            // read the alerts config from an XML file
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sources.xml");
            
            if (is == null) {
                throw new Exception("Can;t find sources.xml");
            } else {
                HelperContext helperContext = HelperProvider.getDefaultContext();
                TypesFactory.INSTANCE.register(helperContext);
                 XMLDocument xmlDoc = helperContext.getXMLHelper().load(is);
                alertSources = (ConfigType)xmlDoc.getRootObject();
            }
        } catch (Exception ex) {
            System.err.println("Exception " + ex.toString());
        }
    }
   
    /**
     * Return all of the configured alert sources.
     *
     * @return the list of alert sources
     */
    public ConfigType getAlertSources (String id)
    {           
        System.err.println("getAlertSources(" + id + ")");
        
        // convert alert sources to POJOs so that 
        // the JSONRPC binding will work
        ConfigTypeNonSDOImpl sources = new ConfigTypeNonSDOImpl();
        
        for (Object source : alertSources.getSource()) {
            SourceTypeNonSDOImpl newSource = new SourceTypeNonSDOImpl();
            newSource.setAddress(((SourceTypeImpl)source).getAddress());
            newSource.setFeedAddress(((SourceTypeImpl)source).getFeedAddress());
            newSource.setId(((SourceTypeImpl)source).getId());
            newSource.setLastChecked(((SourceTypeImpl)source).getLastChecked());
            newSource.setName(((SourceTypeImpl)source).getName());
            newSource.setFeedType(((SourceTypeImpl)source).getFeedType());
            sources.getSource().add(newSource);
        }
        
        return sources;
    }
    
    /**
     * Return a single alert source.
     * @param id the alert source id number
     * @return the alert source
     */
    public SourceType getAlertSource (String id)
    {           
        System.err.println("getAlertSource(" + id + ")");
        SourceType alertSource = null;
        
        for (Object source : alertSources.getSource()) {
            if ( ((SourceType)source).getId().equals(id)) {
                alertSource = (SourceType)source;
            }
        }
        return alertSource;
    } 
    
    /**
     * Update an alert source.
     *
     * @param updatedSource the alert source to update
     */
    public void updateAlertSource (SourceType updatedSource)
    {           
        System.err.println("updateAlertSource()");
            
        Object originalSource = null;
        
        for (Object source : alertSources.getSource()) {
            if ( ((SourceType)source).getId().equals(updatedSource.getId())) {
                originalSource = source;
                break;
            }
        }
        
        if (originalSource != null){
            alertSources.getSource().add(updatedSource);
            alertSources.getSource().remove(originalSource);
        }
    }    
      
    /**
     * Add an alert source.
     *
     * @param newSource the alert source to add
     */
    public String addAlertSource (SourceType newSource)
    {           
        System.err.println("addAlertSource()");
        // set the date to now less 2 hours so we 
        // get some alerts straight away
        DateFormat dateFormatter = DateFormat.getDateTimeInstance();
        Date now = new Date();
        now.setHours(now.getHours()-2);
        String nowString = dateFormatter.format(now);        
        newSource.setLastChecked(nowString); 
        alertSources.getSource().add(newSource);  
        return "Done";
    }    

    /**
     * Remove an alert source.
     *
     * @param oldSource the alert source to remove
     */
    public void removeAlertSource (String id)
    {           
        System.err.println("removeAlertSource()");
        
        Object originalSource = null;
        
        for (Object source : alertSources.getSource()) {
            if ( ((SourceType)source).getId().equals(id)) {
                originalSource = source;
                break;
            }
        }  
        
        if (originalSource != null) {
            alertSources.getSource().remove(originalSource);
        }

    } 
 
}
