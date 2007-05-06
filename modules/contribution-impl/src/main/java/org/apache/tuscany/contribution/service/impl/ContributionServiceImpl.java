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
package org.apache.tuscany.contribution.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.ContributionFactory;
import org.apache.tuscany.contribution.DeployedArtifact;
import org.apache.tuscany.contribution.processor.PackageProcessor;
import org.apache.tuscany.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.contribution.service.ContributionMetadataLoaderException;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.util.IOHelper;

/**
 * @version $Rev$ $Date$
 */
public class ContributionServiceImpl implements ContributionService {

    /**
     * Repository where contributions are stored. Usually set by injection.
     */
    private ContributionRepository contributionRepository;

    /**
     * Registry of available package processors.
     */
    private PackageProcessor packageProcessor;

    /**
     * Registry of available artifact processors
     */

    private URLArtifactProcessor artifactProcessor;

    /**
     * Artifact Resolver
     */
    private ArtifactResolver artifactResolver;
    
    /**
     * xml factory used to create reader instance to load contribution metadata
     */
    private XMLInputFactory xmlFactory;
    
    /**
     * contribution metadata loader
     */
    private ContributionMetadataLoaderImpl contributionLoader;

    /**
     * Contribution registry This is a registry of processed Contributios index by URI
     */
    private Map<URI, Contribution> contributionRegistry = new HashMap<URI, Contribution>();

    /**
     * Contribution model facotry
     */
    private ContributionFactory contributionFactory;
    
    
    public ContributionServiceImpl(ContributionRepository repository,
                                   PackageProcessor packageProcessor,
                                   URLArtifactProcessor artifactProcessor,
                                   ArtifactResolver artifactResolver,
                                   AssemblyFactory assemblyFactory,
                                   ContributionFactory contributionFactory,
                                   XMLInputFactory xmlFactory) {
        super();
        this.contributionRepository = repository;
        this.packageProcessor = packageProcessor;
        this.artifactProcessor = artifactProcessor;
        this.artifactResolver = artifactResolver;
        this.xmlFactory = xmlFactory;

        this.contributionFactory = contributionFactory;
        this.contributionLoader = new ContributionMetadataLoaderImpl(assemblyFactory, contributionFactory);
    }

    public Contribution contribute(URI contributionURI, URL sourceURL, boolean storeInRepository) throws ContributionException,
        IOException {
        if (contributionURI == null) {
            throw new IllegalArgumentException("URI for the contribution is null");
        }
        if (sourceURL == null) {
            throw new IllegalArgumentException("Source URL for the contribution is null");
        }
        return addContribution(contributionURI, sourceURL, null, storeInRepository);
    }

    public Contribution contribute(URI contributionURI, URL sourceURL, InputStream input) 
        throws ContributionException, IOException {
        return addContribution(contributionURI, sourceURL, input, true);
    }

    private Contribution initializeContributionMetadata(URL sourceURL) throws ContributionException {
        Contribution contributionMetadata = null;
        URL contributionMetadataURL;
        URL generatedContributionMetadataURL;
        InputStream metadataStream = null;

        URL[] clUrls = {sourceURL};
        URLClassLoader cl = new URLClassLoader(clUrls);

        contributionMetadataURL = cl.getResource(Contribution.SCA_CONTRIBUTION_META);
        generatedContributionMetadataURL = cl.getResource(Contribution.SCA_CONTRIBUTION_GENERATED_META);

        try {
            if (contributionMetadataURL == null && generatedContributionMetadataURL == null) {
                contributionMetadata = this.contributionFactory.createContribution();
            } else {
                URL metadataURL = contributionMetadataURL != null ? contributionMetadataURL
                                                                 : generatedContributionMetadataURL;

                try {
                    metadataStream = metadataURL.openStream();
                    XMLStreamReader xmlReader = this.xmlFactory.createXMLStreamReader(metadataStream);
                    contributionMetadata = this.contributionLoader.load(xmlReader);

                } catch (IOException ioe) {
                    throw new InvalidContributionMetadataException(ioe.getMessage(), metadataURL.toExternalForm(), ioe);
                } catch (XMLStreamException xmle) {
                    throw new InvalidContributionMetadataException(xmle.getMessage(), metadataURL.toExternalForm(),
                                                                   xmle);
                } catch (ContributionMetadataLoaderException le) {
                    throw new InvalidContributionMetadataException(le.getMessage(), metadataURL.toExternalForm(), le);
                }
            }
        } finally {
            IOHelper.closeQuietly(metadataStream);
            metadataStream = null;
        }

        if (contributionMetadata == null) {
            contributionMetadata = this.contributionFactory.createContribution();
        }

        return contributionMetadata;

    }

    public Contribution getContribution(URI id) {
        return this.contributionRegistry.get(id);
    }

    public void remove(URI contribution) throws ContributionException {
        // remove from repository
        this.contributionRegistry.remove(contribution);
    }

    public void addDeploymentComposite(URI contributionURI, Composite composite) throws ContributionException {
        Contribution contribution = getContribution(contributionURI);

        if (contribution == null) {
            throw new InvalidContributionURIException("Invalid/Inexistent contribution uri '" 
                    + contributionURI.toString());
        }

        URI compositeURI = URI.create(composite.getURI());
        DeployedArtifact artifact = this.contributionFactory.createDeplyedArtifact();
        artifact.setURI(compositeURI.toString());
        artifact.setModel(composite);

        contribution.getArtifacts().add(artifact);

        contribution.getDeployables().add(composite);
    }

    public <M> M resolve(Class modelClass,
                         Class<M> elementClass,
                         Object modelKey,
                         Object elementKey,
                         Map<String, Object> attributes) {
        return null;
    }

    /**
     * Utility/Helper methods for contribution service
     */

    /**
     * Note:
     * 
     * @param contributionURI ContributionID
     * @param sourceURL contribution location
     * @param contributionStream contribution content
     * @param storeInRepository flag if we store the contribution into the
     *            repository or not
     * @return the contribution model representing the contribution 
     * @throws IOException
     * @throws DeploymentException
     */
    private Contribution addContribution(URI contributionURI,
                                 URL sourceURL,
                                 InputStream contributionStream,
                                 boolean storeInRepository) throws IOException, ContributionException {
        
        if (contributionStream == null && sourceURL == null) {
            throw new IllegalArgumentException("The content of the contribution is null");
        }

        // store the contribution in the contribution repository
        URL locationURL = sourceURL;
        if (contributionRepository != null && storeInRepository) {
            if (contributionStream == null) {
                locationURL = contributionRepository.store(contributionURI, sourceURL);
            } else {
                locationURL = contributionRepository.store(contributionURI, sourceURL, contributionStream);
            }
        }

        Contribution contribution = initializeContributionMetadata(locationURL);
        contribution.setURI(contributionURI.toString());
        contribution.setLocation(locationURL.toString());

        List<URI> contributionArtifacts = null;

        //NOTE: if a contribution is stored on the repository
        //the stream would be consumed at this point
        if (storeInRepository || contributionStream == null) {
            contributionStream = sourceURL.openStream();
            try {
                // process the contribution
                contributionArtifacts = this.packageProcessor.getArtifacts(locationURL, contributionStream);
            } finally {
                IOHelper.closeQuietly(contributionStream);
                contributionStream = null;
            }
        } else {
            // process the contribution
            contributionArtifacts = this.packageProcessor.getArtifacts(locationURL, contributionStream);
        }

        processReadPhase(contribution, contributionArtifacts);
        processResolvePhase(contribution);
        
        // store the contribution on the registry
        this.contributionRegistry.put(URI.create(contribution.getURI()), contribution);
        
        return contribution;
    }

    /**
     * This utility method process each artifact and delegates to proper 
     * artifactProcessor to read the model and generate the in-memory representation
     *  
     * @param contribution
     * @param artifacts
     * @throws ContributionException
     * @throws MalformedURLException
     */
    private void processReadPhase(Contribution contribution, List<URI> artifacts) throws ContributionException,
        MalformedURLException {
        URL contributionURL = new URL(contribution.getLocation()); 
        for (URI a : artifacts) {
            URL artifactURL = packageProcessor.getArtifactURL(new URL(contribution.getLocation()), a);
            Object model = this.artifactProcessor.read(contributionURL, a, artifactURL);
            
            if (model != null) {
                artifactResolver.add(model);
                
                DeployedArtifact artifact = this.contributionFactory.createDeplyedArtifact();
                artifact.setURI(a.toString());
                artifact.setLocation(artifactURL.toString());
                artifact.setModel(model);
                contribution.getArtifacts().add(artifact);
            }
        }
    }

    /**
     * This utility method process each artifact and delegates to proper 
     * artifactProcessor to resolve the model references
     * 
     * @param contribution
     * @throws ContributionException
     */
    @SuppressWarnings("unchecked")
    private void processResolvePhase(Contribution contribution) throws ContributionException {
        // for each artifact that was processed on the contribution
        for (DeployedArtifact artifact : contribution.getArtifacts()) {
            // resolve the model object
            if (artifact.getModel() != null) {
                this.artifactProcessor.resolve(artifact.getModel(), artifactResolver);
            }
        }
        
        //resolve deployables from contribution metadata
        List<Composite> resolvedDeployables = new ArrayList<Composite>();
        for (Composite deployableComposite : contribution.getDeployables()) {
            Composite resolvedDeployable = artifactResolver.resolve(Composite.class, deployableComposite);
            resolvedDeployables.add(resolvedDeployable);
        }
        
        contribution.getDeployables().clear();
        contribution.getDeployables().addAll(resolvedDeployables);
    }

}
