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

package org.apache.tuscany.sca.domain;

import java.util.List;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.model.Domain;

/**
 * Represents an SCA domain.
 * 
 * @version $Rev: 580520 $ $Date: 2007-09-29 00:50:25 +0100 (Sat, 29 Sep 2007) $
 */
public interface SCADomainSPI extends SCADomain {

    /**
     * Add information about a node in the domain
     * 
     * @param nodeURI
     * @param nodeURL
     * @return
     */
    public String addNode(String nodeURI, String nodeURL);
        
    /**
     * Remove information about a node in a domain
     * 
     * @param nodeURI
     * @param nodeURL
     * @return
     */
    public String removeNode(String nodeURI);

    
    /**
     * Accepts information about a service endpoint and holds onto it
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     * @param url the enpoint url
     * @return TBD - information about the registration
     */
    public String registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL);
    
    /**
     * Removes information about a service endpoint
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     */    
    public String removeServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName);
     
    /**
     * Locates information about a service endpoint 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that we want to find an endpoint for
     * @return url the endpoint url
     */
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName); 
    
    
    /** 
     * Returns the model of the domain
     * @return
     */
    public Domain getDomainModel();
    
    /** 
     * Returns information for all registered services
     * @return
     */
   // public ServiceInfo getServiceInfo(); 
    
    
    /**
     * Return information about all the nodes in the domain
     * 
     * @return
     */
  //  public List<NodeInfo> getNodeInfo();    
    
}