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
package org.apache.tuscany.sca.contribution.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionMetadataLoaderException;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.util.IOHelper;

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
     * xml factory used to create reader instance to load contribution metadata
     */
    private XMLInputFactory xmlFactory;
    
    /**
     * contribution metadata loader
     */
    private ContributionMetadataLoaderImpl contributionLoader;

    /**
     * Contribution registry This is a registry of processed Contributions indexed by URI
     */
    private Map<String, Contribution> contributionRegistry = new ConcurrentHashMap<String, Contribution>();

    /**
     * Contribution model facotry
     */
    private ContributionFactory contributionFactory;
    
    
    public ContributionServiceImpl(ContributionRepository repository,
                                   PackageProcessor packageProcessor,
                                   URLArtifactProcessor artifactProcessor,
                                   AssemblyFactory assemblyFactory,
                                   ContributionFactory contributionFactory,
                                   XMLInputFactory xmlFactory) {
        super();
        this.contributionRepository = repository;
        this.packageProcessor = packageProcessor;
        this.artifactProcessor = artifactProcessor;
        this.xmlFactory = xmlFactory;

        this.contributionFactory = contributionFactory;
        this.contributionLoader = new ContributionMetadataLoaderImpl(assemblyFactory, contributionFactory);
    }

    public Contribution contribute(String contributionURI, URL sourceURL, ModelResolver modelResolver, boolean storeInRepository) throws ContributionException,
        IOException {
        if (contributionURI == null) {
            throw new IllegalArgumentException("URI for the contribution is null");
        }
        if (sourceURL == null) {
            throw new IllegalArgumentException("Source URL for the contribution is null");
        }
        
        return addContribution(contributionURI, sourceURL, null, modelResolver, storeInRepository);
    }

    public Contribution contribute(String contributionURI, URL sourceURL, InputStream input, ModelResolver modelResolver) 
        throws ContributionException, IOException {
        
        return addContribution(contributionURI, sourceURL, input, modelResolver, true);
    }

    private Contribution initializeContributionMetadata(URL sourceURL, ModelResolver modelResolver) throws ContributionException {
        Contribution contributionMetadata = null;
        URL contributionMetadataURL;
        URL generatedContributionMetadataURL;
        InputStream metadataStream = null;

        URL[] clUrls = {sourceURL};
        URLClassLoader cl = new URLClassLoader(clUrls);

        contributionMetadataURL = cl.getResource(Contribution.SCA_CONTRIBUTION_META);
        generatedContributionMetadataURL = cl.getResource(Contribution.SCA_CONTRIBUTION_GENERATED_META);

        try {
            contributionMetadata = this.contributionFactory.createContribution();
            contributionMetadata.setModelResolver(modelResolver);
            if (contributionMetadataURL != null || generatedContributionMetadataURL != null) {
                URL metadataURL = contributionMetadataURL != null ? contributionMetadataURL
                                                                 : generatedContributionMetadataURL;

                try {
                    metadataStream = metadataURL.openStream();
                    XMLStreamReader xmlReader = this.xmlFactory.createXMLStreamReader(metadataStream);
                    this.contributionLoader.load(contributionMetadata, xmlReader);

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

    public Contribution getContribution(String id) {
        return this.contributionRegistry.get(id);
    }

    public void remove(String contribution) throws ContributionException {
        this.contributionRegistry.remove(contribution);
    }

    public void addDeploymentComposite(Contribution contribution, Composite composite) throws ContributionException {
        DeployedArtifact artifact = this.contributionFactory.createDeployedArtifact();
        artifact.setURI(composite.getURI());
        artifact.setModel(composite);

        contribution.getArtifacts().add(artifact);

        contribution.getDeployables().add(composite);
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
    private Contribution addContribution(String contributionURI,
                                 URL sourceURL,
                                 InputStream contributionStream,
                                 ModelResolver modelResolver,
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

        Contribution contribution = initializeContributionMetadata(locationURL, modelResolver);
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
        this.contributionRegistry.put(contribution.getURI(), contribution);
        
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
                contribution.getModelResolver().addModel(model);
                
                DeployedArtifact artifact = this.contributionFactory.createDeployedArtifact();
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
                this.artifactProcessor.resolve(artifact.getModel(), contribution.getModelResolver());
            }
        }
        
        //resolve deployables from contribution metadata
        List<Composite> resolvedDeployables = new ArrayList<Composite>();
        for (Composite deployableComposite : contribution.getDeployables()) {
            Composite resolvedDeployable = contribution.getModelResolver().resolveModel(Composite.class, deployableComposite);
            resolvedDeployables.add(resolvedDeployable);
        }
        
        contribution.getDeployables().clear();
        contribution.getDeployables().addAll(resolvedDeployables);
    }

}
