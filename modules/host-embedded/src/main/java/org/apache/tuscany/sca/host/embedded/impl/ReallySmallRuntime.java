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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingFactoryImpl;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.DefaultContextFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.impl.ContributionFactoryImpl;
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

        // Get factory extension point
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        
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
