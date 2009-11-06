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
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.bpel.BPELFactory;
import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * BPEL document processor responsible for reading a BPEL file and producing necessary model info about it
 * 
 * Handles both BPEL 1.1 documents and BPEL 2.0 documents
 * @version $Rev$ $Date$
 */
public class BPELDocumentProcessor extends BaseStAXArtifactProcessor implements URLArtifactProcessor<BPELProcessDefinition> {
    
    private final static XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    
    private final BPELFactory factory;
    private WSDLFactory WSDLfactory;
    private AssemblyFactory assemblyFactory;

    public BPELDocumentProcessor(FactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(BPELFactory.class);
        this.WSDLfactory = modelFactories.getFactory(WSDLFactory.class);
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
    }
    
    public String getArtifactType() {
        return "*.bpel";
    }    

    public Class<BPELProcessDefinition> getModelType() {
        return BPELProcessDefinition.class;
    }

    /** 
     * Read the BPEL Process definition file from the location identified by an artifact URL
     * @param contributionURL	- URL of the Contribution containing the Process definition
     * @param artifactURI		- URI of the artifact containing the BPEL Process definition
     * @param artifactURL		- URL of the artifact containing the BPEL Process definition
     * @return BPELProcessDefinition - SCA model of the BPEL Process
     */
    public BPELProcessDefinition read(URL contributionURL, URI artifactURI, URL artifactURL, ProcessorContext context) throws ContributionReadException {
        BPELProcessDefinition processDefinition = null;
        try {
            processDefinition = readProcessDefinition(artifactURL, context.getMonitor());
            processDefinition.setURI(artifactURI.toString());
            processDefinition.setUnresolved(true);
        } catch (Exception e) {
            ContributionReadException ce = new ContributionReadException(e);
            error(context.getMonitor(), "ContributionReadException", artifactURL, ce);
        }

        return processDefinition;
    }

    public void resolve(BPELProcessDefinition model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
    	// Delegate resolving to model resolver
    	if (model != null || model.isUnresolved()) {
    		resolver.resolveModel(BPELProcessDefinition.class, model, context);
    	}
    	
    } // end resolve

    /**
     * Read a process definition.
     * 
     * @param doc
     * @return
     * @throws Exception
     */
    private BPELProcessDefinition readProcessDefinition(URL doc, Monitor monitor) throws Exception {
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
                        if (BPELProcessorConstants.PROCESS_ELEMENT.equals(qname) || BPELProcessorConstants.PROCESS_ELEMENT_20.equals(qname)) {
                            QName processName = new QName(getString(reader, BPELProcessorConstants.TARGET_NAMESPACE), getString(reader, BPELProcessorConstants.NAME_ELEMENT));
                            processDefinition.setName(processName);
                        } else if (BPELProcessorConstants.PARTNERLINK_ELEMENT.equals(qname) || BPELProcessorConstants.PARTNERLINK_ELEMENT_20.equals(qname)) {
                            processDefinition.getPartnerLinks().add(processPartnerLinkElement(reader, monitor));
                        } else if (BPELProcessorConstants.ONEVENT_ELEMENT.equals(qname) || BPELProcessorConstants.RECEIVE_ELEMENT.equals(qname) || BPELProcessorConstants.ONMESSAGE_ELEMENT.equals(qname) || 
                        		BPELProcessorConstants.ONEVENT_ELEMENT_20.equals(qname) || BPELProcessorConstants.RECEIVE_ELEMENT_20.equals(qname) || BPELProcessorConstants.ONMESSAGE_ELEMENT_20.equals(qname)) {
                            processPartnerLinkAsService(reader.getAttributeValue(null, "partnerLink"), processDefinition.getPartnerLinks(), monitor);
                        } else if (BPELProcessorConstants.INVOKE_ELEMENT.equals(qname) || BPELProcessorConstants.INVOKE_ELEMENT_20.equals(qname)) {
                            processPartnerLinkAsReference(reader.getAttributeValue(null, "partnerLink"), processDefinition.getPartnerLinks(), monitor);
                        } else if (BPELProcessorConstants.IMPORT_ELEMENT.equals(qname) || BPELProcessorConstants.IMPORT_ELEMENT_20.equals(qname)) {
                            processDefinition.getImports().add(processImportElement(reader));
                        } else if (BPELProcessorConstants.VARIABLE_ELEMENT.equals(qname) || BPELProcessorConstants.VARIABLE_ELEMENT_20.equals(qname)) {
                        	// deal with variables that are SCA properties through the presence of a sca-bpel:property="yes" attribute
                        	Property aProperty = processVariableElement(reader);
                        	if( aProperty != null ) {
                        		processDefinition.getProperties().add( aProperty );
                        	} // end if
                        } // end if
                        break;
                    case END_ELEMENT:
                    	qname = reader.getName();
                    	if (BPELProcessorConstants.PROCESS_ELEMENT.equals(qname) || BPELProcessorConstants.PROCESS_ELEMENT_20.equals(qname)) {
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
    	String scaProperty = reader.getAttributeValue(BPELProcessorConstants.SCA_BPEL_NS, "property");
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
    private BPELPartnerLinkElement processPartnerLinkElement(XMLStreamReader reader, Monitor monitor) throws ContributionReadException {
        BPELPartnerLinkElement partnerLink = new BPELPartnerLinkElement( reader.getAttributeValue(null, "name"),
                                                                         getQNameValue(reader, reader.getAttributeValue(null, "partnerLinkType")),
                                                                         reader.getAttributeValue(null, "myRole"),
                                                                         reader.getAttributeValue(null, "partnerRole"));
        
        // See if there are any SCA extension attributes
        String scaService = reader.getAttributeValue(BPELProcessorConstants.SCA_BPEL_NS, "service");
        String scaReference = reader.getAttributeValue(BPELProcessorConstants.SCA_BPEL_NS, "reference");
        if ((scaService != null) && (scaReference != null)) {
            // It is incorrect to set both service & reference attributes
            error(monitor, "PartnerLinkHasBothAttr", partnerLink, reader.getAttributeValue(null, "name"));
            throw new ContributionReadException("BPEL PartnerLink " + reader.getAttributeValue(null, "name") + 
            		   " has both sca:reference and sca:service attributes set");
        } // end if
        
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
    private void processPartnerLinkAsService(String partnerLinkName, List<BPELPartnerLinkElement> partnerLinks, Monitor monitor) {
        BPELPartnerLinkElement partnerLink = findPartnerLinkByName(partnerLinks, partnerLinkName);
        if (partnerLink == null) {
            warning(monitor, "ReferencePartnerLinkNotInList", partnerLinkName, partnerLinkName);
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
    private void processPartnerLinkAsReference(String partnerLinkName, List<BPELPartnerLinkElement> partnerLinks, Monitor monitor) {
        BPELPartnerLinkElement partnerLink = findPartnerLinkByName(partnerLinks, partnerLinkName);
        if (partnerLink == null) {
            warning(monitor, "ReferencePartnerLinkNotInList", partnerLinkName, partnerLinkName);
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
