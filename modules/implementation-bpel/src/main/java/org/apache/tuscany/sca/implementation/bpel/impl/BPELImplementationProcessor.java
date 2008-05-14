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
package org.apache.tuscany.sca.implementation.bpel.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.implementation.bpel.BPELFactory;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;
import org.apache.tuscany.sca.implementation.bpel.DefaultBPELFactory;
import org.apache.tuscany.sca.implementation.bpel.xml.BPELPartnerLinkElement;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;

/**
 * Implements a STAX artifact processor for BPEL implementations.
 * 
 * The artifact processor is responsible for processing <implementation.bpel>
 * elements in SCA assembly XML composite files and populating the BPEL
 * implementation model, resolving its references to other artifacts in the SCA
 * contribution, and optionally write the model back to SCA assembly XML.
 * 
 *  @version $Rev$ $Date$
 */
public class BPELImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<BPELImplementation> {
	private static final String PROCESS = "process";
	private static final String IMPLEMENTATION_BPEL = "implementation.bpel";
	private static final QName IMPLEMENTATION_BPEL_QNAME = new QName(Constants.SCA10_NS, IMPLEMENTATION_BPEL);
    
    private AssemblyFactory assemblyFactory;
    private BPELFactory bpelFactory;
    private WSDLFactory wsdlFactory;
    
    public BPELImplementationProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        this.bpelFactory = new DefaultBPELFactory(modelFactories);
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_BPEL_QNAME;
    }

    public Class<BPELImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return BPELImplementation.class;
    }

    public BPELImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        assert IMPLEMENTATION_BPEL_QNAME.equals(reader.getName());
        
        // Read an <implementation.bpel> element

        // Read the process attribute. 
        QName process = getAttributeValueNS(reader, PROCESS);


        // Create and initialize the BPEL implementation model
        BPELImplementation implementation = bpelFactory.createBPELImplementation();
        implementation.setProcess(process);
        implementation.setUnresolved(true);
        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_BPEL_QNAME.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(BPELImplementation impl, ModelResolver resolver) throws ContributionResolveException {
        if( impl != null && impl.isUnresolved()) {
            BPELProcessDefinition processDefinition = resolveBPELProcessDefinition(impl, resolver);
            if(processDefinition.isUnresolved()) {
                throw new ContributionResolveException("Can't find BPEL Process : " + processDefinition.getName());
            }
            
            impl.setProcessDefinition(processDefinition);
            
            // Get the component type from the process definition
            generateComponentType( impl );
            
            //resolve component type
            mergeComponentType(resolver, impl);
                        
            //set current implementation resolved 
            impl.setUnresolved(false);
        }
        
    } // end resolve

    /*
     * Write out the XML representation of the BPEL implementation
     * <implementation.bpel process="..." />
     * 
     * One complexity here is that the value of the process attribute is a QName
     * In this implementation, the QName is written out in XML Namespaces recommendation format,
     * as described in the documentation of the getAttributeValueNS method:
     * 
     * ie:  {http://example.com/somenamespace}SomeName
     * 
     * This may well NOT be the format in which the attribute was originally read from the
     * composite file.
     */
    public void write( BPELImplementation bpelImplementation, 
    		           XMLStreamWriter writer ) throws ContributionWriteException, XMLStreamException {
        //FIXME Deal with policy processing...
        // Write <implementation.bpel process="..."/>
        // policyProcessor.writePolicyPrefixes(bpelImplementation, writer);
        writer.writeStartElement(Constants.SCA10_NS, IMPLEMENTATION_BPEL);
        // policyProcessor.writePolicyAttributes(bpelImplementation, writer);
        
        if (bpelImplementation.getProcess() != null) {
            writer.writeAttribute(PROCESS, bpelImplementation.getProcess().toString() );
        }

        writer.writeEndElement();

    } // end write

    private BPELProcessDefinition resolveBPELProcessDefinition(BPELImplementation impl, ModelResolver resolver) throws ContributionResolveException {
        QName processName = impl.getProcess();
        BPELProcessDefinition processDefinition = this.bpelFactory.createBPELProcessDefinition();
        processDefinition.setName(processName);
        processDefinition.setUnresolved(true);
        
        return resolver.resolveModel(BPELProcessDefinition.class, processDefinition);
    } // end resolveBPELProcessDefinition
    
    // Calculates the component type of the supplied implementation and attaches it to the
    // implementation
    private void generateComponentType(BPELImplementation impl ) 
    	throws ContributionResolveException {
        // Create a ComponentType and mark it unresolved
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        impl.setComponentType(componentType);
        
        // Each partner link in the process represents either a service or a reference
        // - or both, in the sense of involving a callback
        BPELProcessDefinition theProcess = impl.getProcessDefinition();
        List<BPELPartnerLinkElement> partnerLinks = theProcess.getPartnerLinks();
        
        for( BPELPartnerLinkElement pLink : partnerLinks ) {
        	// check that the partner link has been designated as service or reference in SCA terms
        	if ( pLink.isSCATyped() ) {
        		String SCAName = pLink.getSCAName(); 
        		if( pLink.querySCAType().equals("reference") ) {
        			componentType.getReferences().add(
	        			generateReference( SCAName, 
	        					           pLink.getMyRolePortType(), 
	        					           pLink.getPartnerRolePortType(),
		        					       theProcess.getInterfaces() )
        			);
        		} else {
        			componentType.getServices().add(
	        			generateService( SCAName, 
	        					         pLink.getMyRolePortType(), 
	        					         pLink.getPartnerRolePortType(),
	        					         theProcess.getInterfaces() ) 
        			);
        		} // end if
        	} // end if
        } // end for
        
    	
    } // end getComponentType
    
    /**
     * Create an SCA reference for a partnerLink
     * @param name - name of the reference
     * @param myRolePT - partner link type of myRole
     * @param partnerRolePT - partner link type of partnerRole
     * @param theInterfaces - list of WSDL interfaces associated with the BPEL process
     * @return
     */
    private Reference generateReference( String name, PortType myRolePT, 
    		PortType partnerRolePT, Collection<WSDLInterface> theInterfaces  ) 
    		throws ContributionResolveException {
        Reference reference = assemblyFactory.createReference();
        WSDLInterfaceContract interfaceContract = wsdlFactory.createWSDLInterfaceContract();
        reference.setInterfaceContract(interfaceContract);
        
        // Establish whether there is just a call interface or a call + callback interface
        PortType callPT = null;
        PortType callbackPT = null;
        if( myRolePT != null ) {
        	callPT = myRolePT;
        	if( partnerRolePT != null ) {
        		if( !myRolePT.equals(partnerRolePT) ){
        			callbackPT = partnerRolePT;
        		} // end if
        	} // end if
        } else if ( partnerRolePT != null ) {
        	callPT = partnerRolePT;
        } // end if
        // No interfaces mean an error - throw an exception
        if( callPT == null && callbackPT == null ) {
        	throw new ContributionResolveException("Error: myRole and partnerRole port types are both null");
        } // end if 
        
        // Set the name of the reference to the supplied name and the multiplicity of the reference
        // to 1..1 
        // TODO: support other multiplicities 
        reference.setName(name);
        reference.setMultiplicity(Multiplicity.ONE_ONE);

        // Set the call interface and, if present, the callback interface
        WSDLInterface callInterface = null;
        for( WSDLInterface anInterface : theInterfaces ) {
        	if( anInterface.getPortType().equals(callPT)) callInterface = anInterface;
        } // end for
        // Throw an exception if no interface is found
        if( callInterface == null ) {
        	throw new ContributionResolveException("Interface not found for port type " +
        			callPT.getQName().toString() );
        } // end if 
        reference.getInterfaceContract().setInterface(callInterface);
 
        // There is a callback if the partner role is not null and if the partner role port type
        // is not the same as the port type for my role
        if ( callbackPT != null ) {
            WSDLInterface callbackInterface = null;
            for( WSDLInterface anInterface : theInterfaces ) {
            	if( anInterface.getPortType().equals(callbackPT)) callbackInterface = anInterface;
            } // end for
            // Throw an exception if no interface is found
            if( callbackInterface == null ) {
            	throw new ContributionResolveException("Interface not found for port type " +
            			callbackPT.getQName().toString() );
            } // end if 
            reference.getInterfaceContract().setCallbackInterface(callbackInterface);
        } // end if
    	
    	return reference;
    } // end generateReference
    
    /**
     * Create an SCA service for a partnerLink
     * @param name - name of the reference
     * @param myRolePT - partner link type of myRole
     * @param partnerRolePT - partner link type of partnerRole
     * @param theInterfaces - list of WSDL interfaces associated with the BPEL process
     * @return
     */
    private Service generateService( String name, PortType myRolePT, 
    		PortType partnerRolePT, Collection<WSDLInterface> theInterfaces ) 
    		throws ContributionResolveException {
        Service service = assemblyFactory.createService();
        WSDLInterfaceContract interfaceContract = wsdlFactory.createWSDLInterfaceContract();
        service.setInterfaceContract(interfaceContract);
        
        // Set the name of the service to the supplied name 
        service.setName(name);
        
        // Establish whether there is just a call interface or a call + callback interface
        PortType callPT = null;
        PortType callbackPT = null;
        if( myRolePT != null ) {
        	callPT = myRolePT;
        	if( partnerRolePT != null ) {
        		if( !myRolePT.equals(partnerRolePT) ){
        			callbackPT = partnerRolePT;
        		} // end if
        	} // end if
        } else if ( partnerRolePT != null ) {
        	callPT = partnerRolePT;
        } // end if
        // No interfaces mean an error - throw an exception
        if( callPT == null && callbackPT == null ) {
        	throw new ContributionResolveException("Error: myRole and partnerRole port types are both null");
        } // end if 


        // Set the call interface and, if present, the callback interface
        WSDLInterface callInterface = null;
        for( WSDLInterface anInterface : theInterfaces ) {
        	if( anInterface.getPortType().equals(callPT)) callInterface = anInterface;
        } // end for
        // Throw an exception if no interface is found
        if( callInterface == null ) {
        	throw new ContributionResolveException("Interface not found for port type " +
        			callPT.getQName().toString() );
        } // end if 

        service.getInterfaceContract().setInterface(callInterface);    
        
        // There is a callback if the partner role is not null and if the partner role port type
        // is not the same as the port type for my role
        if ( callbackPT != null ) {
            WSDLInterface callbackInterface = null;
            for( WSDLInterface anInterface : theInterfaces ) {
            	if( anInterface.getPortType().equals(callbackPT)) callbackInterface = anInterface;
            } // end for
            // Throw an exception if no interface is found
            if( callbackInterface == null ) {
            	throw new ContributionResolveException("Interface not found for port type " +
            			callbackPT.getQName().toString() );
            } // end if 

            service.getInterfaceContract().setCallbackInterface(callbackInterface);
        } // end if
    	
    	return service;
    } // end generateService
    
    /**
     * Merge the componentType from introspection and from external file
     * @param resolver
     * @param impl
     */
    private void mergeComponentType(ModelResolver resolver, BPELImplementation impl) {
    	// Load the component type from a component type file, if any
        ComponentType componentType = getComponentType(resolver, impl);
        if (componentType != null && !componentType.isUnresolved()) {
            
            Map<String, Reference> refMap = new HashMap<String, Reference>();
            for (Reference ref : impl.getReferences()) {
                refMap.put(ref.getName(), ref);
            }
            for (Reference reference : componentType.getReferences()) {
            	//set default dataBinding to DOM to help on reference invocation
            	reference.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
                refMap.put(reference.getName(), reference);
            }
            impl.getReferences().clear();
            impl.getReferences().addAll(refMap.values());

            Map<String, Service> serviceMap = new HashMap<String, Service>();
            for (Service service : componentType.getServices()) {
                //set default dataBinding to DOM
                service.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
                
                serviceMap.put(service.getName(), service);
            }
            // For the present, overwrite anything arising from the component type sidefile if
            // equivalent services are defined in the implementation
            for (Service svc : impl.getServices()) {
                if(svc != null) {
                    serviceMap.put(svc.getName(), svc);    
                }
            }

            impl.getServices().clear();
            impl.getServices().addAll(serviceMap.values());

            Map<String, Property> propMap = new HashMap<String, Property>();
            for (Property prop : impl.getProperties()) {
                propMap.put(prop.getName(), prop);
            }
        }
    }


    /**
     * Find the componentType side file based on the BPEL implementation artifact
     * @param resolver
     * @param impl
     * @return
     */
    private ComponentType getComponentType(ModelResolver resolver, BPELImplementation impl) {
        String bpelProcessURI = impl.getProcessDefinition().getURI().toString();
        
        // Get the component type definition contained in the componentType file, if any
        String componentTypeURI = bpelProcessURI.replace(".bpel", ".componentType");
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        componentType.setURI(componentTypeURI);
        componentType = resolver.resolveModel(ComponentType.class, componentType);
        if (!componentType.isUnresolved()) {
            return componentType;
        }
        return null;
    } // end getComponentType

    /**
     * Returns a QName of a BPEL process as from its string representation in the process
     * attribute in the process XML
     * 
     * The process attribute of a BPEL process is a QName - this may be presented in one of
     * two alternative formats:
     * 1) In the form of a local name with a prefix, with the prefix referencing a namespace
     * URI declaration elsewhere in the composite (typically on the composite element)
     * 
     * ie:   nms:SomeName
     *       xmlns:nms="http://example.com/somenamespace"
     *       
     * 2) In the XML Namespaces recommendation format (see http://jclark.com/xml/xmlns.htm )
     * where the namespace URI and the local name are encoded into a single string, with the 
     * namespace URI enclosed between a pair of braces {...}
     * 
     *  ie:  {http://example.com/somenamespace}SomeName
     */
    private QName getAttributeValueNS(XMLStreamReader reader, String attribute) {
        String fullValue = reader.getAttributeValue(null, "process");
        
        // Deal with the attribute in the XML Namespaces recommendation format
        // - trim off any leading/trailing spaces and check that the first character is '{'
        if( fullValue.trim().charAt(0) == '{' ){
        	try{
        		// Attempt conversion to a QName object
        		QName theProcess = QName.valueOf( fullValue );
        		return theProcess;
        	} catch ( IllegalArgumentException e ) {
        		// This exception happens if the attribute begins with '{' but doesn't conform
        		// to the XML Namespaces recommendation format
        		throw new BPELProcessException("Attribute " + attribute + " with value " + fullValue +
                " in your composite should be of the form {namespaceURI}localname");
        	}
        } // endif
        
        // Deal with the attribute in the local name + prefix format
        if (fullValue.indexOf(":") < 0)
            throw new BPELProcessException("Attribute " + attribute + " with value " + fullValue +
                    " in your composite should be prefixed (process=\"prefix:name\").");
        String prefix = fullValue.substring(0, fullValue.indexOf(":"));
        String name = fullValue.substring(fullValue.indexOf(":") + 1);
        String nsUri = reader.getNamespaceContext().getNamespaceURI(prefix);
        if (nsUri == null)
            throw new BPELProcessException("Attribute " + attribute + " with value " + fullValue +
                    " in your composite has un unrecognized namespace prefix.");
        return new QName(nsUri, name, prefix);
    }

}
