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
 * @version $Rev$ $Date$
 */
public class SCANodeFactoryImpl extends SCANodeFactory {
    
        
    public SCANodeFactoryImpl() {

    }

    public SCANode createSCANode(String physicalNodeURI, String domainURI) throws NodeException {
        return new SCANodeImpl(physicalNodeURI, domainURI);
    }
    
    @Deprecated
    public SCANode createSCANode(String physicalNodeURI, String domainURI, String logicalNodeURI) throws NodeException {
        return null;       
    }
    
    @Deprecated
    public SCANode createSCANode(String physicalNodeURI, String domainURI, String logicalNodeURI, ClassLoader classLoader) throws NodeException {
        return null;               
    }
    

}
