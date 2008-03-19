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
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.tuscany.sca.node.Node2Exception;
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
public class NodeImpl implements SCANode2, SCAClient {
	
    private final static Logger logger = Logger.getLogger(NodeImpl.class.getName());
	     
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
     * @throws Node2Exception
     */
    public NodeImpl(String configurationURI) throws Node2Exception {
        try {
            init(configurationURI);

        } catch (Exception e) {
            throw new Node2Exception(e);
        }        
    }
    
    private void init(String configurationURI) throws Exception {
        logger.log(Level.INFO, "Creating node: " + configurationURI);               

        this.configurationURI = configurationURI;

        // Create a node runtime for the domain contributions to run on
        ClassLoader contextClassLoader =  Thread.currentThread().getContextClassLoader();
        runtime = new ReallySmallRuntime(contextClassLoader);
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
        
        // Find if any contribution JARs already available locally on the classpath
        Map<String, URL> localContributions = new HashMap<String, URL>();
        collectJARs(localContributions, contextClassLoader);

        // Load the specified contributions
        ContributionService contributionService = runtime.getContributionService();
        List<Contribution> contributions = new ArrayList<Contribution>();
        for (Contribution contribution: configuration.getContributions()) {
            
            // Build contribution URL
            URL contributionURL = new URL(configurationURL, contribution.getLocation());

            // Extract contribution file name
            String file =contributionURL.getPath();
            int i = file.lastIndexOf('/');
            if (i != -1 && i < file.length() -1 ) {
                file = file.substring(i +1);
                
                // If we find the local contribution file on the classpath, use it in
                // place of the original contribution URL
                URL localContributionURL = localContributions.get(file);
                if (localContributionURL != null) {
                    contributionURL = localContributionURL;
                }
            }
            
            // Load the contribution
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
        
        // Build the composite
        runtime.buildComposite(composite);
    }
    
    public void start() throws Node2Exception {
        logger.log(Level.INFO, "Starting node: " + configurationURI);               
        
        try {
            
            // Activate the composite
            activator.activate(composite);
            
            // Start the composite
            activator.start(composite);
            
        } catch (ActivationException e) {
            throw new Node2Exception(e);
        }
    }
    
    public void stop() throws Node2Exception {
        logger.log(Level.INFO, "Stopping node: " + configurationURI);               
        
        try {
            
            // Stop the composite
            activator.stop(composite);
            
            // Deactivate the composite
            activator.deactivate(composite);
            
        } catch (ActivationException e) {
            throw new Node2Exception(e);
        }
    }
    
    /**
     * Returns the extension point registry used by this node.
     * 
     * @return
     */
    public ExtensionPointRegistry getExtensionPointRegistry() {
        return runtime.getExtensionPointRegistry();
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

    /**
     * Collect JARs on the classpath of a URLClassLoader
     * @param urls
     * @param cl
     */
    private static void collectJARs(Map<String, URL> urls, ClassLoader cl) {
        if (cl == null) {
            return;
        }
        
        // Collect JARs from the URLClassLoader's classpath
        if (cl instanceof URLClassLoader) {
            for (URL jarURL: ((URLClassLoader)cl).getURLs()) {
                String file =jarURL.getPath();
                int i = file.lastIndexOf('/');
                if (i != -1 && i < file.length() -1 ) {
                    file = file.substring(i +1);
                    urls.put(file, jarURL);
                }
            }
        }
        
        // Collect JARs from the parent classloader
        collectJARs(urls, cl.getParent());
    }
}
