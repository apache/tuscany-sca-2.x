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
package org.apache.tuscany.sca.workspace.processor.impl;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.scanner.ContributionScanner;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.workspace.scanner.impl.DirectoryContributionScanner;
import org.apache.tuscany.sca.workspace.scanner.impl.JarContributionScanner;

/**
 * URLArtifactProcessor that handles contribution files and the artifacts they contain
 * and returns a contribution model.
 * 
 * @version $Rev$ $Date$
 */
public class ContributionContentProcessor implements URLArtifactProcessor<Contribution>{
    private ContributionFactory contributionFactory;
    private ModelResolverExtensionPoint modelResolvers;
    private ModelFactoryExtensionPoint modelFactories;
    private URLArtifactProcessor<Object> artifactProcessor;

    public ContributionContentProcessor(ModelFactoryExtensionPoint modelFactories, ModelResolverExtensionPoint modelResolvers, URLArtifactProcessor<Object> artifactProcessor) {
        this.modelFactories = modelFactories;
        this.modelResolvers = modelResolvers;
        hackResolvers(modelResolvers);
        this.artifactProcessor = artifactProcessor;
        this.contributionFactory = modelFactories.getFactory(ContributionFactory.class);
    }
    
    public String getArtifactType() {
        return null;
    }
    
    public Class<Contribution> getModelType() {
        return Contribution.class;
    }
    
    public Contribution read(URL parentURL, URI contributionURI, URL contributionURL) throws ContributionReadException {
        
        // Create contribution model
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(contributionURI.toString());
        contribution.setLocation(contributionURL.toString());
        ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
        contribution.setModelResolver(modelResolver);
        contribution.setUnresolved(true);

        // Create a contribution scanner
        ContributionScanner scanner;
        if ("file".equals(contributionURL.getProtocol()) && new File(contributionURL.getFile()).isDirectory()) {
            scanner = new DirectoryContributionScanner();
        } else {
            scanner = new JarContributionScanner();
        }
        
        // Scan the contribution and list the artifacts contained in it
        List<Artifact> artifacts = contribution.getArtifacts();
        boolean contributionMetadata = false;
        for (String artifactURI: scanner.getArtifacts(contributionURL)) {
            URL artifactURL = scanner.getArtifactURL(contributionURL, artifactURI);

            // Add the deployed artifact model to the contribution
            Artifact artifact = this.contributionFactory.createArtifact();
            artifact.setURI(artifactURI);
            artifact.setLocation(artifactURL.toString());
            artifacts.add(artifact);
            modelResolver.addModel(artifact);
            
            // Read each artifact
            Object model = artifactProcessor.read(contributionURL, URI.create(artifactURI), artifactURL);
            if (model != null) {
                artifact.setModel(model);

                // Add the loaded model to the model resolver
                modelResolver.addModel(model);

                // Merge contribution metadata into the contribution model
                if (model instanceof Contribution) {
                    contributionMetadata = true;
                    Contribution c = (Contribution)model;
                    contribution.getImports().addAll(c.getImports());
                    contribution.getExports().addAll(c.getExports());
                    contribution.getDeployables().addAll(c.getDeployables());
                }
            }
        }
        
        // If no sca-contribution.xml file was provided then just consider
        // all composites in the contribution as deployables
        if (!contributionMetadata) {
            for (Artifact artifact: artifacts) {
                if (artifact.getModel() instanceof Composite) {
                    contribution.getDeployables().add((Composite)artifact.getModel());
                }
            }
        }
        
        return contribution;
    }
    
    public void resolve(Contribution contribution, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve all artifact models
        ModelResolver contributionResolver = contribution.getModelResolver();
        for (Artifact artifact : contribution.getArtifacts()) {
            Object model = artifact.getModel();
            if (model != null) {
                try {
                   artifactProcessor.resolve(model, contributionResolver);
                } catch (Exception e) {
                    //FIXME this shouldn't happen
                }
            }
        }
        
        // Resolve the contribution model itself
        artifactProcessor.resolve(contribution, contributionResolver);
    }

    /**
     * FIXME Temporary hack for testing the ClassLoaderModelResolver.
     * 
     * @param modelResolvers
     */
    private static void hackResolvers(ModelResolverExtensionPoint modelResolvers) {
        modelResolvers.getResolver(ClassReference.class);
        try {
            Class<?> loaderResolverClass = Class.forName("org.apache.tuscany.sca.contribution.java.impl.ClassLoaderModelResolver", true, ContributionContentProcessor.class.getClassLoader());
            modelResolvers.addResolver(ClassReference.class, (Class<? extends ModelResolver>)loaderResolverClass);
        } catch (ClassNotFoundException e) {
        }
    }
}
