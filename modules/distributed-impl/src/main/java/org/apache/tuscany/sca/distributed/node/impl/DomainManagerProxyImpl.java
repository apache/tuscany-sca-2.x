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

package org.apache.tuscany.sca.distributed.node.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.distributed.domain.DomainManagerService;
import org.apache.tuscany.sca.distributed.domain.NodeInfo;
import org.apache.tuscany.sca.distributed.domain.impl.ServiceDiscoveryServiceImpl.ServiceEndpoint;
import org.apache.tuscany.sca.distributed.node.NodeManagerService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date$
 */
@Scope("COMPOSITE")
public class DomainManagerProxyImpl implements DomainManagerService{
    
    @Reference
    protected DomainManagerService domainManager;

    public String registerNode(String domainUri, String nodeUri) {
        return domainManager.registerNode(domainUri, nodeUri);
    }

    public String removeNode(String domainUri, String nodeUri) {
        return domainManager.removeNode(domainUri, nodeUri);
    }
    
    public List<NodeInfo> getNodeInfo(){
        return domainManager.getNodeInfo();
    }
}
