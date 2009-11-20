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
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.management.ConfigAttributes;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClient;

public class DomainNode {

    public static final String DOMAIN_NAME_ATTR = "domainName";
    public static final String DOMAIN_SCHEME_ATTR = "domainScheme";
    public static final String DEFAULT_DOMAIN_SCHEME = "vm";
    public static final String DEFAULT_DOMAIN_NAME = "defaultDomain";

    private ConfigAttributes configAttributes = new ConfigAttributesImpl();
    private String domainRegistryURI;
    
    private NodeFactory nodeFactory;
    private Map<String, Node> nodes = new HashMap<String, Node>();
    
    public DomainNode() {
       this(DEFAULT_DOMAIN_SCHEME + "://" + DEFAULT_DOMAIN_NAME);   
    }
    
    public DomainNode(String configURI) {
        this.domainRegistryURI = configURI;
        parseConfigURI(configURI);
        start();
    }
    
    public DomainNode(String configURI, String... contributionLocations) {
        this.domainRegistryURI = configURI;
        parseConfigURI(configURI);
        start();
        for (String loc : contributionLocations) {
            addContribution(loc);
        }
    }
    
    public void start() {
        if (nodeFactory != null) {
            throw new IllegalStateException("The node is already started");
        }
        
        nodeFactory = NodeFactory.getInstance(configAttributes.getAttributes().get(DOMAIN_NAME_ATTR));
    }
    
    public boolean isStarted() {
        return nodeFactory != null;
    }

    public void stop() {
        if (nodeFactory == null) {
            throw new IllegalStateException("The node is not started");
        }
        
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
        NodeConfiguration configuration = nodeFactory.createNodeConfiguration();
        configuration.addContribution(uri, location);
        configuration.setDomainRegistryURI(domainRegistryURI);
        configuration.setDomainURI(configAttributes.getAttributes().get(DOMAIN_NAME_ATTR));
        configuration.setURI(uri);
        Node node = nodeFactory.createNode(configuration).start();
        nodes.put(uri, node);
    }

    public void removeContribution(String uri) {
        if (!nodes.containsKey(uri)) {
            throw new IllegalArgumentException("contribution not found: " + uri);
        }
        Node node = nodes.remove(uri);
        node.stop();
    }

    public ConfigAttributes getConfigAttributes() {
        return configAttributes;
    }
    
    public String getDomainName() {
        return configAttributes.getAttributes().get(DOMAIN_NAME_ATTR);
    }
    
    protected void parseConfigURI(String configURI) {
        URI uri = URI.create(fixScheme(configURI));
        String dn = uri.getHost();
        if (dn == null || dn.length() < 1) {
            dn = DEFAULT_DOMAIN_NAME;
        }
        configAttributes.getAttributes().put(DOMAIN_NAME_ATTR, dn);  
        String scheme = uri.getScheme();
        if (scheme != null && scheme.length() > 0) {
            configAttributes.getAttributes().put(DOMAIN_SCHEME_ATTR, scheme);  
        }

        String query = uri.getQuery();
        if (query != null && query.length() > 0) {
            String[] params = query.split("&");
            for (String param : params){
                String name = param.split("=")[0];  
                String value = param.split("=")[1];  
                configAttributes.getAttributes().put(name, value);  
            }
        }
    }
    
    /**
     * I keep typing the scheme part with just a colon instead of colon slash slash
     * which URI doesn't parse properly which irritates me so fix it up here
     */
    private String fixScheme(String uri) {
        int i = uri.indexOf(":");
        if (i > -1 && uri.charAt(i+1) != '/') {
            uri = uri.replaceFirst(":", ":/");
        }
        if (i > -1 && uri.charAt(i+2) != '/') {
            uri = uri.replaceFirst(":/", "://");
        }
        return uri;
    }
    
    public <T> T getService(Class<T> interfaze, String uri) throws NoSuchServiceException {
        try {
            return SCAClient.getService(interfaze, configAttributes.getAttributes().get(DOMAIN_NAME_ATTR) + "/" + uri);
        } catch (NoSuchDomainException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
