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

package org.apache.tuscany.sca.distributed.node.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osoa.sca.annotations.Scope; 

/**
 * Represents the mapping between components and runtime nodes.
 * 
 * TODO - just a place holder at the moment. For example, 
 *        doesn;t take any notice of domains at present
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class DefaultComponentRegistry {
    
    /**
     * A map of component name to node name
     */
    private HashMap<String, String> components = new HashMap<String, String>();
    
    /**
     * Retrieves the name of the node that is responsible for running
     * the named component
     *  
     * @param componentName the string name for the component of interest
     * @return node name
     */
    public String getComponentNode(String domainName, String componentName){
        return components.get(componentName);
    }
    
    /**
     * Sets the name of the node that is responsible for running
     * the named component
     *  
     * @param componentName the string name for the component of interest
     * @return node name
     */
    public void setComponentNode(String domainName, String componentName, String nodeName){
        components.put(componentName, nodeName);
    }
    
    /**
     * Loop through all the components in the model getting all the 
     * component names for the specified node name
     * 
     * @param nodeName
     * @return
     */
    public List<String> getComponentsForNode(String domainName, String nodeName) {
        List<String> componentList = new ArrayList<String>();
        
        for (String componentName : components.keySet()) {
            if (components.get(componentName).equals(nodeName)) {
                componentList.add(componentName);
            }
        }
        
        return componentList;
    }
}
