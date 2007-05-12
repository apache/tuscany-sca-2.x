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

import org.apache.tuscany.sca.core.RuntimeComponent;
import org.apache.tuscany.sca.core.RuntimeComponentService;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;

import crud.CRUDImplementation;
import crud.backend.ResourceManager;

/**
 * The model representing a sample CRUD implementation in an SCA assembly model.
 * The sample CRUD implementation is not a full blown implementation, it only
 * supports a subset of what a component implementation can support: - a single
 * fixed service (as opposed to a list of services typed by different
 * interfaces) - a directory attribute used to specify where a CRUD component is
 * going to persist resources - no references or properties - no policy intents
 * or policy sets
 * 
 * @version $$Rev$$ $$Date: 2007-04-23 19:18:54 -0700 (Mon, 23 Apr
 *          2007) $$
 */
public class CRUDImplementationProvider implements ImplementationProvider {
    
    private RuntimeComponent component;
    private CRUDImplementation implementation;

    /**
     * Constructs a new CRUD implementation.
     */
    public CRUDImplementationProvider(RuntimeComponent component, CRUDImplementation implementation) {
        this.component = component;
        this.implementation = implementation;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        CRUDInvoker invoker = new CRUDInvoker(operation, new ResourceManager(implementation.getDirectory()));
        return invoker;
    }

    public Invoker createCallbackInvoker(Operation operation) {
        CRUDInvoker invoker = new CRUDInvoker(operation, new ResourceManager(implementation.getDirectory()));
        return invoker;
    }

    public void start() {
        System.out.println("Starting " + component.getName());
    }

    public void stop() {
        System.out.println("Stopping " + component.getName());
    }

}
