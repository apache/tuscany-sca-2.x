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
package org.apache.tuscany.sca.implementation.bpel.provider;

import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.ode.EmbeddedODEServer;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The model representing a sample CRUD implementation in an SCA assembly model.
 * The sample CRUD implementation is not a full blown implementation, it only
 * supports a subset of what a component implementation can support: - a single
 * fixed service (as opposed to a list of services typed by different
 * interfaces) - a directory attribute used to specify where a CRUD component is
 * going to persist resources - no references or properties - no policy intents
 * or policy sets
 */
public class BPELImplementationProvider implements ImplementationProvider {
    
    private RuntimeComponent component;
    private BPELImplementation implementation;
    private EmbeddedODEServer odeServer;

    /**
     * Constructs a new CRUD implementation.
     */
    public BPELImplementationProvider(RuntimeComponent component, BPELImplementation implementation,
                                      EmbeddedODEServer odeServer) {
        this.component = component;
        this.implementation = implementation;
        this.odeServer = odeServer;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        BPELInvoker invoker = new BPELInvoker(operation);
        return invoker;
    }

    public Invoker createCallbackInvoker(Operation operation) {
        BPELInvoker invoker = new BPELInvoker(operation);
        return invoker;
    }

    public void start() {
        System.out.println("Starting " + component.getName() + " " + component.getClass().getName());
        if (!odeServer.isInitialized()) odeServer.init();
        
        //FIXME:lresende
        //odeServer.getBpelServer().register(implementation.getProcessConf());
    }

    public void stop() {
        System.out.println("Stopping " + component.getName() + " " + component.getClass().getName());
    }

}
