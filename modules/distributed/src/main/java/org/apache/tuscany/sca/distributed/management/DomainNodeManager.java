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
 * The management interface for the part of the distributed domain that
 * runs on a node
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public interface DomainNodeManager {

    /**
     * Creates the internal structures to represent the distributed domain on this 
     * node
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     */
    public void createDomainNode(String domainUri, String nodeUri);
    
    /**
     * Starts the domain running on the current node
     *  
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     */
    public void startDomainNode(String domainUri, String nodeUri);
    
    /**
     * Stops the running domain on the current node
     *  
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node 
     */
    public void stopDomainNode(String domainUri, String nodeUri);    
 
}
