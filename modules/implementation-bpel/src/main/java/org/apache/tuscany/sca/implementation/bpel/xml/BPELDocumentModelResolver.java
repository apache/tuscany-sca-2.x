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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
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
 * A Model Resolver for BPEL process models.
 *
 * @version $Rev$ $Date$
 */
public class BPELDocumentModelResolver implements ModelResolver {
	
    private WSDLFactory wsdlFactory;
    private Contribution contribution;
    private Map<QName, BPELProcessDefinition> map = new HashMap<QName, BPELProcessDefinition>();
    
    public BPELDocumentModelResolver(Contribution contribution, FactoryExtensionPoint modelFactories) {
    	this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        this.contribution = contribution;
    }

    public void addModel(Object resolved, ProcessorContext context) {
        BPELProcessDefinition process = (BPELProcessDefinition)resolved;
        map.put(process.getName(), process);
    }
    
    public Object removeModel(Object resolved, ProcessorContext context) {
        return map.remove(((BPELProcessDefinition)resolved).getName());
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved, ProcessorContext context) {    	
    	BPELProcessDefinition resolved = null;
    	QName qname = ((BPELProcessDefinition)unresolved).getName();
    	
    	// Lookup a definition for the given namespace, from imports
    	List<String> locations = new ArrayList<String>();
        // Collection of namespace imports with location
        Map<String, NamespaceImport> locationMap = new HashMap<String, NamespaceImport>();
        for (Import import_ : this.contribution.getImports()) {
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                if (namespaceImport.getNamespace().equals(qname.getNamespaceURI())) {
                    if (namespaceImport.getLocation() == null) {
	                    // Delegate the resolution to the import resolver
	                    resolved = namespaceImport.getModelResolver().resolveModel(BPELProcessDefinition.class, (BPELProcessDefinition)unresolved, context);
	                    if (!resolved.isUnresolved()) {
	                        return modelClass.cast(resolved);
	                    }
                    } else {
                    	// We might have multiple imports for the same namespace,
                		// need to search them in lexical order.
                		locations.add(namespaceImport.getLocation());
                    }
                }
            }
        }
        // Search namespace imports with locations in lexical order
        Collections.sort(locations);
        for (String location : locations) {
        	NamespaceImport namespaceImport = (NamespaceImport)locationMap.get(location);
        	// Delegate the resolution to the namespace import resolver
            resolved = namespaceImport.getModelResolver().resolveModel(BPELProcessDefinition.class, (BPELProcessDefinition)unresolved, context);
            if (!resolved.isUnresolved()) {
                return modelClass.cast(resolved);
            }
        }
        
        
        // Not found, Lookup a definition for the given namespace, within contribution       
        resolved = (BPELProcessDefinition) map.get(qname);
        
        if(resolved.isUnresolved()) {
        	try {
        		resolve(resolved, context);
        	} catch(Exception e) {
        		//FIXME
        	}
        }
        
        if (resolved != null) {
            return modelClass.cast(resolved);
        }        
        
        return (T)unresolved;
    }
    
    public void resolve(BPELProcessDefinition unresolved, ProcessorContext context) throws ContributionResolveException {
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

    	List<BPELImportElement> theImports = unresolved.getImports();
    	Set<Definition> wsdlDefinitions = getImportedWSDLDefinitions( theImports, contribution.getModelResolver(), context );
    	
    	// Fetch the sets of partner links, port types and interfaces
    	List<BPELPartnerLinkTypeElement> thePLinkTypes = getPartnerLinkTypes( wsdlDefinitions, context.getMonitor() );
    	Collection<WSDLInterface> theInterfaces = (Collection<WSDLInterface>)new ArrayList<WSDLInterface>();
    	Collection<PortType> thePortTypes = getAllPortTypes( theImports, theInterfaces, contribution.getModelResolver(), context );
    	
    	// Store the Port Types and the Interfaces for later calculation of the component type...
    	unresolved.getPortTypes().addAll(thePortTypes);
    	unresolved.getInterfaces().addAll(theInterfaces);
    	
    	// Now, for each partnerLink in the BPEL process, find the related partnerLinkType element 
        List<BPELPartnerLinkElement> thePartnerLinks = unresolved.getPartnerLinks();
        for (BPELPartnerLinkElement thePartnerLink : thePartnerLinks) {
            QName partnerLinkType = thePartnerLink.getPartnerLinkType();
            BPELPartnerLinkTypeElement pLinkType = findPartnerLinkType(partnerLinkType, thePLinkTypes);
            if (pLinkType == null) {
                error(context.getMonitor(), "PartnerLinkNoMatchingType", thePartnerLink, thePartnerLink.getName());
            } else {
                thePartnerLink.setPartnerLinkType(pLinkType);
            }
        } // end for
        
        unresolved.setUnresolved(false);
    	
    } // end resolve    
    
    /**
     * Get all the WSDL definitions referenced through the import statements of the BPEL process
     * @param theImports - a list of the import statements
     * @return - a Set containing all the referenced WSDL definitions
     */
    private Set<Definition> getImportedWSDLDefinitions( List<BPELImportElement> theImports, ModelResolver resolver, ProcessorContext context ) {
    	Set<Definition> wsdlDefinitions = null;
    	for (BPELImportElement theImport : theImports) {
            if (theImport.getImportType().equals(BPELProcessorConstants.WSDL_NS)) {
            	// If the Import is a WSDL import, resolve the WSDL
            	WSDLDefinition theWSDL = resolveWSDLDefinition( theImport.getLocation(), 
            			                                        theImport.getNamespace(), resolver, context );
                if( theWSDL != null ) {
	            	theImport.setWSDLDefinition( theWSDL );
	            	
	                // Find all the WSDL definitions matching the imported namespace
	            	if( wsdlDefinitions == null ) {
	            		wsdlDefinitions = new HashSet<Definition>();
	            	} // end if 
	            	
	                wsdlDefinitions.add(theWSDL.getDefinition());
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
     * @param context 
     * @return - a WSDLDefinition object for the referenced WSDL, or null if the WSDL cannot be resolved
     */
    private WSDLDefinition resolveWSDLDefinition( String wsdlLocation, String wsdlNamespace, ModelResolver resolver, ProcessorContext context ) {
        
        // Resolve the WSDL definition
        WSDLDefinition proxy = wsdlFactory.createWSDLDefinition();
        proxy.setUnresolved(true);
        proxy.setNamespace(wsdlNamespace);
        if (wsdlLocation != null) {
            proxy.setLocation(URI.create(wsdlLocation));
        }
        WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, proxy, context);
        if (resolved != null && !resolved.isUnresolved()) {
        	return resolved;
        } else {
            error(context.getMonitor(), "CannotResolveWSDLReference", resolver, wsdlLocation, wsdlNamespace);
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
    private List<BPELPartnerLinkTypeElement> getPartnerLinkTypes( Set<Definition> wsdlDefinitions, Monitor monitor ) throws ContributionResolveException {
    	
    	List<BPELPartnerLinkTypeElement> thePLinks = new ArrayList<BPELPartnerLinkTypeElement>();

        // The BPEL partnerLinkType elements are extension elements within the WSDL definitions
        for (Definition wsdlDefinition: wsdlDefinitions) {
            for (ExtensibilityElement theElement : (List<ExtensibilityElement>)wsdlDefinition.getExtensibilityElements()) {
                QName elementType = theElement.getElementType();
                if (elementType.equals(BPELProcessorConstants.LINKTYPE_ELEMENT) || elementType.equals(BPELProcessorConstants.LINKTYPE_ELEMENT_20)) {
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
                        error(monitor, "PartnerLinkTypeNoRoles", theElement, pLinkElement.getName());
                        throw new ContributionResolveException("partnerLinkType " + pLinkElement.getName() + " has no Roles defined");
                    } else
                        thePLinks.add(pLinkElement);
                } // end if
            } // end for
        } // end for
        return thePLinks;
    } // end getPartnerLinkTypes
    

    /**
     * Finds a partnerLinkType definition within the WSDLs imported by the BPEL
     * process.
     * 
     * @param partnerLinkTypeName - the name of the partnerLinkType
     * @param theImports a list of the WSDL import declarations
     * @return a BPELPartnerLinkTypeElement for the partnerLinkType or null if it cannot be
     * found
     */
    private BPELPartnerLinkTypeElement findPartnerLinkType( QName partnerLinkTypeName, List<BPELPartnerLinkTypeElement> thePLinkTypes) {
    	// We must find the partner link type element from amongst the imported WSDLs
    	for ( BPELPartnerLinkTypeElement thePLinkType : thePLinkTypes ){
    		if( thePLinkType.getName().equals(partnerLinkTypeName) ) return thePLinkType;
     	} // end for
    	return null;
    } // end findPartnerLinkType
    

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
                                                 Collection<WSDLInterface> theInterfaces, 
                                                 ModelResolver resolver,
                                                 ProcessorContext context) throws ContributionResolveException {

        Set<PortType> thePortTypes = new HashSet<PortType>();
        for (BPELImportElement theImport : theImports) {
            if (theImport.getImportType().equals(BPELProcessorConstants.WSDL_NS)) {
                
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
                                wsdlInterface = wsdlFactory.createWSDLInterface(wsdlPortType.getElement(), theWSDL, resolver, context.getMonitor());
                                wsdlInterface.setWsdlDefinition(theWSDL);
                            } catch (InvalidInterfaceException e) {
                                ContributionResolveException ce = 
                                	new ContributionResolveException("Unable to create WSDLInterface for portType " + portType.getQName(),e);
                                error(context.getMonitor(), "ContributionResolveException", resolver, ce);
                                throw ce;
                            } // end try
                            resolver.addModel(wsdlInterface, context);
                            theInterfaces.add(wsdlInterface);
                        } // end if
                    } // end for
                }
            }
        } // end for

        return thePortTypes;
    } // end getAllPortTypes
    
    /**
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void warning(Monitor monitor, String message, Object model, Object... messageParameters) {
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
    private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
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
    private void error(Monitor monitor, String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "impl-bpel-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }
    }
    
}
