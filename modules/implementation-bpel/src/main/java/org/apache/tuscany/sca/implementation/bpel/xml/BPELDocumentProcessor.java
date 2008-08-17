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
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.implementation.bpel.BPELFactory;
import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.BPELPartnerLinkTypeExt;
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
    public final static QName BPEL_PROCESS_DEFINITION = new QName("http://schemas.xmlsoap.org/ws/2004/03/business-process/", "process");
    public final static QName BPEL_EXECUTABLE_DEFINITION = new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "process");
    private static final String SCA_BPEL_NS = "http://docs.oasis-open.org/ns/opencsa/sca-bpel/200801";
    private static final String BPEL_NS = "http://schemas.xmlsoap.org/ws/2004/03/business-process/";
    private static final String BPEL_PLINK_NS = "http://schemas.xmlsoap.org/ws/2004/03/partner-link/";
    private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";
    private static final QName PROCESS_ELEMENT = new QName(BPEL_NS, "process");
    private static final QName PARTNERLINK_ELEMENT = new QName(BPEL_NS, "partnerLink");
    private static final QName ONEVENT_ELEMENT = new QName(BPEL_NS, "onEvent");
    private static final QName RECEIVE_ELEMENT = new QName(BPEL_NS, "receive");
    private static final QName ONMESSAGE_ELEMENT = new QName(BPEL_NS, "onMessage");
    private static final QName INVOKE_ELEMENT = new QName(BPEL_NS, "invoke");
    private static final QName IMPORT_ELEMENT = new QName(BPEL_NS, "import");
    private static final String LINKTYPE_NAME = "partnerLinkType";
    private static final QName LINKTYPE_ELEMENT = new QName(BPEL_PLINK_NS, LINKTYPE_NAME);
    public final static String NAME_ELEMENT = "name";
    
    private final static XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    
    private final BPELFactory factory;
    private WSDLFactory WSDLfactory;
    private Monitor monitor;

    public BPELDocumentProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.factory     = modelFactories.getFactory(BPELFactory.class);
        this.WSDLfactory = modelFactories.getFactory(WSDLFactory.class);
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
            processDefinition.setURI(artifactURI);
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
        for (BPELImportElement theImport : theImports) {

            // Deal with WSDL imports
            if (theImport.getImportType().equals(WSDL_NS)) {
                String wsdlLocation = theImport.getLocation();
                String wsdlNamespace = theImport.getNamespace();

                // Resolve the WSDL definition
                WSDLDefinition proxy = WSDLfactory.createWSDLDefinition();
                proxy.setUnresolved(true);
                proxy.setNamespace(wsdlNamespace);
                if (wsdlLocation != null) {
                    proxy.setLocation(URI.create(wsdlLocation));
                }
                WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, proxy);
                if (resolved != null && !resolved.isUnresolved()) {
                    theImport.setWSDLDefinition(resolved);
                } else {
                    error("CannotResolveWSDLReference", resolver, wsdlLocation, wsdlNamespace);
                    return;
                } // end if
            } // end if
        } // end for
    	
    	// Fetch the sets of partner links, port types and interfaces
    	List<BPELPartnerLinkTypeElement> thePLinkTypes = getPartnerLinkTypes( theImports );
    	Collection<WSDLInterface> theInterfaces = (Collection<WSDLInterface>)new ArrayList<WSDLInterface>();
    	Collection<PortType> thePortTypes = getAllPortTypes( theImports, theInterfaces, resolver );
    	
    	// Store the Port Types and the Interfaces for later calculation of the component type...
    	model.getPortTypes().addAll(thePortTypes);
    	model.getInterfaces().addAll(theInterfaces);
    	
    	// Now, for each partnerLink in the BPEL process, find the related partnerLinkType
    	// element 
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
     * Retrieve all the Partner Link types defined in the imported WSDL files
     * 
     * @param theImports
     */
    private List<BPELPartnerLinkTypeElement> getPartnerLinkTypes( List<BPELImportElement> theImports) throws ContributionResolveException {
    	
    	List<BPELPartnerLinkTypeElement> thePLinks = new ArrayList<BPELPartnerLinkTypeElement>();

        // We must find the partner link type elements from amongst the imported
        // WSDLs
        for (BPELImportElement theImport : theImports) {
            if (theImport.getImportType().equals(WSDL_NS)) {

                // Find all the WSDL definitions matching the imported namespace
                List<Definition> wsdlDefinitions = new ArrayList<Definition>();
                WSDLDefinition theWSDL = theImport.getWSDLDefinition();
                wsdlDefinitions.add(theWSDL.getDefinition());
                for (WSDLDefinition importedWSDL: theWSDL.getImportedDefinitions()) {
                    wsdlDefinitions.add(importedWSDL.getDefinition());
                }

                // The BPEL partnerLinkType elements are extension elements within
                // the WSDL definitions
                for (Definition wsdlDefinition: wsdlDefinitions) {
                    for (ExtensibilityElement theElement : (List<ExtensibilityElement>)wsdlDefinition.getExtensibilityElements()) {
                        QName elementType = theElement.getElementType();
                        if (elementType.equals(LINKTYPE_ELEMENT)) {
                            BPELPartnerLinkTypeExt pLinkExt = (BPELPartnerLinkTypeExt)theElement;
                            
                            // Fetch the name of the partnerLinkType
                            String name = pLinkExt.getName();
                            QName qName = new QName(wsdlDefinition.getTargetNamespace(), name);
                            BPELPartnerLinkTypeElement pLinkElement = new BPELPartnerLinkTypeElement(qName);

                            // The partnerLinkType must have one and may have 2 role
                            // child elements
                            int count = 0;
                            for (int i = 0; i < 2; i++) {
                                if (pLinkExt.getRoleName(i) == null)
                                    continue;
                                PortType pType = wsdlDefinition.getPortType(pLinkExt.getRolePortType(i));
                                if (count == 0) {
                                    pLinkElement.setRole1(pLinkExt.getRoleName(i), pLinkExt.getRolePortType(i), pType);
                                    count++;
                                } else if (count == 1) {
                                    pLinkElement.setRole2(pLinkExt.getRoleName(i), pLinkExt.getRolePortType(i), pType);
                                    count++;
                                } else {
                                    break;
                                } // end if
                            } // end for

                            if (count == 0) {
                                error("PartnerLinkTypeNoRoles", theElement, pLinkElement.getName());
                                throw new ContributionResolveException("partnerLinkType " + pLinkElement.getName() + " has no Roles defined");
                            } else
                                thePLinks.add(pLinkElement);
                        } // end if

                    } // end for
                }
            }
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
    private Collection<PortType> getAllPortTypes(List<BPELImportElement> theImports,
                                                 Collection<WSDLInterface> theInterfaces, ModelResolver resolver) throws ContributionResolveException {

        Collection<PortType> thePortTypes = (Collection<PortType>)new ArrayList<PortType>();
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
                    thePortTypes.addAll(portTypes);

                    // Create WSDLInterface elements for each PortType found
                    for (PortType portType : portTypes) {
                        WSDLObject<PortType> wsdlPortType = theWSDL.getWSDLObject(PortType.class, portType.getQName());
                        WSDLInterface wsdlInterface;
                        if (wsdlPortType != null) {
                            // Introspect the WSDL portType and add the resulting
                            // WSDLInterface to the resolver
                            try {
                                theWSDL.setDefinition(wsdlPortType.getDefinition());
                                wsdlInterface = WSDLfactory.createWSDLInterface(wsdlPortType.getElement(), theWSDL, resolver);
                                wsdlInterface.setWsdlDefinition(theWSDL);
                            } catch (InvalidInterfaceException e) {
                                ContributionResolveException ce = new ContributionResolveException(e);
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
        processDefinition.setLocation(doc);

        InputStream is = doc.openStream();
        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(is);

            /*
             * The principle here is to look for partnerLink elements, which
             * form either services or references. A partnerLink can be EITHER -
             * the algorithm for deciding is: 1) Explicit marking with
             * sca:reference or sca:service attribute 2) "first use" of the
             * partnerLink by specific BPEL activity elements: <onEvent../>,
             * <receive../> or <pick../> elements imply a service <invoke../>
             * implies a reference
             */

            // TODO - need to handle <scope../> elements as kind of "nested" processes
            // - and scopes introduce the possibility of partnerLinks with the
            // same name at different levels of scope.... (yuk!!)
            boolean completed = false;
            while (!completed) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName qname = reader.getName();
                        if (BPEL_PROCESS_DEFINITION.equals(qname) || BPEL_EXECUTABLE_DEFINITION.equals(qname)) {
                            QName processName = new QName(getString(reader, org.apache.tuscany.sca.assembly.xml.Constants.TARGET_NAMESPACE), getString(reader, NAME_ELEMENT));
                            processDefinition.setName(processName);
                        } else if (PARTNERLINK_ELEMENT.equals(qname)) {
                            processDefinition.getPartnerLinks().add(processPartnerLinkElement(reader));
                        } else if (ONEVENT_ELEMENT.equals(qname) || RECEIVE_ELEMENT.equals(qname) || ONMESSAGE_ELEMENT.equals(qname)) {
                            processPartnerLinkAsService(reader.getAttributeValue(null, "partnerLink"), processDefinition.getPartnerLinks());
                        } else if (INVOKE_ELEMENT.equals(qname)) {
                            processPartnerLinkAsReference(reader.getAttributeValue(null, "partnerLink"), processDefinition.getPartnerLinks());
                        } else if (IMPORT_ELEMENT.equals(qname)) {
                            processDefinition.getImports().add(processImportElement(reader));
                        } // end if
                        break;
                    case END_ELEMENT:
                        if (PROCESS_ELEMENT.equals(reader.getName())) {
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
     * Processes a partnerLink element from the BPEL process and creates a
     * BPELPartnerLink object
     * 
     * @param reader
     */
    private BPELPartnerLinkElement processPartnerLinkElement(XMLStreamReader reader) throws ContributionReadException {
        BPELPartnerLinkElement partnerLink = new BPELPartnerLinkElement(
                                                                        reader.getAttributeValue(null, "name"),
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
            Problem problem = new ProblemImpl(this.getClass().getName(), "impl-bpel-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
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
            Problem problem = new ProblemImpl(this.getClass().getName(), "impl-bpel-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
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
            Problem problem = new ProblemImpl(this.getClass().getName(), "impl-bpel-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }
    }
}
