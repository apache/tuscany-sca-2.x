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

package org.apache.tuscany.sca.core;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;

/**
 * The endpoint reference for a component service or reference
 * 
 * @version $Rev$ $Date$
 */
public interface EndpointReference {
    /**
     * Get the component for the endpoint
     * @return The component, null of the EPR is for a non-SCA service
     */
    RuntimeComponent getComponent();

    /**
     * Get the component service or reference for the endpoint
     * @return The component service or reference, null if the EPR is for a non-SCA service
     */
    Contract getContract();

    /**
     * Get the binding for the endpoint
     * @return The binding
     */
    Binding getBinding();

    /**
     * Get the interface contract for the endpoint
     * @return The interface contract
     */
    InterfaceContract getInterfaceContract();
    
    /**
     * Get the URI for this endpoint
     * @return
     */
    String getURI();
    
    /**
     * Test if the EPR is resolved
     * @return
     */
    boolean isUnresolved();
    
    /**
     * Set the unresolved flag for the EPR
     * @param unresolved
     */
    void setUnresolved(boolean unresolved);
    
}
