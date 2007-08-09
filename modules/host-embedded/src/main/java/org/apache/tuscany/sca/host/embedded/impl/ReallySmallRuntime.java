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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingFactoryImpl;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.DefaultContextFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.impl.ContributionFactoryImpl;
import org.apache.tuscany.sca.contribution.processor.ArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.core.runtime.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.work.ThreadPoolWorkManager;
import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.Transformer;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.LazyDataBinding;
import org.apache.tuscany.sca.databinding.impl.LazyPullTransformer;
import org.apache.tuscany.sca.databinding.impl.LazyPushTransformer;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.scope.ScopeRegistry;

public class ReallySmallRuntime {

    private List<ModuleActivator> modules;
    private ExtensionPointRegistry registry;

    private ClassLoader classLoader;
    private AssemblyFactory assemblyFactory;
    private ContributionService contributionService;
    private CompositeActivator compositeActivator;
    private CompositeBuilder compositeBuilder;
    private ThreadPoolWorkManager workManager;
    private ScopeRegistry scopeRegistry;

    public ReallySmallRuntime(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void start() throws ActivationException {

        // Create our extension point registry
        registry = new DefaultExtensionPointRegistry();

        // Create a work manager
        workManager = new ThreadPoolWorkManager(10);

        // Create an interface contract mapper
        InterfaceContractMapper mapper = new InterfaceContractMapperImpl();

        // Create factory extension point
        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        registry.addExtensionPoint(factories);
        
        // Create context factory extension point
        ContextFactoryExtensionPoint contextFactories = new DefaultContextFactoryExtensionPoint();
        registry.addExtensionPoint(contextFactories);
        
        // Create Message factory
        MessageFactory messageFactory = new MessageFactoryImpl();
        factories.addFactory(messageFactory);

        // Create a proxy factory
        ProxyFactory proxyFactory = ReallySmallRuntimeBuilder.createProxyFactory(registry, mapper, messageFactory);

        // Create model factories
        assemblyFactory = new RuntimeAssemblyFactory(mapper, proxyFactory);
        factories.addFactory(assemblyFactory);
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        factories.addFactory(policyFactory);
        SCABindingFactory scaBindingFactory = new SCABindingFactoryImpl();
        factories.addFactory(scaBindingFactory);
        ContributionFactory contributionFactory = new ContributionFactoryImpl(); 
        factories.addFactory(contributionFactory);
        
        // Create a contribution service
        contributionService = ReallySmallRuntimeBuilder.createContributionService(classLoader,
                                                                                  registry,
                                                                                  contributionFactory,
                                                                                  assemblyFactory,
                                                                                  policyFactory,
                                                                                  mapper);

        // Create the ScopeRegistry
        scopeRegistry = ReallySmallRuntimeBuilder.createScopeRegistry(registry);
        
        // Create a composite builder
        compositeBuilder = ReallySmallRuntimeBuilder.createCompositeBuilder(assemblyFactory,
                                                                            scaBindingFactory,
                                                                            mapper);

        // Create a composite activator
        compositeActivator = ReallySmallRuntimeBuilder.createCompositeActivator(registry,
                                                                                assemblyFactory,
                                                                                scaBindingFactory,
                                                                                mapper,
                                                                                scopeRegistry,
                                                                                workManager);

        // Load the runtime modules
        modules = loadModules(registry, classLoader);
        
        // Start the runtime modules
        startModules(registry, modules);
        
        // Load the artifact processor extensions
        loadArtifactProcessors(registry, classLoader, URLArtifactProcessor.class);
        loadArtifactProcessors(registry, classLoader, StAXArtifactProcessor.class);
        
        // Load the provider factory extensions
        loadProviderFactories(registry, classLoader, BindingProviderFactory.class);
        loadProviderFactories(registry, classLoader, ImplementationProviderFactory.class);

    }

    public void stop() throws ActivationException {

        // Stop the runtime modules
        stopModules(registry, modules);

        // Stop and destroy the work manager
        workManager.destroy();

        // Cleanup
        modules = null;
        registry = null;
        assemblyFactory = null;
        contributionService = null;
        compositeActivator = null;
        workManager = null;
        scopeRegistry = null;
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

    @SuppressWarnings("unchecked")
    private List<ModuleActivator> loadModules(ExtensionPointRegistry registry, ClassLoader classLoader) throws ActivationException {

        // Load and instantiate the modules found on the classpath
        modules = new ArrayList<ModuleActivator>();
        try {
            Set<String> classNames = TempServiceDeclarationUtil.getServiceClassNames(classLoader, ModuleActivator.class.getName());
            for (String className : classNames) {       
                Class moduleClass = Class.forName(className, true, classLoader);
                ModuleActivator module = (ModuleActivator)moduleClass.newInstance();
                modules.add(module);
                Object[] extensionPoints = module.getExtensionPoints();
                if (extensionPoints != null) {
                    for (Object e : extensionPoints) {
                        registry.addExtensionPoint(e);
                    }
                }
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
    
    private void startModules(ExtensionPointRegistry registry, List<ModuleActivator> modules) throws ActivationException {

        // Start all the extension modules
        for (ModuleActivator activator : modules) {
            activator.start(registry);
        }
    }

    private List<ArtifactProcessor> loadArtifactProcessors(ExtensionPointRegistry registry, ClassLoader classLoader, Class<?> processorClass) {

        // Get the processor service declarations
        Set<String> processorDeclarations; 
        try {
            processorDeclarations = TempServiceDeclarationUtil.getServiceClassNames(classLoader, processorClass.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Get the target extension points
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        URLArtifactProcessorExtensionPoint urlProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        List<ArtifactProcessor> processors = new ArrayList<ArtifactProcessor>();
        
        for (String processorDeclaration: processorDeclarations) {
            Map<String, String> attributes = TempServiceDeclarationUtil.parseServiceDeclaration(processorDeclaration);
            String className = attributes.get("class");
            
            // Load a StAX artifact processor
            if (processorClass == StAXArtifactProcessor.class) {
                QName artifactType = null;
                String qname = attributes.get("type");
                if (qname != null) {
                    int h = qname.indexOf('#');
                    if (h == -1) {
                        artifactType = new QName(Constants.SCA10_NS, qname);
                    } else {
                        artifactType = new QName(qname.substring(0, h), qname.substring(h+1));
                    }
                }
                
                String modelTypeName = attributes.get("model");
                
                // Create a processor wrapper and register it
                StAXArtifactProcessor processor = new LazyStAXArtifactProcessor(modelFactories, artifactType, modelTypeName, classLoader, className);
                staxProcessors.addArtifactProcessor(processor);
                processors.add(processor);

            } else if (processorClass == URLArtifactProcessor.class) {

                String artifactType = attributes.get("type");
                String modelTypeName = attributes.get("model");
                
                // Create a processor wrapper and register it
                URLArtifactProcessor processor = new LazyURLArtifactProcessor(modelFactories, artifactType, modelTypeName, classLoader, className);
                urlProcessors.addArtifactProcessor(processor);
                processors.add(processor);

            }
        }
        return processors;
    }

    private List<ProviderFactory> loadProviderFactories(ExtensionPointRegistry registry, ClassLoader classLoader, Class<?> factoryClass) {

        // Get the provider factory service declarations
        Set<String> factoryDeclarations; 
        try {
            factoryDeclarations = TempServiceDeclarationUtil.getServiceClassNames(classLoader, factoryClass.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Get the target extension point
        ProviderFactoryExtensionPoint factoryExtensionPoint = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        List<ProviderFactory> factories = new ArrayList<ProviderFactory>();
        
        for (String factoryDeclaration: factoryDeclarations) {
            Map<String, String> attributes = TempServiceDeclarationUtil.parseServiceDeclaration(factoryDeclaration);
            String className = attributes.get("class");
            
            // Load an implementation provider factory
            if (factoryClass == ImplementationProviderFactory.class) {
                String modelTypeName = attributes.get("model");
                
                // Create a provider factory wrapper and register it
                ImplementationProviderFactory factory = new LazyImplementationProviderFactory(registry, modelTypeName, classLoader, className);
                factoryExtensionPoint.addProviderFactory(factory);
                factories.add(factory);

            } else if (factoryClass == BindingProviderFactory.class) {

                // Load a binding provider factory
                String modelTypeName = attributes.get("model");
                
                // Create a provider factory wrapper and register it
                BindingProviderFactory factory = new LazyBindingProviderFactory(registry, modelTypeName, classLoader, className);
                factoryExtensionPoint.addProviderFactory(factory);
                factories.add(factory);
            }
        }
        return factories;
    }

    private void stopModules(ExtensionPointRegistry registry, List<ModuleActivator> modules) {
        for (ModuleActivator module : modules) {
            module.stop(registry);
        }
    }

}
