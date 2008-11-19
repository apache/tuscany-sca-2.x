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

import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.endpointresolver.EndpointResolver;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;

/**
 * The runtime component reference. Provides the bridge between the 
 * assembly model representation of a component reference and its runtime 
 * realization
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeComponentReference extends ComponentReference {

    /**
     * Get a list of runtime wires to the reference
     * 
     * @return The list of wires
     */
    List<RuntimeWire> getRuntimeWires();
    
    /**
     * Get the runtime wire for the given binding
     * @param binding The assembly model binding 
     * @return The runtime wire
     */
    RuntimeWire getRuntimeWire(Binding binding);

    /**
     * Returns the reference binding provider associated with this
     * component reference and the given binding.
     * 
     * @param binding The assembly model binding 
     * @return The runtime reference binding provider
     */
    ReferenceBindingProvider getBindingProvider(Binding binding);
    
    /**
     * Sets the reference binding provider associated with this
     * component reference and the given binding.
     * 
     * @param binding The assembly model binding 
     * @param bindingProvider The runtime reference binding provider
     */
    void setBindingProvider(Binding binding, ReferenceBindingProvider bindingProvider);
    
    /**
     * Returns the endpoint resolver associated with this
     * component reference and the given endpoint.
     * 
     * @param endpont The assembly model endpoint 
     * @return The enpoint resolver
     */
    EndpointResolver getEndpointResolver(Endpoint endpoint);
    
    /**
     * Sets the endpoint resolver associated with this
     * component reference and the given endpoint.
     * 
     * @param binding The assembly model binding 
     * @param bindingProvider The runtime reference binding provider
     */
    void setEndpointResolver(Endpoint endpoint, EndpointResolver endpointResolver);    
    
    /**
     * Add a policy provider for the given binding to the reference
     * @param binding The assembly model binding
     * @param policyProvider The policy handler
     */
    void addPolicyProvider(Binding binding, PolicyProvider policyProvider);
    
    /**
     * Get a list of policy providers for the given binding
     * @param binding The assembly model binding
     * @return A list of policy providers for the given binding
     */
    List<PolicyProvider> getPolicyProviders(Binding binding);    
    
    /**
     * Get the invoker for the given binding and operation
     * @param binding The assembly model binding
     * @param operation The assembly model operation
     * @return The runtime Invoker
     */
    Invoker getInvoker(Binding binding, Operation operation); 
    
    /**
     * Set the owning component
     * @param component
     */
    void setComponent(RuntimeComponent component);
}
