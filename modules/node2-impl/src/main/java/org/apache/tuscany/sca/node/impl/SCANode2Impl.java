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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntime;
import org.apache.tuscany.sca.implementation.node.ConfiguredNodeImplementation;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A local representation of the sca domain running on a single node
 * 
 * @version $Rev$ $Date$
 */
public class SCANode2Impl implements SCANode2, SCAClient {
	
    private final static Logger logger = Logger.getLogger(SCANode2Impl.class.getName());
	     
    // The node configuration URI
    private String configurationURI;

    // The tuscany runtime that does the hard work
    private ReallySmallRuntime runtime;
    private CompositeActivator activator;

    // The composite loaded into this node
    private Composite composite; 
    
    /** 
     * Constructs a new SCA node.
     *  
     * @param configurationURI the URI of the node configuration information.
     * @throws NodeException
     */
    public SCANode2Impl(String configurationURI) throws NodeException {
        try {
            init(configurationURI);

        } catch (Exception e) {
            throw new NodeException(e);
        }        
    }
    
    private void init(String configurationURI) throws Exception {
        logger.log(Level.INFO, "Creating node: " + configurationURI);               

        this.configurationURI = configurationURI;

        // Create a node runtime for the domain contributions to run on
        runtime = new ReallySmallRuntime(Thread.currentThread().getContextClassLoader());
        runtime.start();
        activator = runtime.getCompositeActivator();
        
        // Get the various factories we need
        ExtensionPointRegistry registry = runtime.getExtensionPointRegistry();
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);

        // Create the required artifact processors
        StAXArtifactProcessorExtensionPoint artifactProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<ConfiguredNodeImplementation> configurationProcessor = artifactProcessors.getProcessor(ConfiguredNodeImplementation.class);
        StAXArtifactProcessor<Composite> compositeProcessor = artifactProcessors.getProcessor(Composite.class);
        
        // Read the node configuration feed
        URL configurationURL = new URL(configurationURI);
        InputStream is = configurationURL.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
        reader.nextTag();
        ConfiguredNodeImplementation configuration = configurationProcessor.read(reader);
        is.close();

        // Load the specified contributions
        ContributionService contributionService = runtime.getContributionService();
        List<Contribution> contributions = new ArrayList<Contribution>();
        for (Contribution contribution: configuration.getContributions()) {
            URL contributionURL = new URL(configurationURL, contribution.getLocation());
            logger.log(Level.INFO, "Loading contribution: " + contributionURL);
            contributions.add(contributionService.contribute(contribution.getURI(), contributionURL, false));
        }
        
        // Load the specified composite
        URL compositeURL = new URL(configurationURL, configuration.getComposite().getURI());
        logger.log(Level.INFO, "Loading composite: " + compositeURL);
        is = compositeURL.openStream();
        reader = inputFactory.createXMLStreamReader(is);
        composite = compositeProcessor.read(reader);
        
        // Resolve it within the context of the first contribution
        Contribution mainContribution = contributions.get(contributions.size()-1);
        compositeProcessor.resolve(composite, mainContribution.getModelResolver());
            
        // Create a top level composite to host our composite
        // This is temporary to make the activator happy
        AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
        Composite tempComposite = assemblyFactory.createComposite();
        tempComposite.setName(new QName(configurationURI, "temp"));
        tempComposite.setURI(configurationURI);
        
        // Include the node composite in the top-level composite 
        tempComposite.getIncludes().add(composite);
        
        // Update the policy definitions processed from the contribution.
        // I'm not sure what that exactly does...
        runtime.updateSCADefinitions(contributionService.getContributionSCADefinitions());

        // Build the composite
        runtime.buildComposite(composite);

        // Activate the composite
        activator.activate(composite);
    }
    
    public void start() throws NodeException {
        logger.log(Level.INFO, "Starting node: " + configurationURI);               
        
        try {
            
            // Start the composite
            activator.start(composite);
            
        } catch (ActivationException e) {
            throw new NodeException(e);
        }
    }
    
    public void stop() throws NodeException {
        logger.log(Level.INFO, "Stopping node: " + configurationURI);               
        
        try {
            
            // Stop the composite
            activator.stop(composite);
            
        } catch (ActivationException e) {
            throw new NodeException(e);
        }
    }
    
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R)runtime.getProxyFactory().cast(target);
    }
    
    public <B> B getService(Class<B> businessInterface, String serviceName) {
        
        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    public <B> ServiceReference<B> createServiceReference(Class<B> businessInterface, String targetURI) {
        try {
          
            AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
            Composite composite = assemblyFactory.createComposite();
            composite.setName(new QName(configurationURI, "default"));
            RuntimeComponent component = (RuntimeComponent)assemblyFactory.createComponent();
            component.setName("default");
            component.setURI("default");
            runtime.getCompositeActivator().configureComponentContext(component);
            composite.getComponents().add(component);
            RuntimeComponentReference reference = (RuntimeComponentReference)assemblyFactory.createComponentReference();
            reference.setName("default");
            ModelFactoryExtensionPoint factories =
                runtime.getExtensionPointRegistry().getExtensionPoint(ModelFactoryExtensionPoint.class);
            JavaInterfaceFactory javaInterfaceFactory = factories.getFactory(JavaInterfaceFactory.class);
            InterfaceContract interfaceContract = javaInterfaceFactory.createJavaInterfaceContract();
            interfaceContract.setInterface(javaInterfaceFactory.createJavaInterface(businessInterface));
            reference.setInterfaceContract(interfaceContract);
            component.getReferences().add(reference);
            reference.setComponent(component);
            SCABindingFactory scaBindingFactory = factories.getFactory(SCABindingFactory.class);
            SCABinding binding = scaBindingFactory.createSCABinding();
            
            binding.setURI(targetURI);
            reference.getBindings().add(binding);       
            return new ServiceReferenceImpl<B>(businessInterface, component, reference, binding, runtime
                .getProxyFactory(), runtime.getCompositeActivator());
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
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
         
        for (Component compositeComponent: composite.getComponents()) {
            if (compositeComponent.getName().equals(componentName)) {
                component = compositeComponent;
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
                        if (serviceName != null) {
                            serviceName = "$promoted$." + serviceName;
                        }
                        componentContext =
                            ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                        return componentContext.createSelfReference(businessInterface, compositeService
                            .getPromotedService());
                    }
                    break;
                }
            }
            // No matching service is found
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

}
