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

import org.osoa.sca.annotations.Remotable;


/**
 * The management interface for distributed domain. This is resposible for 
 * creating appropriate configuration on all the nodes that are running 
 * domain nodes for the distributed domain. 
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Remotable
public interface SCADomainEventService {

    /**
     * Add information about a node in the domain
     * 
     * @param nodeURI
     * @param nodeURL
     * @return
     */
    public void registerNode(String nodeURI, String nodeURL) throws DomainException;
        
    /**
     * Remove information about a node in a domain
     * 
     * @param nodeURI
     * @param nodeURL
     * @return
     */
    public void unregisterNode(String nodeURI) throws DomainException;
    
    /**
     * In the case where a contribution is added at a node this method is used to 
     * record the relationship directly. This is different from adding a contribution
     * to a domain as the contribution has alread been allocated to a node
     * 
     * @param nodeURI the string uri for the node
     * @param contributionURI the string uri for the contribution
     * @param nodeURL the location of the contribution
     * @return
     */
    public void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException;
    

    /** 
     * In the case where a contribution is removed from a node locally this method is
     * used to remove the contribution from the domain
     * 
     * @param nodeURI the string uri for the node
     * @param contributionURI the string uri for the contribution
     * @return
     */
    public void unregisterContribution(String nodeURI, String contributionURI) throws DomainException;     

    
    /**
     * Accepts information about a service endpoint and holds onto it
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     * @param url the endpoint url
     * @return TBD - information about the registration
     */
    public void registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL) throws DomainException;
    
    /**
     * Removes information about a service endpoint
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     */    
    public void unregisterServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName) throws DomainException;
     
    /**
     * Locates information about a service endpoint 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that we want to find an endpoint for
     * @return url the endpoint url
     */
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName) throws DomainException; 
     
}
