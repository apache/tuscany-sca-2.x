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
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.management.DomainInfo;
import org.apache.tuscany.sca.domain.management.NodeInfo;
import org.apache.tuscany.sca.domain.management.SCADomainManagerInitService;
import org.apache.tuscany.sca.domain.management.SCADomainManagerService;
import org.apache.tuscany.sca.domain.model.DomainModel;
import org.apache.tuscany.sca.domain.model.NodeModel;
import org.apache.tuscany.sca.domain.spi.SCADomainAPIService;
import org.apache.tuscany.sca.domain.spi.SCADomainEventService;
import org.apache.tuscany.sca.domain.spi.SCADomainSPI;
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
@Service(interfaces = {SCADomainEventService.class, SCADomainManagerInitService.class, SCADomainManagerService.class, SCADomainAPIService.class})
public class SCADomainManagerServiceImpl implements SCADomainEventService, SCADomainManagerInitService, SCADomainManagerService, SCADomainAPIService {
    
    private static final Logger logger = Logger.getLogger(SCADomainManagerServiceImpl.class.getName());
    
    private SCADomain domain;
    private SCADomainSPI domainSPI;
    private SCADomainEventService domainEventService;
    
    // DomainManagerInitService methods
    
    public void setDomain(SCADomain domain) {
        this.domain = domain;
    }
    
    public void setDomainSPI(SCADomainSPI domainSPI) {
        this.domainSPI = domainSPI;
    }
    
    public void setDomainEventService(SCADomainEventService domainEventService) {
        this.domainEventService = domainEventService;
    }
    
    // DomainEventService methods
    
    public void registerNode(String nodeURI, String nodeURL, Externalizable nodeManagerReference) throws DomainException{
        // get a reference to the node manager here so that the CallableReference
        // the right context to construct itself. Don't actually have to do 
        // anything with the result as the context is cached inside the CallableReference
        ((CallableReference<SCANodeManagerService>)nodeManagerReference).getService();
        
        // pass on to the domain
        domainEventService.registerNode(nodeURI, nodeURL, nodeManagerReference);
    }
    
    public void unregisterNode(String nodeURI) throws DomainException { 
        domainEventService.unregisterNode(nodeURI);
    } 
    
    public void registerNodeStart(String nodeURI) throws DomainException {
        domainEventService.registerNodeStart(nodeURI);
    }
    
    public void registerNodeStop(String nodeURI) throws DomainException {
        domainEventService.registerNodeStop(nodeURI);
    }
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException {
        domainEventService.registerContribution(nodeURI, contributionURI, contributionURL);
    }
    
    public void unregisterContribution(String nodeURI,String contributionURI)throws DomainException {
        domainEventService.unregisterContribution(nodeURI, contributionURI);
    }
    
    public void registerDomainLevelComposite(String nodeURI, String compositeQNameString) throws DomainException{
        domainEventService.registerDomainLevelComposite(nodeURI, compositeQNameString);
    }
    
    public void registerServiceEndpoint(String domainURI, String nodeUri, String serviceName, String bindingName, String URL) throws DomainException {
        domainEventService.registerServiceEndpoint(domainURI, nodeUri, serviceName, bindingName, URL);
    }
   
    public void unregisterServiceEndpoint(String domainURI, String nodeUri, String serviceName, String bindingName) throws DomainException{
         domainEventService.unregisterServiceEndpoint(domainURI, nodeUri, serviceName, bindingName);
    }
   

    public String findServiceEndpoint(String domainURI, String serviceName, String bindingName) throws DomainException{
        return domainEventService.findServiceEndpoint(domainURI, serviceName, bindingName);
    }  
    
    public String findServiceNode(String domainURI, String serviceName, String bindingName) throws DomainException {
        return domainEventService.findServiceNode(domainURI, serviceName, bindingName);
    }
    
    // DomainAPIService methods

    public void start() throws DomainException {
        domain.start();
    }

    public void stop() throws DomainException {
        domain.stop();
    }

    public void destroyDomain() throws DomainException {
        domain.destroy();
    }

    public String getURI() {
        return domain.getURI();
    }
     
    public void addContribution(String contributionURI, String contributionURL) throws DomainException {
        try {
            domain.addContribution(contributionURI, new URL(contributionURL));
        } catch (Exception ex) {
            throw new DomainException (ex);
        }
    }
    
    public void updateContribution(String contributionURI, String contributionURL) throws DomainException {
        try {
            domain.updateContribution(contributionURI, new URL(contributionURL));
        } catch (Exception ex) {
            throw new DomainException (ex);
        }
    }
    
    public void removeContribution(String contributionURI) throws DomainException {
        domain.removeContribution(contributionURI);
    }
    
    public void addDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        domain.addDeploymentComposite(contributionURI, compositeXML);
    }
    
    public void updateDeploymentComposite(String contributionURI, String compositeXML) throws DomainException {
        domain.updateDeploymentComposite(contributionURI, compositeXML);
    }

    public void addToDomainLevelComposite(String compositeQName) throws DomainException {
        domain.addToDomainLevelComposite(QName.valueOf(compositeQName));
    }
    
    public void removeFromDomainLevelComposite(String compositeQName) throws DomainException {
       domain.removeFromDomainLevelComposite(QName.valueOf(compositeQName));
    }
    
    public String getDomainLevelComposite() throws DomainException {
        return domain.getDomainLevelComposite();
    }
    
    public String getQNameDefinition(String artifact) throws DomainException {
        return domain.getQNameDefinition(QName.valueOf(artifact));
    }
    
    public void startComposite(String compositeQName) throws DomainException {
        domain.startComposite(QName.valueOf(compositeQName));
    }
    
    public void stopComposite(String compositeQName) throws DomainException {
        domain.stopComposite(QName.valueOf(compositeQName));
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
