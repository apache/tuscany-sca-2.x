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

package org.apache.tuscany.sca.topology;

import java.util.List;

/**
 * Represents an SCA node implementation. An SCA node is a running instance
 * of a program able to run SCA assemblies. A distributes runtime contains
 * main nodes
 *
 * @version $Rev$ $Date$
 */
public interface Node {
    
    /**
     * Get the node name
     * 
     * @return
     */
    public String getName();
    
    /**
     * Set the node name
     * 
     * @param name
     */
    public void setName(String name);
    
    /**
     * Return the Scheme/base URL pairs that this node uses for the 
     * specified domain. 
     * 
     * @param domainName
     * @return
     */    
    public List<Scheme> getSchemes(String domainName);

    /**
     * Return the list of components that this node will run for the
     * specified domain
     * 
     * @param domainName
     * @return
     */
    public List<Component> getComponents(String domainName);

}
