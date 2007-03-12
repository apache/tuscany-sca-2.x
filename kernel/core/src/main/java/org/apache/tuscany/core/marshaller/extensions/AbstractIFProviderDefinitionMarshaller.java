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
package org.apache.tuscany.core.marshaller.extensions;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * Abstract marshaller for physical wire target definition.
 * 
 * @version $Revision$ $Date$
 */
public abstract class AbstractIFProviderDefinitionMarshaller<IFPD extends InstanceFactoryProviderDefinition> extends
    AbstractExtensibleMarshallerExtension<IFPD> {

    /**
     * Marshalls a physical java reference definition to the xml writer.
     */
    public void marshal(IFPD modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            
            QName qname = getModelObjectQName();
            writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
            writer.writeNamespace(qname.getPrefix(), qname.getNamespaceURI());
            
            handleExtension(modelObject, writer);
            
            writer.writeEndElement();
            
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }
        
    }

    /**
     * Unmarshalls a java physical reference definition from the xml reader.
     */
    public IFPD unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            IFPD ifpd = getConcreteModelObject();
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        handleExtension(ifpd, reader);
                        break;
                    case END_ELEMENT:
                        if (getModelObjectQName().equals(reader.getName())) {
                            return ifpd;
                        }

                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

}
