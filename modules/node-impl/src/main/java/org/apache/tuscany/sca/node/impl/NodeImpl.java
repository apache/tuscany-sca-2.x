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

package org.apache.tuscany.sca.node.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFinder;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.oasisopen.sca.CallableReference;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * An SCA Node that is managed by the NodeManager
 */
public class NodeImpl implements Node, Client {
    private static final Logger logger = Logger.getLogger(NodeImpl.class.getName());
    private ProxyFactory proxyFactory;
    private CompositeActivator compositeActivator;
    private NodeConfiguration configuration;
    private NodeFactoryImpl manager;

    public NodeImpl(NodeFactoryImpl manager, NodeConfiguration configuration) {
        super();
        this.configuration = configuration;
        this.manager = manager;
    }

    public void destroy() {
    }

    public Node start() {
        logger.log(Level.INFO, "Starting node: " + configuration.getURI());

        manager.init();
        manager.addNode(configuration, this);
        this.proxyFactory = manager.proxyFactory;
        this.compositeActivator =
            manager.extensionPoints.getExtensionPoint(UtilityExtensionPoint.class).getUtility(CompositeActivator.class,
                                                                                              true);
        try {

            compositeActivator.setDomainComposite(manager.configureNode(configuration));
            for (Composite composite : compositeActivator.getDomainComposite().getIncludes()) {
                // Activate the composite
                compositeActivator.activate(composite);

                // Start the composite
                compositeActivator.start(composite);
            }

            NodeFinder.addNode(NodeUtil.createURI(configuration.getDomainURI()), this);

            return this;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public void stop() {
        logger.log(Level.INFO, "Stopping node: " + configuration.getURI());

        try {
            if (compositeActivator == null) {
                return;
            }
            NodeFinder.removeNode(NodeUtil.createURI(configuration.getDomainURI()));
            if( compositeActivator.getDomainComposite() != null ) {
	            List<Composite> composites = compositeActivator.getDomainComposite().getIncludes();
	            for (Composite composite : composites) {
	
	                // Stop the composite
	                compositeActivator.stop(composite);
	
	                // Deactivate the composite
	                compositeActivator.deactivate(composite);
	
	            } // end for
	            composites.clear();
            } // end if 

            manager.removeNode(configuration);
            this.compositeActivator = null;
            this.proxyFactory = null;

        } catch (ActivationException e) {
            throw new IllegalStateException(e);
        }

    }

    @SuppressWarnings("unchecked")
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R)proxyFactory.cast(target);
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {

        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {

        // Extract the component name
        String componentName;
        String serviceName;
        int i = name.indexOf('/');
        if (i != -1) {
            componentName = name.substring(0, i);
            serviceName = name.substring(i + 1);

        } else {
            componentName = name;
            serviceName = null;
        }

        // Lookup the component
        Component component = null;

        for (Composite composite : compositeActivator.getDomainComposite().getIncludes()) {
            for (Component compositeComponent : composite.getComponents()) {
                if (compositeComponent.getName().equals(componentName)) {
                    component = compositeComponent;
                }
            }
        }

        if (component == null) {
            throw new ServiceRuntimeException("The service " + name + " has not been contributed to the domain");
        }
        RuntimeComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        componentContext =
                            ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                        return componentContext.createSelfReference(businessInterface, compositeService
                            .getPromotedService());
                    }
                    break;
                }
            }
            // No matching service found
            throw new ServiceRuntimeException("Composite service not found: " + name);
        } else {
            componentContext = ((RuntimeComponent)component).getComponentContext();
            if (serviceName != null) {
                return componentContext.createSelfReference(businessInterface, serviceName);
            } else {
                return componentContext.createSelfReference(businessInterface);
            }
        }
    }

    public NodeConfiguration getConfiguration() {
        return configuration;
    }

    public ExtensionPointRegistry getExtensionPoints() {
        return manager.getExtensionPoints();
    }
    
    /**
     * Get the service endpoints in this Node
     * TODO: needs review, works for the very simple testcase but i expect there are
     *    other endpoints to be included
     */
    public List<Endpoint2> getServiceEndpoints() {
        List<Endpoint2> endpoints = new ArrayList<Endpoint2>();
        if (compositeActivator != null) {
            Composite domainComposite = compositeActivator.getDomainComposite();
            if (domainComposite != null) {
                for (Composite composite : domainComposite.getIncludes()) {
                    for (Component component : composite.getComponents()) {
                        for (Service service : component.getServices()) {
                        	// MJE 28/05/2009 - changed to RuntimeComponentService from RuntimeComponentServiceImpl
                        	// - no need to access the Impl directly here
                            if (service instanceof RuntimeComponentService) {
                                endpoints.addAll(((RuntimeComponentService)service).getEndpoints());
                            }
                        }
                    }
                }
            }
        }
        return endpoints;
    }

}
