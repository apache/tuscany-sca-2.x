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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFinder;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.node.management.NodeManager;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.ServiceUnavailableException;

/**
 * An SCA Node that is managed by the NodeManager
 */
public class NodeImpl implements Node, Client {
    private static final Logger logger = Logger.getLogger(NodeImpl.class.getName());
    private ProxyFactory proxyFactory;
    private CompositeActivator compositeActivator;
    private NodeConfiguration configuration;
    private NodeFactoryImpl manager;
    private List<Contribution> contributions;
    private NodeManager mbean;

    /**
     * Create a node from the configuration
     * @param manager
     * @param configuration
     */
    public NodeImpl(NodeFactoryImpl manager, NodeConfiguration configuration) {
        super();
        this.configuration = configuration;
        this.manager = manager;
    }
    
    /**
     * Create a node from the configuration and loaded contributions
     * @param manager
     * @param configuration
     * @param contributions
     */
    public NodeImpl(NodeFactoryImpl manager, NodeConfiguration configuration, List<Contribution> contributions) {
        super();
        this.configuration = configuration;
        this.manager = manager;
        this.contributions = new ArrayList<Contribution>(contributions);
    }

    public String getURI() {
        return getConfiguration().getURI();
    }

    public void destroy() {
    }

    public Node start() {
        logger.log(Level.INFO, "Starting node: " + configuration.getURI() + " domain: " + configuration.getDomainName());

        manager.init();
        manager.addNode(configuration, this);
        this.proxyFactory = manager.proxyFactory;
        this.compositeActivator =
            manager.extensionPoints.getExtensionPoint(UtilityExtensionPoint.class).getUtility(CompositeActivator.class,
                                                                                              manager.getNodeKey(configuration));
        try {
            Composite composite = manager.configureNode(configuration, contributions);
            
            // get the top level composite for this node
            compositeActivator.setDomainComposite(composite);

            // Activate the composite
            compositeActivator.activate(compositeActivator.getDomainComposite());

            // Start the composite
            compositeActivator.start(compositeActivator.getDomainComposite());

            NodeFinder.addNode(NodeUtil.createURI(configuration.getDomainURI()), this);

            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            try {
                mbean = new NodeManager(this);
                mBeanServer.registerMBean(mbean, mbean.getName());
                /*
                LocateRegistry.createRegistry(9999);
                JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
                JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
                connectorServer.start();
                */
            } catch (Exception e) {
                mbean = null;
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

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

            if (mbean != null) {
                MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                try {
                    mBeanServer.unregisterMBean(mbean.getName());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    mbean = null;
                }
            }

            NodeFinder.removeNode(this);
            if( compositeActivator.getDomainComposite() != null ) {

                // Stop the composite
                compositeActivator.stop(compositeActivator.getDomainComposite());

                // Deactivate the composite
                compositeActivator.deactivate(compositeActivator.getDomainComposite());

            } // end if

            manager.removeNode(configuration);
            manager.extensionPoints.getExtensionPoint(UtilityExtensionPoint.class).removeUtility(compositeActivator);
            this.compositeActivator = null;
            this.proxyFactory = null;
            
            ThreadMessageContext.removeMessageContext();

        } catch (ActivationException e) {
            throw new IllegalStateException(e);
        }

    }

    @SuppressWarnings("unchecked")
    public <B, R extends ServiceReference<B>> R cast(B target) throws IllegalArgumentException {
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

        for (Component compositeComponent : compositeActivator.getDomainComposite().getComponents()) {
            if (compositeComponent.getName().equals(componentName)) {
                component = compositeComponent;
            }
        }

        if (component == null) {
            throw new ServiceUnavailableException("The service " + name + " has not been contributed to the domain");
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
    public List<Endpoint> getServiceEndpoints() {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        if (compositeActivator != null) {
            Composite domainComposite = compositeActivator.getDomainComposite();
            if (domainComposite != null) {
                for (Component component : domainComposite.getComponents()) {
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
        return endpoints;
    }
    
    public Composite getDomainComposite() {
        return compositeActivator.getDomainComposite();
    }    
}
