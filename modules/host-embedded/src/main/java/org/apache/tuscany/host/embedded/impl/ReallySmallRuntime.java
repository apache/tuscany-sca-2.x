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

package org.apache.tuscany.host.embedded.impl;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.core.runtime.ActivationException;
import org.apache.tuscany.core.runtime.CompositeActivator;
import org.apache.tuscany.core.runtime.RuntimeAssemblyFactory;
import org.apache.tuscany.core.work.ThreadPoolWorkManager;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.policy.DefaultPolicyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.component.GroupInitializationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;

public class ReallySmallRuntime {
    
    private List<ModuleActivator> modules;
    private ExtensionPointRegistry registry;
    
    private ClassLoader classLoader;
    private AssemblyFactory assemblyFactory;
    private ContributionService contributionService;
    private CompositeActivator compositeActivator;
    private WorkContext workContext;
    private ThreadPoolWorkManager workManager;
    private ScopeRegistry scopeRegistry;
    
    public ReallySmallRuntime(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public void start() throws ActivationException {

        // Create our extension point registry
        registry = new DefaultExtensionPointRegistry();
        
        // Create a work context
        workContext = ReallySmallRuntimeBuilder.createWorkContext(registry);
        
        // Create a work manager
        workManager = new ThreadPoolWorkManager(10);

        // Create an interface contract mapper
        InterfaceContractMapper mapper = new DefaultInterfaceContractMapper();
        
        // Create a proxy factory
        ProxyFactory proxyFactory = ReallySmallRuntimeBuilder.createProxyFactory(registry, workContext, mapper);

        // Create model factories
        assemblyFactory = new RuntimeAssemblyFactory(proxyFactory);
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        
        // Create a contribution service
        contributionService = ReallySmallRuntimeBuilder.createContributionService(registry, assemblyFactory, policyFactory, mapper);
        
        // Create a composite activator
        compositeActivator = ReallySmallRuntimeBuilder.createCompositeActivator(registry, assemblyFactory, mapper, workContext, workManager);
        
        scopeRegistry = ReallySmallRuntimeBuilder.createScopeRegistry(registry);
        
        // Start the runtime modules
        modules = startModules(registry, classLoader);

    }
    
    public void stop() throws ActivationException {

        //FIXME remove this
        workContext.setIdentifier(Scope.COMPOSITE, null);
        
        // Stop and destroy the work manager
        workManager.destroy();
        
        // Stop the runtime modules
        stopModules(registry, modules);
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
    private List<ModuleActivator> startModules(ExtensionPointRegistry registry, ClassLoader classLoader) throws ActivationException {
        
        // Load and instantiate the modules found on the classpath
        List<ModuleActivator> modules = ReallySmallRuntimeBuilder.getServices(classLoader, ModuleActivator.class);
        for (ModuleActivator module : modules) {
            Map<Class, Object> extensionPoints = module.getExtensionPoints();
            if (extensionPoints != null) {
                for (Map.Entry<Class, Object> e : extensionPoints.entrySet()) {
                    registry.addExtensionPoint(e.getKey(), e.getValue());
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
        for (ModuleActivator module: modules) {
            module.stop(registry);
        }
    }

    //FIXME Remove this
    @SuppressWarnings("unchecked")
    public void startDomainWorkContext(Composite domain) {
        workContext.setIdentifier(Scope.COMPOSITE, domain);        
        WorkContextTunnel.setThreadWorkContext(workContext);
        try {
            scopeRegistry.getScopeContainer(Scope.COMPOSITE).startContext(domain, URI.create("/"));
        } catch (GroupInitializationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
