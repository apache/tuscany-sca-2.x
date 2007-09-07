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

package org.apache.tuscany.sca.distributed.domain;

import java.util.List;

import org.osoa.sca.annotations.Remotable;


/**
 * The management interface for distributed domain. This is resposible for 
 * creating appropriate configuration on all the nodes that are running 
 * domain nodes for the distributed domain. 
 * 
 * @version $Rev: 552343 $ $Date$
 */
@Remotable
public interface DomainManagerService {

    /**
     * A node registers with the distributed domain manager. The mechanism whereby this
     * registration interface is discovered is not defined. For example, JMS, JINI
     * or a hard coded configuration could all be candidates in the java world. 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param nodeManagementUrl the endpoint for the nodes management service
     */
    public String registerNode(String domainUri, String nodeUri);
    
    /**
     * A node registers with the distributed domain manager. The mechanism whereby this
     * registration interface is discovered is not defined. For example, JMS, JINI
     * or a hard coded configuration could all be candidates in the java world. 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param nodeManagementUrl the endpoint for the nodes management service
     */
    public String removeNode(String domainUri, String nodeUri);    
    
    /**
     * Retrieve the configuration for the specified node. The return type is interesting
     * here. There are many ways in which all of the information that comprises a 
     * configuration can be provisioned onto a node, for example, shared file system,
     * ftp, http. The return value is the url of where to look for the configuration 
     * information. From a management point of view it is convenient to maintain all 
     * current and previous node configurations. This can easily be achieved by providing
     * a different URL each time the configuration is changed. 
     *  
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @return the URL from where the configuration can be retrieved
     */
   //public String getDomainNodeConfiguration(String domainUri, String nodeUri);  
    
    /**
     * Retrieve a list of all of the registered nodes 
     * 
     * @return the list of node information 
     */
    public List<NodeInfo> getNodeInfo();    
}
