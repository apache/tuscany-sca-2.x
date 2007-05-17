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

import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultSCABindingFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.core.runtime.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.work.ThreadPoolWorkManager;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.scope.ScopeRegistry;

public class ReallySmallRuntime {

    private List<ModuleActivator> modules;
    private ExtensionPointRegistry registry;

    private ClassLoader classLoader;
    private AssemblyFactory assemblyFactory;
    private ContributionService contributionService;
    private CompositeActivator compositeActivator;
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
        SCABindingFactory scaBindingFactory = new DefaultSCABindingFactory();
        factories.addFactory(scaBindingFactory);
        
        // Create a contribution service
        contributionService = ReallySmallRuntimeBuilder.createContributionService(registry,
                                                                                  assemblyFactory,
                                                                                  policyFactory,
                                                                                  mapper);

        // Create the ScopeRegistry
        scopeRegistry = ReallySmallRuntimeBuilder.createScopeRegistry(registry);

        // Create a composite activator
        compositeActivator = ReallySmallRuntimeBuilder.createCompositeActivator(registry,
                                                                                assemblyFactory,
                                                                                scaBindingFactory,
                                                                                mapper,
                                                                                scopeRegistry,
                                                                                workManager);

        // Start the runtime modules
        modules = startModules(registry, classLoader);

    }

    public void stop() throws ActivationException {

        // Stop the runtime modules
        stopModules(registry, modules);

        // Stop and destroy the work manager
        workManager.destroy();
        
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
    private List<ModuleActivator> startModules(ExtensionPointRegistry registry, ClassLoader classLoader)
        throws ActivationException {

        // Load and instantiate the modules found on the classpath
        List<ModuleActivator> modules = ReallySmallRuntimeBuilder.getServices(classLoader, ModuleActivator.class);
        for (ModuleActivator module : modules) {
            Object[] extensionPoints = module.getExtensionPoints();
            if (extensionPoints != null) {
                for (Object e : extensionPoints) {
                    registry.addExtensionPoint(e);
                }
            }
        }

        // Start all the extension modules
        for (ModuleActivator activator : modules) {
            activator.start(registry);
        }

        return modules;
    }

    private void stopModules(ExtensionPointRegistry registry, List<ModuleActivator> modules) {
        for (ModuleActivator module : modules) {
            module.stop(registry);
        }
    }

}
