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

package org.apache.tuscany.sca.endpoint.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.zookeeper.ZooKeeper;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * 
 */
public class DistributedRegistry implements EndpointRegistry, LifeCycleListener {
    private String domainURI;
    private String registryURI;
    private ZooKeeper zooKeeper;

    /**
     * 
     */
    public DistributedRegistry(String domainURI, String registryURI) {
        this.domainURI = domainURI;
        this.registryURI = registryURI;
    }

    public void start() {
        try {
            zooKeeper = new ZooKeeper(registryURI, 100, null);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void stop() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                throw new ServiceRuntimeException(e);
            }
            zooKeeper = null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#addEndpoint(org.apache.tuscany.sca.assembly.Endpoint)
     */
    public void addEndpoint(Endpoint endpoint) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#addEndpointReference(org.apache.tuscany.sca.assembly.EndpointReference)
     */
    public void addEndpointReference(EndpointReference endpointReference) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#addListener(org.apache.tuscany.sca.runtime.EndpointListener)
     */
    public void addListener(EndpointListener listener) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#findEndpoint(org.apache.tuscany.sca.assembly.EndpointReference)
     */
    public List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#findEndpointReference(org.apache.tuscany.sca.assembly.Endpoint)
     */
    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#getEndpoint(java.lang.String)
     */
    public Endpoint getEndpoint(String uri) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#getEndpointRefereneces()
     */
    public List<EndpointReference> getEndpointRefereneces() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#getEndpoints()
     */
    public List<Endpoint> getEndpoints() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#getListeners()
     */
    public List<EndpointListener> getListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#removeEndpoint(org.apache.tuscany.sca.assembly.Endpoint)
     */
    public void removeEndpoint(Endpoint endpoint) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#removeEndpointReference(org.apache.tuscany.sca.assembly.EndpointReference)
     */
    public void removeEndpointReference(EndpointReference endpointReference) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#removeListener(org.apache.tuscany.sca.runtime.EndpointListener)
     */
    public void removeListener(EndpointListener listener) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.sca.runtime.EndpointRegistry#updateEndpoint(java.lang.String, org.apache.tuscany.sca.assembly.Endpoint)
     */
    public void updateEndpoint(String uri, Endpoint endpoint) {
        // TODO Auto-generated method stub

    }

}
