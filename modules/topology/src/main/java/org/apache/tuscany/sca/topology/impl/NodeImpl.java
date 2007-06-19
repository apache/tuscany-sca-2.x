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

package org.apache.tuscany.sca.topology.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.topology.Node;
import org.apache.tuscany.sca.topology.Scheme;
import org.apache.tuscany.sca.topology.Component;

/**
 * Represents an SCA node implementation.
 *
 * @version $Rev$ $Date$
 */

public class NodeImpl implements Node {
    private HashMap<String, DomainContainer> nodeDomain = new HashMap<String, DomainContainer>();
    private String name;
    
    public class DomainContainer {
        private List<Scheme> schemes = new ArrayList<Scheme>();
        private List<Component> components = new ArrayList<Component>();
        
        public List<Scheme> getSchemes() {
            return schemes;
        }
        
        public List<Component> getComponents() {
            return components;
        }        
    }
    
    /**
     * Constructs a new node.
     */
    protected NodeImpl() {
    }
    
    /**
     * Get the node name
     * 
     * @return
     */    
    public String getName() {
        return name;
    }
    
    /**
     * Set the node name
     * 
     * @param name
     */    
    public void setName(String name) {
        this.name = name;
    }    
    
    /**
     * Get domain names
     * 
     * @return
     */
    public Set<String> getDomainNames() {
        return nodeDomain.keySet();
    }
    
    /**
     * Return the Scheme/base URL pairs that this node uses for the 
     * specified domain. 
     * 
     * @param domainName
     * @return
     */    
    public List<Scheme> getSchemes(String domainName) {
        DomainContainer container = nodeDomain.get(domainName);
        
        if (container == null) {
            container = new DomainContainer();
            nodeDomain.put(domainName, container);
        }
        return container.getSchemes();
    }

    /**
     * Return the list of components that this node will run for the
     * specified domain
     * 
     * @param domainName
     * @return
     */
    public List<Component> getComponents(String domainName) {
        DomainContainer container = nodeDomain.get(domainName);
        
        if (container == null) {
            container = new DomainContainer();
            nodeDomain.put(domainName, container);
        }
        return container.getComponents();
    }
}