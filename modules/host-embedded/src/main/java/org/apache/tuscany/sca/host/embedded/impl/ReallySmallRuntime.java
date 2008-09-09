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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.ExtensibleProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.definitions.impl.SCADefinitionsImpl;
import org.apache.tuscany.sca.definitions.util.SCADefinitionsUtil;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorFactoryImpl;
import org.apache.tuscany.sca.policy.DefaultIntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.provider.SCADefinitionsProvider;
import org.apache.tuscany.sca.provider.SCADefinitionsProviderExtensionPoint;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 *
 * @version $Rev$ $Date$
 */
public class ReallySmallRuntime {
	private static final Logger logger = Logger.getLogger(ReallySmallRuntime.class.getName());
    private List<ModuleActivator> modules;
    private ExtensionPointRegistry registry;

    private ClassLoader classLoader;
    private AssemblyFactory assemblyFactory;
    private ContributionService contributionService;
    private CompositeActivator compositeActivator;
    private CompositeBuilder compositeBuilder;
    // private DomainBuilder domainBuilder;    
    private WorkScheduler workScheduler;
    private ScopeRegistry scopeRegistry;
    private ProxyFactory proxyFactory;
    private List<SCADefinitions> policyDefinitions;
    private ModelResolver policyDefinitionsResolver;
    private Monitor monitor;

    public ReallySmallRuntime(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void start() throws ActivationException {
    	long start = System.currentTimeMillis();
    	
        // Create our extension point registry
        registry = new DefaultExtensionPointRegistry();
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);

        // Get work scheduler
        workScheduler = utilities.getUtility(WorkScheduler.class);

        // Create an interface contract mapper
        InterfaceContractMapper mapper = utilities.getUtility(InterfaceContractMapper.class);

        // Get factory extension point
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        
        // Get Message factory
        MessageFactory messageFactory = factories.getFactory(MessageFactory.class);

        // Get proxy factory
        ProxyFactoryExtensionPoint proxyFactories = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);  
        proxyFactory = new ExtensibleProxyFactory(proxyFactories); 

        // Create model factories
        assemblyFactory = new RuntimeAssemblyFactory();
        factories.addFactory(assemblyFactory);
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        factories.addFactory(policyFactory);
        
        // Load the runtime modules
        modules = loadModules(registry);
        
        // Start the runtime modules
        startModules(registry, modules);
        
        SCABindingFactory scaBindingFactory = factories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory intentAttachPointTypeFactory = new DefaultIntentAttachPointTypeFactory();
        factories.addFactory(intentAttachPointTypeFactory);
        ContributionFactory contributionFactory = factories.getFactory(ContributionFactory.class);
        
        // Create a monitor
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        
        if (monitorFactory != null){
            monitor = monitorFactory.createMonitor();
        } else {
            monitorFactory = new DefaultMonitorFactoryImpl();
            monitor = monitorFactory.createMonitor();
            utilities.addUtility(monitorFactory);
            //logger.fine("No MonitorFactory is found on the classpath.");
        }
        
        // Create a contribution service
        policyDefinitions = new ArrayList<SCADefinitions>();
        policyDefinitionsResolver = new DefaultModelResolver();
        contributionService = ReallySmallRuntimeBuilder.createContributionService(classLoader,
                                                                                  registry,
                                                                                  contributionFactory,
                                                                                  assemblyFactory,
                                                                                  policyFactory,
                                                                                  mapper,
                                                                                  policyDefinitions,
                                                                                  policyDefinitionsResolver,
                                                                                  monitor);
        
        // Create the ScopeRegistry
        scopeRegistry = ReallySmallRuntimeBuilder.createScopeRegistry(registry); 
        
        // Create a composite activator
        compositeActivator = ReallySmallRuntimeBuilder.createCompositeActivator(registry,
                                                                                assemblyFactory,
                                                                                messageFactory,
                                                                                scaBindingFactory,
                                                                                mapper,
                                                                                proxyFactory,
                                                                                scopeRegistry,
                                                                                workScheduler);

        // Load the definitions.xml
        loadSCADefinitions();
        
        if (logger.isLoggable(Level.FINE)) {
            long end = System.currentTimeMillis();
            logger.fine("The tuscany runtime is started in " + (end - start) + " ms.");
        }
    }
    
    public void stop() throws ActivationException {
    	long start = System.currentTimeMillis();

        // Stop the runtime modules
        stopModules(registry, modules);

        // Stop and destroy the work manager
        workScheduler.destroy(); 

        // Cleanup
        modules = null;
        registry = null;
        assemblyFactory = null;
        contributionService = null;
        compositeActivator = null;
        workScheduler = null;
        scopeRegistry = null;
        
        if (logger.isLoggable(Level.FINE)) {
            long end = System.currentTimeMillis();
            logger.fine("The tuscany runtime is stopped in " + (end - start) + " ms.");
        }
    }
    
    public void buildComposite(Composite composite) throws CompositeBuilderException {
        //Get factory extension point
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        SCABindingFactory scaBindingFactory = factories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory intentAttachPointTypeFactory = factories.getFactory(IntentAttachPointTypeFactory.class);
        EndpointFactory endpointFactory = factories.getFactory(EndpointFactory.class);        
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper mapper = utilities.getUtility(InterfaceContractMapper.class);
        DocumentBuilderFactory documentBuilderFactory = factories.getFactory(DocumentBuilderFactory.class);
        TransformerFactory transformerFactory = factories.getFactory(TransformerFactory.class);
        
        //Create a composite builder
        SCADefinitions aggregatedDefinitions = new SCADefinitionsImpl();
        for ( SCADefinitions definition : ((List<SCADefinitions>)policyDefinitions) ) {
            SCADefinitionsUtil.aggregateSCADefinitions(definition, aggregatedDefinitions);
        }
        compositeBuilder = ReallySmallRuntimeBuilder.createCompositeBuilder(monitor,
                                                                            assemblyFactory,
                                                                            scaBindingFactory,
                                                                            endpointFactory,
                                                                            intentAttachPointTypeFactory,
                                                                            documentBuilderFactory,
                                                                            transformerFactory,
                                                                            mapper, 
                                                                            aggregatedDefinitions);
        compositeBuilder.build(composite);
        
    }
    
    public ContributionService getContributionService() {
        return contributionService;
    }

    public CompositeActivator getCompositeActivator() {
        return compositeActivator;
    }
    
    public CompositeBuilder getCompositeBuilder() {
        return compositeBuilder;
    }

    public AssemblyFactory getAssemblyFactory() {
        return assemblyFactory;
    }
   
    private void  loadSCADefinitions() throws ActivationException {
        try {
            URLArtifactProcessorExtensionPoint documentProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            URLArtifactProcessor<SCADefinitions> definitionsProcessor = documentProcessors.getProcessor(SCADefinitions.class);
            SCADefinitionsProviderExtensionPoint scaDefnProviders = registry.getExtensionPoint(SCADefinitionsProviderExtensionPoint.class);
            
            SCADefinitions systemSCADefinitions = new SCADefinitionsImpl();
            SCADefinitions aSCADefn = null;
            for ( SCADefinitionsProvider aProvider : scaDefnProviders.getSCADefinitionsProviders() ) {
               aSCADefn = aProvider.getSCADefinition(); 
               SCADefinitionsUtil.aggregateSCADefinitions(aSCADefn, systemSCADefinitions);
            }
            
            policyDefinitions.add(systemSCADefinitions);
            
            //we cannot expect that providers will add the intents and policysets into the resolver
            //so we do this here explicitly
            for ( Intent intent : systemSCADefinitions.getPolicyIntents() ) {
                policyDefinitionsResolver.addModel(intent);
            }
            
            for ( PolicySet policySet : systemSCADefinitions.getPolicySets() ) {
                policyDefinitionsResolver.addModel(policySet);
            }
            
            for ( IntentAttachPointType attachPoinType : systemSCADefinitions.getBindingTypes() ) {
                policyDefinitionsResolver.addModel(attachPoinType);
            }
            
            for ( IntentAttachPointType attachPoinType : systemSCADefinitions.getImplementationTypes() ) {
                policyDefinitionsResolver.addModel(attachPoinType);
            }
            
            //now that all system sca definitions have been read, lets resolve them right away
            definitionsProcessor.resolve(systemSCADefinitions, 
                                         policyDefinitionsResolver);
        } catch ( Exception e ) {
            throw new ActivationException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<ModuleActivator> loadModules(ExtensionPointRegistry registry) throws ActivationException {

        // Load and instantiate the modules found on the classpath (or any registered ClassLoaders)
        modules = new ArrayList<ModuleActivator>();
        try {
            Set<ServiceDeclaration> moduleActivators =
                ServiceDiscovery.getInstance().getServiceDeclarations(ModuleActivator.class);
            Set<String> moduleClasses = new HashSet<String>();
            for (ServiceDeclaration moduleDeclarator : moduleActivators) {
                if (moduleClasses.contains(moduleDeclarator.getClassName())) {
                    continue;
                }
                moduleClasses.add(moduleDeclarator.getClassName());
                Class<?> moduleClass = moduleDeclarator.loadClass();
                ModuleActivator module = (ModuleActivator)moduleClass.newInstance();
                modules.add(module);
            }
        } catch (IOException e) {
            throw new ActivationException(e);
        } catch (ClassNotFoundException e) {
            throw new ActivationException(e);
        } catch (InstantiationException e) {
            throw new ActivationException(e);
        } catch (IllegalAccessException e) {
            throw new ActivationException(e);
        }

        return modules;
    }
    
    private void startModules(ExtensionPointRegistry registry, List<ModuleActivator> modules)
        throws ActivationException {
        boolean debug = logger.isLoggable(Level.FINE);
        // Start all the extension modules
        for (ModuleActivator module : modules) {
            long start = 0L;
            if (debug) {
                logger.fine(module.getClass().getName() + " is starting.");
                start = System.currentTimeMillis();
            }
            try {
                module.start(registry);
                if (debug) {
                    long end = System.currentTimeMillis();
                    logger.fine(module.getClass().getName() + " is started in " + (end - start) + " ms.");
                }
            } catch (Throwable e) {
            	logger.log(Level.WARNING, "Exception starting module " + module.getClass().getName() + " :" + e.getMessage());
            	logger.log(Level.FINE, "Exception starting module " + module.getClass().getName(), e);
            }
        }
    }

    private void stopModules(final ExtensionPointRegistry registry, List<ModuleActivator> modules) {
        boolean debug = logger.isLoggable(Level.FINE);
        for (ModuleActivator module : modules) {
            long start = 0L;
            if (debug) {
                logger.fine(module.getClass().getName() + " is stopping.");
                start = System.currentTimeMillis();
            }
            module.stop(registry);
            if (debug) {
                long end = System.currentTimeMillis();
                logger.fine(module.getClass().getName() + " is stopped in " + (end - start) + " ms.");
            }
        }
    }

    /**
     * @return the proxyFactory
     */
    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    /**
     * @return the registry
     */
    public ExtensionPointRegistry getExtensionPointRegistry() {
        return registry;
    }

}
