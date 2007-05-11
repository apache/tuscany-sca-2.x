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

import java.util.List;

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;

/**
 * @version $Rev$ $Date$
 */
public interface RuntimeComponentService extends ComponentService {
    /**
     * Add a runtime wire to the service
     * 
     * @param wire
     */
    void addRuntimeWire(RuntimeWire wire);

    /**
     * Get a list of runtime wires to the service
     * 
     * @return
     */
    List<RuntimeWire> getRuntimeWires();
    /**
     * Get the runtime wire for the given binding
     * @param binding
     * @return
     */
    RuntimeWire getRuntimeWire(Binding binding);
    
    /**
     * Add a callback wire if the service has a callabck interface
     * 
     * @param wire
     */
    void addCallbackWire(RuntimeWire wire);

    /**
     * Get the callback wires assoicated with this service
     * 
     * @return
     */
    List<RuntimeWire> getCallbackWires();

    /**
     * Returns the service binding provider associated with this
     * component service and the given binding.
     * 
     * @param binding
     * @return
     */
    ServiceBindingProvider getBindingProvider(Binding binding);
    
    /**
     * Sets the service binding provider associated with this
     * component service and the given binding.
     *
     * @param binding
     * @param bindingProvider
     */
    void setBindingProvider(Binding binding, ServiceBindingProvider bindingProvider);
    
    /**
     * Get the invoker for the given binding and operation
     * @param binding
     * @param operation
     * @return
     */
    Invoker getInvoker(Binding binding, Operation operation);    
    
    /**
     * Get the callback invoker for the given binding and operation
     * @param binding
     * @param operation
     * @return
     */
    Invoker getCallbackInvoker(Binding binding, Operation operation);    
}
