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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.DefaultContextFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.util.ServiceConfigurationUtil;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.DefaultIntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.SCADefinitions;
import org.apache.tuscany.sca.policy.xml.SCADefinitionsDocumentProcessor;
import org.apache.tuscany.sca.work.WorkScheduler;

public class ReallySmallRuntime {

    private List<ModuleActivator> modules;
    private ExtensionPointRegistry registry;

    private ClassLoader classLoader;
    private AssemblyFactory assemblyFactory;
    private ContributionService contributionService;
    private CompositeActivator compositeActivator;
    private CompositeBuilder compositeBuilder;
    private WorkScheduler workScheduler;
    private ScopeRegistry scopeRegistry;
    private ProxyFactory proxyFactory;

    public ReallySmallRuntime(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void start() throws ActivationException {

        // Create our extension point registry
        registry = new DefaultExtensionPointRegistry();

//      Get work scheduler
        workScheduler = registry.getExtensionPoint(WorkScheduler.class);

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
        proxyFactory = ReallySmallRuntimeBuilder.createProxyFactory(registry, mapper, messageFactory);

        // Create model factories
        assemblyFactory = new RuntimeAssemblyFactory();
        factories.addFactory(assemblyFactory);
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        factories.addFactory(policyFactory);
        SCABindingFactory scaBindingFactory = factories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory intentAttachPointTypeFactory = new DefaultIntentAttachPointTypeFactory();
        factories.addFactory(intentAttachPointTypeFactory);
        ContributionFactory contributionFactory = factories.getFactory(ContributionFactory.class); 
        
        SCADefinitionsDocumentProcessor scaDocDefnProcessor = 
            ReallySmallRuntimeBuilder.createSCADefinitionsDocProcessor(registry, policyFactory);
        
        // Create a contribution service
        contributionService = ReallySmallRuntimeBuilder.createContributionService(classLoader,
                                                                                  registry,
                                                                                  contributionFactory,
                                                                                  assemblyFactory,
                                                                                  policyFactory,
                                                                                  mapper,
                                                                                  scaDocDefnProcessor.getDomainModelResolver());

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
                                                                                proxyFactory,
                                                                                scopeRegistry,
                                                                                workScheduler);

        
        // Load the runtime modules
        modules = loadModules(registry, classLoader);
        
        // Start the runtime modules
        startModules(registry, modules);
        
        loadDomainDefinitions(scaDocDefnProcessor);
    }
    
    private void loadDomainDefinitions(SCADefinitionsDocumentProcessor scaDocDefnProcessor) throws ActivationException {
        URL url = this.classLoader.getResource("definitions.xml");
        
        if ( url != null ) {
            try {
                SCADefinitions scaDefinitions = scaDocDefnProcessor.read(null, null, url);
                scaDocDefnProcessor.resolve(scaDefinitions, scaDocDefnProcessor.getDomainModelResolver());
            } catch ( ContributionReadException e ) {
                throw new ActivationException(e);
            } catch ( ContributionResolveException e ) {
                throw new ActivationException(e);
            }
        } 
    }

    public void stop() throws ActivationException {

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
            List<String> classNames = ServiceConfigurationUtil.getServiceClassNames(classLoader, ModuleActivator.class.getName());
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

    private void stopModules(ExtensionPointRegistry registry, List<ModuleActivator> modules) {
        for (ModuleActivator module : modules) {
            module.stop(registry);
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
