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

package org.apache.tuscany.sca.runtime;

import java.io.Serializable;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;

/**
 * The runtime representation of an endpoint reference
 * @tuscany.spi.extension.asclient
 */
public interface RuntimeEndpointReference extends EndpointReference, Invocable, Serializable {
    /**
     * Set the reference binding provider for the endpoint reference
     * @param provider The binding provider
     */
    void setBindingProvider(ReferenceBindingProvider provider);

    /**
     * Get the reference binding provider for the endpoint reference
     * @return The binding provider
     */
    ReferenceBindingProvider getBindingProvider();
    
    /**
     * Get the interface contract for the binding. This represents the data types that the binding
     * protocol stack can process.
     * @return The binding interface contract
     */
    InterfaceContract getBindingInterfaceContract();
    
    
    /**
     * Get the interface contract of the reference of the source component type, i.e., the
     * componentType.reference.interfaceContract. This represents the data types that the 
     * implementation code uses to make the outbound call.
     * @return The source component type reference interface contract
     */
    InterfaceContract getComponentTypeReferenceInterfaceContract();   
    
    /**
     * Check that endpoint reference has compatible interface at the component and binding ends. 
     * The user can specify the interfaces at both ends so there is a danger that they won't be compatible.
     * There is checking in the activator but of course endpoint references may not have a binding assigned
     * until final resolution. 
     */
    public void validateReferenceInterfaceCompatibility();     
    
    boolean isOutOfDate();
    void rebuild();
    boolean isStarted();
}
