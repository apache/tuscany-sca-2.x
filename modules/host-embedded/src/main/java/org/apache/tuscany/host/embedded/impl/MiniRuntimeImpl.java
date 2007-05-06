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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.impl.DefaultContributionFactory;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessor;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessor;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.contribution.service.util.FileHelper;
import org.apache.tuscany.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.core.runtime.ActivationException;
import org.apache.tuscany.core.runtime.RuntimeActivatorImpl;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

/**
 * @version $Rev$ $Date$
 */
public class MiniRuntimeImpl extends RuntimeActivatorImpl<SimpleRuntimeInfo> {
    public MiniRuntimeImpl(SimpleRuntimeInfo runtimeInfo) {
        super(SimpleRuntimeInfo.class, runtimeInfo, runtimeInfo.getClassLoader(), new DefaultExtensionPointRegistry());
    }

    private static URL getContributionLocation(URL applicationSCDL, String compositePath) {
        URL root = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String scdlUrl = applicationSCDL.toExternalForm();
            String protocol = applicationSCDL.getProtocol();
            if ("file".equals(protocol)) {
                // directory contribution
                if (scdlUrl.endsWith(compositePath)) {
                    String location = scdlUrl.substring(0, scdlUrl.lastIndexOf(compositePath));
                    // workaround from evil url/uri form maven
                    root = FileHelper.toFile(new URL(location)).toURI().toURL();
                }

            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = scdlUrl.substring(4, scdlUrl.lastIndexOf("!/"));
                // workaround from evil url/uri form maven
                root = FileHelper.toFile(new URL(location)).toURI().toURL();
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        }

        return root;
    }

    @SuppressWarnings("unchecked")
    public void start() throws ActivationException {
        super.init();

        ContributionService contributionService = createContributionService();

        super.start();

        // Contribute and activate the SCA contribution
        URI uri = URI.create("sca://default/");
        URL root = getContributionLocation(runtimeInfo.getApplicationSCDL(), runtimeInfo.getCompositePath());
        try {
            contributionService.contribute(uri, root, false);
        } catch (Exception e) {
            throw new ActivationException(e);
        }
        Contribution contribution = contributionService.getContribution(uri);

        super.start(contribution, runtimeInfo.getCompositePath());
        CompositeContext context = new SimpleCompositeContextImpl(this, domain);
        CurrentCompositeContext.setContext(context);
    }

    protected ContributionService createContributionService() throws ActivationException {
        // Add artifact processor extension points
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        DefaultStAXArtifactProcessor staxProcessor = new DefaultStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        extensionPointRegistry.addExtensionPoint(StAXArtifactProcessorExtensionPoint.class, staxProcessors);
        DefaultURLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        DefaultURLArtifactProcessor documentProcessor = new DefaultURLArtifactProcessor(documentProcessors);
        extensionPointRegistry.addExtensionPoint(URLArtifactProcessorExtensionPoint.class, documentProcessors);

        // Register base artifact processors
        staxProcessors.addArtifactProcessor(new CompositeProcessor(assemblyFactory, policyFactory, interfaceContractMapper,
                                                           staxProcessor));
        staxProcessors.addArtifactProcessor(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessor));
        staxProcessors.addArtifactProcessor(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessor));

        XMLInputFactory inputFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass()
            .getClassLoader());
        documentProcessors.addArtifactProcessor(new CompositeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ComponentTypeDocumentProcessor(staxProcessor, inputFactory));
        documentProcessors.addArtifactProcessor(new ConstrainingTypeDocumentProcessor(staxProcessor, inputFactory));

        // Create package processor extension point
        PackageTypeDescriberImpl describer = new PackageTypeDescriberImpl();
        PackageProcessorExtensionPoint packageProcessors = new DefaultPackageProcessorExtensionPoint(describer);
        extensionPointRegistry.addExtensionPoint(PackageProcessorExtensionPoint.class, packageProcessors);

        // Register base package processors
        new JarContributionProcessor(packageProcessors);
        new FolderContributionProcessor(packageProcessors);

        // Create contribution service
        ContributionRepository repository;
        try {
            repository = new ContributionRepositoryImpl("target");
        } catch (IOException e) {
            throw new ActivationException(e);
        }

        DefaultArtifactResolver artifactResolver = new DefaultArtifactResolver(hostClassLoader);
        ContributionService contributionService = new ContributionServiceImpl(repository, packageProcessors,
                                                                              documentProcessor, artifactResolver,
                                                                              assemblyFactory,
                                                                              new DefaultContributionFactory(),
                                                                              xmlFactory);
        return contributionService;
    }

    public <T> T getExtensionPoint(Class<T> type) throws TargetResolutionException {
        return extensionPointRegistry.getExtensionPoint(type);
    }

    @Override
    public void stop() throws ActivationException {
        getWorkContext().setIdentifier(Scope.COMPOSITE, null);
        super.stop();
    }

}
