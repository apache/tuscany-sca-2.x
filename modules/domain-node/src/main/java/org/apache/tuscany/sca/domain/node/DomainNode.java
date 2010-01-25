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

package org.apache.tuscany.sca.domain.node;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

public class DomainNode {

    private static final String DEFAULT_DOMAIN_SCHEME = "vm";
    private static final String DEFAULT_DOMAIN_NAME = "defaultDomain";
    private static final String DEFAULT_CONFIG_URI = DEFAULT_DOMAIN_SCHEME + "://" + DEFAULT_DOMAIN_NAME;

    private String domainName;
    private String domainRegistryURI;
    
    private Map<String, Node> nodes = new HashMap<String, Node>();
    
    public DomainNode() {
       this(DEFAULT_CONFIG_URI, new String[]{});   
    }
    
    public DomainNode(String... contributionLocations) {
        this(DEFAULT_CONFIG_URI, contributionLocations);   
    }
    
    public DomainNode(String configURI, String[] contributionLocations) {
        this.domainRegistryURI = configURI;
        initDomainName(configURI);
        if (contributionLocations == null || contributionLocations.length == 0) {
            addContribution(null, "_null");
        } else {
            for (String loc : contributionLocations) {
                addContribution(loc);
            }
        }
    }

    public void stop() {
        for (Node node : nodes.values()) {
            node.stop();
        }
    }

    public String addContribution(String location) {
        String uri = location;
        addContribution(uri, location);
        return uri;
    }

    public void addContribution(String location, String uri) {
        if (nodes.containsKey(uri)) {
            throw new IllegalArgumentException("contribution already added: " + uri);
        }
        NodeConfiguration configuration = NodeFactory.getInstance().createNodeConfiguration();
        if (location != null) {
            configuration.addContribution(uri, location);
        }
        configuration.setDomainRegistryURI(domainRegistryURI);
        configuration.setDomainURI(domainName);
        configuration.setURI(uri); //???
        Node node = NodeFactory.getInstance().createNode(configuration).start();
        nodes.put(uri, node);
    }

    public void removeContribution(String uri) {
        if (!nodes.containsKey(uri)) {
            throw new IllegalArgumentException("contribution not found: " + uri);
        }
        Node node = nodes.remove(uri);
        node.stop();
    }

    public String getDomainName() {
        return domainName;
    }
    
    public String getDomainConfigURI() {
        return domainRegistryURI;
    }
    
    public List<String> getServiceNames() {
        List<String> serviceNames = new ArrayList<String>();
        if (nodes.size() > 0) {
            ExtensionPointRegistry extensionsRegistry = ((NodeImpl)nodes.values().iterator().next()).getExtensionPoints();
            UtilityExtensionPoint utilities = extensionsRegistry.getExtensionPoint(UtilityExtensionPoint.class);
            DomainRegistryFactory domainRegistryFactory = utilities.getUtility(DomainRegistryFactory.class);
            EndpointRegistry endpointRegistry = domainRegistryFactory.getEndpointRegistry(getDomainConfigURI(), getDomainName());
            for (Endpoint endpoint : endpointRegistry.getEndpoints()) {
                // Would be nice if Endpoint.getURI() returned this:
                String name = endpoint.getComponent().getName() + "/" + endpoint.getService().getName();
                if (endpoint.getBinding() != null) {
                    // TODO: shouldn't the binding name be null if its not explicitly specified? 
                    //       For now don't include it if the same as the default
                    if (!endpoint.getService().getName().equals(endpoint.getBinding().getName())) {
                        name += "/" + endpoint.getBinding().getName();
                    }
                }
                serviceNames.add(name);
            }
        }
        return serviceNames;
    }

    public <T> T getService(Class<T> interfaze, String uri) throws NoSuchServiceException {
        try {
            return SCAClientFactory.newInstance(URI.create(getDomainName())).getService(interfaze, uri);
        } catch (NoSuchDomainException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void initDomainName(String configURI) {
//        URI uri = URI.create(fixScheme(configURI));
//        String dn = uri.getHost();
//        if (dn == null || dn.length() < 1) {
//            dn = DEFAULT_DOMAIN_NAME;
//        }
        if (configURI.startsWith("tuscany:vm:")) {
            domainName = configURI.substring("tuscany:vm:".length());  
        } else if (configURI.startsWith("tuscany:")) {
            int i = configURI.indexOf('?');
            if (i == -1) {
                domainName = configURI.substring("tuscany:".length());  
            } else{
                domainName = configURI.substring("tuscany:".length(), i);  
            }
        } else {
            domainName = configURI;  
        }
    }
    
    /**
     * I keep typing the scheme part with just a colon instead of colon slash slash
     * which URI doesn't parse properly which irritates me so fix it up here
     */
    protected String fixScheme(String uri) {
        int i = uri.indexOf(":");
        if (i > -1 && uri.charAt(i+1) != '/') {
            uri = uri.replaceFirst(":", ":/");
        }
        if (i > -1 && uri.charAt(i+2) != '/') {
            uri = uri.replaceFirst(":/", "://");
        }
        return uri;
    }
}
