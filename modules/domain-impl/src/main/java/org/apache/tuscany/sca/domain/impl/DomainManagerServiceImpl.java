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

package org.apache.tuscany.sca.domain.impl;

import java.util.logging.Logger;

import org.apache.tuscany.sca.domain.DomainManagerInitService;
import org.apache.tuscany.sca.domain.DomainManagerNodeEventService;
import org.apache.tuscany.sca.domain.SCADomainSPI;
import org.apache.tuscany.sca.domain.management.DomainInfo;
import org.apache.tuscany.sca.domain.management.DomainManagementService;
import org.apache.tuscany.sca.domain.management.NodeInfo;
import org.apache.tuscany.sca.domain.management.impl.DomainInfoImpl;
import org.apache.tuscany.sca.domain.management.impl.NodeInfoImpl;
import org.apache.tuscany.sca.domain.model.DomainModel;
import org.apache.tuscany.sca.domain.model.NodeModel;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Scope("COMPOSITE")
@Service(interfaces = {DomainManagerNodeEventService.class, DomainManagerInitService.class, DomainManagementService.class})
public class DomainManagerServiceImpl implements DomainManagerNodeEventService, DomainManagerInitService, DomainManagementService {
    
    private final static Logger logger = Logger.getLogger(DomainManagerServiceImpl.class.getName());
    
    private SCADomainSPI scaDomain;
    
    // DomainManagerInitService methods
    
    public void setDomain(SCADomainSPI scaDomain) {
        this.scaDomain = scaDomain;
    }
    
    // DomainManagerNodeEventService methods
    
    public String registerNode(String nodeURI, String nodeURL){ 
        return scaDomain.addNode(nodeURI, nodeURL);
    }
    
    public String removeNode(String nodeURI){ 
        return scaDomain.removeNode(nodeURI);
    }  
    
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) {
        scaDomain.registerContribution(nodeURI, contributionURI, contributionURL);
    }
    
    public void unregisterContribution(String contributionURI){
        scaDomain.unregisterContribution(contributionURI);
    }
    
    public String  registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
        return scaDomain.registerServiceEndpoint(domainUri, nodeUri, serviceName, bindingName, URL);
    }
   
    public String  removeServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName){
        return scaDomain.removeServiceEndpoint(domainUri, nodeUri, serviceName, bindingName);
    }
   

    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){
        return scaDomain.findServiceEndpoint(domainUri, serviceName, bindingName);
    }    
    
    // DomainManagementService methods
    
    public DomainInfo getDomainDescription(){
        
        DomainInfo domainInfo = new DomainInfoImpl();
        DomainModel domain =  scaDomain.getDomainModel();
        
        domainInfo.setDomainURI(domain.getDomainURI());
        domainInfo.setDomainURL(domain.getDomainURL());
        domainInfo.getNodes().addAll(domain.getNodes().keySet());
        domainInfo.getContributions().addAll(domain.getContributions().keySet());
        domainInfo.getDeployedComposites().addAll(domain.getDeployedComposites().keySet());
        
        return domainInfo;
    }
    
    public NodeInfo getNodeDescription(String nodeURI){
        
        NodeInfo nodeInfo = new NodeInfoImpl();
        DomainModel domain =  scaDomain.getDomainModel();
        NodeModel node = domain.getNodes().get(nodeURI);
        
        nodeInfo.setNodeURI(nodeURI);
        nodeInfo.setNodeURL(node.getNodeURL());
        nodeInfo.getContributions().addAll(node.getContributions().keySet());
        nodeInfo.getDeployedComposites().addAll(node.getDeployedComposites().keySet());
        nodeInfo.getServices().addAll(node.getServices().keySet());
                
        return nodeInfo;
    }    
}
