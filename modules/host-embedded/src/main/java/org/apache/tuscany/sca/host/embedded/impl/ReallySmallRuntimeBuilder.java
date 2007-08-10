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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.binding.sca.xml.SCABindingProcessor;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ContributionPostProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionPostProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleContributionPostProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensiblePackageProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionListenerExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.ExtensibleContributionListener;
import org.apache.tuscany.sca.contribution.service.TypeDescriber;
import org.apache.tuscany.sca.contribution.service.impl.ContributionMetadataProcessor;
import org.apache.tuscany.sca.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.sca.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.sca.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.invocation.DefaultProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ExtensibleWireProcessor;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.CompositeActivator;
import org.apache.tuscany.sca.core.runtime.CompositeActivatorImpl;
import org.apache.tuscany.sca.core.scope.CompositeScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ConversationalScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.RequestScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ScopeRegistryImpl;
import org.apache.tuscany.sca.core.scope.StatelessScopeContainerFactory;
import org.apache.tuscany.sca.core.work.Jsr237WorkScheduler;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.scope.ScopeContainerFactory;
import org.apache.tuscany.sca.scope.ScopeRegistry;
import org.apache.tuscany.sca.work.WorkScheduler;

import commonj.work.WorkManager;

public class ReallySmallRuntimeBuilder {

    public static ProxyFactory createProxyFactory(ExtensionPointRegistry registry,
                                                  InterfaceContractMapper mapper,
                                                  MessageFactory messageFactory) {

        ProxyFactory proxyFactory = new DefaultProxyFactoryExtensionPoint(messageFactory, mapper);

        // FIXME Pass these around differently as they are not extension points
        registry.addExtensionPoint(proxyFactory);
        registry.addExtensionPoint(mapper);

        return proxyFactory;
    }

    public static CompositeActivator createCompositeActivator(ExtensionPointRegistry registry,
                                                              AssemblyFactory assemblyFactory,
                                                              SCABindingFactory scaBindingFactory,
                                                              InterfaceContractMapper mapper,
                                                              ScopeRegistry scopeRegistry,
                                                              WorkManager workManager) {

        // Create a work scheduler
        //FIXME Pass the work scheduler differently as it's not an extension point
        WorkScheduler workScheduler = new Jsr237WorkScheduler(workManager);
        registry.addExtensionPoint(workScheduler);

        // Create a wire post processor extension point
        RuntimeWireProcessorExtensionPoint wireProcessors = registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        RuntimeWireProcessor wireProcessor = new ExtensibleWireProcessor(wireProcessors);

        // Add the SCABindingProcessor extension
        PolicyFactory policyFactory = registry.getExtensionPoint(PolicyFactory.class);
        SCABindingProcessor scaBindingProcessor =
            new SCABindingProcessor(assemblyFactory, policyFactory, scaBindingFactory);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(scaBindingProcessor);

        // Create a provider factory extension point
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

        // Create the composite activator
        CompositeActivator compositeActivator =
            new CompositeActivatorImpl(assemblyFactory, scaBindingFactory, mapper, scopeRegistry, workScheduler,
                                       wireProcessor, providerFactories);

        return compositeActivator;
    }
    
    public static CompositeBuilder createCompositeBuilder(AssemblyFactory assemblyFactory, SCABindingFactory scaBindingFactory, InterfaceContractMapper interfaceContractMapper) {
        return new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, interfaceContractMapper, null);
    }

    /**
     * Create the contribution service used by this domain.
     * 
     * @throws ActivationException
     */
    public static ContributionService createContributionService(ClassLoader classLoader,
                                                                ExtensionPointRegistry registry,
                                                                ContributionFactory contributionFactory,
                                                                AssemblyFactory assemblyFactory,
                                                                PolicyFactory policyFactory,
                                                                InterfaceContractMapper mapper)
        throws ActivationException {

        XMLInputFactory xmlFactory = registry.getExtensionPoint(XMLInputFactory.class);

        // Create STAX artifact processor extension point
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        // Create and register STAX processors for SCA assembly XML
        ExtensibleStAXArtifactProcessor staxProcessor = 
            new ExtensibleStAXArtifactProcessor(staxProcessors, xmlFactory, XMLOutputFactory.newInstance());
        staxProcessors.addArtifactProcessor(new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory, mapper, staxProcessor));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessor));

        // Register STAX processors for Contribution Metadata
        staxProcessors.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, staxProcessor));

        // Create URL artifact processor extension point
        URLArtifactProcessorExtensionPoint documentProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);

        // Create and register document processors for SCA assembly XML
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));
        
        // Create Model Resolver extension point
        ModelResolverExtensionPoint modelResolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);

        // Create contribution package processor extension point
        TypeDescriber describer = new PackageTypeDescriberImpl();
        PackageProcessor packageProcessor = new ExtensiblePackageProcessor(registry.getExtensionPoint(PackageProcessorExtensionPoint.class), describer);

        // Get the model factory extension point
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        
        //FIXME remove this
        ContributionPostProcessor postProcessor = new ExtensibleContributionPostProcessor(registry.getExtensionPoint(ContributionPostProcessorExtensionPoint.class));

        // Create contribution listener
        ExtensibleContributionListener contributionListener = new ExtensibleContributionListener(registry.getExtensionPoint(ContributionListenerExtensionPoint.class));
        
        // Create a contribution repository
        ContributionRepository repository;
        try {
            repository = new ContributionRepositoryImpl("target");
        } catch (IOException e) {
            throw new ActivationException(e);
        }

        ExtensibleURLArtifactProcessor documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors);

        ContributionService contributionService =
            new ContributionServiceImpl(repository, packageProcessor, documentProcessor, staxProcessor, contributionListener,
                    postProcessor, modelResolvers, modelFactories, assemblyFactory, contributionFactory, xmlFactory);
        return contributionService;
    }

    public static ScopeRegistry createScopeRegistry(ExtensionPointRegistry registry) {
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        ScopeContainerFactory[] factories =
            new ScopeContainerFactory[] {new CompositeScopeContainerFactory(), new StatelessScopeContainerFactory(),
                                         new RequestScopeContainerFactory(),
                                         new ConversationalScopeContainerFactory(null),
            // new HttpSessionScopeContainer(monitor)
            };
        for (ScopeContainerFactory f : factories) {
            scopeRegistry.register(f);
        }

        //FIXME Pass the scope container differently as it's not an extension point
        registry.addExtensionPoint(scopeRegistry);

        return scopeRegistry;
    }

}
