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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 *
 * @version $Rev$ $Date$
 */
public class WSDLInterfaceProcessor implements StAXArtifactProcessor<WSDLInterfaceContract>, WSDLConstants {

    private WSDLFactory wsdlFactory;
    private Monitor monitor;

    public WSDLInterfaceProcessor(FactoryExtensionPoint modelFactories, Monitor monitor) {
        this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        this.monitor = monitor;
    }
    
    /**
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void warning(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "interface-wsdlxml-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
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
            Problem problem = monitor.createProblem(this.getClass().getName(), "interface-wsdlxml-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
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
            Problem problem = monitor.createProblem(this.getClass().getName(), "interface-wsdlxml-validation-messages", Severity.ERROR, model, message, ex);
            monitor.problem(problem);
        }        
    }
    
    /**
     * Create a WSDL interface from a URI.
     * @param uri - the URI in the form nameSpace#wsdl.interface(porttypeName) or nameSpace#wsdl.porttype(porttypeName)
     * @return a WSDLInterface object 
     * @throws ContributionReadException
     */
    private static String FRAGMENT_INTERFACE = "wsdl.interface";
    private static String FRAGMENT_PORTTYPE = "wsdl.porttype";
    private WSDLInterface createWSDLInterface(String uri) throws ContributionReadException {
        
    	WSDLInterface wsdlInterface = null;        

        // Read a QName in the form:
        // namespace#wsdl.interface(name)
        int index = uri.indexOf('#');
        if (index == -1) {
        	error("InvalidWSDLInterfaceAttr", wsdlFactory, uri);
            //throw new ContributionReadException("Invalid WSDL interface attribute: " + uri);
        } else {
        	// Read the URI and extract namespace and fragment
        	String namespace = uri.substring(0, index);
        	String name = uri.substring(index + 1);
        	String porttype = null;
        	if( name.contains(FRAGMENT_INTERFACE)) {
        		// Deal with the case where #wsdl.interface is used
        		porttype = name.substring("wsdl.interface(".length(), name.length() - 1);
        	} // end if
        	if( name.contains(FRAGMENT_PORTTYPE)) {
        		// Deal with the case where #wsdl.porttype is used
        		porttype = name.substring("wsdl.porttype(".length(), name.length() - 1);
        	} // end if
        	if( porttype == null ) {
        		error("InvalidWSDLInterfaceAttr", wsdlFactory, uri);
        		return null;
        	} // end if
        	wsdlInterface = wsdlFactory.createWSDLInterface();
            wsdlInterface.setUnresolved(true);
            wsdlInterface.setName(new QName(namespace, porttype));
        } // end if       
        
        return wsdlInterface;
    } // end method createWSDLInterface

    /**
     * Creates a WSDLInterfaceContract from a <interface.wsdl/> element in a SCDL file
     * 
     * The form of the <interface.wsdl/> element is as follows:
     * 
     * <interface.wsdl interface="http://sampleNamespace#wsdl.interface(porttypeName)"
     *                 callbackInterface="http://sampleNamespace#wsdl.porttype(callbackPorttypeName)"/>
     * where interface = URI pointing to the WSDL document containing a WSDL interface or porttype for the forward call interface
     *       callbackInterface = URI pointing to the WSDL document containing a WSDL interface or porttype for the callback interface
     * 
     * @param reader - XMLStreamReader holding the <interface.wsdl/> element
     * @return - the WSDLInterfaceContract
     */
    public WSDLInterfaceContract read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        // Read an <interface.wsdl>
        WSDLInterfaceContract wsdlInterfaceContract = wsdlFactory.createWSDLInterfaceContract();
        
        // Read wsdlLocation
        String location = reader.getAttributeValue(WSDLI_NS, WSDL_LOCATION);
        wsdlInterfaceContract.setLocation(location);
        
        String uri = reader.getAttributeValue(null, INTERFACE);
        if (uri != null) {
            WSDLInterface wsdlInterface = createWSDLInterface(uri);
            if (wsdlInterface != null)
                wsdlInterfaceContract.setInterface(wsdlInterface);
        }
        
        uri = reader.getAttributeValue(null, CALLBACK_INTERFACE);
        if (uri != null) {
            WSDLInterface wsdlCallbackInterface = createWSDLInterface(uri);
            if (wsdlCallbackInterface != null)
                wsdlInterfaceContract.setCallbackInterface(wsdlCallbackInterface);
        }
            
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && INTERFACE_WSDL_QNAME.equals(reader.getName())) {
                break;
            }
        }
        return wsdlInterfaceContract;
    }
    
    public void write(WSDLInterfaceContract wsdlInterfaceContract, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        // Write an <interface.wsdl>
        writer.writeStartElement(WSDLConstants.SCA11_NS, INTERFACE_WSDL);

        // Write interface name
        WSDLInterface wsdlInterface = (WSDLInterface)wsdlInterfaceContract.getInterface();
        if (wsdlInterface != null) {
            QName qname = wsdlInterface.getName();
            String uri = qname.getNamespaceURI() + "#wsdl.interface(" + qname.getLocalPart() + ")";
            writer.writeAttribute(INTERFACE, uri);
        }

        WSDLInterface wsdlCallbackInterface = (WSDLInterface)wsdlInterfaceContract.getCallbackInterface();
        if (wsdlCallbackInterface != null) {
            QName qname = wsdlCallbackInterface.getName();
            String uri = qname.getNamespaceURI() + "#wsdl.interface(" + qname.getLocalPart() + ")";
            writer.writeAttribute(CALLBACK_INTERFACE, uri);
        }
        
        // Write location
        if (wsdlInterfaceContract.getLocation() != null) {
            writer.writeAttribute(WSDLI_NS, WSDL_LOCATION, wsdlInterfaceContract.getLocation());
        }
        
        writer.writeEndElement();
    }
    
    private WSDLInterface resolveWSDLInterface(WSDLInterface wsdlInterface, ModelResolver resolver) throws ContributionResolveException {
        
        if (wsdlInterface != null && wsdlInterface.isUnresolved()) {

            // Resolve the WSDL interface
            wsdlInterface = resolver.resolveModel(WSDLInterface.class, wsdlInterface);
            if (wsdlInterface.isUnresolved()) {

                // If the WSDL interface has never been resolved yet, do it now
                // First, resolve the WSDL definition for the given namespace
                WSDLDefinition wsdlDefinition = wsdlFactory.createWSDLDefinition();
                wsdlDefinition.setUnresolved(true);
                wsdlDefinition.setNamespace(wsdlInterface.getName().getNamespaceURI());
                WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, wsdlDefinition);
                if (!resolved.isUnresolved()) {
                    wsdlDefinition.setDefinition(resolved.getDefinition());
                    wsdlDefinition.setLocation(resolved.getLocation());
                    wsdlDefinition.setURI(resolved.getURI());
                    wsdlDefinition.getImportedDefinitions().addAll(resolved.getImportedDefinitions());
                    wsdlDefinition.getXmlSchemas().addAll(resolved.getXmlSchemas());
                    wsdlDefinition.setUnresolved(false);
                    WSDLObject<PortType> portType = wsdlDefinition.getWSDLObject(PortType.class, wsdlInterface.getName());
                    if (portType != null) {                        
                        // Introspect the WSDL portType and add the resulting
                        // WSDLInterface to the resolver
                        try {
                            wsdlDefinition.setDefinition(portType.getDefinition());
                            wsdlInterface = wsdlFactory.createWSDLInterface(portType.getElement(), wsdlDefinition, resolver);
                            wsdlInterface.setWsdlDefinition(wsdlDefinition);
                            resolver.addModel(wsdlInterface);
                        } catch (InvalidInterfaceException e) {
                        	ContributionResolveException ce = new ContributionResolveException("Invalid interface when resolving " + 
                        			                                                            portType.toString(), e);
                        	error("ContributionResolveException", wsdlFactory, ce);
                            //throw ce;
                        }                        
                    }
                    else {
                    	warning("WsdlInterfaceDoesNotMatch", wsdlDefinition, wsdlInterface.getName());
                    } // end if
                } else {
                	// If we get here, the WSDLDefinition is unresolved...
                	ContributionResolveException ce = new ContributionResolveException("WSDLDefinition unresolved " + 
                			wsdlInterface.getName().getNamespaceURI() );
                    error("ContributionResolveException", wsdlFactory, ce);
                }// end if
            } // end if
        } // end if
        return wsdlInterface;
    }
    
    public void resolve(WSDLInterfaceContract wsdlInterfaceContract, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the interface and callback interface
        WSDLInterface wsdlInterface = resolveWSDLInterface((WSDLInterface)wsdlInterfaceContract.getInterface(), resolver);
        wsdlInterfaceContract.setInterface(wsdlInterface);
        
        WSDLInterface wsdlCallbackInterface = resolveWSDLInterface((WSDLInterface)wsdlInterfaceContract.getCallbackInterface(), resolver);
        wsdlInterfaceContract.setCallbackInterface(wsdlCallbackInterface);
    }
    
    public QName getArtifactType() {
        return WSDLConstants.INTERFACE_WSDL_QNAME;
    }
    
    public Class<WSDLInterfaceContract> getModelType() {
        return WSDLInterfaceContract.class;
    }
}
