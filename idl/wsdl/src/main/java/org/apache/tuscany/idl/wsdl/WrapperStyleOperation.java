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

package org.apache.tuscany.idl.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * The "Wrapper Style" WSDL operation is defined by The Java API for XML-Based Web Services (JAX-WS) 2.0 specification,
 * section 2.3.1.2 Wrapper Style.
 * <p>
 * A WSDL operation qualifies for wrapper style mapping only if the following criteria are met:
 * <ul>
 * <li>(i) The operation’s input and output messages (if present) each contain only a single part
 * <li>(ii) The input message part refers to a global element declaration whose localname is equal to the operation
 * name
 * <li>(iii) The output message part refers to a global element declaration
 * <li>(iv) The elements referred to by the input and output message parts (henceforth referred to as wrapper elements)
 * are both complex types defined using the xsd:sequence compositor
 * <li>(v) The wrapper elements only contain child elements, they must not contain other structures such as wildcards
 * (element or attribute), xsd:choice, substitution groups (element references are not permitted) or attributes;
 * furthermore, they must not be nillable.
 * </ul>
 */
public class WrapperStyleOperation {
    private XMLSchemaRegistry schemaRegistry;

    private Operation operation;

    private List<XmlSchemaElement> inputElements;

    private List<XmlSchemaElement> outputElements;

    /**
     * @param operation
     * @param schemaRegistry
     */
    public WrapperStyleOperation(Operation operation, XMLSchemaRegistry schemaRegistry) {
        super();
        this.operation = operation;
        this.schemaRegistry = schemaRegistry;
    }

    /**
     * Test if the operation qualifies wrapper style as defined by the JAX-WS 2.0 spec
     * 
     * @return true if the operation qualifies wrapper style, otherwise false
     */
    public boolean isWrapperStyle() {
        return getInputChildElements() != null && (operation.getOutput() == null || getOutputChildElements() != null);
    }

    private List<XmlSchemaElement> getChildElements(QName elementName) {
        XmlSchemaElement element = schemaRegistry.getElement(elementName);
        if (element == null) {
            return null;
        }
        XmlSchemaType type = element.getSchemaType();
        if (!(type instanceof XmlSchemaComplexType)) {
            // Has to be a complexType
            return null;
        }
        XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
        if (complexType.getAttributes().getCount() != 0 || complexType.getAnyAttribute() != null) {
            // No attributes
            return null;
        }
        XmlSchemaParticle particle = complexType.getParticle();
        if (particle == null) {
            // No particle
            return Collections.emptyList();
        }
        if (!(particle instanceof XmlSchemaSequence)) {
            return null;
        }
        XmlSchemaSequence sequence = (XmlSchemaSequence) complexType.getParticle();
        XmlSchemaObjectCollection items = sequence.getItems();
        List<XmlSchemaElement> childElements = new ArrayList<XmlSchemaElement>();
        for (int i = 0; i < items.getCount(); i++) {
            XmlSchemaObject schemaObject = items.getItem(i);
            if (!(schemaObject instanceof XmlSchemaElement)) {
                return null;
            }
            XmlSchemaElement childElement = (XmlSchemaElement) schemaObject;
            if (childElement.getName() == null || childElement.getRefName() != null || childElement.isNillable()) {
                return null;
            }
            // TODO: Do we support maxOccurs >1 ?
            if (childElement.getMaxOccurs() > 1) {
                return null;
            }
            childElements.add(childElement);
        }
        return childElements;
    }

    /**
     * Return a list of child XSD elements under the wrapped request element
     * 
     * @return a list of child XSD elements or null if if the request element is not wrapped
     */
    public List<XmlSchemaElement> getInputChildElements() {
        if (inputElements != null) {
            return inputElements;
        }
        Input input = operation.getInput();
        if (input != null) {
            Message inputMsg = input.getMessage();
            Collection parts = inputMsg.getParts().values();
            if (parts.size() != 1) {
                return null;
            }
            Part part = (Part) parts.iterator().next();
            QName elementName = part.getElementName();
            if (elementName == null) {
                return null;
            }
            if (!operation.getName().equals(elementName.getLocalPart())) {
                return null;
            }
            inputElements = getChildElements(elementName);
            return inputElements;
        } else {
            return null;
        }
    }

    /**
     * Return a list of child XSD elements under the wrapped response element
     * 
     * @return a list of child XSD elements or null if if the response element is not wrapped
     */
    public List<XmlSchemaElement> getOutputChildElements() {
        if (outputElements != null) {
            return outputElements;
        }
        Output output = operation.getOutput();
        if (output != null) {
            Message outputMsg = output.getMessage();
            Collection parts = outputMsg.getParts().values();
            if (parts.size() != 1) {
                return null;
            }
            Part part = (Part) parts.iterator().next();
            QName elementName = part.getElementName();
            if (elementName == null) {
                return null;
            }
            outputElements = getChildElements(elementName);
            // FIXME: Do we support multiple child elements for the response?
            return outputElements;
        } else {
            return null;
        }
    }

    public List<XmlSchemaObject> getMessageSignature(Message message) {
        List<XmlSchemaObject> signature = new ArrayList<XmlSchemaObject>();
        Collection parts = message.getOrderedParts(null);
        for (Object p : parts) {
            Part part = (Part) p;
            QName elementName = part.getElementName();
            if (elementName != null) {
                signature.add(schemaRegistry.getElement(elementName));
            } else {
                // QName name = new QName(null, part.getName());
                signature.add(schemaRegistry.getType(part.getTypeName()));
            }
        }
        return signature;
    }
}
