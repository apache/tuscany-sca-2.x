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

package org.apache.tuscany.sca.client.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeFactoryImpl;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;
import org.oasisopen.sca.client.SCAClientFactoryFinder;

public class SCAClientFactoryImpl extends SCAClientFactory {

    public static void setSCAClientFactoryFinder(SCAClientFactoryFinder factoryFinder) {
        SCAClientFactory.factoryFinder = factoryFinder;
    }

    private ExtensionPointRegistry extensionsRegistry;
    private EndpointRegistry endpointRegistry;
    private NodeFactoryImpl nodeFactory;
    
    public SCAClientFactoryImpl(URI domainURI) throws NoSuchDomainException {
        super(domainURI);
        
        this.nodeFactory = (NodeFactoryImpl)NodeFactory.getInstance();
        this.extensionsRegistry = nodeFactory.getExtensionPoints();
        if (extensionsRegistry != null) {
            UtilityExtensionPoint utilities = extensionsRegistry.getExtensionPoint(UtilityExtensionPoint.class);
            DomainRegistryFactory domainRegistryFactory = utilities.getUtility(DomainRegistryFactory.class);
            this.endpointRegistry = domainRegistryFactory.getEndpointRegistry("tuscanyClient:", getDomainURI().toString()); // TODO: shouldnt use null for reg uri
        }
    }   
    
    @Override
    public <T> T getService(Class<T> serviceInterface, String serviceName) throws NoSuchServiceException, NoSuchDomainException {
        
        if (endpointRegistry != null) {
            List<Endpoint> eps = endpointRegistry.findEndpoint(serviceName);
            if (eps == null || eps.size() < 1) {
                throw new NoSuchServiceException(serviceName);
            }
            Endpoint endpoint = eps.get(0); // TODO: what should be done with multiple endpoints?

            Node localNode = findLocalNode(endpoint);
            if (localNode != null) {
                return localNode.getService(serviceInterface, serviceName);
            }
        }

        String uri = getDomainURI().toString();
        if (uri.startsWith("tuscany:")) {
            uri = uri.replace("tuscany:", "tuscanyClient:");
        }
        InvocationHandler handler = new SCAClientProxyHandler(nodeFactory, uri, serviceName);
        return (T)Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] {serviceInterface}, handler);

    }

    private Node findLocalNode(Endpoint endpoint) {
        for (Node node : nodeFactory.getNodes().values()) {
        	for (Endpoint ep : ((NodeImpl)node).getServiceEndpoints()) {
                if (endpoint.getURI().equals(ep.getURI())) {
                    return node;
                }
        	}
        }
        return null;
    }

}
