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

package org.apache.tuscany.sca.domain.management.impl;

import java.io.Externalizable;
import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomainEventService;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.management.DomainInfo;
import org.apache.tuscany.sca.domain.management.SCADomainManagerInitService;
import org.apache.tuscany.sca.domain.management.SCADomainManagerService;
import org.apache.tuscany.sca.domain.management.NodeInfo;
import org.apache.tuscany.sca.domain.model.DomainModel;
import org.apache.tuscany.sca.domain.model.NodeModel;
import org.apache.tuscany.sca.node.management.SCANodeManagerService;
import org.osoa.sca.CallableReference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Scope("COMPOSITE")
@Service(interfaces = {SCADomainEventService.class, SCADomainManagerInitService.class, SCADomainManagerService.class})
public class SCADomainManagerServiceImpl implements SCADomainEventService, SCADomainManagerInitService, SCADomainManagerService {
    
    private final static Logger logger = Logger.getLogger(SCADomainManagerServiceImpl.class.getName());
    
    private SCADomainSPI domainSPI;
    private SCADomainEventService domainEventService;
    
    // DomainManagerInitService methods
    
    public void setDomainSPI(SCADomainSPI domainSPI) {
        this.domainSPI = domainSPI;
    }
    
    public void setDomainEventService(SCADomainEventService domainEventService) {
        this.domainEventService = domainEventService;
    }
    
    // DomainEventService methods
    
    public void registerNode(String nodeURI, String nodeURL, Externalizable nodeManagerReference) throws DomainException{
        // get a reference to the node manager here so that the callable reference
        // the right context to construct itself. Don't actually have to do 
        // anything with the result as the context is cached inside the callable
        // reference
        ((CallableReference<SCANodeManagerService>)nodeManagerReference).getService();
        
        // pass on to the domain
        domainEventService.registerNode(nodeURI, nodeURL, nodeManagerReference);
    }
    
    public void unregisterNode(String nodeURI) throws DomainException { 
        domainEventService.unregisterNode(nodeURI);
    }  
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException {
        domainEventService.registerContribution(nodeURI, contributionURI, contributionURL);
    }
    
    public void unregisterContribution(String nodeURI,String contributionURI)throws DomainException {
        domainEventService.unregisterContribution(nodeURI, contributionURI);
    }
    
    public void registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL) throws DomainException {
        domainEventService.registerServiceEndpoint(domainUri, nodeUri, serviceName, bindingName, URL);
    }
   
    public void unregisterServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName) throws DomainException{
         domainEventService.unregisterServiceEndpoint(domainUri, nodeUri, serviceName, bindingName);
    }
   

    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName) throws DomainException{
        return domainEventService.findServiceEndpoint(domainUri, serviceName, bindingName);
    }    
    
    // DomainManagementService methods
    
    public DomainInfo getDomainDescription(){
        
        DomainInfo domainInfo = new DomainInfoImpl();
        DomainModel domain =  domainSPI.getDomainModel();
        
        domainInfo.setDomainURI(domain.getDomainURI());
        domainInfo.setDomainURL(domain.getDomainURL());
        domainInfo.getNodes().addAll(domain.getNodes().keySet());
        domainInfo.getContributions().addAll(domain.getContributions().keySet());
        domainInfo.getDeployedComposites().addAll(domain.getDeployedComposites().keySet());
        
        return domainInfo;
    }
    
    public NodeInfo getNodeDescription(String nodeURI){
        
        NodeInfo nodeInfo = new NodeInfoImpl();
        DomainModel domain =  domainSPI.getDomainModel();
        NodeModel node = domain.getNodes().get(nodeURI);
        
        nodeInfo.setNodeURI(nodeURI);
        nodeInfo.setNodeURL(node.getNodeURL());
        nodeInfo.getContributions().addAll(node.getContributions().keySet());
        nodeInfo.getDeployedComposites().addAll(node.getDeployedComposites().keySet());
        nodeInfo.getServices().addAll(node.getServices().keySet());
                
        return nodeInfo;
    }    
}
