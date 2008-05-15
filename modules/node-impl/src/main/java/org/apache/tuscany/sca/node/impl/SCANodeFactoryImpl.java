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

package org.apache.tuscany.sca.node.impl;

import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 * A finder for SCA domains.
 * 
 * @version $Rev: 580520 $ $Date: 2007-09-29 00:50:25 +0100 (Sat, 29 Sep 2007) $
 */
public class SCANodeFactoryImpl extends SCANodeFactory {
    
        
    /**
     * Create a new SCA node factory instance.
     *  
     * @return a new SCA node factory
     */
    public SCANodeFactoryImpl() {

    }
    
    /**
     * Creates a new SCA node.
     * 
     * @param physicalNodeURI the URI of the node, this URI is used to provide the default 
     *        host and port information for the runtime for situations when bindings
     *        do provide this information
     * @param domainURI the URI of the domain that the node belongs to. This URI is 
     *        used to locate the domain manager on the network
     * @return a new SCA node.
     */
    public SCANode createSCANode(String physicalNodeURI, String domainURI) throws NodeException {
        return new SCANodeImpl(physicalNodeURI, domainURI, null);
    }
    
    /**
     * Creates a new SCA node. Many physical nodes may share the same logical URL in load balancing
     *  and failover scenarios where each node in the group runs the same contribution and 
     *  active composites 
     * 
     * @param physicalNodeURI the URI of the node, this URI is used to provide the default 
     *        host and port information for the runtime for situations when bindings
     *        don't provide this information
     * @param domainURI the URI of the domain that the node belongs to. This URI is 
     *        used to locate the domain manager on the network
     * @param logicalNodeURI the URI of the node to be used in situations where more than one node 
     *        are grouped together for failover or load balancing scenarios. The logicalNodeURI
     *        will typically identify the logical node where requests are sent
     * @return a new SCA node.
     */
    public SCANode createSCANode(String physicalNodeURI, String domainURI, String logicalNodeURI) throws NodeException {
        return new SCANodeImpl(physicalNodeURI, domainURI, logicalNodeURI);       
    }
    
    /**
     *  Creates a new SCA node. Many physical nodes may share the same logical URL in load balancing
     *  and failover scenarios where each node in the group runs the same contribution and 
     *  active composites. Also allows a class loaded to b specified. This is the 
     *  ClassLoader that will be used to load the management application used by the 
     *  node to talk to the domain
     * 
     * @param physicalNodeURI the URI of the node, this URI is used to provide the default 
     *        host and port information for the runtime for situations when bindings
     *        don't provide this information
     * @param domainURI the URI of the domain that the node belongs to. This URI is 
     *        used to locate the domain manager on the network
     * @param logicalNodeURI the URI of the node to be used in situations where more than one node 
     *        are grouped together for failover or load balancing scenarios. The logicalNodeURI
     *        will typically identify the logical node where requests are sent. If null is provided
     *        no logicalNodeURI is set.
     * @param classLoader the class loader to use by default when loading contributions. If null is provided
     *        the ClassLoader the derived automatically. 
     * @return a new SCA node.
     */
    public SCANode createSCANode(String physicalNodeURI, String domainURI, String logicalNodeURI, ClassLoader classLoader) throws NodeException {
        return new SCANodeImpl(physicalNodeURI, domainURI, logicalNodeURI, classLoader);               
    }
    

}
