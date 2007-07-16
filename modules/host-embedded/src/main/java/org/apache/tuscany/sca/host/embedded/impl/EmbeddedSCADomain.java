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

package org.apache.tuscany.sca.host.embedded.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.management.ComponentManager;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.Constants;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * An SCA domain facade implementation.
 * 
 * @version $Rev$ $Date$
 */
public class EmbeddedSCADomain extends SCADomain {

    private String uri;
    private Composite domainComposite;
    private ReallySmallRuntime runtime;
    private Map<String, Component> components = new HashMap<String, Component>();
    private DomainCompositeHelper domainCompositeHelper;
    private ComponentManagerImpl componentManager = new ComponentManagerImpl(this);
    
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

        public Set<String> getComponentNames(){
            return  Collections.unmodifiableSet(components.keySet());
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
            componentManager.notifyComponentStarted(component.getName());
        }
        
        /**
         * Stop a component
         * @param component
         * @throws ActivationException
         */
        public void stopComponent(Component component) throws ActivationException {
            CompositeActivator compositeActivator = runtime.getCompositeActivator();
            compositeActivator.stop(component);
            componentManager.notifyComponentStopped(component.getName());
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

        }
    }

    /**
     * Constructs a new domain facade.
     *
     * @param runtimeClassLoader
     * @param domainURI
     */
    public EmbeddedSCADomain(ClassLoader runtimeClassLoader,
                            String domainURI) {
        this.uri = domainURI;
        
        // Create a runtime
        runtime = new ReallySmallRuntime(runtimeClassLoader);
    }
    
    public void start() throws ActivationException {

        // Start the runtime
        runtime.start();
        
        // Create an in-memory domain level composite
        AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
        domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Constants.SCA_NS, "domain"));
        domainComposite.setURI(uri);

        // Create a domain composite helper
        domainCompositeHelper = new DomainCompositeHelper();
    }

    public void stop() throws ActivationException {
        
        // Stop the runtime
        runtime.stop();
        
        // Cleanup
        domainComposite = null;
        domainCompositeHelper = null;
    }

    public ContributionService getContributionService() {
        return runtime.getContributionService();
    }
    
    public DomainCompositeHelper getDomainCompositeHelper() {
        if (domainCompositeHelper == null) {
            throw new IllegalStateException("domain not started");
        }
        return domainCompositeHelper;
    }
    
    public ComponentManager getComponentManager() {
        return componentManager;
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
