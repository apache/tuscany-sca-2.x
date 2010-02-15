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
package org.apache.tuscany.sca.implementation.bpel.ode.provider;

import java.io.File;
import java.net.URI;

import javax.transaction.TransactionManager;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.apache.ode.dao.jpa.ProcessDAOImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.extensibility.ClassLoaderContext;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.ode.EmbeddedODEServer;
import org.apache.tuscany.sca.implementation.bpel.ode.ODEDeployment;
import org.apache.tuscany.sca.implementation.bpel.ode.ODEInitializationException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * BPEL Implementation provider
 *
 * @version $Rev$ $Date$
 */
public class BPELImplementationProvider implements ImplementationProvider {
    private final Log __log = LogFactory.getLog(getClass());

    private RuntimeComponent component;
    private BPELImplementation implementation;

    private EmbeddedODEServer odeServer;
    private TransactionManager txMgr;
    
    private ODEDeployment deployment;

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

        // Configure the service and reference interfaces to use a DOM databinding
        // as it's what ODE expects
        for(Service service: implementation.getServices() ){
            service.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
        } // end for
        
        for(Reference reference: implementation.getReferences() ) {
            reference.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
        } // end for

       for (Service service: component.getServices()) {
        	//TODO - MJE, 06/06/2009 - we can eventually remove the reset of the service interface
        	// contract and leave it to the Endpoints only
            service.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
            for( Endpoint endpoint : service.getEndpoints() ) {
                RuntimeEndpoint ep = (RuntimeEndpoint) endpoint;
                if (ep.getComponentTypeServiceInterfaceContract() != null) {
                    ep.getComponentTypeServiceInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
                }
            } // end for
        } // end for

        for (Reference reference : component.getReferences()) {
            reference.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
            for (EndpointReference endpointReference : reference.getEndpointReferences()) {
                RuntimeEndpointReference epr = (RuntimeEndpointReference)endpointReference;
                if (epr.getComponentTypeReferenceInterfaceContract() != null) {
                    epr.getComponentTypeReferenceInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
                }
            } // end for */
        } // end for

    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        BPELInvoker invoker = new BPELInvoker(component, service, operation, odeServer, txMgr);
        return invoker;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        if(__log.isInfoEnabled()) {
            __log.info("Starting " + component.getName());
        } // end if
        
		// Switch TCCL - use a classloader that can find classes related to the non-OSGi services
        // referenced from the implementation-bpel module which include the Persistence provider (OpenJPA) and
        // the JPA DAO implementation contained in the ODE project
    	ClassLoader tccl = ClassLoaderContext.setContextClassLoader(EmbeddedODEServer.class.getClassLoader(),
    			              PersistenceProviderImpl.class.getClassLoader(),
           					  ProcessDAOImpl.class.getClassLoader() );

        try {
    		if (!odeServer.isInitialized()) {
                // start ode server
                odeServer.init();
            }

            String location = this.implementation.getProcessDefinition().getLocation();
            URI deployURI = new URI(null, location, null);

            File deploymentDir = new File(deployURI).getParentFile();

            if(__log.isInfoEnabled()) {
                __log.info(">>> Deploying : " + deploymentDir.toString());
            }

            // Deploy the BPEL process
            if (odeServer.isInitialized()) {
            	deployment = new ODEDeployment( deploymentDir );
                try {
                    odeServer.registerTuscanyRuntimeComponent(implementation.getProcess(), component);

                    odeServer.deploy(deployment, implementation, component );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (ODEInitializationException inite) {
            throw new RuntimeException("BPEL Component Type Implementation : Error initializing embedded ODE server " + inite.getMessage(), inite);
        } catch(Exception e) {
            throw new RuntimeException("BPEL Component Type Implementation initialization failure : " + e.getMessage(), e);
        } finally {
            // Restore the TCCL if we changed it
            if( tccl != null ) Thread.currentThread().setContextClassLoader(tccl);
        } // end try
    } // end method start()

    public void stop() {
        if(__log.isInfoEnabled()) {
            __log.info("Stopping " + component.getName());
        }

        odeServer.undeploy(deployment);
        
        if (odeServer.isInitialized()) {
            // stop ode server
            odeServer.stop();
        }

        txMgr = null;

        if(__log.isInfoEnabled()) {
            __log.info("Stopped !!!");
        }
    } // end method stop()

}
