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
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;

/**
 * The runtime component service. Provides the bridge between the 
 * assembly model representation of a component service and its runtime 
 * realization
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeComponentService extends ComponentService {

    /**
     * Get a list of runtime wires to the service
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
     * Get the callback wires assoicated with this service
     * 
     * @return The list of runtime callback wires
     */
    List<RuntimeWire> getCallbackWires();

    /**
     * Returns the service binding provider associated with this
     * component service and the given binding.
     * 
     * @param binding The assembly model binding 
     * @return The runtime service binding provider
     */
    ServiceBindingProvider getBindingProvider(Binding binding);
    
    /**
     * Sets the service binding provider associated with this
     * component service and the given binding.
     *
     * @param binding The assembly model binding 
     * @param bindingProvider The runtime service binding provider
     */
    void setBindingProvider(Binding binding, ServiceBindingProvider bindingProvider);
    
    /**
     * Get the invoker for the given binding and operation
     * @param binding The assembly model binding 
     * @param operation The assembly model operation
     * @return The runtime invoker
     */
    Invoker getInvoker(Binding binding, Operation operation);    
    
    /**
     * Get the invocation chain for the given binding and operation
     * @param binding The assembly model binding 
     * @param operation The assembly model operation
     * @return The runtime invocation chain
     */
    InvocationChain getInvocationChain(Binding binding, Operation operation);    
    
    /**
     * Get the callback invoker for the given binding and operation
     * @param binding The assembly model binding 
     * @param operation The assembly model operation
     * @return The runtime callback invoker
     */
    Invoker getCallbackInvoker(Binding binding, Operation operation);    
}
