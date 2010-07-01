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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensionDeserializer;
import javax.wsdl.extensions.UnknownExtensionSerializer;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.sca.common.java.io.IOHelper;
import org.apache.tuscany.sca.common.xml.XMLDocumentHelper;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultImport;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionRuntimeException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLDefinitionImpl;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * A Model Resolver for WSDL models.
 * 
 * @version $Rev$ $Date$
 */
public class WSDLModelResolver implements ModelResolver {
    //Schema element names
    public static final String ELEM_SCHEMA = "schema";
    public static final QName WSDL11_IMPORT = new QName("http://schemas.xmlsoap.org/wsdl/", "import");

    //Schema URI
    public static final String NS_URI_XSD_1999 = "http://www.w3.org/1999/XMLSchema";
    public static final String NS_URI_XSD_2000 = "http://www.w3.org/2000/10/XMLSchema";
    public static final String NS_URI_XSD_2001 = "http://www.w3.org/2001/XMLSchema";

    //Schema QNames
    public static final QName Q_ELEM_XSD_1999 = new QName(NS_URI_XSD_1999, ELEM_SCHEMA);
    public static final QName Q_ELEM_XSD_2000 = new QName(NS_URI_XSD_2000, ELEM_SCHEMA);
    public static final QName Q_ELEM_XSD_2001 = new QName(NS_URI_XSD_2001, ELEM_SCHEMA);
    public static final List<QName> XSD_QNAME_LIST =
        Arrays.asList(new QName[] {Q_ELEM_XSD_1999, Q_ELEM_XSD_2000, Q_ELEM_XSD_2001});
    
    // ---- SCA Policy WSDL Attachments    
    public static final QName Q_POLICY_ATTRIBUTE_EXTENSION = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "requires");
    public static final QName Q_POLICY_END_CONVERSATION_ATTRIBUTE_EXTENSION = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "endsConversation");
    // ---- SCA Callback WSDL Extension
    public static final QName Q_CALLBACK_ATTRIBUTE_EXTENSION = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "callback" );
    
    // ---- BPEL extension elements ---  Mike Edwards 01/05/2008
    public static final String ELEM_PLINKTYPE = "partnerLinkType";
    public static final String NS_BPEL_1_1 = "http://schemas.xmlsoap.org/ws/2004/03/partner-link/";
    public static final QName BPEL_PLINKTYPE = new QName( NS_BPEL_1_1, ELEM_PLINKTYPE );
    public static final String NS_BPEL_2_0 = "http://docs.oasis-open.org/wsbpel/2.0/plnktype";
    public static final QName BPEL_PLINKTYPE_2_0 = new QName( NS_BPEL_2_0, ELEM_PLINKTYPE );
    // ---- end of BPEL extension elements

    private Contribution contribution;
    private Map<String, List<WSDLDefinition>> map = new HashMap<String, List<WSDLDefinition>>();

    private ExtensionRegistry wsdlExtensionRegistry;

    private WSDLFactory wsdlFactory;
    private javax.wsdl.factory.WSDLFactory wsdl4jFactory;
    private ContributionFactory contributionFactory;
    private XSDFactory xsdFactory;

    public WSDLModelResolver(Contribution contribution, FactoryExtensionPoint modelFactories) {
        this.contribution = contribution;

        this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        this.wsdl4jFactory = modelFactories.getFactory(javax.wsdl.factory.WSDLFactory.class);
        this.contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        this.xsdFactory = modelFactories.getFactory(XSDFactory.class);

        wsdlExtensionRegistry = this.wsdl4jFactory.newPopulatedExtensionRegistry();
        // REVIEW: [rfeng] Disable the schema extension for WSDL4J to avoid aggressive loading 
        ExtensionDeserializer deserializer = new UnknownExtensionDeserializer();
        ExtensionSerializer serializer = new UnknownExtensionSerializer();
        for (QName schema : XSD_QNAME_LIST) {
            wsdlExtensionRegistry.registerSerializer(Types.class, schema, serializer);
            wsdlExtensionRegistry.registerDeserializer(Types.class, schema, deserializer);
        }
        // ---- Policy WSDL Extensions
        try {
            wsdlExtensionRegistry.registerExtensionAttributeType(PortType.class, Q_POLICY_ATTRIBUTE_EXTENSION, AttributeExtensible.LIST_OF_QNAMES_TYPE);
            wsdlExtensionRegistry.registerExtensionAttributeType(Operation.class, Q_POLICY_END_CONVERSATION_ATTRIBUTE_EXTENSION, AttributeExtensible.STRING_TYPE);
            wsdlExtensionRegistry.registerExtensionAttributeType(PortType.class, Q_CALLBACK_ATTRIBUTE_EXTENSION, AttributeExtensible.QNAME_TYPE);
        } catch (NoSuchMethodError e) {
            // That method does not exist on older WSDL4J levels
        }
        
        // ---- BPEL additions
        serializer = new BPELExtensionHandler();
        deserializer = new BPELExtensionHandler();
        wsdlExtensionRegistry.registerSerializer(Definition.class, BPEL_PLINKTYPE, serializer);
        wsdlExtensionRegistry.registerDeserializer(Definition.class, BPEL_PLINKTYPE, deserializer);
        wsdlExtensionRegistry.registerSerializer(Definition.class, BPEL_PLINKTYPE_2_0, serializer);
        wsdlExtensionRegistry.registerDeserializer(Definition.class, BPEL_PLINKTYPE_2_0, deserializer);
        // ---- end of BPEL additions
    }

    /**
     * Implementation of a WSDL locator.
     */
    private class WSDLLocatorImpl implements WSDLLocator {
        private ProcessorContext context;
        private InputStream inputStream;
        private URL base;
        private String latestImportURI;
        private Map<String, String> wsdlImports;

        public WSDLLocatorImpl(ProcessorContext context, URL base, InputStream is, Map<String, String> imports) {
            this.context = context;
            this.base = base;
            this.inputStream = is;
            this.wsdlImports = imports;
        }

        public void close() {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        public InputSource getBaseInputSource() {
            try {
                return XMLDocumentHelper.getInputSource(base, inputStream);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public String getBaseURI() {
            return base.toString();
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            try {
                if (importLocation == null) {
                    throw new IllegalArgumentException("Required attribute 'location' is missing.");
                }
                if (importLocation.trim().equals(""))
                	throw new IllegalArgumentException("Required attribute 'location' is empty.");

                URL url = null;
                if (importLocation.startsWith("/")) {
                    // The URI is relative to the contribution
                    String uri = importLocation.substring(1);

                    Artifact proxyArtifact = contributionFactory.createArtifact();
                    proxyArtifact.setURI(uri);

                    //use contribution resolution (this supports import/export)
                    Artifact importedArtifact =
                        contribution.getModelResolver().resolveModel(Artifact.class, proxyArtifact, context);
                    if (importedArtifact.getLocation() != null) {
                        //get the artifact URL
                        url = new URL(importedArtifact.getLocation());
                    }
                } else {
                    url = new URL(new URL(parentLocation), importLocation);
                }
                if (url == null) {
                    return null;
                }
                latestImportURI = url.toString();
                return XMLDocumentHelper.getInputSource(url); 
            } catch (IOException e) {            	
                // If we are not able to resolve the imports using location, then 
            	// try resolving them using the namespace.
            	try {
	            	if (! wsdlImports.isEmpty()) {
	                	for (Artifact artifact : contribution.getArtifacts()) {
	            			if (artifact.getModel() instanceof WSDLDefinitionImpl) {
	            				String namespace = ((WSDLDefinitionImpl)artifact.getModel()).getNamespace();
	            				for (Map.Entry<String, String> entry : ((Map<String, String>)wsdlImports).entrySet()) {
		                            if (entry.getKey().equals(namespace)) {
		                            	URL url = ((WSDLDefinitionImpl)artifact.getModel()).getLocation().toURL();	            					
			                            return XMLDocumentHelper.getInputSource(url);
		                            }
	            				}
	            			}
	            	    }
	                }   
            	} catch (IOException ex) {
            		throw new ContributionRuntimeException(ex);
            	}            	
                throw new ContributionRuntimeException(e);
            }
        }

        public String getLatestImportURI() {
            return latestImportURI;
        }

    }

    public void addModel(Object resolved, ProcessorContext context) {
        WSDLDefinition definition = (WSDLDefinition)resolved;
        for (XSDefinition d : definition.getXmlSchemas()) {
            if (contribution != null) {
                contribution.getModelResolver().addModel(d, context);
            }
        }
        List<WSDLDefinition> list = map.get(definition.getNamespace());
        if (list == null) {
            list = new ArrayList<WSDLDefinition>();
            map.put(definition.getNamespace(), list);
        }
        list.add(definition);
    }

    public Object removeModel(Object resolved, ProcessorContext context) {
        WSDLDefinition definition = (WSDLDefinition)resolved;
        List<WSDLDefinition> list = map.get(definition.getNamespace());
        if (list == null) {
            return null;
        } else {
            return list.remove(definition);
        }
    }

    /**
     * Create a facade Definition which imports all the definitions
     * 
     * @param definitions A list of the WSDL definitions under the same target namespace
     * @param context 
     * @return The aggregated WSDL definition
     */
    @SuppressWarnings("unchecked")
	private WSDLDefinition aggregate(List<WSDLDefinition> definitions, ProcessorContext context) throws ContributionReadException {
        if (definitions == null || definitions.size() == 0) {
            return null;
        }
        if (definitions.size() == 1) {
            WSDLDefinition d = definitions.get(0);
            loadDefinition(d, context);
            return d;
        }
        WSDLDefinition aggregated = wsdlFactory.createWSDLDefinition();
        for (WSDLDefinition d : definitions) {
        	loadDefinition(d, context);
        }
        Definition facade = wsdl4jFactory.newDefinition();
        String ns = definitions.get(0).getNamespace();
        facade.setQName(new QName(ns, "$aggregated$"));
        facade.setTargetNamespace(ns);

        for (WSDLDefinition d : definitions) {
            if (d.getDefinition() != null) {
                javax.wsdl.Import imp = facade.createImport();
                imp.setNamespaceURI(d.getNamespace());
                imp.setDefinition(d.getDefinition());
                imp.setLocationURI(d.getDefinition().getDocumentBaseURI());
                facade.addImport(imp);
                aggregated.getXmlSchemas().addAll(d.getXmlSchemas());
                aggregated.getImportedDefinitions().add(d);
                // Deal with extensibility elements in the imported Definitions...
                List<ExtensibilityElement> extElements = (List<ExtensibilityElement>) d.getDefinition().getExtensibilityElements();
                for( ExtensibilityElement extElement : extElements ) {
                	facade.addExtensibilityElement(extElement);
                } // end for
            }
        }
        aggregated.setDefinition(facade);
        definitions.clear();
        definitions.add(aggregated);
        return aggregated;
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved, ProcessorContext context) {

    	WSDLDefinition resolved = null;
    	String namespace = ((WSDLDefinition)unresolved).getNamespace();
    	if (namespace == null) {
            return modelClass.cast(unresolved);
        }    	
    	
    	// Lookup a definition for the given namespace, from imports        
        for (Import import_ : this.contribution.getImports()) {
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                if (namespaceImport.getNamespace().equals(namespace)) {
                	// Delegate the resolution to the namespace import resolver
	                resolved =
	                	namespaceImport.getModelResolver().resolveModel(WSDLDefinition.class,
	                                                                        (WSDLDefinition)unresolved, context);
	                if (!resolved.isUnresolved()) {
	                	return modelClass.cast(resolved);
	                }
                }
            } else if (import_ instanceof DefaultImport) {                
                // Delegate the resolution to the default import resolver
                resolved =
                    import_.getModelResolver().resolveModel(WSDLDefinition.class,
                                                                    (WSDLDefinition)unresolved, context);
                if (!resolved.isUnresolved()) {
                    return modelClass.cast(resolved);
                }
            }
        }
        
        
        // Not found, lookup a definition for the given namespace, within contribution
        List<WSDLDefinition> list = map.get(namespace);
        try {
        	resolved = aggregate(list, context);
        } catch (ContributionReadException e) {
        	throw new RuntimeException(e);
        }        
        if (resolved != null && !resolved.isUnresolved()) {
            // check that the WSDL we just found has the requisite 
            // port type, binding and/or service. If not return 
            // the input WSDL to force the resolution process to continue
            WSDLDefinition inputWSDL = (WSDLDefinition)unresolved;
            WSDLDefinition outputWSDL = (WSDLDefinition)resolved;
            
            if (inputWSDL.getNameOfPortTypeToResolve() != null){
                if (outputWSDL.getWSDLObject(PortType.class, inputWSDL.getNameOfPortTypeToResolve()) == null){
                    return modelClass.cast(unresolved);
                }
            }
            
            if (inputWSDL.getNameOfBindingToResolve() != null){
                if (outputWSDL.getWSDLObject(Binding.class, inputWSDL.getNameOfBindingToResolve()) == null){
                    return modelClass.cast(unresolved);
                }
            }
            
            if (inputWSDL.getNameOfServiceToResolve() != null){
                if (outputWSDL.getWSDLObject(Service.class, inputWSDL.getNameOfServiceToResolve()) == null){
                    return modelClass.cast(unresolved);
                }
            }            
            
            return modelClass.cast(resolved);
        }
        
        return modelClass.cast(unresolved);
    }

    /**
     * Use non-sca mechanism to resolve the import location, 
     * if not found then use the sca mechanism
     * 
     * @param modelClass
     * @param unresolved
     * @param context 
     * @throws ContributionReadException
     */
    private <T> T resolveImports (Class<T> modelClass, WSDLDefinition unresolved, ProcessorContext context) throws ContributionReadException {
    	
    	WSDLDefinition resolved = null;
    	if (unresolved.getDefinition() == null && unresolved.getLocation() != null) {            
            try {
            	// Load the definition using non-sca mechanism.
            	List<WSDLDefinition> list = new ArrayList<WSDLDefinition>();
                list.add(unresolved);
                map.put(unresolved.getNamespace(), list);            	
            	resolved = aggregate(list, context);            	
            	// if no exception then its resolved.
            	if (unresolved.getNamespace().equals(resolved.getDefinition().getTargetNamespace())) {
            		resolved.setNamespace(resolved.getDefinition().getTargetNamespace());
            		resolved.setUnresolved(false);
            		resolved.setURI(resolved.getLocation());
            		return modelClass.cast(resolved);
            	}
            } catch (ContributionReadException e) {
            	// Resolve the wsdl definition using the namespace, by searching the
            	// contribution artifacts for wsdl definition for the given namespace.
            	for (Artifact artifact : contribution.getArtifacts()) {
        			if (artifact.getModel() instanceof WSDLDefinitionImpl) {
        				String namespace = ((WSDLDefinitionImpl)artifact.getModel()).getNamespace();
        				if (unresolved.getNamespace().equals(namespace)) {        					
        					WSDLDefinition wsdlDefinition = (WSDLDefinition)artifact.getModel();
        					if (wsdlDefinition.getDefinition() == null) {
        						loadDefinition(wsdlDefinition, context);
        					}
                            return modelClass.cast(wsdlDefinition);
        				}
        			}
        	    }
            }
        }
        
        return modelClass.cast(unresolved);
    }    


    /**
     * Load the WSDL definition and inline schemas
     * 
     * @param wsdlDef
     * @param context 
     * @throws ContributionReadException
     */
    private void loadDefinition(WSDLDefinition wsdlDef, ProcessorContext context) throws ContributionReadException {
        if (wsdlDef.getDefinition() != null || wsdlDef.getLocation() == null) {
            return;
        }
        try {
            URL artifactURL = wsdlDef.getLocation().toURL();
            // Read a WSDL document
            InputStream is = IOHelper.openStream(artifactURL);
            WSDLReader reader = wsdl4jFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);
            // FIXME: We need to decide if we should disable the import processing by WSDL4J
            // reader.setFeature("javax.wsdl.importDocuments", false);
            reader.setExtensionRegistry(wsdlExtensionRegistry);  // use a custom registry

            // Collection of namespace,location for wsdl:import definition
            Map<String, String> wsdlImports = indexRead(wsdlDef.getLocation().toURL());
            WSDLLocatorImpl locator = new WSDLLocatorImpl(context, artifactURL, is, wsdlImports);
            Definition definition = reader.readWSDL(locator);
            wsdlDef.setDefinition(definition);

            // If this definition imports any definitions from other namespaces,
            // set the correct WSDLDefinition import relationships.
            for (Map.Entry<String, List<javax.wsdl.Import>> entry :
                    ((Map<String, List<javax.wsdl.Import>>)definition.getImports()).entrySet()) {
                if (!entry.getKey().equals(definition.getTargetNamespace())) { 
                    WSDLDefinition wsdlDefinition = wsdlFactory.createWSDLDefinition();
                    wsdlDefinition.setUnresolved(true);
                    wsdlDefinition.setNamespace(entry.getKey());
                    WSDLDefinition resolved = null;
                    for (javax.wsdl.Import imp : entry.getValue()) {
                    	if (imp.getDefinition() == null)
                            throw new IllegalArgumentException("Required attribute 'location' is missing.");
                    	
                    	try {
                    		wsdlDefinition.setLocation(new URI(imp.getDefinition().getDocumentBaseURI()));
                    		resolved = resolveImports(WSDLDefinition.class, wsdlDefinition, context);
                    		if (!resolved.isUnresolved()) {
                    			if (resolved.getImportedDefinitions().isEmpty()) {
                    				if (resolved.getDefinition().getTargetNamespace().equals(imp.getDefinition().getTargetNamespace())) {
	                    				// this WSDLDefinition contains the imported document
	                                    wsdlDef.getImportedDefinitions().add(resolved);
	                                    imp.setLocationURI(resolved.getURI().toString());
                    				}
                    			} else {
                    				// this is a facade, so look in its imported definitions
                    				for (WSDLDefinition def : resolved.getImportedDefinitions()) {
                                        if (def.getDefinition().getTargetNamespace().equals(imp.getDefinition().getTargetNamespace())) {
                                            wsdlDef.getImportedDefinitions().add(def);
                                            imp.setLocationURI(def.getURI().toString());
                                            break;
                                        }
                                    }
                    			}
                    		}
                    	} catch (Exception e) {
                    		throw new ContributionReadException(e);
                    	}
                    }
                }
            }            
            //Read inline schemas 
            readInlineSchemas(wsdlDef, definition, context);
        } catch (WSDLException e) {
            throw new ContributionReadException(e);
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
    }

    private Document promote(Element element) {
        Document doc = (Document)element.getOwnerDocument().cloneNode(false);
        Element schema = (Element)doc.importNode(element, true);
        doc.appendChild(schema);
        Node parent = element.getParentNode();
        while (parent instanceof Element) {
            Element root = (Element)parent;
            NamedNodeMap nodeMap = root.getAttributes();
            if (nodeMap != null) {
                for (int i = 0; i < nodeMap.getLength(); i++) {
                    Attr attr = (Attr)nodeMap.item(i);
                    String name = attr.getName();
                    if ("xmlns".equals(name) || name.startsWith("xmlns:")) {
                        if (schema.getAttributeNode(name) == null) {
                            schema.setAttributeNodeNS((Attr)doc.importNode(attr, true));
                        }
                    }
                }
            }    
            parent = parent.getParentNode();
        }
        doc.setDocumentURI(element.getOwnerDocument().getDocumentURI());
        return doc;
    }

    /**
     * Populate the inline schemas including those from the imported definitions
     * 
     * @param definition
     * @param context 
     * @param schemaCollection
     */
    private void readInlineSchemas(WSDLDefinition wsdlDefinition, Definition definition, ProcessorContext context) {
        if (contribution == null) {
            // Check null for test cases
            return;
        }
        Types types = definition.getTypes();
        if (types != null) {
            int index = 0;
            for (Object ext : types.getExtensibilityElements()) {
                ExtensibilityElement extElement = (ExtensibilityElement)ext;
                Element element = null;
                if (XSD_QNAME_LIST.contains(extElement.getElementType())) {
                    if (extElement instanceof Schema) {
                        element = ((Schema)extElement).getElement();
                    } else if (extElement instanceof UnknownExtensibilityElement) {
                        element = ((UnknownExtensibilityElement)extElement).getElement();
                    }
                }
                if (element != null) {
                    Document doc = promote(element);
                    XSDefinition xsDefinition = xsdFactory.createXSDefinition();
                    xsDefinition.setUnresolved(true);
                    xsDefinition.setNamespace(element.getAttribute("targetNamespace"));
                    xsDefinition.setDocument(doc);
                    xsDefinition.setLocation(URI.create(doc.getDocumentURI() + "#" + index));
                    XSDefinition resolved =
                        contribution.getModelResolver().resolveModel(XSDefinition.class, xsDefinition, context);
                    if (resolved != null && !resolved.isUnresolved()) {
                        if (!wsdlDefinition.getXmlSchemas().contains(resolved)) {
                            // Don't add resolved because it may be an aggregate that
                            // contains more than we need.  The resolver will have
                            // set the specific schema we need into unresolved.
                            wsdlDefinition.getXmlSchemas().add(xsDefinition);
                        }
                    }
                    index++;
                }
            }
        }
        for (Object imports : definition.getImports().values()) {
            List impList = (List)imports;
            for (Object i : impList) {
                javax.wsdl.Import anImport = (javax.wsdl.Import)i;
                // Read inline schemas 
                if (anImport.getDefinition() != null) {
                    readInlineSchemas(wsdlDefinition, anImport.getDefinition(), context);
                }
            }
        }
    }
    
    /**
     * Read the namespace and location for the WSDL imports
     * 
     * @param doc
     * @return
     * @throws IOException
     * @throws XMLStreamException
     */
    protected Map<String, String> indexRead(URL doc) throws IOException, XMLStreamException {
        
    	Map<String, String> wsdlImports = new HashMap<String, String>();
    	InputStream is = doc.openStream();
        try {
            // Set up a StreamSource for the composite file, since this has an associated URL that
            // can be used by the parser to find references to other files such as DTDs
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            StreamSource wsdlSource = new StreamSource(is, doc.toString());
            XMLStreamReader reader = inputFactory.createXMLStreamReader(wsdlSource);
            
            int eventType = reader.getEventType();
            while (true) {
                if (eventType == XMLStreamConstants.START_ELEMENT) {
                    if (WSDL11_IMPORT.equals(reader.getName())) {
                        String ns = reader.getAttributeValue(null, "namespace");
                        String loc = reader.getAttributeValue(null, "location");
                        wsdlImports.put(ns, loc);                        
                    }
                }
                if (reader.hasNext()) {
                    eventType = reader.next();
                } else {
                    break;
                }
            }
            return wsdlImports;
        } finally {
            is.close();
        }
    }
}
