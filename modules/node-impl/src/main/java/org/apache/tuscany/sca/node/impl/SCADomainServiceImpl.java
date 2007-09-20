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

import org.apache.tuscany.sca.domain.SCADomainService;
import org.apache.tuscany.sca.domain.ServiceInfo;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-09 23:54:46 +0100 (Sun, 09 Sep 2007) $
 */
public class SCADomainServiceImpl implements SCADomainService{
    
    private final static Logger logger = Logger.getLogger(SCADomainServiceImpl.class.getName());
    
    @Property
    protected int retryCount = 100;
    
    @Property 
    protected int retryInterval = 5000; //ms
    
    
    @Reference
    protected SCADomainService scaDomainService;
     
    /**
     * Accepts information about a service endpoint and holds onto it
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     * @param url the endpoint url
     * @return dummy values just so that we don't have a void return which doesn't work 
     */
    public String registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
     
        String dummy = null; 
        
        for (int i =0; i < retryCount; i++){
            try {
                dummy = scaDomainService.registerServiceEndpoint(domainUri, nodeUri, serviceName, bindingName, URL);
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
    
    /**
     * Removes information about a service endpoint 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     * @return dummy values just so that we don't have a void return which doesn't work 
     */
    public String removeServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName){
     
        String dummy = null; 
        
        for (int i =0; i < retryCount; i++){
            try {
                dummy = scaDomainService.removeServiceEndpoint(domainUri, nodeUri, serviceName, bindingName);
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
   
    /**
     * Locates information about a service endpoint 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that we want to find an endpoint for
     * @return url the endpoint url
     */
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){

        
        String url = null;
        
        for (int i =0; i < retryCount; i++){
            try {
                url =  scaDomainService.findServiceEndpoint(domainUri, serviceName, bindingName);
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
    
    public ServiceInfo getServiceInfo(){
        return scaDomainService.getServiceInfo();
    }
    
}
