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

package org.apache.tuscany.sca.distributed.domain.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.distributed.domain.DomainManagerService;
import org.apache.tuscany.sca.distributed.domain.NodeInfo;
import org.apache.tuscany.sca.distributed.domain.ServiceDiscoveryService;
import org.apache.tuscany.sca.distributed.domain.impl.ServiceDiscoveryServiceImpl.ServiceEndpoint;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date$
 */
@Scope("COMPOSITE")
public class DomainManagerServiceImpl implements DomainManagerService{
    
    @Reference 
    public ServiceDiscoveryService serviceDiscovery;

    List<NodeInfo> nodes = new ArrayList<NodeInfo>();
    
    public String registerNode(String domainUri, String nodeUri){ 
        NodeInfo nodeInfo = new NodeInfoImpl(domainUri, nodeUri);
        nodes.add(nodeInfo);
        System.err.println("Registering node: " + nodeUri);
        return nodeUri;
    }
    
    public String removeNode(String domainUri, String nodeUri){ 
        
        NodeInfo nodeToRemove = null;
        
        for(NodeInfo node : nodes){
            if ( node.match(domainUri, nodeUri)){
                nodeToRemove = node;
                break;
            }
        }

        nodes.remove(nodeToRemove);
        System.err.println("Removed node: " + nodeUri);
        
        return nodeUri;
    }    
    
    public List<NodeInfo> getNodeInfo(){
        
        // get the nodeManagerUrl for each node
        for(NodeInfo node : nodes){
            String url = serviceDiscovery.findServiceEndpoint(node.getDomainUri(), 
                                                              node.getNodeUri() + "NodeManagerService",
                                                              "");
                                                 

            if (url != null) {
                node.setNodeManagerUrl(url);
            }
        }
        
        return nodes;
    }
    
}
