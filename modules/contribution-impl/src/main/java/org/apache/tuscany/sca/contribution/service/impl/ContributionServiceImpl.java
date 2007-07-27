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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.NamespaceExport;
import org.apache.tuscany.sca.contribution.NamespaceImport;
import org.apache.tuscany.sca.contribution.processor.ContributionPostProcessor;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.impl.NamespaceExportModelResolverImpl;
import org.apache.tuscany.sca.contribution.resolver.impl.NamespaceImportAllModelResolverImpl;
import org.apache.tuscany.sca.contribution.resolver.impl.NamespaceImportModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.util.IOHelper;

/**
 * Service interface that manages artifacts contributed to a Tuscany runtime.
 * 
 * @version $Rev$ $Date$
 */
/**
 * 
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
     * Registry of available stax processors,
     * used for loading contribution metadata in a extensible way
     */
    private StAXArtifactProcessor staxProcessor;
    
    /**
     * Registry of available model resolvers
     */
    
    private ModelResolverExtensionPoint modelResolverExtensionPoint;
    
    /**
     * Contribution post processor
     */
    private ContributionPostProcessor postProcessor;

    /**
     * xml factory used to create reader instance to load contribution metadata
     */
    private XMLInputFactory xmlFactory;

    /**
     * Assembly factory
     */
    private AssemblyFactory assemblyFactory;

    /**
     * Contribution model facotry
     */
    private ContributionFactory contributionFactory;


    /**
     * Contribution registry This is a registry of processed Contributions indexed by URI
     */
    private Map<String, Contribution> contributionRegistry = new ConcurrentHashMap<String, Contribution>();
    
    public ContributionServiceImpl(ContributionRepository repository,
                                   PackageProcessor packageProcessor,
                                   URLArtifactProcessor documentProcessor,
                                   StAXArtifactProcessor staxProcessor,
                                   ContributionPostProcessor postProcessor,
                                   ModelResolverExtensionPoint modelResolverExtensionPoint,
                                   AssemblyFactory assemblyFactory,
                                   ContributionFactory contributionFactory,
                                   XMLInputFactory xmlFactory) {
        super();
        this.contributionRepository = repository;
        this.packageProcessor = packageProcessor;
        this.artifactProcessor = documentProcessor;
        this.staxProcessor = staxProcessor;
        this.postProcessor = postProcessor;
        this.modelResolverExtensionPoint = modelResolverExtensionPoint;
        this.xmlFactory = xmlFactory;
        this.assemblyFactory = assemblyFactory;
        this.contributionFactory = contributionFactory;
    }

    public Contribution contribute(String contributionURI, URL sourceURL, boolean storeInRepository)
            throws ContributionException, IOException {
        if (contributionURI == null) {
            throw new IllegalArgumentException("URI for the contribution is null");
        }
        if (sourceURL == null) {
            throw new IllegalArgumentException("Source URL for the contribution is null");
        }
        
        return addContribution(contributionURI, sourceURL, null, null, storeInRepository);
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

    public Contribution contribute(String contributionURI, URL sourceURL, InputStream input)
            throws ContributionException, IOException {

        return addContribution(contributionURI, sourceURL, input, null, true);
    }
    
    public Contribution contribute(String contributionURI, URL sourceURL, InputStream input, ModelResolver modelResolver) 
        throws ContributionException, IOException {
        
        return addContribution(contributionURI, sourceURL, input, modelResolver, true);
    }

    /**
     * Perform read of the contribution metada loader (sca-contribution.xml and sca-contribution-generated.xml)
     * When the two metadata files are available, the information provided are merged, and the sca-contribution has priorities
     * 
     * @param sourceURL
     * @return Contribution
     * @throws ContributionException
     */
    private Contribution readContributionMetadata(URL sourceURL) throws ContributionException {
        Contribution contributionMetadata = null;

        URL[] clUrls = {sourceURL};
        URLClassLoader cl = new URLClassLoader(clUrls, null);
        
        
        ContributionMetadataDocumentProcessor metadataDocumentProcessor = 
            new ContributionMetadataDocumentProcessorImpl(cl, this.staxProcessor, this.assemblyFactory, this.contributionFactory, this.xmlFactory);
        contributionMetadata = contributionFactory.createContribution();
        try {
            metadataDocumentProcessor.read(contributionMetadata);
        } catch (XMLStreamException e) {
            throw new InvalidContributionMetadataException("Invalid contribution metadata for contribution.");

        }
        
        return contributionMetadata;
    }

    public Contribution getContribution(String id) {
        return this.contributionRegistry.get(id);
    }

    public void remove(String contribution) throws ContributionException {
        this.contributionRegistry.remove(contribution);
        //FIXME remove references from contributionByExportedNamespace
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
            throw new IllegalArgumentException("The content of the contribution is null.");
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

        //initialize contribution based on it's metadata if available
        Contribution contribution = readContributionMetadata(locationURL);
        
        // Create contribution model resolver
        if (modelResolver == null) {
            modelResolver = new ExtensibleModelResolver(contribution, modelResolverExtensionPoint);
        }
        
        //set contribution initial information
        contribution.setURI(contributionURI.toString());
        contribution.setLocation(locationURL.toString());
        contribution.setModelResolver(modelResolver);
        
        //TODO Move the following to the resolve method of the ArtifactProcessor
        // responsible for handling <imports> and <exports>
        
        // Initialize the contribution exports
        for (Export export: contribution.getExports()) {
            if (export instanceof NamespaceExport) {
                export.setModelResolver(new NamespaceExportModelResolverImpl((NamespaceExport)export, modelResolver));
            }
        }
        
        // Initialize the contribution imports
        for (Import import_: contribution.getImports()) {
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                
                // Find a matching contribution
                if (namespaceImport.getLocation() != null) {
                    Contribution targetContribution = contributionRegistry.get(namespaceImport.getLocation());
                    if (targetContribution != null) {
                    
                        // Find a matching contribution export
                        for (Export export: targetContribution.getExports()) {
                            if (export instanceof NamespaceExport) {
                                NamespaceExport namespaceExport = (NamespaceExport)export;
                                if (namespaceImport.getNamespace().equals(namespaceExport.getNamespace())) {
                                    namespaceImport.setModelResolver(new NamespaceImportModelResolverImpl(namespaceImport, namespaceExport.getModelResolver()));
                                    break;
                                }
                            }
                        }
                    }
                } else {
                
                    // Use a resolver that will consider all contributions
                    namespaceImport.setModelResolver(new NamespaceImportAllModelResolverImpl(namespaceImport, contributionRegistry.values()));
                    
                }
            }
        }

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

        // Read all artifacts in the contribution
        processReadPhase(contribution, contributionArtifacts);
        
        // Resolve them
        processResolvePhase(contribution);
        
        // Add all composites under META-INF/sca-deployables to the
        // list of deployables
        String prefix = Contribution.SCA_CONTRIBUTION_DEPLOYABLES;
        for (DeployedArtifact artifact : contribution.getArtifacts()) {
            if (artifact.getModel() instanceof Composite) {
                if (artifact.getURI().startsWith(prefix)) {
                    Composite composite = (Composite)artifact.getModel();
                    if (!contribution.getDeployables().contains(composite)) {
                        contribution.getDeployables().add(composite);
                    }
                }
            }
        }
        
        //post process contribution
        this.postProcessor.visit(contribution);
        
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
        
        ModelResolver modelResolver = contribution.getModelResolver();
        URL contributionURL = new URL(contribution.getLocation()); 
        for (URI a : artifacts) {
            URL artifactURL = packageProcessor.getArtifactURL(new URL(contribution.getLocation()), a);
            
            // Add the deployed artifact model to the resolver
            DeployedArtifact artifact = this.contributionFactory.createDeployedArtifact();
            artifact.setURI(a.toString());
            artifact.setLocation(artifactURL.toString());
            contribution.getArtifacts().add(artifact);
            modelResolver.addModel(artifact);

            // Let the artifact processor read the artifact into a model
            Object model = this.artifactProcessor.read(contributionURL, a, artifactURL);
            if (model != null) {
                artifact.setModel(model);
                
                // Add the loaded model to the model resolver
                modelResolver.addModel(model);
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

                //processResolveImportsPhase(contribution, artifact);                
            }
        }
        
        //resolve deployables from contribution metadata
        List<Composite> resolvedDeployables = new ArrayList<Composite>();
        for (Composite deployableComposite : contribution.getDeployables()) {
            Composite resolvedDeployable = contribution.getModelResolver().resolveModel(Composite.class, deployableComposite);
            
            /*
            if (resolvedDeployable.isUnresolved()) {
                resolvedDeployable = processResolveImportsPhase(contribution, resolvedDeployable);
            }
            */
            
            resolvedDeployables.add(resolvedDeployable);
        }
        contribution.getDeployables().clear();
        contribution.getDeployables().addAll(resolvedDeployables);
    }

    /*
    @SuppressWarnings("unchecked")
    private Object processResolveImportsPhase(Contribution contribution, DeployedArtifact artifact) throws ContributionException {
        for(ContributionImport contributionImport : contribution.getImports()) {
            String importedContributionURI = contributionImport.getLocation();
            if (importedContributionURI != null && importedContributionURI.length() > 0) {
                //location provided (contribution uri)
                Contribution importedContribution = this.getContribution(importedContributionURI);
                if (importedContribution != null) {
                    this.artifactProcessor.resolve(artifact.getModel(), importedContribution.getModelResolver());
                }
            } else {
                //look into all the contributions that match exported uri
                for(Contribution importedContribution : this.contributionByExportedNamespace.get(contributionImport.getNamespace())) {
                    this.artifactProcessor.resolve(artifact.getModel(), importedContribution.getModelResolver());
                }
            }
            

        }
        
        return artifact.getModel();
    }
    
    @SuppressWarnings("unchecked")
    private Composite processResolveImportsPhase(Contribution contribution, Composite deployableComposite) throws ContributionException {
        Composite resolvedDeployable = deployableComposite;
        
        for(ContributionImport contributionImport : contribution.getImports()) {
            String importedContributionURI = contributionImport.getLocation();
            if (importedContributionURI != null && importedContributionURI.length() > 0) {
                //location provided (contribution uri)
                Contribution importedContribution = this.getContribution(importedContributionURI);
                if (importedContribution != null) {
                    resolvedDeployable = importedContribution.getModelResolver().resolveModel(Composite.class, deployableComposite);
                }
            } else {
                //look into all the contributions that match exported uri
                for(Contribution importedContribution : this.contributionByExportedNamespace.get(contributionImport.getNamespace())) {
                    Composite resolvingDeployable = importedContribution.getModelResolver().resolveModel(Composite.class, deployableComposite);
                    if (resolvingDeployable != null && resolvingDeployable.isUnresolved() == false) {
                        resolvedDeployable = resolvingDeployable;
                        break;
                    }
                }
            }
        }
        
        return resolvedDeployable;
    } 
    */   
}
