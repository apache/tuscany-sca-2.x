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
 * Represents the mapping between protocols and baseuris for a given domain 
 * on a node
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public interface BaseUriMap {

    /**
     * Sets the name of the node that is responsible for running
     * the named component
     * 
     * @param domainUri the string uri for the parent domain
     * @param nodeUri the string uri for the node where the component will run
     * @Param protocol the protocol that the uri represent, e.g. http or https
     */
    public void setBaseUri(String domainUri, String nodeUri, String protocol, String uri);
 
    /**
     * Sets the name of the node that is responsible for running
     * the named component
     * 
     * @param domainUri the string uri for the parent domain
     * @param nodeUri the string uri for the node where the component will run
     * @Param protocol the protocol that the uri represent, e.g. http or https
     */
    public String getBaseUri(String domainUri, String nodeUri, String protocol);
    
}
