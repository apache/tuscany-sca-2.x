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

package org.apache.tuscany.sca.domain.spi;

import java.io.Externalizable;

import org.apache.tuscany.sca.domain.DomainException;
import org.osoa.sca.annotations.Remotable;


/**
 * The management interface for distributed domain. This is responsible for 
 * creating appropriate configuration on all the nodes that are running 
 * domain nodes for the distributed domain. 
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Remotable
public interface SCADomainEventService {
    
    // constants
    String SERVICE_NOT_KNOWN = "SERVICE_NOT_KNOWN";
    String SERVICE_NOT_REGISTERED = "SERVICE_NOT_REGISTERED";

    /**
     * Add information about a node in the domain
     * 
     * @param nodeURI
     * @param nodeURL
     * @return
     */
    void registerNode(String nodeURI, String nodeURL, Externalizable nodeManageReference) throws DomainException;
        
    /**
     * Remove information about a node in a domain
     * 
     * @param nodeURI
     * @param nodeURL
     * @return
     */
    void unregisterNode(String nodeURI) throws DomainException;
    
    /**
     * Tell the domain that a node has been started through the local API
     * 
     * @param nodeURI the URI of the node being started
     * @throws DomainException
     */
    void registerNodeStart(String nodeURI) throws DomainException;
    
    /**
     * Tell the domain that a node has been stopped through the local API
     * 
     * @param nodeURI
     * @throws DomainException
     */
    void registerNodeStop(String nodeURI) throws DomainException;

    /**
     * In the case where a contribution is added at a node this method is used to 
     * record the relationship directly. This is different from adding a contribution
     * to a domain as the contribution has already been allocated to a node
     * 
     * @param nodeURI the string URI for the node
     * @param contributionURI the string URI for the contribution
     * @param contributionURL the location of the contribution
     * @return
     */
    void registerContribution(String nodeURI, String contributionURI, String contributionURL) throws DomainException;
    

    /** 
     * In the case where a contribution is removed from a node locally this method is
     * used to remove the contribution from the domain
     * 
     * @param nodeURI the string URI for the node
     * @param contributionURI the string URI for the contribution
     * @return
     */
    void unregisterContribution(String nodeURI, String contributionURI) throws DomainException;     

    /**
     * In the case where a composite is added to the domain level composite at a node this 
     * method is used to record the event with the domain. 
     * 
     * @param nodeURI the string URI for the node
     * @param compositeQNameString the string QName of the composite
     * @throws DomainException
     */
    void registerDomainLevelComposite(String nodeURI, String compositeQNameString) throws DomainException;
    
    /**
     * Accepts information about a service endpoint and holds onto it
     * 
     * @param domainUri the string URI for the distributed domain
     * @param nodeUri the string URI for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     * @param url the endpoint URL
     * @return TBD - information about the registration
     */
    void registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String url) throws DomainException;
    
    /**
     * Removes information about a service endpoint
     * 
     * @param domainUri the string URI for the distributed domain
     * @param nodeUri the string URI for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     */    
    void unregisterServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName) throws DomainException;
     
    /**
     * Locates information about a service endpoint 
     * 
     * @param domainUri the string URI for the distributed domain
     * @param serviceName the name of the service to be found
     * @param bindingName the remote binding that we want to find an endpoint for
     * @return url the endpoint URL or SERVICE_NOT_REGISTERED
     */
    String findServiceEndpoint(String domainUri, String serviceName, String bindingName) throws DomainException;
    
    /**
     * Determines node that a service is available on
     * @param domainURI the string URI for the distributed domain
     * @param serviceName the name of the service to be found
     * @param bindingName the remote binding that we want to find an endpoint for
     * @return name of node running service or SERVICE_NOT_KNOWN (it's not been contributed) or SERVICE_NOT_REGISTERED (it's been contributed but isn;t running)
     * @throws DomainException
     */
    String findServiceNode(String domainURI, String serviceName, String bindingName) throws DomainException;
     
}
