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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.binding.sca.impl.RuntimeSCABindingProviderFactory;
import org.apache.tuscany.sca.binding.sca.impl.SCABindingFactoryImpl;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.DefaultContextFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.impl.ContributionFactoryImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.ExtensibleWireProcessor;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.core.runtime.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.work.Jsr237WorkScheduler;
import org.apache.tuscany.sca.core.work.ThreadPoolWorkManager;
import org.apache.tuscany.sca.distributed.core.DistributedCompositeActivatorImpl;
import org.apache.tuscany.sca.distributed.host.DistributedSCADomain;
import org.apache.tuscany.sca.host.embedded.impl.ReallySmallRuntimeBuilder;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.DefaultProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.DefaultWireProcessorExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 * This is almost exactly the same as the really small runtime
 * except that it defines it's members as protected and 
 * provides overrideable methods for accessing some of the builder
 * methods so that the builder can be changed
 */
public class DistributedRuntime  {

    protected List<ModuleActivator> modules;
    protected ExtensionPointRegistry registry;

    protected ClassLoader classLoader;
    protected AssemblyFactory assemblyFactory;
    protected ContributionService contributionService;
    protected CompositeActivator compositeActivator;
    protected ThreadPoolWorkManager workManager;
    protected ScopeRegistry scopeRegistry;
    
    public DistributedRuntime(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void start(DistributedSCADomain domain) 
      throws ActivationException {

        // Create our extension point registry
        registry = new DefaultExtensionPointRegistry();
        
        // Add the current domain to the extension point registry
        //FIXME Pass the domain around differently as it's not an extension point
        if (domain != null ){
            registry.addExtensionPoint(domain);
        }

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
        ContributionFactory contributionFactory = new ContributionFactoryImpl(); 
        factories.addFactory(contributionFactory);        
        
        // Create a contribution service
        contributionService = createContributionService(classLoader, 
                                                        registry,
                                                        contributionFactory,
                                                        assemblyFactory,
                                                        policyFactory,
                                                        mapper);

        // Create the ScopeRegistry
        scopeRegistry = ReallySmallRuntimeBuilder.createScopeRegistry(registry);
        
        // Create a work scheduler
        //FIXME Pass this around differently as it's not an extension point
        WorkScheduler workScheduler = new Jsr237WorkScheduler(workManager);
        registry.addExtensionPoint(workScheduler);

        // Create a wire post processor extension point
        RuntimeWireProcessorExtensionPoint wireProcessors = new DefaultWireProcessorExtensionPoint();
        registry.addExtensionPoint(wireProcessors);
        RuntimeWireProcessor wireProcessor = new ExtensibleWireProcessor(wireProcessors);

        // Create a provider factory extension point
        ProviderFactoryExtensionPoint providerFactories = new DefaultProviderFactoryExtensionPoint();
        registry.addExtensionPoint(providerFactories);
        providerFactories.addProviderFactory(new RuntimeSCABindingProviderFactory());
        
        // Start the runtime modules
        modules = startModules(registry, classLoader);
        
        // get the SCA binding factory from the registry. It should have been 
        // loaded from an extension
        SCABindingFactory scaBindingFactory = factories.getFactory(SCABindingFactory.class);
        
        // if not use the core version
        if (scaBindingFactory == null) {
            scaBindingFactory = new SCABindingFactoryImpl();
            factories.addFactory(scaBindingFactory);
        }

        // Create the composite activator
        compositeActivator = createCompositeActivator(registry,
                                                      domain,
                                                      assemblyFactory, 
                                                      scaBindingFactory,
                                                      mapper, 
                                                      scopeRegistry,
                                                      workScheduler, 
                                                      wireProcessor,
                                                      providerFactories);
    }
    
    public  ContributionService createContributionService(ClassLoader classLoader,
                                                          ExtensionPointRegistry registry,
                                                          ContributionFactory contributionFactory,
                                                          AssemblyFactory assemblyFactory,
                                                          PolicyFactory policyFactory,
                                                          InterfaceContractMapper mapper)
      throws ActivationException {        
        return ReallySmallRuntimeBuilder.createContributionService(classLoader,
                                                                   registry,
                                                                   contributionFactory,
                                                                   assemblyFactory,
                                                                   policyFactory,
                                                                   mapper);        
    }
    
    public CompositeActivator createCompositeActivator(ExtensionPointRegistry registry,
                                                       DistributedSCADomain domain,
                                                       AssemblyFactory assemblyFactory,
                                                       SCABindingFactory scaBindingFactory,
                                                       InterfaceContractMapper mapper,
                                                       ScopeRegistry scopeRegistry,
                                                       WorkScheduler workScheduler,
                                                       RuntimeWireProcessor wireProcessor,
                                                       ProviderFactoryExtensionPoint providerFactories) {
       return  new DistributedCompositeActivatorImpl(assemblyFactory, 
                                                     scaBindingFactory,
                                                     mapper, 
                                                     scopeRegistry,
                                                     workScheduler, 
                                                     wireProcessor,
                                                     providerFactories,
                                                     domain);
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

    public AssemblyFactory getAssemblyFactory() {
        return assemblyFactory;
    }

    @SuppressWarnings("unchecked")
    protected List<ModuleActivator> startModules(ExtensionPointRegistry registry, ClassLoader classLoader)
        throws ActivationException {

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

        // Start all the extension modules
        for (ModuleActivator activator : modules) {
            activator.start(registry);
        }

        return modules;
    }

    protected void stopModules(ExtensionPointRegistry registry, List<ModuleActivator> modules) {
        for (ModuleActivator module : modules) {
            module.stop(registry);
        }
    }


}
