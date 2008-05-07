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

import java.io.Externalizable;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.spi.SCADomainEventService;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Scope("COMPOSITE")
public class SCADomainEventServiceProxyImpl implements SCADomainEventService{
    
    private final static Logger logger = Logger.getLogger(SCADomainEventServiceProxyImpl.class.getName());    
    
    @Property
    protected int retryCount = 100;
    
    @Property 
    protected int retryInterval = 5000; //ms    
    
    @Reference
    protected SCADomainEventService domainManager;

    public void registerNode(String nodeURI, String nodeURL, Externalizable nodeManagerService) throws DomainException {
              
        // a retry loop is included on node registration in case the node
        // comes up before the domain it is registering with
        for (int i =0; i < retryCount; i++){
            try {        
                domainManager.registerNode(nodeURI, nodeURL, nodeManagerService);
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
    }

    public void unregisterNode(String nodeURI) throws DomainException {
        domainManager.unregisterNode(nodeURI);
    }
    
    public void registerNodeStart(String nodeURI) throws DomainException {
        domainManager.registerNodeStart(nodeURI);
    }

    public void registerNodeStop(String nodeURI) throws DomainException {
        domainManager.registerNodeStop(nodeURI);
    }
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException {
        domainManager.registerContribution(nodeURI, contributionURI, contributionURL);
    }
    
    public void unregisterContribution(String nodeURI, String contributionURI) throws DomainException {
        domainManager.unregisterContribution(nodeURI, contributionURI);
    }
    
    public void registerDomainLevelComposite(String nodeURI, String compositeQNameString) throws DomainException{
        domainManager.registerDomainLevelComposite(nodeURI, compositeQNameString);
    }
    
    public void registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL) throws DomainException {
        domainManager.registerServiceEndpoint(domainUri, nodeUri, serviceName, bindingName, URL);
    }
    
    public void unregisterServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName) throws DomainException {
        domainManager.unregisterServiceEndpoint(domainUri, nodeUri, serviceName, bindingName);
    }    
   
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName) throws DomainException {
        return domainManager.findServiceEndpoint(domainUri, serviceName, bindingName);
    }
    
    public String findServiceNode(String domainUri, String serviceName, String bindingName) throws DomainException {
        return domainManager.findServiceNode(domainUri, serviceName, bindingName);
    }    
 
}
