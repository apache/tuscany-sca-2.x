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
package org.apache.tuscany.core.marshaller;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;

import static org.apache.tuscany.core.marshaller.PhysicalChangeSetMarshaller.CORE_NS;
import static org.apache.tuscany.core.marshaller.PhysicalChangeSetMarshaller.CORE_PREFIX;

/**
 * Marshaller for physical operation definition.
 * 
 * @version $Revision$ $Date: 2007-03-03 11:36:03 +0000 (Sat, 03 Mar
 *          2007) $
 */
public class PhysicalOperationDefinitionMarshaller extends AbstractMarshallerExtension<PhysicalOperationDefinition> {

    // Operation name attribute
    public static final String NAME = "name";

    // Callback attribute
    public static final String CALLBACK = "callback";

    // Return
    public static final String RETURN_TYPE = "returnType";

    // argument
    public static final String PARAMETER = "parameter";

    // Conversation sequence
    public static final String CONVERSATION_SEQUENCE = "conversationSequence";

    // QName for the root element
    public static final QName QNAME = new QName(CORE_NS, "operation", CORE_PREFIX);

    /**
     * Marshalls a physical operation to the xml writer.
     */
    public void marshal(PhysicalOperationDefinition modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            writer.writeStartElement(QNAME.getPrefix(), QNAME.getLocalPart(), QNAME.getNamespaceURI());
            writer.writeAttribute(NAME, modelObject.getName());
            writer.writeAttribute(CALLBACK, String.valueOf(modelObject.isCallback()));
            writer.writeAttribute(CONVERSATION_SEQUENCE, String.valueOf(modelObject.getConversationSequence()));
            writer.writeStartElement(QNAME.getPrefix(), RETURN_TYPE, QNAME.getNamespaceURI());
            writer.writeCharacters(modelObject.getReturnType());
            writer.writeEndElement();
            for (String parameter : modelObject.getParameters()) {
                writer.writeStartElement(QNAME.getPrefix(), PARAMETER, QNAME.getNamespaceURI());
                writer.writeCharacters(parameter);
                writer.writeEndElement();
            }
            writer.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    /**
     * Unmarshalls a physical operation from the xml reader.
     */
    public PhysicalOperationDefinition unmarshal(XMLStreamReader reader) throws MarshalException {

        try {
            PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
            operation.setName(reader.getAttributeValue(null, NAME));
            operation.setCallback(Boolean.valueOf(reader.getAttributeValue(null, CALLBACK)));
            operation.setConversationSequence(Integer.parseInt(reader.getAttributeValue(null, CONVERSATION_SEQUENCE)));
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        String name = reader.getName().getLocalPart();
                        String textContent = reader.getElementText();
                        if (PARAMETER.equals(name)) {
                            operation.addParameter(textContent);
                        } else if (RETURN_TYPE.equals(name)) {
                            operation.setReturnType(textContent);
                        }
                        break;
                    case END_ELEMENT:
                        if (QNAME.equals(reader.getName())) {
                            return operation;
                        }

                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<PhysicalOperationDefinition> getModelObjectType() {
        return PhysicalOperationDefinition.class;
    }

}
