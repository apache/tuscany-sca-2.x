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

package org.apache.tuscany.sca.implementation.bpel.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.bpel.BPELFactory;
import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.BPELPartnerLinkTypeExt;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * BPEL document processor responsible for reading a BPEL file and producing necessary model info about it
 * 
 * TODO: The namespaces for WS-BPEL include 2 versions - only the earlier BPEL 1.1 versions are
 * supported at present - the BPEL 2.0 namespaces also need support.  This will require inspection
 * of both BPEL process files and of WSDL files for their BPEL namespaces
 * @version $Rev$ $Date$
 */
public class BPELDocumentProcessor extends BaseStAXArtifactProcessor implements URLArtifactProcessor<BPELProcessDefinition> {
//    public final static QName BPEL_PROCESS_DEFINITION = new QName("http://schemas.xmlsoap.org/ws/2004/03/business-process/", "process");
//    public final static QName BPEL_EXECUTABLE_DEFINITION = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "process");

    private static final String SCA_BPEL_NS 	= "http://docs.oasis-open.org/ns/opencsa/sca-bpel/200801";
    private static final String WSDL_NS 		= "http://schemas.xmlsoap.org/wsdl/";

    // BPEL 1.1
    private static final String BPEL_NS 		= "http://schemas.xmlsoap.org/ws/2004/03/business-process/";
    private static final String BPEL_PLINK_NS 	= "http://schemas.xmlsoap.org/ws/2004/03/partner-link/";
    private final static String NAME_ELEMENT 		= "name";
    private static final String LINKTYPE_NAME 		= "partnerLinkType";
    private final static String TARGET_NAMESPACE 	= "targetNamespace";
    private static final QName PROCESS_ELEMENT 		= new QName(BPEL_NS, "process");
    private static final QName PARTNERLINK_ELEMENT 	= new QName(BPEL_NS, "partnerLink");
    private static final QName ONEVENT_ELEMENT 		= new QName(BPEL_NS, "onEvent");
    private static final QName RECEIVE_ELEMENT 		= new QName(BPEL_NS, "receive");
    private static final QName ONMESSAGE_ELEMENT 	= new QName(BPEL_NS, "onMessage");
    private static final QName INVOKE_ELEMENT 		= new QName(BPEL_NS, "invoke");
    private static final QName IMPORT_ELEMENT 		= new QName(BPEL_NS, "import");
    private static final QName VARIABLE_ELEMENT		= new QName(BPEL_NS, "variable");
    private static final QName LINKTYPE_ELEMENT 	= new QName(BPEL_PLINK_NS, LINKTYPE_NAME);
    
    // BPEL 2.0
    private static final String BPEL_NS_20 			= "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
    private static final String BPEL_PLINK_NS_20	= "http://docs.oasis-open.org/wsbpel/2.0/plnktype";
    private static final QName PROCESS_ELEMENT_20 		= new QName(BPEL_NS_20, "process");
    private static final QName PARTNERLINK_ELEMENT_20 	= new QName(BPEL_NS_20, "partnerLink");
    private static final QName ONEVENT_ELEMENT_20 		= new QName(BPEL_NS_20, "onEvent");
    private static final QName RECEIVE_ELEMENT_20 		= new QName(BPEL_NS_20, "receive");
    private static final QName ONMESSAGE_ELEMENT_20 	= new QName(BPEL_NS_20, "onMessage");
    private static final QName INVOKE_ELEMENT_20 		= new QName(BPEL_NS_20, "invoke");
    private static final QName IMPORT_ELEMENT_20 		= new QName(BPEL_NS_20, "import");   
    private static final QName VARIABLE_ELEMENT_20		= new QName(BPEL_NS_20, "variable");
    private static final QName LINKTYPE_ELEMENT_20 		= new QName(BPEL_PLINK_NS_20, LINKTYPE_NAME);
    
    private final static XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    
    private final BPELFactory 	factory;
    private WSDLFactory 		WSDLfactory;
    private AssemblyFactory 	assemblyFactory;
    private Monitor monitor;

    public BPELDocumentProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.factory     		= modelFactories.getFactory(BPELFactory.class);
        this.WSDLfactory 		= modelFactories.getFactory(WSDLFactory.class);
        this.assemblyFactory 	= modelFactories.getFactory(AssemblyFactory.class);
        this.monitor = monitor;
    }
    
    public String getArtifactType() {
        return "*.bpel";
    }    

    public Class<BPELProcessDefinition> getModelType() {
        return BPELProcessDefinition.class;
    }

    public BPELProcessDefinition read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
        BPELProcessDefinition processDefinition = null;
        try {
            // for now we are just using process name
            // and relying on componentType file for service definition
            // so it's OK to set resolved for now
            processDefinition = readProcessDefinition(artifactURL);
            processDefinition.setURI(artifactURI.toString());
            processDefinition.setUnresolved(false);
        } catch (Exception e) {
            ContributionReadException ce = new ContributionReadException(e);
            error("ContributionReadException", artifactURL, ce);
        }

        return processDefinition;
    }

    public void resolve(BPELProcessDefinition model, ModelResolver resolver) throws ContributionResolveException {
        // FIXME - serious resolving needs to happen here
    	
    	// Step 1 is to resolve the WSDL files referenced from this BPEL process
    	// - one complexity here is that the WSDL definitions hold BPEL extension elements for
    	// the partnerLinkType declarations - and these must be used in later steps
    	//
    	// Step 2 is to take all the partnerLink definitions and establish the PortType being
    	// used, by tracing through the related partnerLinkType declarations - the PortType is
    	// effectively a definition of the interface used by the partnerLink.
    	// - another consideration here is that each partnerLink can involve 2 interfaces, one
    	// for the forward calls to the process, the other for calls from the process - depending
    	// on whether the partnerLink is a reference or a service, one of these interfaces is a
    	// callback interface.
    	
    	List<BPELImportElement> theImports = model.getImports();
    	Set<Definition> wsdlDefinitions = getImportedWSDLDefinitions( theImports, resolver );
    	
    	// Fetch the sets of partner links, port types and interfaces
    	List<BPELPartnerLinkTypeElement> thePLinkTypes = getPartnerLinkTypes( wsdlDefinitions );
    	Collection<WSDLInterface> theInterfaces = (Collection<WSDLInterface>)new ArrayList<WSDLInterface>();
    	Collection<PortType> thePortTypes = getAllPortTypes( theImports, theInterfaces, resolver );
    	
    	// Store the Port Types and the Interfaces for later calculation of the component type...
    	model.getPortTypes().addAll(thePortTypes);
    	model.getInterfaces().addAll(theInterfaces);
    	
    	// Now, for each partnerLink in the BPEL process, find the related partnerLinkType element 
        List<BPELPartnerLinkElement> thePartnerLinks = model.getPartnerLinks();
        for (BPELPartnerLinkElement thePartnerLink : thePartnerLinks) {
            QName partnerLinkType = thePartnerLink.getPartnerLinkType();
            BPELPartnerLinkTypeElement pLinkType = findPartnerLinkType(partnerLinkType, thePLinkTypes);
            if (pLinkType == null) {
                error("PartnerLinkNoMatchingType", thePartnerLink, thePartnerLink.getName());
            } else
                thePartnerLink.setPartnerLinkType(pLinkType);
        } // end for
    	
    } // end resolve
    
    /**
     * Get all the WSDL definitions referenced through the import statements of the BPEL process
     * @param theImports - a list of the import statements
     * @return - a Set containing all the referenced WSDL definitions
     */
    private Set<Definition> getImportedWSDLDefinitions( List<BPELImportElement> theImports, 
    		                                            ModelResolver resolver ) {
    	Set<Definition> wsdlDefinitions = null;
    	for (BPELImportElement theImport : theImports) {
            if (theImport.getImportType().equals(WSDL_NS)) {
            	// If the Import is a WSDL import, resolve the WSDL
            	WSDLDefinition theWSDL = resolveWSDLDefinition( theImport.getLocation(), 
            			                                        theImport.getNamespace(), resolver );
                if( theWSDL != null ) {
	            	theImport.setWSDLDefinition( theWSDL );
	
	                // Find all the WSDL definitions matching the imported namespace
	            	if( wsdlDefinitions == null ) {
	            		wsdlDefinitions = new HashSet<Definition>();
	            	} // end if 
	            	
	                wsdlDefinitions.add(theWSDL.getDefinition());
	                // Fetch any definitions that are imported
	                for (WSDLDefinition importedWSDL: theWSDL.getImportedDefinitions()) {
	                    wsdlDefinitions.add(importedWSDL.getDefinition());
	                } // end for
                } // end if
            } // end if
        } // end for
        
        return wsdlDefinitions;
    } // end getImportedWSDLDefinitions
    
    /**
     * Resolve a reference to a WSDL, given by a namespace and a location
     * @param wsdlLocation - a string containing the WSDL location
     * @param wsdlNamespace - a string containing the WSDL namespace
     * @param resolver - a model resolver
     * @return - a WSDLDefinition object for the referenced WSDL, or null if the WSDL cannot be resolved
     */
    private WSDLDefinition resolveWSDLDefinition( String wsdlLocation, String wsdlNamespace, ModelResolver resolver ) {
        
        // Resolve the WSDL definition
        WSDLDefinition proxy = WSDLfactory.createWSDLDefinition();
        proxy.setUnresolved(true);
        proxy.setNamespace(wsdlNamespace);
        if (wsdlLocation != null) {
            proxy.setLocation(URI.create(wsdlLocation));
        }
        WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, proxy);
        if (resolved != null && !resolved.isUnresolved()) {
        	return resolved;
        } else {
            error("CannotResolveWSDLReference", resolver, wsdlLocation, wsdlNamespace);
            return null;
        } // end if
    } // end resolveWSDLDefinition
    
    /**
     * Retrieve all the Partner Link types defined in the imported WSDL files
     * 
     * @param wsdlDefinitions - the set of imported WSDL definitions
     * @return - a List of PartnerLinkType elements
     */
    @SuppressWarnings("unchecked")
    private List<BPELPartnerLinkTypeElement> getPartnerLinkTypes( Set<Definition> wsdlDefinitions ) throws ContributionResolveException {
    	
    	List<BPELPartnerLinkTypeElement> thePLinks = new ArrayList<BPELPartnerLinkTypeElement>();

        // The BPEL partnerLinkType elements are extension elements within the WSDL definitions
        for (Definition wsdlDefinition: wsdlDefinitions) {
            for (ExtensibilityElement theElement : (List<ExtensibilityElement>)wsdlDefinition.getExtensibilityElements()) {
                QName elementType = theElement.getElementType();
                if (elementType.equals(LINKTYPE_ELEMENT) || elementType.equals(LINKTYPE_ELEMENT_20)) {
                    BPELPartnerLinkTypeExt pLinkExt = (BPELPartnerLinkTypeExt)theElement;
                    
                    // Fetch the name of the partnerLinkType
                    QName qName = new QName(wsdlDefinition.getTargetNamespace(), pLinkExt.getName());
                    BPELPartnerLinkTypeElement pLinkElement = new BPELPartnerLinkTypeElement(qName);

                    // The partnerLinkType must have one and may have 2 role child elements
                    int count = 0;
                    for (int i = 0; i < 2; i++) {
                        if( count > 1 ) break;
                    	if (pLinkExt.getRoleName(i) == null) continue;
                        PortType pType = wsdlDefinition.getPortType(pLinkExt.getRolePortType(i));
                        if (count == 0) {
                            pLinkElement.setRole1(pLinkExt.getRoleName(i), pLinkExt.getRolePortType(i), pType);
                        } else {
                            pLinkElement.setRole2(pLinkExt.getRoleName(i), pLinkExt.getRolePortType(i), pType);
                        } // end if
                        count++;
                    } // end for

                    if (count == 0) {
                        error("PartnerLinkTypeNoRoles", theElement, pLinkElement.getName());
                        throw new ContributionResolveException("partnerLinkType " + pLinkElement.getName() + " has no Roles defined");
                    } else
                        thePLinks.add(pLinkElement);
                } // end if
            } // end for
        } // end for
        return thePLinks;
    } // end getPartnerLinkTypes

    /**
     * Returns all the portTypes referenced by the process.
     * 
     * @param theImports
     * @param theInterfaces
     * @param resolver
     * @return
     * @throws ContributionResolveException
     */
    @SuppressWarnings("unchecked")
    private Collection<PortType> getAllPortTypes(List<BPELImportElement> theImports,
                                                 Collection<WSDLInterface> theInterfaces, ModelResolver resolver) throws ContributionResolveException {

        Set<PortType> thePortTypes = new HashSet<PortType>();
        for (BPELImportElement theImport : theImports) {
            if (theImport.getImportType().equals(WSDL_NS)) {
                
                // Find all the WSDL definitions matching the imported namespace
                List<Definition> wsdlDefinitions = new ArrayList<Definition>();
                WSDLDefinition theWSDL = theImport.getWSDLDefinition();
                wsdlDefinitions.add(theWSDL.getDefinition());
                for (WSDLDefinition importedWSDL: theWSDL.getImportedDefinitions()) {
                    wsdlDefinitions.add(importedWSDL.getDefinition());
                }
                for (Definition wsdlDefinition: wsdlDefinitions) {

                    Collection<PortType> portTypes = (Collection<PortType>)wsdlDefinition.getPortTypes().values();
                    
                    // Create WSDLInterface elements for each unique PortType found
                    for (PortType portType : portTypes) {
                    	if( thePortTypes.contains(portType) ) continue;
                    	thePortTypes.add( portType );
                    		
                        WSDLObject<PortType> wsdlPortType = theWSDL.getWSDLObject(PortType.class, portType.getQName());
                        WSDLInterface wsdlInterface;
                        if (wsdlPortType != null) {
                            // Introspect the WSDL portType and add the resulting WSDLInterface to the resolver
                            try {
                                wsdlInterface = WSDLfactory.createWSDLInterface(wsdlPortType.getElement(), theWSDL, resolver);
                                wsdlInterface.setWsdlDefinition(theWSDL);
                            } catch (InvalidInterfaceException e) {
                                ContributionResolveException ce = 
                                	new ContributionResolveException("Unable to create WSDLInterface for portType " + portType.getQName(),e);
                                error("ContributionResolveException", resolver, ce);
                                throw ce;
                            } // end try
                            resolver.addModel(wsdlInterface);
                            theInterfaces.add(wsdlInterface);
                        } // end if
                    } // end for
                }
            }
        } // end for

        return thePortTypes;
    } // end getAllPortTypes
    
    /**
     * Finds a partnerLinkType definition within the WSDLs imported by the BPEL
     * process.
     * 
     * @param partnerLinkTypeName - the name of the partnerLinkType
     * @param theImports a list of the WSDL import declarations
     * @return a BPELPartnerLinkTypeElement for the partnerLinkType or null if it cannot be
     * found
     */
    private BPELPartnerLinkTypeElement findPartnerLinkType( QName partnerLinkTypeName, 
                                                            List<BPELPartnerLinkTypeElement> thePLinkTypes) {
    	// We must find the partner link type element from amongst the imported WSDLs
    	for ( BPELPartnerLinkTypeElement thePLinkType : thePLinkTypes ){
    		if( thePLinkType.getName().equals(partnerLinkTypeName) ) return thePLinkType;
     	} // end for
    	return null;
    } // end findPartnerLinkType
    

    /**
     * Read a process definition.
     * 
     * @param doc
     * @return
     * @throws Exception
     */
    private BPELProcessDefinition readProcessDefinition(URL doc) throws Exception {
        BPELProcessDefinition processDefinition = factory.createBPELProcessDefinition();
        processDefinition.setUnresolved(true);
        processDefinition.setLocation(doc.toString());

        InputStream is = doc.openStream();
        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(is);

            /*
             * The principle here is to look for partnerLink elements, which
             * form either services or references. A partnerLink can be EITHER -
             * the algorithm for deciding is: 
             * 1) Explicit marking with sca:reference or sca:service attribute 
             * 2) "first use" of the partnerLink by specific BPEL activity elements: 
             *    <onEvent../>, <receive../> or <pick../> elements imply a service 
             *    <invoke../> implies a reference
             */

            // TODO - need to handle <scope../> elements as kind of "nested" processes
            // - and scopes introduce the possibility of partnerLinks with the
            // same name at different levels of scope.... (yuk!!)
            boolean completed = false;
            while (!completed) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName qname = reader.getName();
                        if (PROCESS_ELEMENT.equals(qname) || PROCESS_ELEMENT_20.equals(qname)) {
                            QName processName = new QName(getString(reader, TARGET_NAMESPACE), getString(reader, NAME_ELEMENT));
                            processDefinition.setName(processName);
                        } else if (PARTNERLINK_ELEMENT.equals(qname) || PARTNERLINK_ELEMENT_20.equals(qname)) {
                            processDefinition.getPartnerLinks().add(processPartnerLinkElement(reader));
                        } else if (ONEVENT_ELEMENT.equals(qname) || RECEIVE_ELEMENT.equals(qname) || ONMESSAGE_ELEMENT.equals(qname) || 
                        		   ONEVENT_ELEMENT_20.equals(qname) || RECEIVE_ELEMENT_20.equals(qname) || ONMESSAGE_ELEMENT_20.equals(qname)) {
                            processPartnerLinkAsService(reader.getAttributeValue(null, "partnerLink"), processDefinition.getPartnerLinks());
                        } else if (INVOKE_ELEMENT.equals(qname) || INVOKE_ELEMENT_20.equals(qname)) {
                            processPartnerLinkAsReference(reader.getAttributeValue(null, "partnerLink"), processDefinition.getPartnerLinks());
                        } else if (IMPORT_ELEMENT.equals(qname) || IMPORT_ELEMENT_20.equals(qname)) {
                            processDefinition.getImports().add(processImportElement(reader));
                        } else if (VARIABLE_ELEMENT.equals(qname) || VARIABLE_ELEMENT_20.equals(qname)) {
                        	// deal with variables that are SCA properties through the presence of a sca-bpel:property="yes" attribute
                        	Property aProperty = processVariableElement(reader);
                        	if( aProperty != null ) {
                        		processDefinition.getProperties().add( aProperty );
                        	} // end if
                        } // end if
                        break;
                    case END_ELEMENT:
                    	qname = reader.getName();
                    	if (PROCESS_ELEMENT.equals(qname) || PROCESS_ELEMENT_20.equals(qname)) {
                            completed = true;
                            break;
                        } // end if
                } // end switch
            } // end while
        } finally {
            if (reader != null)
                reader.close();
            is.close();
        } // end try

        return processDefinition;
    } // end readProcessDefinition
    
    /**
     * Processes a BPEL <variable/> element and creates an SCA <property/> element if the variable is
     * marked with sca-bpel:property="yes"
     * A BPEL <variable/> element can declare its type in one of 3 ways:
     * 1. using @type attribute, which must reference an XSD declared type
     * 2. using @element attribute, which must reference an XSD global element
     * 3. using @message attribute, which must reference a WSDL defined message type
     * @param reader - XMLStreamReader reading the BPEL process
     * @throws ContributionReadException
     */
    private Property processVariableElement( XMLStreamReader reader) throws ContributionReadException {
    	String scaProperty = reader.getAttributeValue(SCA_BPEL_NS, "property");
    	if( "yes".equals(scaProperty)) {
    		String varName = reader.getAttributeValue(null ,"name");
    		String varType = reader.getAttributeValue(null, "type");
    		String varElement = reader.getAttributeValue(null, "element");
    		String varMessage = reader.getAttributeValue(null, "message");
    		// Pass over this variable if there is no name, or if there is no type information
    		if( varName == null ) return null;
    		if( varType == null && varElement == null && varMessage == null ) return null;
    		QName typeQName = getQNameValue( reader, varType );
    		QName elementQName = getQNameValue( reader, varElement );
    		// TODO deal with properties declared with @message for typing
    		Property theProperty = assemblyFactory.createProperty();
    		theProperty.setName(varName);
    		theProperty.setXSDType(typeQName);
    		theProperty.setXSDElement(elementQName);
    		return theProperty;
    	} // end if
    	return null;
    	
    } // end processVariableElement
    
    /**
     * Processes a partnerLink element from the BPEL process and creates a
     * BPELPartnerLink object
     * 
     * @param reader
     */
    private BPELPartnerLinkElement processPartnerLinkElement(XMLStreamReader reader) throws ContributionReadException {
        BPELPartnerLinkElement partnerLink = new BPELPartnerLinkElement( reader.getAttributeValue(null, "name"),
                                                                         getQNameValue(reader, reader.getAttributeValue(null, "partnerLinkType")),
                                                                         reader.getAttributeValue(null, "myRole"),
                                                                         reader.getAttributeValue(null, "partnerRole"));
        
        // See if there are any SCA extension attributes
        String scaService = reader.getAttributeValue(SCA_BPEL_NS, "service");
        String scaReference = reader.getAttributeValue(SCA_BPEL_NS, "reference");
        if ((scaService != null) && (scaReference != null)) {
            // It is incorrect to set both service & reference attributes
            error("PartnerLinkHasBothAttr", partnerLink, reader.getAttributeValue(null, "name"));
            throw new ContributionReadException("BPEL PartnerLink " + reader.getAttributeValue(null, "name") + " has both sca:reference and sca:service attributes set");
        }
        
        // Set the SCA type and the related name, if present
        if (scaService != null)
            partnerLink.setAsService(scaService);
        else if (scaReference != null)
            partnerLink.setAsReference(scaReference);
        return partnerLink;
        
    } // end processPartnerLinkElement

    /**
     * Processes an <import../> element from the BPEL process and creates a
     * BPELImportElement object
     * 
     * @param reader
     */
    private BPELImportElement processImportElement(XMLStreamReader reader) {
        return (new BPELImportElement(reader.getAttributeValue(null, "location"),
                                      reader.getAttributeValue(null, "importType"),
                                      reader.getAttributeValue(null, "namespace")));
        
    } // end processImportElement

    /**
     * Mark a named partnerLink as a Service, unless it is already marked as a
     * Reference
     * 
     * @param partnerLinkName
     * @param partnerLinks
     */
    private void processPartnerLinkAsService(String partnerLinkName, List<BPELPartnerLinkElement> partnerLinks) {
        BPELPartnerLinkElement partnerLink = findPartnerLinkByName(partnerLinks, partnerLinkName);
        if (partnerLink == null) {
            warning("ReferencePartnerLinkNotInList", partnerLinkName, partnerLinkName);
        } else {
            // Set the type of the partnerLink to "service" if not already
            // set...
            if (!partnerLink.isSCATyped())
                partnerLink.setAsService(partnerLinkName);
        } // endif
    } // end processPartnerLinkAsReference

    /**
     * Mark a named partnerLink as a Reference, unless it is already marked as a
     * Service
     * 
     * @param partnerLinkName
     * @param partnerLinks
     */
    private void processPartnerLinkAsReference(String partnerLinkName, List<BPELPartnerLinkElement> partnerLinks) {
        BPELPartnerLinkElement partnerLink = findPartnerLinkByName(partnerLinks, partnerLinkName);
        if (partnerLink == null) {
            warning("ReferencePartnerLinkNotInList", partnerLinkName, partnerLinkName);
        } else {
            // Set the type of the partnerLink to "service" if not already
            // set...
            if (!partnerLink.isSCATyped())
                partnerLink.setAsReference(partnerLinkName);
        } // endif
    } // end processPartnerLinkAsReference

    /**
     * Finds a PartnerLink by name from a List of PartnerLinks returns null if
     * there is no partnerLink with a matching name - returns the PartnerLink
     * with a matching name
     * 
     * @param partnerLinks
     * @param partnerLinkName
     */
    private BPELPartnerLinkElement findPartnerLinkByName(List<BPELPartnerLinkElement> partnerLinks, String partnerLinkName) {
        // Scan the list looking for a partner link with the supplied name
        Iterator<BPELPartnerLinkElement> it = partnerLinks.iterator();
        while (it.hasNext()) {
            BPELPartnerLinkElement thePartnerLink = it.next();
            if (thePartnerLink.getName().equals(partnerLinkName))
                return thePartnerLink;
        }
        return null;
    } // end method findPartnerLinkByName

    /**
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void warning(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "impl-bpel-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
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
            Problem problem = monitor.createProblem(this.getClass().getName(), "impl-bpel-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "impl-bpel-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }
    }
}
