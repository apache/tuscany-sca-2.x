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
import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
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
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * Implements a StAX artifact processor for BPEL implementations.
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
    private Monitor monitor;
    
    public BPELImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        this.bpelFactory = modelFactories.getFactory(BPELFactory.class);
        this.monitor = monitor;
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
        BPELImplementation implementation = null;
        
        // Read the process attribute. 
        QName process = getAttributeValueNS(reader, PROCESS);
        if (process == null) {
        	return implementation;
        }

        // Create and initialize the BPEL implementation model
        implementation = bpelFactory.createBPELImplementation();
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

    public void resolve(BPELImplementation implementation, ModelResolver resolver) throws ContributionResolveException {
        
    	if( implementation != null && implementation.isUnresolved()) 
    	{
            BPELProcessDefinition processDefinition = resolveBPELProcessDefinition(implementation, resolver);
            if(processDefinition.isUnresolved()) {
            	error("BPELProcessNotFound", implementation, processDefinition.getName());
            } else {            
                implementation.setProcessDefinition(processDefinition);
            
                // Get the component type from the process definition
                generateComponentType( implementation );
            
                //resolve component type
                mergeComponentType(resolver, implementation);
                        
                //set current implementation resolved 
                implementation.setUnresolved(false);
            }
        }
        
    } // end resolve

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
    
    /**
     * Calculates the component type of the supplied implementation and attaches it to the
     * implementation.
     * 
     * @param impl
     * @throws ContributionResolveException
     */
    private void generateComponentType(BPELImplementation impl) throws ContributionResolveException {

        // Create a ComponentType and mark it unresolved
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        impl.setComponentType(componentType);

        // Each partner link in the process represents either a service or a
        // reference
        // - or both, in the sense of involving a callback
        BPELProcessDefinition theProcess = impl.getProcessDefinition();
        List<BPELPartnerLinkElement> partnerLinks = theProcess.getPartnerLinks();

        for (BPELPartnerLinkElement pLink : partnerLinks) {

            // check that the partner link has been designated as service or
            // reference in SCA terms
            if (pLink.isSCATyped()) {
                String scaName = pLink.getSCAName();
                if (pLink.querySCAType().equals("reference")) {
                    componentType.getReferences().add(generateReference(scaName, pLink.getMyRolePortType(), pLink.getPartnerRolePortType(), theProcess.getInterfaces()));
                } else {
                    componentType.getServices().add(generateService(scaName, pLink.getMyRolePortType(), pLink.getPartnerRolePortType(), theProcess.getInterfaces()));
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
    		PortType partnerRolePT, Collection<WSDLInterface> theInterfaces) throws ContributionResolveException {
        
        Reference reference = assemblyFactory.createReference();
        WSDLInterfaceContract interfaceContract = wsdlFactory.createWSDLInterfaceContract();
        reference.setInterfaceContract(interfaceContract);

        // Establish whether there is just a call interface or a call + callback
        // interface
        PortType callPT = null;
        PortType callbackPT = null;
        if (myRolePT != null) {
            callPT = myRolePT;
            // If the 2 port types are not the same one, there is a callback...
            if (partnerRolePT != null) {
                if (!myRolePT.getQName().equals(partnerRolePT.getQName())) {
                    callbackPT = partnerRolePT;
                } // end if
            } // end if
        } else if (partnerRolePT != null) {
            callPT = partnerRolePT;
        } // end if

        // No interfaces mean an error
        if (callPT == null && callbackPT == null) {
            error("MyRolePartnerRoleNull", theInterfaces);
        } // end if

        // Set the name of the reference to the supplied name and the
        // multiplicity of the reference
        // to 1..1
        // TODO: support other multiplicities
        reference.setName(name);
        reference.setMultiplicity(Multiplicity.ONE_ONE);

        if (callPT != null) {
            // Set the call interface and, if present, the callback interface
            WSDLInterface callInterface = null;
            for (WSDLInterface anInterface : theInterfaces) {
                if (anInterface.getPortType().getQName().equals(callPT.getQName()))
                    callInterface = anInterface;
            } // end for
            if (callInterface == null) {
                error("NoInterfaceForPortType", theInterfaces, callPT.getQName().toString());
            } else
                reference.getInterfaceContract().setInterface(callInterface);
        }

        // There is a callback if the partner role is not null and if the
        // partner role port type
        // is not the same as the port type for my role
        if (callbackPT != null) {
            WSDLInterface callbackInterface = null;
            for (WSDLInterface anInterface : theInterfaces) {
                if (anInterface.getPortType().getQName().equals(callbackPT.getQName()))
                    callbackInterface = anInterface;
            } // end for
            if (callbackInterface == null) {
                error("NoInterfaceForPortType", theInterfaces, callbackPT.getQName().toString());
            } else
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

        // Establish whether there is just a call interface or a call + callback
        // interface
        PortType callPT = null;
        PortType callbackPT = null;
        if (myRolePT != null) {
            callPT = myRolePT;
            // If the 2 port types are not the same one, there is a callback...
            if (partnerRolePT != null) {
                if (!myRolePT.getQName().equals(partnerRolePT.getQName())) {
                    callbackPT = partnerRolePT;
                } // end if
            } // end if
        } else if (partnerRolePT != null) {
            callPT = partnerRolePT;
        } // end if

        // No interfaces mean an error
        if (callPT == null && callbackPT == null) {
            error("MyRolePartnerRoleNull", theInterfaces);
        } // end if

        if (callPT != null) {
            // Set the call interface and, if present, the callback interface
            WSDLInterface callInterface = null;
            for (WSDLInterface anInterface : theInterfaces) {
                if (anInterface.getPortType().getQName().equals(callPT.getQName()))
                    callInterface = anInterface;
            } // end for
            if (callInterface == null) {
                error("NoInterfaceForPortType", theInterfaces, callPT.getQName().toString());
            } else
                service.getInterfaceContract().setInterface(callInterface);
        } // end if

        // There is a callback if the partner role is not null and if the
        // partner role port type
        // is not the same as the port type for my role
        if (callbackPT != null) {
            WSDLInterface callbackInterface = null;
            for (WSDLInterface anInterface : theInterfaces) {
                if (anInterface.getPortType().getQName().equals(callbackPT.getQName()))
                    callbackInterface = anInterface;
            } // end for
            if (callbackInterface == null) {
                error("NoInterfaceForPortType", theInterfaces, callbackPT.getQName().toString());
            } else
                service.getInterfaceContract().setCallbackInterface(callbackInterface);
        } // end if

        return service;
    } // end generateService
    
    /**
     * Merge the componentType from introspection and from external file
     * 
     * Note the setting of the DataBinding for both Services and References to DOM, since this is
     * the data format expected by the ODE BPEL implementation code.
     * 
     * @param resolver
     * @param impl
     */
    private void mergeComponentType(ModelResolver resolver, BPELImplementation impl) {

        // Load the component type from a component type file, if any
        ComponentType componentType = getComponentType(resolver, impl);
        if (componentType != null && !componentType.isUnresolved()) {

            // References...
            Map<String, Reference> refMap = new HashMap<String, Reference>();
            for (Reference reference : componentType.getReferences()) {
                refMap.put(reference.getName(), reference);
            } // end for

            // For the present, overwrite anything arising from the component
            // type sidefile if
            // equivalent services are defined in the implementation.
            // TODO - a more careful merge must be done, using the
            // implementation introspection data
            // as the master but adding any additional and non-conflicting
            // information from the
            // sidefile
            for (Reference ref : impl.getReferences()) {
                refMap.put(ref.getName(), ref);
            } // end for

            impl.getReferences().clear();
            impl.getReferences().addAll(refMap.values());

            // Services.....
            Map<String, Service> serviceMap = new HashMap<String, Service>();
            for (Service service : componentType.getServices()) {
                serviceMap.put(service.getName(), service);
            } // end for

            // For the present, overwrite anything arising from the component
            // type sidefile if
            // equivalent services are defined in the implementation.
            // TODO - a more careful merge must be done, using the
            // implementation introspection data
            // as the master but adding any additional and non-conflicting
            // information from the
            // sidefile
            for (Service svc : impl.getServices()) {
                serviceMap.put(svc.getName(), svc);
            } // end for

            impl.getServices().clear();
            impl.getServices().addAll(serviceMap.values());

            // Properties
            Map<String, Property> propMap = new HashMap<String, Property>();
            for (Property property : componentType.getProperties()) {
                propMap.put(property.getName(), property);
            } // end for

            // A simple overwrite of any equivalent properties from the
            // component type sidefile
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
     * Returns a QName from its string representation in a named attribute of an XML element
     * supplied in an XMLStreamReader
     * 
     * QName attributes of an XML element (such as  BPEL process) is presented in one of
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
        String fullValue = reader.getAttributeValue(null, attribute);
        if (fullValue == null) {
            error("AttributeProcessMissing", reader);
            return null;
        }

        // Deal with the attribute in the XML Namespaces recommendation format
        // - trim off any leading/trailing spaces and check that the first
        // character is '{'
        if (fullValue.trim().charAt(0) == '{') {
            try {
                // Attempt conversion to a QName object
                QName theProcess = QName.valueOf(fullValue);
                return theProcess;
            } catch (IllegalArgumentException e) {
                // This exception happens if the attribute begins with '{' but
                // doesn't conform
                // to the XML Namespaces recommendation format
                error("AttributeWithoutNamespace", reader, attribute, fullValue);
                return null;
            }
        } // endif

        // Deal with the attribute in the local name + prefix format
        if (fullValue.indexOf(":") < 0) {
            error("AttributeWithoutPrefix", reader, attribute, fullValue);
            return null;
        }
        String prefix = fullValue.substring(0, fullValue.indexOf(":"));
        String name = fullValue.substring(fullValue.indexOf(":") + 1);
        String nsUri = reader.getNamespaceContext().getNamespaceURI(prefix);
        if (nsUri == null) {
            error("AttributeUnrecognizedNamespace", reader, attribute, fullValue);
            return null;
        }
        return new QName(nsUri, name, prefix);
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
     
}
