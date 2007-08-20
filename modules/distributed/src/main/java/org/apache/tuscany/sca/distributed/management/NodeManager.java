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

package org.apache.tuscany.sca.distributed.management;


/**
 * The management interface for a node
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public interface NodeManager {

    /**
     * Starts the node and assigns a uri to the node. For covenience this 
     * uri should be the URL of the service that provides this interface
     * 
     * @param nodeUri the string uri for the current node
     */
    public void start(String nodeUri);
    
    /**
     * Starts the node running 
     *  
     */
    public void stop();
    
    /**
     * The node joins the distributed domain specified by the 
     * domainUri. 
     *  
     * @param domainUri the string uri for the distributed domain
     */
    public void joinDomain(String domainUri);    
    
    /**
     * The configuration of a domain running on this node has changed.
     * It is the responsibility of the node to respond to this and retrieve
     * any relevent configuration changes 
     *  
     * @param domainUri the string uri for the distributed domain
     */
    public void domainNodeConfigurationChange(String domainUri);   
 
}
