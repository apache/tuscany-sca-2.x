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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.DeployedArtifact;
import org.apache.tuscany.contribution.service.ContributionException;
import org.apache.tuscany.contribution.service.ContributionMetadataLoaderException;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.processor.ContributionPackageProcessor;
import org.apache.tuscany.contribution.service.processor.URLArtifactProcessor;
import org.apache.tuscany.contribution.service.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.service.util.IOHelper;

/**
 * @version $Rev$ $Date$
 */
public class ContributionServiceImpl implements ContributionService {

    /**
     * Repository where contributions are stored. Usually set by injection.
     */
    protected ContributionRepository contributionRepository;

    /**
     * Registry of available package processors.
     */
    protected ContributionPackageProcessor packageProcessor;

    /**
     * Registry of available artifact processors
     */

    protected URLArtifactProcessor artifactProcessor;

    /**
     * xml factory used to create reader instance to load contribution metadata
     */
    protected XMLInputFactory xmlFactory;
    /**
     * contribution metadata loader
     */
    protected ContributionMetadataLoaderImpl contributionLoader;

    /**
     * Contribution registry This is a registry of processed Contributios index
     * by URI
     */
    protected Map<URI, Contribution> contributionRegistry = new HashMap<URI, Contribution>();

    protected ArtifactResolver artifactResolver;

    public ContributionServiceImpl(ContributionRepository repository,
                                   ContributionPackageProcessor packageProcessor,
                                   URLArtifactProcessor artifactProcessor,
                                   ArtifactResolver artifactResolver) {
        super();
        this.contributionRepository = repository;
        this.packageProcessor = packageProcessor;
        this.artifactProcessor = artifactProcessor;
        this.artifactResolver = artifactResolver;

        this.xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
        this.contributionLoader = new ContributionMetadataLoaderImpl();
    }

    public void contribute(URI contributionURI, URL sourceURL, boolean storeInRepository) throws ContributionException,
        IOException {
        if (contributionURI == null) {
            throw new IllegalArgumentException("URI for the contribution is null");
        }
        if (sourceURL == null) {
            throw new IllegalArgumentException("Source URL for the contribution is null");
        }

        addContribution(contributionURI, sourceURL, null, storeInRepository);
    }

    public void contribute(URI contributionURI, InputStream input) throws ContributionException, IOException {
        addContribution(contributionURI, null, input, true);
    }

    private Contribution initializeContributionMetadata(URL sourceURL) throws ContributionException {
        Contribution contributionMetadata = null;
        URL contributionMetadataURL;
        URL generatedContributionMetadataURL;
        InputStream metadataStream = null;

        URL[] clUrls = {sourceURL};
        URLClassLoader cl = new URLClassLoader(clUrls, getClass().getClassLoader());

        contributionMetadataURL = cl.getResource(Contribution.SCA_CONTRIBUTION_META);
        generatedContributionMetadataURL = cl.getResource(Contribution.SCA_CONTRIBUTION_GENERATED_META);

        try {
            if (contributionMetadataURL == null && generatedContributionMetadataURL == null) {
                contributionMetadata = new Contribution();
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
            contributionMetadata = new Contribution();
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

    public void addDeploymentComposite(URI contribution, Object composite) {
        /*
         * CompositeComponentType model = (CompositeComponentType)composite; URI
         * compositeURI = contribution.resolve(model.getName() + ".composite");
         * DeployedArtifact artifact = new DeployedArtifact(compositeURI); //
         * FIXME: the namespace should be from the CompositeComponentType model
         * artifact.addModelObject(composite.getClass(), null, composite);
         * Contribution contributionObject =
         * (Contribution)getContribution(contribution);
         * contributionObject.addArtifact(artifact);
         */
    }

    /*
     * public <T> T resolve(URI contribution, Class<T> definitionType, String
     * namespace, String name) { Contribution contributionObject =
     * getContribution(contribution); return
     * resolverRegistry.resolve(contributionObject, definitionType, namespace,
     * name, null); } public URL resolve(URI contribution, String namespace, URI
     * uri, URI baseURI) { Contribution contributionObject =
     * getContribution(contribution); return
     * resolverRegistry.resolve(contributionObject, namespace, uri.toString(),
     * baseURI.toString()); }
     */

    public <M> M resolve(Class modelClass,
                         Class<M> elementClass,
                         Object modelKey,
                         Object elementKey,
                         Map<String, Object> attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     */

    /**
     * Note:
     * 
     * @param contributionURI ContributionID
     * @param sourceURL contribution location
     * @param contributionStream contribution content
     * @param storeInRepository flag if we store the contribution into the
     *            repository or not
     * @throws IOException
     * @throws DeploymentException
     */
    private void addContribution(URI contributionURI,
                                 URL sourceURL,
                                 InputStream contributionStream,
                                 boolean storeInRepository) throws IOException, ContributionException {
        if (contributionStream == null && sourceURL == null) {
            throw new IllegalArgumentException("The content of the contribution is null");
        }

        // store the contribution in the contribution repository
        URL locationURL = sourceURL;
        if (contributionRepository != null && storeInRepository) {
            if (sourceURL != null) {
                locationURL = contributionRepository.store(contributionURI, sourceURL);
            } else {
                locationURL = contributionRepository.store(contributionURI, contributionStream);
            }
        }

        Contribution contribution = initializeContributionMetadata(locationURL);
        contribution.setURI(contributionURI);
        contribution.setLocation(locationURL);

        List<URI> contributionArtifacts = null;

        if (contributionStream == null) {
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
        processOptimizationPhase(contribution);
        
        // store the contribution on the registry
        this.contributionRegistry.put(contribution.getUri(), contribution);
    }

    private void processReadPhase(Contribution contribution, List<URI> artifacts) throws ContributionException,
        MalformedURLException {
        for (URI a : artifacts) {
            URL artifactURL = packageProcessor.getArtifactURL(contribution.getLocation(), a);
            Object model = this.artifactProcessor.read(artifactURL);
            
            if (model != null) {
                artifactResolver.add(model);
                
                URI artifactURI = contribution.getUri().resolve(a);
                DeployedArtifact artifact = new DeployedArtifact(artifactURI);
                artifact.setLocation(artifactURL);
                artifact.setModelObject(model);
                contribution.addArtifact(artifact);
            }
        }
    }

    private void processResolvePhase(Contribution contribution) throws ContributionException {
        // for each artifact that was processed on the contribution
        for (DeployedArtifact artifact : contribution.getArtifacts().values()) {
            // resolve the model object
            if (artifact.getModelObject() != null) {
                this.artifactProcessor.resolve(artifact.getModelObject(), artifactResolver);
            }
        }

    }

    private void processOptimizationPhase(Contribution contribution) throws ContributionException {
        // for each artifact that was processed on the contribution
        for (DeployedArtifact artifact : contribution.getArtifacts().values()) {
            // resolve the model object
           this.artifactProcessor.wire(artifact.getModelObject());
        }

    }

}
