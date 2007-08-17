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

package org.apache.tuscany.sca.distributed.management.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.distributed.management.ServiceDiscovery;
import org.osoa.sca.annotations.Scope;


/**
 * Stores details of services exposed and retrieves details of remote services
 * 
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
@Scope("COMPOSITE")
public class ServiceDiscoveryMemoryImpl implements ServiceDiscovery{
    
    List<ServiceEndpoint> serviceEndpoints = new ArrayList<ServiceEndpoint>();
    
    public class ServiceEndpoint {
        private String domainUri;
        private String nodeUri;
        private String serviceName;
        private String bindingName;
        private String url;
        
        public ServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
            this.domainUri = domainUri;
            this.nodeUri = nodeUri;
            this.serviceName = serviceName;
            this.bindingName = bindingName;
            this.url = URL;
        }
        
        public boolean match(String domainUri, String serviceName, String bindingName) {
            return ((this.domainUri.equals(domainUri)) &&
                    (this.serviceName.equals(serviceName)) &&
                    (this.bindingName.equals(bindingName)));
        }
        
        public String getUrl() {
            return url;
        }     
        
        public String toString (){
            return "[" +
                   domainUri + " " +
                   nodeUri + " " +
                   serviceName + " " +
                   bindingName + " " + 
                   url +
                   "]";
        }
    }
     
    /**
     * Accepts information about a service endpoint and holds onto it
     * 
     * @param domainUri the string uri for the distributed domain
     * @param nodeUri the string uri for the current node
     * @param serviceName the name of the service that is exposed and the provided endpoint
     * @param bindingName the remote binding that is providing the endpoint
     * @param url the enpoint url
     */
    public String  registerServiceEndpoint(String domainUri, String nodeUri, String serviceName, String bindingName, String URL){
        ServiceEndpoint serviceEndpoint = new ServiceEndpoint (domainUri, nodeUri, serviceName, bindingName, URL);
        serviceEndpoints.add(serviceEndpoint);
        System.err.println("Registering service: " + serviceEndpoint.toString());
        return "";
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
        
        String url = "";
        
        for(ServiceEndpoint serviceEndpoint : serviceEndpoints){
            if ( serviceEndpoint.match(domainUri, serviceName, bindingName)){
                url = serviceEndpoint.getUrl();
                System.err.println("Matching service url: " + url); 
            }
        }
        return url;
    }
    
}
