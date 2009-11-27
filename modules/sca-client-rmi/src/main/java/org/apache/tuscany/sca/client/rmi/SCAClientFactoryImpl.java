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

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFinder;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.ServiceUnavailableException;
import org.oasisopen.sca.client.SCAClientFactory;

public class SCAClientFactoryImpl extends SCAClientFactory {

    private EndpointRegistry endpointRegistry;
    private ExtensionPointRegistry extensionsRegistry;

    public SCAClientFactoryImpl(URI domainURI) {
        super(domainURI);

        if (!"vm".equals(domainURI.getScheme())) {
            this.extensionsRegistry = new DefaultExtensionPointRegistry();
            UtilityExtensionPoint utilities = extensionsRegistry.getExtensionPoint(UtilityExtensionPoint.class);
            DomainRegistryFactory domainRegistryFactory = utilities.getUtility(DomainRegistryFactory.class);
            this.endpointRegistry = domainRegistryFactory.getEndpointRegistry(getDomainURI().toString(), getDomainName());

            try {
                // TODO: wait a mo for the endpoint registry to replicate
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceInterface, String serviceName) throws NoSuchServiceException, NoSuchDomainException {
        if ("vm".equals(getDomainURI().getScheme())) {
            return getLocalService(serviceInterface, serviceName);
        } else {
            InvocationHandler handler = new SCAClientProxyHandler(serviceName, extensionsRegistry, endpointRegistry);
            return (T)Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] {serviceInterface}, handler);
        }
    }
    
    private String getDomainName() {
//        String uri = getDomainURI().toString();
//        int i = uri.indexOf(":");
//        if (i > -1 && uri.charAt(i+1) != '/') {
//            uri = uri.replaceFirst(":", ":/");
//        }
//        if (i > -1 && uri.charAt(i+2) != '/') {
//            uri = uri.replaceFirst(":/", "://");
//        }
//        if (i < 0) {
//            return uri;
//        } else {
//            return URI.create(uri).getHost();
//        }
        return getDomainURI().toString();
    }
    
    public void stop() {
        extensionsRegistry.stop();
    }

    public EndpointRegistry getEndpointRegistry() {
        return endpointRegistry;
    }

    public ExtensionPointRegistry getExtensionsRegistry() {
        return extensionsRegistry;
    }

    public <T> T getLocalService(Class<T> serviceInterface, String serviceName) throws NoSuchServiceException, NoSuchDomainException {
        URI domainURI = getDomainURI();
        if (domainURI == null) {
            domainURI = URI.create(Node.DEFAULT_DOMAIN_URI);
        }
        List<Node> nodes = NodeFinder.getNodes(domainURI);
        if (nodes == null || nodes.size() < 1) {
            throw new NoSuchDomainException(domainURI.toString());
        }

        for (Node n : nodes) {
            try {
                return n.getService(serviceInterface, serviceName);
            } catch(ServiceUnavailableException e) {
                // Ingore and continue
            }
        }

        throw new NoSuchServiceException(serviceName);
    }
}
