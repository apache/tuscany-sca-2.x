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

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;

/**
 * The runtime representation of a service endpoint
 * @tuscany.spi.extension.asclient
 */
public interface RuntimeEndpoint extends Endpoint, Invocable, Serializable {
    /**
     * Attach the service binding provider
     * @param provider
     */
    void setBindingProvider(ServiceBindingProvider provider);

    /**
     * Get the service binding provider
     * @return
     */
    ServiceBindingProvider getBindingProvider();
    
    /**
     * Get the interface contract for the binding. This represents the data types that the binding
     * protocol stack can process.
     * @return The binding interface contract
     */
    InterfaceContract getBindingInterfaceContract();
    
    /**
     * Get the interface contract of the service of the target component type, i.e., the
     * componentType.service.interfaceContract. This represents the data types that the implementation
     * code can process.
     * @return The target component type service interface contract
     */
    InterfaceContract getComponentTypeServiceInterfaceContract();
    
    
    /**
     * Check that endpoint  has compatible interface at the component and binding ends. 
     * The user can specify the interfaces at both ends so there is a danger that they won't be compatible.
     */
    void validateServiceInterfaceCompatibility();    
    
    /**
     * Get the composite context for the composite that contains this endpoint. This
     * is useful for accessing various composite level objects from within the 
     * runtime code
     */
    CompositeContext getCompositeContext();
    
    /**
     * Retrieve the normalized WSDL contract relating to the input WSDL contract
     * 
     * @param interfaceContract
     * @return
     */
    public InterfaceContract getGeneratedWSDLContract(InterfaceContract interfaceContract);    
    
}
