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
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ContributionMetadata;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.ExtensibleContributionListener;
import org.apache.tuscany.sca.contribution.service.util.IOHelper;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Service interface that manages artifacts contributed to a Tuscany runtime.
 * 
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
     * Registry of available StAX processors,
     * used for loading contribution metadata in a extensible way
     */
    private StAXArtifactProcessor staxProcessor;

    /**
     * Event listener for contribution operations
     */
    private ExtensibleContributionListener contributionListener;

    /**
     * Registry of available model resolvers
     */

    private ModelResolverExtensionPoint modelResolvers;

    /**
     * Model factory extension point
     */

    private ModelFactoryExtensionPoint modelFactories;

    /**
     * XML factory used to create reader instance to load contribution metadata
     */
    private XMLInputFactory xmlFactory;

    /**
     * Assembly factory
     */
    private AssemblyFactory assemblyFactory;

    /**
     * Contribution model factory
     */
    private ContributionFactory contributionFactory;
    
    
    private ModelResolver policyDefinitionsResolver;

    private List policyDefinitions; 
    
    private Monitor monitor;
    
    private String COMPOSITE_FILE_EXTN = ".composite";    

    public ContributionServiceImpl(ContributionRepository repository,
                                   PackageProcessor packageProcessor,
                                   URLArtifactProcessor documentProcessor,
                                   StAXArtifactProcessor staxProcessor,
                                   ExtensibleContributionListener contributionListener,
                                   ModelResolver policyDefinitionsResolver,
                                   ModelResolverExtensionPoint modelResolvers,
                                   ModelFactoryExtensionPoint modelFactories,
                                   AssemblyFactory assemblyFactory,
                                   ContributionFactory contributionFactory,
                                   XMLInputFactory xmlFactory,
                                   List<SCADefinitions> policyDefinitions,
                                   Monitor monitor) {
        super();
        this.contributionRepository = repository;
        this.packageProcessor = packageProcessor;
        this.artifactProcessor = documentProcessor;
        this.staxProcessor = staxProcessor;
        this.contributionListener = contributionListener;
        this.modelResolvers = modelResolvers;
        this.modelFactories = modelFactories;
        this.xmlFactory = xmlFactory;
        this.assemblyFactory = assemblyFactory;
        this.contributionFactory = contributionFactory;
        this.policyDefinitionsResolver = policyDefinitionsResolver;
        this.policyDefinitions = policyDefinitions;
        this.monitor = monitor;
    }
    
    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-impl-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    public Contribution contribute(String contributionURI, URL sourceURL, boolean storeInRepository)
        throws ContributionException, IOException {
        if (contributionURI == null) {
        	error("ContributionURINull", contributionURI);
            throw new IllegalArgumentException("URI for the contribution is null");
        }
        if (sourceURL == null) {
        	error("SourceURLNull", sourceURL);
            throw new IllegalArgumentException("Source URL for the contribution is null");
        }
        return addContribution(contributionURI, sourceURL, null, null, storeInRepository);
    }

    public Contribution contribute(String contributionURI,
                                   URL sourceURL,
                                   ModelResolver modelResolver,
                                   boolean storeInRepository) throws ContributionException, IOException {
        if (contributionURI == null) {
        	error("ContributionURINull", contributionURI);
            throw new IllegalArgumentException("URI for the contribution is null");
        }
        if (sourceURL == null) {
        	error("SourceURLNull", sourceURL);
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

    public Contribution getContribution(String uri) {
        return this.contributionRepository.getContribution(uri);
    }

    /**
     * Remove a contribution and notify listener that contribution was removed
     */
    public void remove(String uri) throws ContributionException {
        Contribution contribution = contributionRepository.getContribution(uri);
        this.contributionRepository.removeContribution(contribution);
        this.contributionListener.contributionRemoved(this.contributionRepository, contribution);
    }

    /**
     * Add a composite model to the contribution
     */
    public void addDeploymentComposite(Contribution contribution, Composite composite) throws ContributionException {
        Artifact artifact = this.contributionFactory.createArtifact();
        artifact.setURI(composite.getURI());
        artifact.setModel(composite);

        contribution.getArtifacts().add(artifact);

        contribution.getDeployables().add(composite);
    }

    /**
     * Utility/Helper methods for contribution service
     */

    /**
     * Perform read of the contribution metadata loader (sca-contribution.xml and sca-contribution-generated.xml)
     * When the two metadata files are available, the information provided are merged, and the sca-contribution has priorities
     * 
     * @param sourceURL
     * @return Contribution
     * @throws ContributionException
     */
    private Contribution readContributionMetadata(URL sourceURL) throws ContributionException {
        Contribution contributionMetadata = contributionFactory.createContribution();

        ContributionMetadataDocumentProcessor metadataDocumentProcessor =
            new ContributionMetadataDocumentProcessor(modelFactories, staxProcessor, monitor);
        
        final URL[] urls = {sourceURL};
        // Allow access to create classloader. Requires RuntimePermission in security policy.
        URLClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
            public URLClassLoader run() {
                return new URLClassLoader(urls, null);
            }
        });           
        for (String path: new String[]{
                                       Contribution.SCA_CONTRIBUTION_GENERATED_META,
                                       Contribution.SCA_CONTRIBUTION_META}) {
            URL url = cl.getResource(path);
            if (url != null) {
                ContributionMetadata contribution = metadataDocumentProcessor.read(sourceURL, URI.create(path), url);
                contributionMetadata.getImports().addAll(contribution.getImports());
                contributionMetadata.getExports().addAll(contribution.getExports());
                contributionMetadata.getDeployables().addAll(contribution.getDeployables());
            }
        }
        
        // For debugging purposes, write it back to XML
        //        if (contributionMetadata != null) {
        //            try {
        //                ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        //                outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        //                staxProcessor.write(contributionMetadata, outputFactory.createXMLStreamWriter(bos));
        //                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(bos.toByteArray()));
        //                OutputFormat format = new OutputFormat();
        //                format.setIndenting(true);
        //                format.setIndent(2);
        //                XMLSerializer serializer = new XMLSerializer(System.out, format);
        //                serializer.serialize(document);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }

        return contributionMetadata;
    }

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
        	error("ContributionContentNull", contributionStream);
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
            //FIXME Remove this domain resolver, visibility of policy declarations should be handled by
            // the contribution import/export mechanism instead of this domainResolver hack.
            modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories, policyDefinitionsResolver);
        }

        //set contribution initial information
        contribution.setURI(contributionURI);
        contribution.setLocation(locationURL.toString());
        contribution.setModelResolver(modelResolver);
        
        List<URI> contributionArtifacts = null;

        //NOTE: if a contribution is stored on the repository
        //the stream would be consumed at this point
        if (storeInRepository || contributionStream == null) {
            URLConnection connection = sourceURL.openConnection();
            connection.setUseCaches(false);
            // Allow access to open URL stream. Add FilePermission to added to security policy file.
            final URLConnection finalConnection = connection;
            try {
                contributionStream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                    public InputStream run() throws IOException {
                        return finalConnection.getInputStream();
                    }
                });
            } catch (PrivilegedActionException e) {
                throw (IOException)e.getException();
            }
            
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
        try {
        	// Allow access to read system properties. Requires PropertyPermission in security policy.
        	// Any security exceptions are caught and wrapped as ContributionException.
            processReadPhase(contribution, contributionArtifacts);
        } catch ( Exception e ) {
            throw new ContributionException(e);
        }

        //
        this.contributionListener.contributionAdded(this.contributionRepository, contribution);

        // Resolve them
        processResolvePhase(contribution);

        // Add all composites under META-INF/sca-deployables to the
        // list of deployables
        String prefix = Contribution.SCA_CONTRIBUTION_DEPLOYABLES;
        for (Artifact artifact : contribution.getArtifacts()) {
            if (artifact.getModel() instanceof Composite) {
                if (artifact.getURI().startsWith(prefix)) {
                    Composite composite = (Composite)artifact.getModel();
                    if (!contribution.getDeployables().contains(composite)) {
                        contribution.getDeployables().add(composite);
                    }
                }
            }
        }

        // store the contribution on the registry
        this.contributionRepository.addContribution(contribution);

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
        MalformedURLException, XMLStreamException {

        ModelResolver modelResolver = contribution.getModelResolver();
        URL contributionURL = new URL(contribution.getLocation());
        
        List<URI> compositeUris = new ArrayList<URI>();
        
        Object model = null;
        for (URI anArtifactUri : artifacts) {
            if ( anArtifactUri.toString().endsWith(COMPOSITE_FILE_EXTN)) {
                compositeUris.add(anArtifactUri);
            } else {
                URL artifactURL = packageProcessor.getArtifactURL(new URL(contribution.getLocation()), anArtifactUri);

                // Add the deployed artifact model to the resolver
                Artifact artifact = this.contributionFactory.createArtifact();
                artifact.setURI(anArtifactUri.toString());
                artifact.setLocation(artifactURL.toString());
                contribution.getArtifacts().add(artifact);
                modelResolver.addModel(artifact);
                
                model = this.artifactProcessor.read(contributionURL, anArtifactUri, artifactURL);
                
                if (model != null) {
                    artifact.setModel(model);

                    // Add the loaded model to the model resolver
                    modelResolver.addModel(model);
                    
                    // Add policy definitions to the list of policy definitions
                    if (model instanceof SCADefinitions) { 
                        policyDefinitions.add(model);
                        
                        SCADefinitions definitions = (SCADefinitions)model;
                        for (Intent intent : definitions.getPolicyIntents() ) {
                            policyDefinitionsResolver.addModel(intent);
                        }
                        
                        for (PolicySet policySet : definitions.getPolicySets() ) {
                            policyDefinitionsResolver.addModel(policySet);
                        }
                        
                        for (IntentAttachPointType attachPointType : definitions.getBindingTypes() ) {
                            policyDefinitionsResolver.addModel(attachPointType);
                        }
                        
                        for (IntentAttachPointType attachPointType : definitions.getImplementationTypes() ) {
                            policyDefinitionsResolver.addModel(attachPointType);
                        }
                        for (Object binding : definitions.getBindings() ) {
                            policyDefinitionsResolver.addModel(binding);
                        }
                    }
                }
            }
        }
        
        for (URI anArtifactUri : compositeUris) {
            URL artifactURL = packageProcessor.getArtifactURL(new URL(contribution.getLocation()), anArtifactUri);

            // Add the deployed artifact model to the resolver
            Artifact artifact = this.contributionFactory.createArtifact();
            artifact.setURI(anArtifactUri.toString());
            artifact.setLocation(artifactURL.toString());
            contribution.getArtifacts().add(artifact);
            modelResolver.addModel(artifact);
            
            model = this.artifactProcessor.read(contributionURL, anArtifactUri, artifactURL);
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
        List<Artifact> composites = new ArrayList<Artifact>();

        // for each artifact that was processed on the contribution
        for (Artifact artifact : contribution.getArtifacts()) {
            //leave the composites to be resolved at the end
            if (artifact.getURI().endsWith(".composite")) {
                composites.add(artifact);
            } else {
                // resolve the model object
                if (artifact.getModel() != null) {
                    // System.out.println("Processing Resolve Phase : " + artifact.getURI());
                    this.artifactProcessor.resolve(artifact.getModel(), contribution.getModelResolver());
                }
            }
        }

        //process each composite file
        for (Artifact artifact : composites) {
            // resolve the model object
            if (artifact.getModel() != null) {
                this.artifactProcessor.resolve(artifact.getModel(), contribution.getModelResolver());
            }
        }

        //resolve deployables from contribution metadata
        List<Composite> resolvedDeployables = new ArrayList<Composite>();
        for (Composite deployableComposite : contribution.getDeployables()) {
            Composite resolvedDeployable =
                contribution.getModelResolver().resolveModel(Composite.class, deployableComposite);

            resolvedDeployables.add(resolvedDeployable);
        }
        contribution.getDeployables().clear();
        contribution.getDeployables().addAll(resolvedDeployables);
    }
}
