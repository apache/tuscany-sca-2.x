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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeModelResolver;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeModelResolver;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeModelResolver;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ContributionPostProcessor;
import org.apache.tuscany.sca.contribution.processor.DefaultContributionPostProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleContributionPostProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensiblePackageProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.sca.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionListenerExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.DefaultContributionListenerExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ExtensibleContributionListener;
import org.apache.tuscany.sca.contribution.service.impl.ContributionMetadataProcessor;
import org.apache.tuscany.sca.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.sca.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.sca.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.distributed.node.impl.NodeDocumentProcessor;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * This is almost exactly the same as the really small runtime builder
 * except that it loads the topology document processor when the contribution
 * service is created. This is really just a place holder for whatever runtime
 * options are required to run and manage the node. 
 *
 */
public class NodeRuntimeBuilder {


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

        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

        // Create STAX artifact processor extension point
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(staxProcessors);

        // Create and register STAX processors for SCA assembly XML
        ExtensibleStAXArtifactProcessor staxProcessor = 
            new ExtensibleStAXArtifactProcessor(staxProcessors, xmlFactory, XMLOutputFactory.newInstance());
        staxProcessors.addArtifactProcessor(new CompositeProcessor(contributionFactory, assemblyFactory, policyFactory, mapper, staxProcessor));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        //Register STAX processors for Contribution Metadata 
        staxProcessors.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory,contributionFactory, staxProcessor));

        // Create URL artifact processor extension point
        // FIXME use the interface instead of the class
        DefaultURLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        registry.addExtensionPoint(documentProcessors);

        // Create and register document processors for SCA assembly XML
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        documentProcessors.addArtifactProcessor(new NodeDocumentProcessor(staxProcessor, inputFactory));
        //documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));

        // Create Contribution Model Resolver extension point
        ModelResolverExtensionPoint modelResolvers = new DefaultModelResolverExtensionPoint();
        registry.addExtensionPoint(modelResolvers);
        
        // Register model resolvers for SCA assembly XML
        modelResolvers.addResolver(Composite.class, CompositeModelResolver.class);
        modelResolvers.addResolver(ConstrainingType.class, ConstrainingTypeModelResolver.class);
        modelResolvers.addResolver(ComponentType.class, ComponentTypeModelResolver.class);

        // Create contribution package processor extension point
        PackageTypeDescriberImpl describer = new PackageTypeDescriberImpl();
        PackageProcessorExtensionPoint packageProcessors = new DefaultPackageProcessorExtensionPoint();
        registry.addExtensionPoint(packageProcessors);

        // Register base package processors
        packageProcessors.addPackageProcessor(new JarContributionProcessor());
        packageProcessors.addPackageProcessor(new FolderContributionProcessor());

        PackageProcessor packageProcessor = new ExtensiblePackageProcessor(packageProcessors, describer);
        
        // Get the model factory extension point
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        
        //FIXME Deprecate and remove this
        // Create contribution postProcessor extension point
        DefaultContributionPostProcessorExtensionPoint contributionPostProcessors = new DefaultContributionPostProcessorExtensionPoint();
        ContributionPostProcessor postProcessor = new ExtensibleContributionPostProcessor(contributionPostProcessors);
        registry.addExtensionPoint(contributionPostProcessors);        

        // Create contribution listener extension point
        ContributionListenerExtensionPoint contributionListeners = new DefaultContributionListenerExtensionPoint();
        registry.addExtensionPoint(contributionListeners);
        
        ExtensibleContributionListener contributionListener = new ExtensibleContributionListener(contributionListeners);
        

        // Create a contribution repository
        ContributionRepository repository;
        try {
            repository = new ContributionRepositoryImpl("target");
        } catch (IOException e) {
            throw new ActivationException(e);
        }

        ExtensibleURLArtifactProcessor documentProcessor = new ExtensibleURLArtifactProcessor(documentProcessors);
        ContributionService contributionService = new ContributionServiceImpl(repository, 
                                                                              packageProcessor,
                                                                              documentProcessor, 
                                                                              staxProcessor,
                                                                              contributionListener,
                                                                              postProcessor,
                                                                              modelResolvers,
                                                                              modelFactories,
                                                                              assemblyFactory,
                                                                              contributionFactory, 
                                                                              xmlFactory);
        return contributionService;
    }
}
