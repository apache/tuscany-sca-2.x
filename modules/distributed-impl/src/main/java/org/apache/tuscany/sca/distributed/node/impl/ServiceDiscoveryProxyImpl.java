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

import org.apache.tuscany.sca.distributed.domain.ServiceDiscoveryService;
import org.apache.tuscany.sca.distributed.domain.ServiceInfo;
import org.osoa.sca.annotations.Reference;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date$
 */
public class ServiceDiscoveryProxyImpl implements ServiceDiscoveryService{
    
    @Reference
    protected ServiceDiscoveryService serviceDiscovery;
     
    /**
     * Accepts information about a service endpoint and holds onto it
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     * @param url the enpoint url
     * @return dummy valus just so that we don;t have a void return which doesn't work 
     */
    public String registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
        System.err.println("Registering service: [" + 
                domainUri + " " +
                nodeUri + " " +
                serviceName + " " +
                bindingName + " " +
                URL +
                "]");
        
        String dummy = null; 
        
     //   try {
            dummy =  serviceDiscovery.registerServiceEndpoint(domainUri, nodeUri, serviceName, bindingName, URL);
     //   } catch(Exception ex) {
            // When we get round to caching we could keep a list of registered endpoints
            // and try again later either when this object is called or when the 
            // domain comes back up again
            /* not sure this should be an exception 
            throw new IllegalStateException("Unable to  registering service: "  +
                                            domainUri + " " +
                                            nodeUri + " " +
                                            serviceName + " " +
                                            bindingName + " " +
                                            URL, ex );
            */
     //   }
        
        return dummy;
    }
    
   
    /**
     * Locates information about a service endpoint 
     * 
     * @param domainUri the string uri for the distributed domain
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that we want to find an endpoint for
     * @return url the endpoint url
     */
    public String findServiceEndpoint(String domainUri, String serviceName, String bindingName){
        System.err.println("Finding service: [" + 
                domainUri + " " +
                serviceName + " " +
                bindingName +
                "]");
        
        String url = null;
        
        try {
            url =  serviceDiscovery.findServiceEndpoint(domainUri, serviceName, bindingName);
        } catch(Exception ex) {
            // do nothing here. 
            // If we can't find a service fo what ever reason then just return null
        }
        
        return url;
    }
    
    public ServiceInfo getServiceInfo(){
        return serviceDiscovery.getServiceInfo();
    }
    
}
