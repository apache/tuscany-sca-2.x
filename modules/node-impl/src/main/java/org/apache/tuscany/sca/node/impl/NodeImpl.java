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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.context.ThreadMessageContext;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.ExtensibleDomainRegistryFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.ServiceUnavailableException;

/**
 * An SCA Node that is managed by the NodeManager
 */
public class NodeImpl implements Node {
    private static final Logger logger = Logger.getLogger(NodeImpl.class.getName());
    private ProxyFactory proxyFactory;
    private CompositeActivator compositeActivator;
    private CompositeContext compositeContext;
    private Composite domainComposite;
    private NodeConfiguration configuration;
    private NodeFactoryImpl nodeFactory;
    private List<Contribution> contributions;
    // private NodeManager mbean;

    /**
     * Create a node from the configuration
     * @param manager
     * @param configuration
     */
    public NodeImpl(NodeFactoryImpl nodeFactory, NodeConfiguration configuration) {
        super();
        this.configuration = configuration;
        this.nodeFactory = nodeFactory;
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
        this.nodeFactory = manager;
        this.contributions = new ArrayList<Contribution>(contributions);
    }

    public String getURI() {
        return getConfiguration().getURI();
    }

    public Node start() {
        logger.log(Level.INFO, "Starting node: " + configuration.getURI() + " domain: " + configuration.getDomainURI());

        nodeFactory.init();
        nodeFactory.addNode(configuration, this);
        this.proxyFactory = nodeFactory.proxyFactory;
        
        DomainRegistryFactory domainRegistryFactory = ExtensibleDomainRegistryFactory.getInstance(nodeFactory.registry);
        EndpointRegistry endpointRegistry =
            domainRegistryFactory.getEndpointRegistry(configuration.getDomainRegistryURI(), configuration
                .getDomainURI());
        
        UtilityExtensionPoint utilities = nodeFactory.registry.getExtensionPoint(UtilityExtensionPoint.class);
        this.compositeActivator = utilities.getUtility(CompositeActivator.class);
        try {
            Monitor monitor = nodeFactory.monitorFactory.createMonitor();
            ProcessorContext context = new ProcessorContext(monitor);
            
            // Set up the thead context monitor
            Monitor tcm = nodeFactory.monitorFactory.setContextMonitor(monitor);
            try {
                if (contributions == null) {
                    contributions = nodeFactory.loadContributions(configuration, context);
                }
                domainComposite = nodeFactory.configureNode(configuration, contributions, context);

                this.compositeContext =
                    new CompositeContext(nodeFactory.registry, 
                                         endpointRegistry, 
                                         domainComposite, 
                                         configuration.getDomainURI(), 
                                         configuration.getURI(),
                                         nodeFactory.getDeployer().getSystemDefinitions());
                
            } finally {
                // Reset the thread context monitor
                nodeFactory.monitorFactory.setContextMonitor(tcm);
            }
            
            // Activate the composite
            compositeActivator.activate(compositeContext, domainComposite);

            // Start the composite
            compositeActivator.start(compositeContext, domainComposite);

            // FIXME: [rfeng] We should turn the management capability into a system utility.
            // In certain environment such as Google App Engine, the JMX API is not allowed
            try {
                /*
                MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                mbean = new NodeManager(this);
                mBeanServer.registerMBean(mbean, mbean.getName());
                */
                /*
                LocateRegistry.createRegistry(9999);
                JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
                JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
                connectorServer.start();
                */
            } catch (Throwable e) {
                // Ignore the error for now
                // mbean = null;
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

            return this;

        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }

    }

    public void stop() {
        logger.log(Level.INFO, "Stopping node: " + configuration.getURI());

        try {
            if (compositeActivator == null) {
                return;
            }

            /*
            if (mbean != null) {
                try {
                    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                    mBeanServer.unregisterMBean(mbean.getName());
                } catch (Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    mbean = null;
                }
            }
            */

            if( domainComposite != null ) {

                // Stop the composite
                compositeActivator.stop(compositeContext, domainComposite);

                // Deactivate the composite
                compositeActivator.deactivate(domainComposite);

            } // end if

            nodeFactory.removeNode(configuration);
            this.compositeActivator = null;
            this.proxyFactory = null;
            this.domainComposite = null;
            this.compositeContext = null;
            
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

        for (Component compositeComponent : domainComposite.getComponents()) {
            if (compositeComponent.getName().equals(componentName)) {
                component = compositeComponent;
                break;
            }
        }

        if (component == null) {
            throw new ServiceUnavailableException("The service " + name + " has not been contributed to the domain");
        }
        
        return ((RuntimeComponent)component).getServiceReference(businessInterface, serviceName);
    }

    public NodeConfiguration getConfiguration() {
        return configuration;
    }

    public ExtensionPointRegistry getExtensionPointRegistry() {
        return nodeFactory.getExtensionPointRegistry();
    }

    /**
     * Get the service endpoints in this Node
     * TODO: needs review, works for the very simple testcase but i expect there are
     *    other endpoints to be included
     */
    public List<Endpoint> getServiceEndpoints() {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        if (compositeActivator != null) {
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
        return domainComposite;
    }   
    
    public String dumpDomainComposite() {
        
        StAXArtifactProcessorExtensionPoint xmlProcessors = 
            getExtensionPointRegistry().getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Composite>  compositeProcessor = 
            xmlProcessors.getProcessor(Composite.class);   
     
        return writeComposite(getDomainComposite(), compositeProcessor);
    }
       
    private String writeComposite(Composite composite, StAXArtifactProcessor<Composite> compositeProcessor){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory =
            nodeFactory.getExtensionPointRegistry().getExtensionPoint(FactoryExtensionPoint.class)
                .getFactory(XMLOutputFactory.class);
        
        try {
            compositeProcessor.write(composite, outputFactory.createXMLStreamWriter(bos), new ProcessorContext(nodeFactory.registry));
        } catch(Exception ex) {
            return ex.toString();
        }
        
        String result = bos.toString();
        
        // write out and nested composites
        for (Component component : composite.getComponents()) {
            if (component.getImplementation() instanceof Composite) {
                result +=
                    "\n<!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->\n" + writeComposite((Composite)component
                                                                                                          .getImplementation(),
                                                                                                      compositeProcessor);
            }
        }
        
        return result;
    }
    
    public List<Contribution> getContributions() {
        return contributions;
    }

}
