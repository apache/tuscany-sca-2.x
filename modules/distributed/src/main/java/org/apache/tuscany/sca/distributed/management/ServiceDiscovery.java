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

import java.util.List;


/**
 * Provides a mechanism whereby service enpoints can be shared amongst the 
 * nodes of a distributed domain.
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public interface ServiceDiscovery {
   
    /**
     * Accepts information about a service endpoint and holds onto it
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param url the enpoint url
     */
    public void registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String URL);
    
    /**
     * Locates information about a service endpoint 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @return url
     */
    public String findServiceEndpoint(String domainUri, String serviceName);  
 
}
