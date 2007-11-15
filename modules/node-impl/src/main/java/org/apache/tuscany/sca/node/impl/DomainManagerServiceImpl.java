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

package org.apache.tuscany.sca.node.impl;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.DomainManagerNodeEventService;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Scope("COMPOSITE")
public class DomainManagerServiceImpl implements DomainManagerNodeEventService{
    
    private final static Logger logger = Logger.getLogger(DomainManagerServiceImpl.class.getName());    
    
    @Property
    protected int retryCount = 100;
    
    @Property 
    protected int retryInterval = 5000; //ms    
    
    @Reference
    protected DomainManagerNodeEventService domainManager;

    public String registerNode(String nodeURI, String nodeURL) {
        
        String returnValue = null;
        
        for (int i =0; i < retryCount; i++){
            try {        
                returnValue =  domainManager.registerNode(nodeURI, nodeURL);
                break;
            } catch(UndeclaredThrowableException ex) {
                ex.printStackTrace();
                logger.log(Level.INFO, "Trying to register node " + 
                                       nodeURI + 
                                       " at endpoint " +
                                       nodeURL);
          
            }
            
            try {
                Thread.sleep(retryInterval);
            } catch(InterruptedException ex) {
            }
         }
        
        return returnValue;
    }

    public String removeNode(String nodeURI) {
        return domainManager.removeNode(nodeURI);
    }
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL){
        domainManager.registerContribution(nodeURI, contributionURI, contributionURL);
    }
    
    public void unregisterContribution(String contributionURI){
        domainManager.unregisterContribution(contributionURI);
    }


    public String registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
     
        String dummy = null; 
        
        for (int i =0; i < retryCount; i++){
            try {
                dummy = domainManager.registerServiceEndpoint(domainUri, nodeUri, serviceName, bindingName, URL);
                break;
            } catch(UndeclaredThrowableException ex) {
                logger.log(Level.INFO, "Trying to connect to domain " + 
                                       domainUri + 
                                       " to register service " +
                                       serviceName);
          
            }
            
            try {
                Thread.sleep(retryInterval);
            } catch(InterruptedException ex) {
            }
         }
        
        return dummy;
    }
    
    public String removeServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName){
     
        String dummy = null; 
        
        for (int i =0; i < retryCount; i++){
            try {
                dummy = domainManager.removeServiceEndpoint(domainUri, nodeUri, serviceName, bindingName);
                break;
            } catch(UndeclaredThrowableException ex) {
                logger.log(Level.INFO, "Trying to connect to domain " + 
                                       domainUri + 
                                       " to remove service " +
                                       serviceName);
          
            }
            
            try {
                Thread.sleep(retryInterval);
            } catch(InterruptedException ex) {
            }
         }
        
        return dummy;
    }    
   
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){

        
        String url = null;
        
        for (int i =0; i < retryCount; i++){
            try {
                url =  domainManager.findServiceEndpoint(domainUri, serviceName, bindingName);
                break;
            } catch(UndeclaredThrowableException ex) {
                logger.log(Level.INFO, "Trying to connect to domain " + 
                                       domainUri + 
                                       " to find service " +
                                       serviceName);
          
            }
            
            try {
                Thread.sleep(retryInterval);
            } catch(InterruptedException ex) {
            }
         }
        
        return url;
    }
 
}
