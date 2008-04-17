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

import java.io.File;
import java.net.URL;

import javax.transaction.TransactionManager;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.ode.EmbeddedODEServer;
import org.apache.tuscany.sca.implementation.bpel.ode.ODEDeployment;
import org.apache.tuscany.sca.implementation.bpel.ode.ODEInitializationException;
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

    private EmbeddedODEServer odeServer;
    private TransactionManager txMgr;

    private BPELImplementation implementation;
    
    /**
     * Constructs a new BPEL Implementation.
     */
    public BPELImplementationProvider(RuntimeComponent component,
                                      BPELImplementation implementation,
                                      EmbeddedODEServer odeServer,
                                      TransactionManager txMgr) {
        this.component = component;
        this.implementation = implementation;
        this.odeServer = odeServer;
        this.txMgr = txMgr;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        BPELInvoker invoker = new BPELInvoker(component, service, operation, odeServer, txMgr);
        return invoker;
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        System.out.println("Starting " + component.getName());
        
        try {
            if (!odeServer.isInitialized()) {
                // start ode server
                odeServer.init();
            }

            URL deployURL = this.implementation.getProcessDefinition().getLocation();
            
            File deploymentDir = new File(deployURL.toURI().getPath()).getParentFile();
            System.out.println(">>> Deploying : " + deploymentDir.toString());

            // deploy the process
            if (odeServer.isInitialized()) {
                try {
                    txMgr.begin();
                    odeServer.setTuscanyRuntimeComponent(component.getName(), component);
                    odeServer.deploy(new ODEDeployment(deploymentDir));
                    txMgr.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    txMgr.rollback();
                }
            }
            
        } catch (ODEInitializationException inite) {
            throw new RuntimeException("BPEL Component Type Implementation : Error initializing embedded ODE server " + inite.getMessage(), inite);
        } catch(Exception e) {
            throw new RuntimeException("BPEl Component Type Implementation initialization failure : " + e.getMessage(), e);
        }
    }

    public void stop() {
        System.out.println("Stopping " + component.getName());
        
        if (odeServer.isInitialized()) {
            // start ode server
            odeServer.stop();
        }
        
        txMgr = null;
        
        System.out.println("Stopped !!!");

    }

}
