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
import org.apache.tuscany.sca.distributed.core.DistributedSCADomainExtensionPoint;
import org.apache.tuscany.sca.distributed.host.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Constants;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * An SCA domain implementation that allows the damain to be distributed
 * across one or more processors. There is nothing particularly complicated
 * about this domain implementation. It differs from the embedded domain implementation
 * in that a different runtime is used to allow the domain to be stored in the 
 * extension registry and for a new DistributedCompositeActivator to be use which, 
 * in turn, uses the SCA binding to insert remote bindings at the right points 
 * in the model. This domain also holds information about the node in which it 
 * is running so that this information in available at various points in the runtime
 * that need to make decisions based on node information. Namely the DistributedComposite
 * Activator and the Distributed SCA binding. 
 * 
 * @version $Rev$ $Date$
 */
public class DistributedSCADomainImpl extends DistributedSCADomain implements DistributedSCADomainExtensionPoint  {
    
    private String uri;
    private Composite domainComposite;
    private DistributedRuntime runtime;
    private Map<String, Component> components = new HashMap<String, Component>();
    private DomainCompositeHelper domainCompositeHelper;
    private ClassLoader runtimeClassLoader;
    private DistributedSCADomain nodeDomain;
       
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
                ((SCADomain)nodeDomain).getService(ComponentRegistry.class, "ComponentRegistry");
            
            List<String> components = componentRegistry.getComponentsForNode(uri, nodeDomain.getNodeName());
            
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
                ((SCADomain)nodeDomain).getService(ComponentRegistry.class, "ComponentRegistry");
            
            List<String> components = componentRegistry.getComponentsForNode(uri, nodeDomain.getNodeName());
            
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
     * Constructs a distributed domain for a node
     *
     * @param runtimeClassLoader
     * @param domainURI
     */
    public DistributedSCADomainImpl(ClassLoader runtimeClassLoader,
                                    String domainURI,
                                    DistributedSCADomain nodeDomain) {
        this.runtimeClassLoader = runtimeClassLoader;
        this.uri = domainURI;
        this.nodeDomain = nodeDomain;

        // create a runtime to host the application services
        this.runtime = new DistributedRuntime(this.runtimeClassLoader);
    }
    
    /**
     * Returns the name of the node that this part of the
     * distributed domain is running on
     * 
     * @return the node name
     */
    public String getNodeName(){
        return nodeDomain.getNodeName();
    }
    
    /**
     * Returns the domain that is running the system
     * components for this node
     * 
     * @return the node name
     */
    public DistributedSCADomain getNodeDomain(){
        return nodeDomain;
    }
    
    /** Starts the domain operation. Usually involves starting the
     *  runtime and creating the top level composite ready for 
     *  new contributions
     *  
     * @throws ActivationException
     */    
    public void start()
      throws ActivationException {

        try {           
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
    
    /**
     * Stops the runtime and all running components
     * 
     * @throws ActivationException
     */    
    public void stop() throws ActivationException {
        
        // close the domain 
        close();
        
        // Stop the SCA domain components
        getDomainCompositeHelper().stopComponents();    
        
        // Stop the runtime
        try {
            runtime.stop();
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }
        
        // Cleanup
        domainComposite = null;
        domainCompositeHelper = null;
    }

    /** 
     * Get the contribution service from the runtime. Nodes
     * use this to add and remove contributions from the domain
     * 
     * @return
     */
    public ContributionService getContributionService() {
        return runtime.getContributionService();
    }
    
    /** 
     * Get the composite helper for this domain. Nodes use this
     * for fine grain controled over the operation of the domain
     * 
     * @return
     */
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
    
}
