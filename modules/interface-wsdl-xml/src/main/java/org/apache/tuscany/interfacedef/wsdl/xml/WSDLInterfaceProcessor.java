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

package org.apache.tuscany.interfacedef.wsdl.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.assembly.xml.Constants;
import org.apache.tuscany.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.interfacedef.wsdl.impl.DefaultWSDLFactory;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.ContributionResolveException;
import org.apache.tuscany.services.spi.contribution.ContributionWireException;
import org.apache.tuscany.services.spi.contribution.ContributionWriteException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;

public class WSDLInterfaceProcessor implements StAXArtifactProcessor<WSDLInterface>, WSDLConstants {

    private WSDLFactory wsdlFactory;

    public WSDLInterfaceProcessor(WSDLFactory wsdlFactory) {
        this.wsdlFactory = wsdlFactory;
    }
    
    public WSDLInterfaceProcessor() {
        this(new DefaultWSDLFactory());
    }

    public WSDLInterface read(XMLStreamReader reader) throws ContributionReadException {
        try {
    
            // Read an <interface.wsdl>
            WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();
            wsdlInterface.setUnresolved(true);

            // Read a qname in the form:
            // namespace#wsdl.interface(name)
            String uri = reader.getAttributeValue(null, INTERFACE);
            if (uri != null) {
                int index = uri.indexOf('#');
                if (index == -1) {
                    throw new ContributionReadException("Invalid WSDL interface attribute: " + uri);
                }
                String namespace = uri.substring(0, index);
                String name = uri.substring(index + 1);
                name = name.substring("wsdl.interface(".length(), name.length() - 1);
                wsdlInterface.setName(new QName(namespace, name));
            }
            
            // Read wsdlLocation
            wsdlInterface.setLocation(reader.getAttributeValue(WSDLI_NS, WSDL_LOCATION));
                
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && INTERFACE_WSDL_QNAME.equals(reader.getName())) {
                    break;
                }
            }
            return wsdlInterface;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }
    
    public void write(WSDLInterface wsdlInterface, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <interface.wsdl>
            writer.writeStartElement(Constants.SCA10_NS, INTERFACE_WSDL);

            // Write interface name
            if (wsdlInterface.getName() != null) {
                QName qname = wsdlInterface.getName();
                String uri = qname.getNamespaceURI() + "#wsdl.interface(" + qname.getLocalPart() + ")";
                writer.writeAttribute(INTERFACE, uri);
            }
            
            // Write location
            if (wsdlInterface.getLocation() != null) {
                writer.writeAttribute(WSDLI_NS, WSDL_LOCATION, wsdlInterface.getLocation());
            }
            
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    public void resolve(WSDLInterface wsdlInterface, ArtifactResolver resolver) throws ContributionResolveException {
    }
    
    public void wire(WSDLInterface model) throws ContributionWireException {
        // TODO Auto-generated method stub
    }
    
    public QName getArtifactType() {
        return WSDLConstants.INTERFACE_WSDL_QNAME;
    }
    
    public Class<WSDLInterface> getModelType() {
        return WSDLInterface.class;
    }
}
