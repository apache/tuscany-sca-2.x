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

import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.PackageProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionException;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionRepository;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.ExtensibleContributionListener;
import org.apache.tuscany.sca.contribution.service.util.IOHelper;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.definitions.SCADefinitions;
import org.apache.tuscany.sca.policy.PolicySet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
     * xml factory used to create reader instance to load contribution metadata
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
    
    
    private List<SCADefinitions> contributionSCADefinitions = new ArrayList<SCADefinitions>(); 

    private ModelResolver domainResolver;
    
    private Map<QName, PolicySet> policySetMap = new Hashtable<QName, PolicySet>();
    
    private SCADefinitions systemSCADefinitions = null;
    
    private String COMPOSITE_FILE_EXTN = ".composite";
    private String POLICYSET_PREFIX = "tp_";
    private String APPLICABLE_POLICYSET_ATTR_NS = "http://tuscany.apache.org/xmlns/sca/1.0"; 
    private String APPLICABLE_POLICYSET_ATTR = "applicablePolicySets"; 
    private String POLICY_SETS_ATTR = "policySets"; 
    private String APPLICABLE_POLICYSET_ATTR_PREFIX = "tuscany";
    private String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";

    public ContributionServiceImpl(ContributionRepository repository,
                                   PackageProcessor packageProcessor,
                                   URLArtifactProcessor documentProcessor,
                                   StAXArtifactProcessor staxProcessor,
                                   ExtensibleContributionListener contributionListener,
                                   ModelResolver domainResolver,
                                   ModelResolverExtensionPoint modelResolvers,
                                   ModelFactoryExtensionPoint modelFactories,
                                   AssemblyFactory assemblyFactory,
                                   ContributionFactory contributionFactory,
                                   XMLInputFactory xmlFactory,
                                   SCADefinitions scaDefinitions) {
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
        this.domainResolver = domainResolver;
        this.systemSCADefinitions = scaDefinitions;
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

    public Contribution contribute(String contributionURI,
                                   URL sourceURL,
                                   ModelResolver modelResolver,
                                   boolean storeInRepository) throws ContributionException, IOException {
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
     * Perform read of the contribution metada loader (sca-contribution.xml and sca-contribution-generated.xml)
     * When the two metadata files are available, the information provided are merged, and the sca-contribution has priorities
     * 
     * @param sourceURL
     * @return Contribution
     * @throws ContributionException
     */
    private Contribution readContributionMetadata(URL sourceURL) throws ContributionException {
        Contribution contributionMetadata = contributionFactory.createContribution();

        ContributionMetadataDocumentProcessor metadataDocumentProcessor =
            new ContributionMetadataDocumentProcessor(staxProcessor, xmlFactory);
        
        URL[] urls = {sourceURL};
        URLClassLoader cl = new URLClassLoader(urls, null);
        
        for (String path: new String[]{
                                       Contribution.SCA_CONTRIBUTION_GENERATED_META,
                                       Contribution.SCA_CONTRIBUTION_META}) {
            URL url = cl.getResource(path);
            if (url != null) {
                Contribution contribution = metadataDocumentProcessor.read(sourceURL, URI.create(path), url);
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
            modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories, domainResolver);
        }

        //set contribution initial information
        contribution.setURI(contributionURI.toString());
        contribution.setLocation(locationURL.toString());
        contribution.setModelResolver(modelResolver);
        
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
        
        //at this point the systemSCADefinitions will be updated by the runtime with all the 
        //contents of definitions.xml in the META-INF/services subdirectory.  So first update the 
        //policysetMap for the systemSCADefinitions
        updatePolicySetMap(systemSCADefinitions);

        // Read all artifacts in the contribution
        try {
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
                    
                    if ( model instanceof SCADefinitions ) {
                        contributionSCADefinitions.add((SCADefinitions)model);
                        updatePolicySetMap((SCADefinitions)model);
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
            
            byte[] transformedArtifactContent = addApplicablePolicySets(artifactURL);
            artifact.setContents(transformedArtifactContent);
            XMLStreamReader reader = XMLInputFactory.newInstance().
                                createXMLStreamReader(new ByteArrayInputStream(transformedArtifactContent));
            reader.nextTag();
            Composite composite = (Composite)staxProcessor.read(reader);
            if (composite != null) {
                composite.setURI(anArtifactUri.toString());

                artifact.setModel(composite);
                // Add the loaded model to the model resolver
                modelResolver.addModel(composite);
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
                    this.artifactProcessor.resolve(artifact.getModel(), contribution.getModelResolver());
                }
            }
        }

        //process each composite file
        for (Artifact artifact : composites) {
            // resolve the model object
            if (artifact.getModel() != null) {
                // System.out.println("Processing Resolve Phase : " + artifact.getURI());
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

    public List<SCADefinitions> getContributionSCADefinitions() {
        return contributionSCADefinitions;
    }
    
    private void updatePolicySetMap(SCADefinitions scaDefns) {
        for ( PolicySet policySet : scaDefns.getPolicySets() ) {
            policySetMap.put(policySet.getName(), policySet);
        }
    }
    
    private byte[] addApplicablePolicySets(Document doc, Collection<PolicySet> policySets) throws 
                                                                XPathExpressionException,
                                                                TransformerConfigurationException,
                                                                TransformerException  {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath path = xpathFactory.newXPath();
        path.setNamespaceContext(new DOMNamespaceContext(doc));
        int prefixCount = 1;
        
        for ( PolicySet policySet : policySets ) {
            if ( policySet.getAppliesTo() != null ) {
                addApplicablePolicySets(policySet, path, doc, prefixCount);
            }
            
            if ( policySet.getAlwaysAppliesTo() != null ) {
                addAlwaysApplicablePolicySets(policySet, path, doc, prefixCount);
            }
        }
        
        StringWriter sw = new StringWriter();
        Source domSource = new DOMSource(doc);
        Result finalResult = new StreamResult(sw);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        //transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(domSource, finalResult);
        return sw.toString().getBytes();
    }
    
    private void addAlwaysApplicablePolicySets(PolicySet policySet, XPath path, Document doc, int prefixCount) throws XPathExpressionException {
        XPathExpression expression = path.compile(policySet.getAlwaysAppliesTo());
        NodeList result = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
        
        if ( result != null ) {
            for ( int counter = 0 ; counter < result.getLength() ; ++counter ) {
                Node aResultNode = result.item(counter);
            
                String alwaysApplicablePolicySets = null;
                String policySetPrefix = POLICYSET_PREFIX + prefixCount++;
                String policySetsAttrPrefix = "sca";
                
                policySetPrefix = declareNamespace((Element)aResultNode, policySetPrefix, policySet.getName().getNamespaceURI());
                policySetsAttrPrefix = declareNamespace((Element)aResultNode, policySetsAttrPrefix, SCA10_NS);
                if ( aResultNode.getAttributes().getNamedItem( POLICY_SETS_ATTR) != null ) {
                    alwaysApplicablePolicySets =
                        aResultNode.getAttributes().getNamedItem(POLICY_SETS_ATTR).getNodeValue();
                }
                
                if ( alwaysApplicablePolicySets != null && alwaysApplicablePolicySets.length() > 0 ) {
                    alwaysApplicablePolicySets = alwaysApplicablePolicySets + " " + policySetPrefix + ":" + policySet.getName().getLocalPart();
                } else {
                    alwaysApplicablePolicySets = policySetPrefix + ":" + policySet.getName().getLocalPart();
                }
                
                ((Element)aResultNode).setAttribute(POLICY_SETS_ATTR, 
                                                      alwaysApplicablePolicySets);
            }
        }
    }
    
    private void addApplicablePolicySets(PolicySet policySet, XPath path, Document doc, int prefixCount) throws XPathExpressionException {
        XPathExpression expression = path.compile(policySet.getAppliesTo());
        NodeList result = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
        
        if ( result != null ) {
            for ( int counter = 0 ; counter < result.getLength() ; ++counter ) {
                Node aResultNode = result.item(counter);
            
                String applicablePolicySets = null;
                String policySetPrefix = POLICYSET_PREFIX + prefixCount++;
                String appPolicyAttrPrefix = APPLICABLE_POLICYSET_ATTR_PREFIX;
                
                policySetPrefix = declareNamespace((Element)aResultNode, policySetPrefix, policySet.getName().getNamespaceURI());
                appPolicyAttrPrefix = declareNamespace((Element)aResultNode, appPolicyAttrPrefix, APPLICABLE_POLICYSET_ATTR_NS);
                if ( aResultNode.getAttributes().getNamedItemNS(APPLICABLE_POLICYSET_ATTR_NS, APPLICABLE_POLICYSET_ATTR) != null ) {
                    applicablePolicySets =
                        aResultNode.getAttributes().getNamedItemNS(APPLICABLE_POLICYSET_ATTR_NS, APPLICABLE_POLICYSET_ATTR).getNodeValue();
                }
                
                if ( applicablePolicySets != null && applicablePolicySets.length() > 0 ) {
                    applicablePolicySets = applicablePolicySets + " " + policySetPrefix + ":" + policySet.getName().getLocalPart();
                } else {
                    applicablePolicySets = policySetPrefix + ":" + policySet.getName().getLocalPart();
                }
                
                ((Element)aResultNode).setAttributeNS(APPLICABLE_POLICYSET_ATTR_NS, 
                                                      appPolicyAttrPrefix + ":" + APPLICABLE_POLICYSET_ATTR, 
                                                 applicablePolicySets);
            }
        }
    }
    
    private byte[] addApplicablePolicySets(URL artifactUrl) throws ContributionReadException {
        try {
            DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
            dbFac.setNamespaceAware(true);
            DocumentBuilder db = dbFac.newDocumentBuilder();
            Document doc = db.parse(artifactUrl.toURI().toString());
            return addApplicablePolicySets(doc, policySetMap.values());
        } catch ( Exception e ) {
            throw new ContributionReadException(e);
        }
    }
    
    private String declareNamespace(Element element, String prefix, String ns) {
        if (ns == null) {
            ns = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        String qname = null;
        if ("".equals(prefix)) {
            qname = "xmlns";
        } else {
            qname = "xmlns:" + prefix;
        }
        Node node = element;
        boolean declared = false;
        while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            if ( node.lookupPrefix(ns) != null ) {
                prefix = node.lookupPrefix(ns);
                declared = true;
                break;
            } else {
                /*NamedNodeMap attrs = node.getAttributes();
                if (attrs == null) {
                    break;
                }
                Node attr = attrs.getNamedItem(qname);
                if (attr != null) {
                    declared = ns.equals(attr.getNodeValue());
                    break;
                }*/
                node = node.getParentNode();
            }
        }
        if (!declared) {
            org.w3c.dom.Attr attr = element.getOwnerDocument().createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, qname);
            attr.setValue(ns);
            element.setAttributeNodeNS(attr);
        }
        return prefix;
    }
    
    private static class DOMNamespaceContext implements NamespaceContext {
        private Node node;

        /**
         * @param node
         */
        public DOMNamespaceContext(Node node) {
            super();
            this.node = node;
        }

        public String getNamespaceURI(String prefix) {
            return node.lookupNamespaceURI(prefix);
        }

        public String getPrefix(String namespaceURI) {
            return node.lookupPrefix(namespaceURI);
        }

        public Iterator<?> getPrefixes(String namespaceURI) {
            return null;
        }

    }
    
    private static String print(Node node)  {
        if ( node.getNodeType() != 3 ) {
            System.out.println("********************************************************" + node.getNodeType());
            StringWriter sw = new StringWriter();
            Source domSource = new DOMSource(node);
            Result finalResult = new StreamResult(sw);
            
            try {
                Transformer t = TransformerFactory.newInstance().newTransformer();
                
                t.setOutputProperty("omit-xml-declaration", "yes");
                //System.out.println(" ***** - " + t.getOutputProperties());
                t.transform(domSource, finalResult);
            } catch ( Exception e ) {
                e.printStackTrace();
             }
            System.out.println(sw.toString());
            System.out.println("********************************************************");
            return sw.toString();
        } else {
            return null;
        }
    }

}
