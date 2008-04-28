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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.tuscany.sca.assembly.Composite;
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
 * URLArtifactProcessor that handles contribution files and returns a contribution
 * info model.
 * 
 * @version $Rev$ $Date$
 */
public class ContributionInfoProcessor implements URLArtifactProcessor<Contribution>{
    private ContributionFactory contributionFactory;
    private ModelResolverExtensionPoint modelResolvers;
    private ModelFactoryExtensionPoint modelFactories;
    private URLArtifactProcessor<Object> artifactProcessor;

    public ContributionInfoProcessor(ModelFactoryExtensionPoint modelFactories, ModelResolverExtensionPoint modelResolvers, URLArtifactProcessor<Object> artifactProcessor) {
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
        
        // Read generated and user sca-contribution.xml files
        boolean contributionMetadata = false;
        for (String path: new String[]{
                                       Contribution.SCA_CONTRIBUTION_GENERATED_META,
                                       Contribution.SCA_CONTRIBUTION_META}) {
            URL url = scanner.getArtifactURL(contributionURL, path);
            try {
                // Check if the file actually exists before trying to read it
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                InputStream is = connection.getInputStream();
                is.close();
            } catch (IOException e) {
                continue;
            }
            contributionMetadata = true;
            
            // Read the sca-contribution.xml file
            Contribution c = (Contribution)artifactProcessor.read(contributionURL, URI.create(path), url);
            contribution.getImports().addAll(c.getImports());
            contribution.getExports().addAll(c.getExports());
            contribution.getDeployables().addAll(c.getDeployables());
        }
        
        // If no sca-contribution.xml file was provided then just consider
        // all composites in the contribution as deployables
        if (!contributionMetadata) {
            List<String> artifactURIs;
            try {
                artifactURIs = scanner.getArtifacts(contributionURL);
            } catch (ContributionReadException e) {
                artifactURIs = null;
            }
            if (artifactURIs != null) {
                for (String artifactURI: artifactURIs) {
                    if (!artifactURI.endsWith(".composite")) {
                        continue;
                    }
                    URL artifactURL = scanner.getArtifactURL(contributionURL, artifactURI);
    
                    // Read each artifact
                    Object model = artifactProcessor.read(contributionURL, URI.create(artifactURI), artifactURL);
                    if (model instanceof Composite) {
                        contribution.getDeployables().add((Composite)model);
                    }
                }
            }
        }
        
        // Resolve imports and exports right away
        try {
            for (Export export: contribution.getExports()) {
                artifactProcessor.resolve(export, modelResolver);
            }
            for (Import import_: contribution.getImports()) {
                artifactProcessor.resolve(import_, modelResolver);
            }
        } catch (ContributionResolveException e) {
            throw new ContributionReadException(e);
        }
        
        return contribution;
    }
    
    public void resolve(Contribution contribution, ModelResolver resolver) throws ContributionResolveException {
        artifactProcessor.resolve(contribution, contribution.getModelResolver());
    }

    /**
     * FIXME Temporary hack for testing the ClassLoaderModelResolver.
     * 
     * @param modelResolvers
     */
    private static void hackResolvers(ModelResolverExtensionPoint modelResolvers) {
        Class<?> resolverClass = modelResolvers.getResolver(ClassReference.class);
        if (resolverClass==null || !resolverClass.getName().equals("org.apache.tuscany.sca.contribution.java.impl.ClassLoaderModelResolver")) {
            try {
                Class<?> loaderResolverClass = Class.forName("org.apache.tuscany.sca.contribution.java.impl.ClassLoaderModelResolver", true, ContributionContentProcessor.class.getClassLoader());
                modelResolvers.addResolver(ClassReference.class, (Class<? extends ModelResolver>)loaderResolverClass);
            } catch (ClassNotFoundException e) {
            }
        }
    }
}
