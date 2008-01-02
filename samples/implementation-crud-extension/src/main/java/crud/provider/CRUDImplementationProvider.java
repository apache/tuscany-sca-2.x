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
package crud.provider;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

import crud.CRUDImplementation;
import crud.backend.ResourceManager;


/**
 * An implementation provider for sample CRUD implementations.
 * 
 * The implementation provider is responsible for handling the lifecycle of a component
 * implementation and creating operation invokers for the service operations provided
 * by the implementation.
 * 
 * The start() and stop() methods are called when a component is started
 * and stopped.
 *
 * The createInvoker method is called for each operation provided by the component
 * implementation. The implementation provider can create an invoker and initialize it
 * at that time to minimize the amount of work to be performed on each invocation.  
 */
class CRUDImplementationProvider implements ImplementationProvider {
    
    private RuntimeComponent component;
    private CRUDImplementation implementation;

    /**
     * Constructs a new CRUD implementation.
     */
    CRUDImplementationProvider(RuntimeComponent component, CRUDImplementation implementation) {
        this.component = component;
        this.implementation = implementation;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        CRUDImplementationInvoker invoker = new CRUDImplementationInvoker(operation, new ResourceManager(implementation.getDirectory()));
        return invoker;
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        System.out.println("Starting " + component.getName());
    }

    public void stop() {
        System.out.println("Stopping " + component.getName());
    }

}
