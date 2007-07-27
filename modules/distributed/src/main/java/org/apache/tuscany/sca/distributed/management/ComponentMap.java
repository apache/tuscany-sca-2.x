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

package org.apache.tuscany.sca.distributed.management;

import java.util.List;


/**
 * Represents the mapping between components and runtime nodes.
 * 
 *
 * @version $Rev: 552343 $ $Date: 2007-07-01 18:43:40 +0100 (Sun, 01 Jul 2007) $
 */
public interface ComponentMap {
    
    /**
     * Sets the name of the node that is responsible for running
     * the named component
     * 
     * @param domainUri the string uri for the parent domain
     * @param nodeUri the string uri for the node where the component will run
     * @param componentName the string name for the component of interest
     */
    public void addComponent(String domainUri, String nodeUri, String componentName );
    
    /**
     * Removes the named component from the map
     *  
     * @param domainUri the string uri for the parent domain
     * @param nodeUri the string uri for the node where the component will run 
     * @param componentName the string name for the component of interest 
     */
    public void removeComponent(String domainUri, String nodeUri, String componentName);
    
    /**
     * Return the names of all components that will run on the specified node
     * 
     * @param domainUri the string uri for the parent domain
     * @param nodeUri the string uri for the node where the component will run
     * @return
     */
    public List<String> getComponentsForNode(String domainUri, String nodeUri);    
}
