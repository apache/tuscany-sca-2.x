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

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;

public class WSDLInterfaceProcessor implements StAXArtifactProcessor<WSDLInterfaceContract>, WSDLConstants {

    private WSDLFactory wsdlFactory;

    public WSDLInterfaceProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
    }
    
    /**
     * Create a WSDL interface from a URI.
     * @param uri
     * @return
     * @throws ContributionReadException
     */
    private WSDLInterface createWSDLInterface(String uri) throws ContributionReadException {
        WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();
        wsdlInterface.setUnresolved(true);

        // Read a qname in the form:
        // namespace#wsdl.interface(name)
        int index = uri.indexOf('#');
        if (index == -1) {
            throw new ContributionReadException("Invalid WSDL interface attribute: " + uri);
        }
        String namespace = uri.substring(0, index);
        String name = uri.substring(index + 1);
        name = name.substring("wsdl.interface(".length(), name.length() - 1);
        wsdlInterface.setName(new QName(namespace, name));
        
        return wsdlInterface;
    }

    public WSDLInterfaceContract read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        // Read an <interface.wsdl>
        WSDLInterfaceContract wsdlInterfaceContract = wsdlFactory.createWSDLInterfaceContract();
        
        // Read wsdlLocation
        String location = reader.getAttributeValue(WSDLI_NS, WSDL_LOCATION);
        wsdlInterfaceContract.setLocation(location);
        
        String uri = reader.getAttributeValue(null, INTERFACE);
        if (uri != null) {
            WSDLInterface wsdlInterface = createWSDLInterface(uri);
            wsdlInterfaceContract.setInterface(wsdlInterface);
        }
        
        uri = reader.getAttributeValue(null, CALLBACK_INTERFACE);
        if (uri != null) {
            WSDLInterface wsdlCallbackInterface = createWSDLInterface(uri);
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
        writer.writeStartElement(Constants.SCA10_NS, INTERFACE_WSDL);

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
                wsdlDefinition = resolver.resolveModel(WSDLDefinition.class, wsdlDefinition);
                if (!wsdlDefinition.isUnresolved()) {
                    PortType portType = wsdlDefinition.getDefinition().getPortType(wsdlInterface.getName());
                    if (portType != null) {
                        
                        // Introspect the WSDL portType and add the resulting
                        // WSDLInterface to the resolver
                        try {
                            wsdlInterface = wsdlFactory.createWSDLInterface(portType, wsdlDefinition.getInlinedSchemas(), resolver);
                        } catch (InvalidInterfaceException e) {
                            throw new ContributionResolveException(e);
                        }
                        resolver.addModel(wsdlInterface);
                        wsdlInterface.setWsdlDefinition(wsdlDefinition);
                    }
                }
            }
        }
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
