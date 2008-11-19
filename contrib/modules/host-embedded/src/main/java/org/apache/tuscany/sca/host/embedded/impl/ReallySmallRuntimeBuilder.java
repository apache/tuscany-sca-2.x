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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.processor.DefaultValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensiblePackageProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionListenerExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.ExtensibleContributionListener;
import org.apache.tuscany.sca.contribution.service.TypeDescriber;
import org.apache.tuscany.sca.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.sca.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.sca.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.CompositeActivatorImpl;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.invocation.ExtensibleWireProcessor;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.scope.CompositeScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ConversationalScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.RequestScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.core.scope.ScopeRegistryImpl;
import org.apache.tuscany.sca.core.scope.StatelessScopeContainerFactory;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.endpointresolver.EndpointResolverFactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.apache.tuscany.sca.work.WorkScheduler;

/**
 *
 * @version $Rev$ $Date$
 */
public class ReallySmallRuntimeBuilder {
    
    // private static final Logger logger = Logger.getLogger(ReallySmallRuntimeBuilder.class.getName());
	
    public static CompositeActivator createCompositeActivator(ExtensionPointRegistry registry,
                                                              AssemblyFactory assemblyFactory,
                                                              MessageFactory messageFactory,
                                                              SCABindingFactory scaBindingFactory,
                                                              InterfaceContractMapper mapper,
                                                              ProxyFactory proxyFactory,
                                                              ScopeRegistry scopeRegistry,
                                                              WorkScheduler workScheduler) {

        // Create a wire post processor extension point
        RuntimeWireProcessorExtensionPoint wireProcessors =
            registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        RuntimeWireProcessor wireProcessor = new ExtensibleWireProcessor(wireProcessors);

        // Retrieve the processors extension point
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);        

        // Create a provider factory extension point
        ProviderFactoryExtensionPoint providerFactories =
            registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        
        // Create a endpoint resolver factory extension point
        EndpointResolverFactoryExtensionPoint endpointResolverFactories =
            registry.getExtensionPoint(EndpointResolverFactoryExtensionPoint.class);        

        JavaInterfaceFactory javaInterfaceFactory =
            registry.getExtensionPoint(ModelFactoryExtensionPoint.class).getFactory(JavaInterfaceFactory.class);
        RequestContextFactory requestContextFactory =
            registry.getExtensionPoint(ContextFactoryExtensionPoint.class).getFactory(RequestContextFactory.class);

        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        ConversationManager conversationManager = utilities.getUtility(ConversationManager.class);
        
        // Create the composite activator
        CompositeActivator compositeActivator =
            new CompositeActivatorImpl(assemblyFactory, messageFactory, javaInterfaceFactory, scaBindingFactory,
                                       mapper, scopeRegistry, workScheduler, wireProcessor, requestContextFactory,
                                       proxyFactory, providerFactories, endpointResolverFactories, processors, conversationManager);

        return compositeActivator;
    }

    public static CompositeBuilder createCompositeBuilder(Monitor monitor,
                                                          AssemblyFactory assemblyFactory,
                                                          SCABindingFactory scaBindingFactory,
                                                          EndpointFactory endpointFactory,
                                                          IntentAttachPointTypeFactory intentAttachPointTypeFactory,
                                                          DocumentBuilderFactory documentBuilderFactory,
                                                          TransformerFactory transformerFactory,
                                                          InterfaceContractMapper interfaceContractMapper,
                                                          SCADefinitions policyDefinitions) {
      
        
        return new CompositeBuilderImpl(assemblyFactory, 
                                        endpointFactory,
                                        scaBindingFactory, 
                                        intentAttachPointTypeFactory, 
                                        documentBuilderFactory,
                                        transformerFactory,
                                        interfaceContractMapper,
                                        policyDefinitions,
                                        monitor);
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
                                                                InterfaceContractMapper mapper,
                                                                List<SCADefinitions> policyDefinitions,
                                                                ModelResolver policyDefinitionResolver,
                                                                Monitor monitor)
        throws ActivationException {

        // Get the model factory extension point
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);

        // Create a new XML input factory
        // Allow privileged access to factory. Requires RuntimePermission in security policy file.
        XMLInputFactory inputFactory = AccessController.doPrivileged(new PrivilegedAction<XMLInputFactory>() {
            public XMLInputFactory run() {
                return XMLInputFactory.newInstance();
            }
        });
        modelFactories.addFactory(inputFactory);
        
        // Create a validation XML schema extension point
        ValidationSchemaExtensionPoint schemas = new DefaultValidationSchemaExtensionPoint();
               
        // Create a validating XML input factory
        XMLInputFactory validatingInputFactory = new DefaultValidatingXMLInputFactory(inputFactory, schemas, monitor);
        modelFactories.addFactory(validatingInputFactory);
        
        // Create StAX artifact processor extension point
        StAXArtifactProcessorExtensionPoint staxProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        // Create and register StAX processors for SCA assembly XML
        // Allow privileged access to factory. Requires RuntimePermission in security policy file.
        XMLOutputFactory outputFactory = AccessController.doPrivileged(new PrivilegedAction<XMLOutputFactory>() {
            public XMLOutputFactory run() {
                return XMLOutputFactory.newInstance();
            }
        });           
        ExtensibleStAXArtifactProcessor staxProcessor =
            new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, monitor);

        // Create URL artifact processor extension point
        URLArtifactProcessorExtensionPoint documentProcessors =
            registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);

        // Create and register document processors for SCA assembly XML
        documentProcessors.getProcessor(Composite.class);
        DocumentBuilderFactory documentBuilderFactory = AccessController.doPrivileged(new PrivilegedAction<DocumentBuilderFactory>() {
            public DocumentBuilderFactory run() {
                return DocumentBuilderFactory.newInstance();
            }
        });           
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, validatingInputFactory, documentBuilderFactory, policyDefinitions, monitor));

        // Create Model Resolver extension point
        ModelResolverExtensionPoint modelResolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);

        // Create contribution package processor extension point
        TypeDescriber describer = new PackageTypeDescriberImpl();
        PackageProcessor packageProcessor =
            new ExtensiblePackageProcessor(registry.getExtensionPoint(PackageProcessorExtensionPoint.class), describer, monitor);

        // Create contribution listener
        ExtensibleContributionListener contributionListener =
            new ExtensibleContributionListener(registry.getExtensionPoint(ContributionListenerExtensionPoint.class));

        // Create a contribution repository
        ContributionRepository repository;
        try {
            repository = new ContributionRepositoryImpl("target", inputFactory, monitor);
        } catch (IOException e) {
            throw new ActivationException(e);
        }

        ExtensibleURLArtifactProcessor documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors, monitor);

        // Create the contribution service
        ContributionService contributionService =
            new ContributionServiceImpl(repository, packageProcessor, documentProcessor, staxProcessor,
                                        contributionListener, policyDefinitionResolver, modelResolvers, modelFactories,
                                        assemblyFactory, contributionFactory, inputFactory, policyDefinitions, monitor);
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
