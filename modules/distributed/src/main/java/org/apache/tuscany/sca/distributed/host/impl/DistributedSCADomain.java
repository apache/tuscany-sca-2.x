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

package org.apache.tuscany.sca.distributed.host.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.distributed.host.SCADomainNode;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Constants;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * An SCA domain implementation that allows the damin to be distributed
 * across one or more processors.
 * 
 * @version $Rev$ $Date$
 */
public class DistributedSCADomain extends SCADomain implements SCADomainNode{
    
    private String uri;
    private Composite domainComposite;
    private DistributedRuntime runtime;
    private Map<String, Component> components = new HashMap<String, Component>();
    private DomainCompositeHelper domainCompositeHelper;
    
    private String nodeName;
    private NodeServiceRuntime nodeServiceRuntime;
    private Composite nodeComposite;
    private ClassLoader runtimeClassLoader;
    private Map<String, Component> nodeComponents = new HashMap<String, Component>();
    
    public class DomainCompositeHelper {
        
        /**
         * Add a composite to the domain
         * @param composite
         * @return
         */
        public Composite addComposite(Composite composite) throws ActivationException {
            domainComposite.getIncludes().add(composite);
            //CompositeActivator compositeActivator = runtime.getCompositeActivator();
            //compositeActivator.activate(composite);
            //for (Component component : composite.getComponents()) {
            //    components.put(component.getName(), component);
            //}
            return composite;
        }

        /**
         * Remove a composite from the domain
         * @param composite
         * @throws ActivationException
         */
        public void removeComposite(Composite composite) throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            compositeActivator.deactivate(composite);
            domainComposite.getIncludes().remove(composite);
            for (Component component : composite.getComponents()) {
                components.remove(component.getName());
            }
        }
        
        /**
         * Start a composite
         * @deprecated
         * @param composite
         * @throws ActivationException
         */
        public void startComposite(Composite composite) throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            compositeActivator.start(composite);
        }
        
        /**
         * Stop a composite
         * @deprecated
         * @param composite
         * @throws ActivationException
         */
        public void stopComposite(Composite composite) throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            compositeActivator.stop(composite);
        }

        /**
         * Get a reference to a component by name
         * @param componentName
         * @return
         */
        public Component getComponent(String componentName){
            return (Component) components.get(componentName);
        }
        
        /**
         * Start a component
         * @param component
         * @throws ActivationException
         */
        public void startComponent(Component component) throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            compositeActivator.start(component);
        }
        
        /**
         * Stop a component
         * @param component
         * @throws ActivationException
         */
        public void stopComponent(Component component) throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            compositeActivator.stop(component);
        }    
        
        /**
         * Start all components in the node
         * @param component
         * @throws ActivationException
         */
        public void startComponents() throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            
            ComponentRegistry componentRegistry = 
                getNodeService(ComponentRegistry.class, "ComponentRegistry");
            
            List<String> components = componentRegistry.getComponentsForNode(nodeName);
            
            for (String componentName : components) {
                compositeActivator.start(getComponent(componentName));
            }
        }
        
        /**
         * Stop al components in the node
         * @param component
         * @throws ActivationException
         */
        public void stopComponents() throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            
            ComponentRegistry componentRegistry = 
                getNodeService(ComponentRegistry.class, "ComponentRegistry");
            
            List<String> components = componentRegistry.getComponentsForNode(nodeName);
            
            for (String componentName : components) {
                compositeActivator.stop(getComponent(componentName));
            }
        }          
        
        /**
         * Activate SCA Domain
         * @throws ActivationException
         */
        public void activateDomain() throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            compositeActivator.activate(domainComposite);
            for (Component component : domainComposite.getComponents()) {
                components.put(component.getName(), component);
            }
            
            
         /**
          * Start the node components that should be running in this 
          * runtime
          */

        }
    }    
    
    /**
     * Constructs a new node in the distributed domain
     *
     * @param runtimeClassLoader
     * @param domainURI
     */
    public DistributedSCADomain(ClassLoader runtimeClassLoader,
                                String domainURI,
                                String nodeName) {
        this.runtimeClassLoader = runtimeClassLoader;
        this.uri = domainURI;
        this.nodeName = nodeName;

        // create a runtime to host the application services
        this.runtime = new DistributedRuntime(runtimeClassLoader);
        
        // Create a runtime to host the node services
        nodeServiceRuntime = new NodeServiceRuntime(runtimeClassLoader);
    }
    
    /**
     * Returns the name of the node that this part of the
     * distributed domain is running on
     * 
     * @return the node name
     */
    public String getNodeName(){
        return nodeName;
    }
    
    public void start()
      throws ActivationException {

        try {
        
            // start up the node services
            
            // Start the node service runtime. Null is passed in here
            // to stop the SCAbinding in this runtime trying to look 
            // back into the domain node model
            nodeServiceRuntime.start(null);
            
            // get the node service configuration model
                   
            // we expect the node file to be in the same package as the
            // mainline for the node itself. At some point we may want to
            // get smart about where we find this information. 
            // For now we look if there is a file specifically for this
            // node and if not we read the generic file. 
            URL url = runtimeClassLoader.getResource(nodeName + ".node");
            
            if (url == null) {
                url = runtimeClassLoader.getResource("default.node");
            }
            
            if (url == null) {
                throw new ServiceRuntimeException("Node file not found at either  " 
                                                  + nodeName + 
                                                  ".node or node.node");
            }
            
            // load the node file
            nodeComposite = nodeServiceRuntime.getNodeComposite(url);
                       
            // activate the composite
            CompositeActivator compositeActivator = nodeServiceRuntime.getCompositeActivator();
            compositeActivator.activate(nodeComposite);
            
            // record the components
            for (Component component : nodeComposite.getComponents()) {
                nodeComponents.put(component.getName(), component);
            }
            
            // start up the application domain
            
            // Start the runtime
            runtime.start(this);
            
            // Create an in-memory domain level composite
            AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
            domainComposite = assemblyFactory.createComposite();
            domainComposite.setName(new QName(Constants.SCA_NS, "domain"));
            domainComposite.setURI(uri);
    
            // Create a domain composite helper
            domainCompositeHelper = new DomainCompositeHelper();            
            
        } catch(Exception ex) {
            throw new ActivationException(ex);
        }
    }
    
    public void stop() throws ActivationException {
        
        // Stop the runtimes
        runtime.stop();
        nodeServiceRuntime.stop();
        
        // Cleanup
        domainComposite = null;
        domainCompositeHelper = null;
    }

    public ContributionService getContributionService() {
        return runtime.getContributionService();
    }
    
    public DomainCompositeHelper getDomainCompositeHelper() {
        return domainCompositeHelper;
    }
    
    @Override
    public void close() {
        super.close();
    }

    @Override
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    @Override
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

        // Lookup the component in the domain
        Component component = components.get(componentName);
        if (component == null) {
            throw new ServiceRuntimeException("Component not found: " + componentName);
        }
        ComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite
        // component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            ComponentService promotedService = null;
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {

                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        promotedService = compositeService.getPromotedService();
                        SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                        if (scaBinding != null) {
                            Component promotedComponent = scaBinding.getComponent();
                            if (serviceName != null) {
                                serviceName = "$promoted$." + serviceName;
                            }
                            componentContext = (ComponentContext)promotedComponent;
                        }
                    }
                    break;
                }
            }
            if (componentContext == null) {
                throw new ServiceRuntimeException("Composite service not found: " + name);
            }
        } else {
            componentContext = (ComponentContext)component;
        }

        ServiceReference<B> serviceReference;
        if (serviceName != null) {
            serviceReference = componentContext.createSelfReference(businessInterface, serviceName);
        } else {
            serviceReference = componentContext.createSelfReference(businessInterface);
        }
        return serviceReference;

    }

    @Override
    public String getURI() {
        return uri;
    }    
    
    /**
     * Direct copy from getService but dealing in nodeCompinents
     * 
     * @param <B>
     * @param businessInterface
     * @param serviceName
     * @return
     */
    public <B> B getNodeService(Class<B> businessInterface, String serviceName) {       
        ServiceReference<B> serviceReference = getNodeServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }  
    
    /**
     * Direct copy from getServiceReference but dealing in nodeCompinents
     * 
     * @param <B>
     * @param businessInterface
     * @param name
     * @return
     */
    public <B> ServiceReference<B> getNodeServiceReference(Class<B> businessInterface, String name) {

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

        // Lookup the component in the domain
        Component component = nodeComponents.get(componentName);
        if (component == null) {
            throw new ServiceRuntimeException("Component not found: " + componentName);
        }
        ComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite
        // component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            ComponentService promotedService = null;
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {

                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        promotedService = compositeService.getPromotedService();
                        SCABinding scaBinding = promotedService.getBinding(SCABinding.class);
                        if (scaBinding != null) {
                            Component promotedComponent = scaBinding.getComponent();
                            if (serviceName != null) {
                                serviceName = "$promoted$." + serviceName;
                            }
                            componentContext = (ComponentContext)promotedComponent;
                        }
                    }
                    break;
                }
            }
            if (componentContext == null) {
                throw new ServiceRuntimeException("Composite service not found: " + name);
            }
        } else {
            componentContext = (ComponentContext)component;
        }

        ServiceReference<B> serviceReference;
        if (serviceName != null) {
            serviceReference = componentContext.createSelfReference(businessInterface, serviceName);
        } else {
            serviceReference = componentContext.createSelfReference(businessInterface);
        }
        return serviceReference;

    }    
}
