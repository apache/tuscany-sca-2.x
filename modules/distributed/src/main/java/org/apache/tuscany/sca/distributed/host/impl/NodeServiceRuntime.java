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

import java.net.URL;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultSCABindingFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
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
import org.apache.tuscany.sca.core.runtime.CompositeActivatorImpl;
import org.apache.tuscany.sca.core.runtime.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.work.ThreadPoolWorkManager;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.work.WorkScheduler;

import commonj.work.WorkManager;

/**
 * This is almost exactly the same as the really small runtime
 * except that it uses the node service runtime builder.
 * This is really just a place holder for whatever runtime is required
 * to run and manage the node
 *
 */
public class NodeServiceRuntime extends DistributedRuntime  {

    public NodeServiceRuntime(ClassLoader classLoader) {
        super(classLoader);
    }
    
    /**
     * The node service runtime uses a bog standard composite activator
     * 
     * @param registry
     * @param assemblyFactory
     * @param scaBindingFactory
     * @param mapper
     * @param scopeRegistry
     * @param workScheduler
     * @param wireProcessor
     * @param providerFactories
     * @return
     */
    public static CompositeActivator createCompositeActivator(ExtensionPointRegistry registry,
                                                              SCADomain domainNode,
                                                              AssemblyFactory assemblyFactory,
                                                              SCABindingFactory scaBindingFactory,
                                                              InterfaceContractMapper mapper,
                                                              ScopeRegistry scopeRegistry,
                                                              WorkScheduler workScheduler,
                                                              RuntimeWireProcessor wireProcessor,
                                                              ProviderFactoryExtensionPoint providerFactories) {
        return  new CompositeActivatorImpl(assemblyFactory, 
                                           scaBindingFactory,
                                           mapper, 
                                           scopeRegistry,
                                           workScheduler, 
                                           wireProcessor,
                                           providerFactories);

    }    
    
    /**
     *  Use the node service runtime builder. The only thing that is different is
     *  that the .node document processor is added instead of .composite
     */
    @Override
    public ContributionService createContributionService(ExtensionPointRegistry registry,
                                                         ContributionFactory contributionFactory,
                                                         AssemblyFactory assemblyFactory,
                                                         PolicyFactory policyFactory,
                                                         InterfaceContractMapper mapper)
      throws ActivationException {        
        return NodeServiceRuntimeBuilder.createContributionService(registry,
                                                                   contributionFactory,
                                                                   assemblyFactory,
                                                                   policyFactory,
                                                                   mapper);        
    }
    
    public Composite getNodeComposite(URL nodeFileURL)
      throws ContributionReadException, ContributionResolveException {
        DefaultURLArtifactProcessorExtensionPoint documentProcessors = 
            (DefaultURLArtifactProcessorExtensionPoint)
            registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        ExtensibleURLArtifactProcessor documentProcessor = 
            new ExtensibleURLArtifactProcessor(documentProcessors);  
        
        // read the node model
        Composite nodeComposite  = (Composite)documentProcessor.read(null, null, nodeFileURL);
    
        // resolve the node model
        ModelResolver resolver = new ModelResolverImpl(classLoader);
        documentProcessor.resolve(nodeComposite, resolver);
        
        return nodeComposite;
    }
}
