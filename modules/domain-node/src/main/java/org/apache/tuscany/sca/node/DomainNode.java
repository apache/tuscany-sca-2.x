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

package org.apache.tuscany.sca.node;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class DomainNode {

    private String domainName;
    
    private NodeFactory nodeFactory;
    private Map<String, Node> nodes = new HashMap<String, Node>();
    
    public DomainNode() {
       this("vm://defaultDomain");   
    }
    
    public DomainNode(String configURI) {
        parseConfigURI(configURI);
        start();
    }
    
    public DomainNode(String configURI, String... contributionLocations) {
        parseConfigURI(configURI);
        start();
        for (String loc : contributionLocations) {
            addContribution(loc);
        }
    }
    
    public void start() {
        if (nodeFactory != null) {
            throw new IllegalStateException("Already started");
        }
        
        nodeFactory = NodeFactory.getInstance(domainName);
    }
    
    public boolean isStarted() {
        return nodeFactory != null;
    }

    public void stop() {
        if (nodeFactory == null) {
            throw new IllegalStateException("not started");
        }
        
        for (Node node : nodes.values()) {
            node.stop();
        }
        
// TODO: stopping the node factory stops _all_ domain nodes not just this instance        
//        nodeFactory.destroy();
//        nodeFactory = null;
//        nodes.clear();
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
        Node node = nodeFactory.createNode(new Contribution(uri, location)).start();
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
    
    protected void parseConfigURI(String configURI) {
        URI uri = URI.create(configURI);

        this.domainName = uri.getHost();
    }

}
