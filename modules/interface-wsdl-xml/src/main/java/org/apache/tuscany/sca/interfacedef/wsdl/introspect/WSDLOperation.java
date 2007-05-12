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

package org.apache.tuscany.sca.interfacedef.wsdl.introspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.apache.tuscany.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.impl.OperationImpl;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.XSDefinition;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Metadata for a WSDL operation
 * 
 * @version $Rev$ $Date$
 */
public class WSDLOperation {
    private WSDLFactory wsdlFactory;
    protected ModelResolver resolver;
    protected XmlSchemaCollection inlineSchemas;
    protected javax.wsdl.Operation operation;
    protected Operation operationModel;
    protected DataType<List<DataType>> inputType;
    protected DataType outputType;
    protected List<DataType> faultTypes;
    private String dataBinding;

    /**
     * @param operation The WSDL4J operation
     * @param dataBinding The default databinding
     * @param schemaRegistry The XML Schema registry
     */
    public WSDLOperation(
                         WSDLFactory wsdlFactory,
                         javax.wsdl.Operation operation,
                         XmlSchemaCollection inlineSchemas,
                         String dataBinding,
                         ModelResolver resolver) {
        super();
        this.wsdlFactory = wsdlFactory;
        this.operation = operation;
        this.inlineSchemas = inlineSchemas;
        this.resolver = resolver;
        this.dataBinding = dataBinding;
        this.wrapper = new Wrapper();
    }

    private Wrapper wrapper;

    private Boolean wrapperStyle;

    /**
     * Test if the operation qualifies wrapper style as defined by the JAX-WS
     * 2.0 spec
     * 
     * @return true if the operation qualifies wrapper style, otherwise false
     */
    public boolean isWrapperStyle() throws InvalidWSDLException {
        if (wrapperStyle == null) {
            wrapperStyle =
                wrapper.getInputChildElements() != null && (operation.getOutput() == null || wrapper
                    .getOutputChildElements() != null);
        }
        return wrapperStyle;
    }

    public Wrapper getWrapper() throws InvalidInterfaceException {
        if (!isWrapperStyle()) {
            throw new IllegalStateException("The operation is not wrapper style.");
        } else {
            return wrapper;
        }
    }

    /**
     * @return
     * @throws InvalidServiceContractException
     */
    public DataType<List<DataType>> getInputType() throws InvalidWSDLException {
        if (inputType == null) {
            Input input = operation.getInput();
            Message message = (input == null) ? null : input.getMessage();
            inputType = getMessageType(message);
            inputType.setDataBinding("idl:input");
        }
        return inputType;
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public DataType<XMLType> getOutputType() throws InvalidWSDLException {
        if (outputType == null) {
            Output output = operation.getOutput();
            Message outputMsg = (output == null) ? null : output.getMessage();

            List outputParts = (outputMsg == null) ? null : outputMsg.getOrderedParts(null);
            if (outputParts != null && outputParts.size() > 0) {
                if (outputParts.size() > 1) {
                    // We don't support output with multiple parts
                    throw new InvalidWSDLException("Multi-part output is not supported");
                }
                Part part = (Part)outputParts.get(0);
                outputType = new WSDLPart(part, Object.class).getDataType();
                // outputType.setMetadata(WSDLOperation.class.getName(), this);
            }
        }
        return outputType;
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public List<DataType> getFaultTypes() throws InvalidWSDLException {
        if (faultTypes == null) {
            Collection faults = operation.getFaults().values();
            faultTypes = new ArrayList<DataType>();
            for (Object f : faults) {
                Fault fault = (Fault)f;
                Message faultMsg = fault.getMessage();
                List faultParts = faultMsg.getOrderedParts(null);
                if (faultParts.size() != 1) {
                    throw new InvalidWSDLException("The fault message MUST have a single part");
                }
                Part part = (Part)faultParts.get(0);
                WSDLPart wsdlPart = new WSDLPart(part, FaultException.class);
                faultTypes.add(wsdlPart.getDataType());
            }
        }
        return faultTypes;
    }

    private DataType<List<DataType>> getMessageType(Message message) throws InvalidWSDLException {
        List<DataType> partTypes = new ArrayList<DataType>();
        if (message != null) {
            Collection parts = message.getOrderedParts(null);
            for (Object p : parts) {
                WSDLPart part = new WSDLPart((Part)p, Object.class);
                DataType<XMLType> partType = part.getDataType();
                partTypes.add(partType);
            }
        }
        return new DataTypeImpl<List<DataType>>(dataBinding, Object[].class, partTypes);
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public Operation getOperation() throws InvalidInterfaceException {
        if (operationModel == null) {
            boolean oneway = (operation.getOutput() == null);
            operationModel = new OperationImpl();
            operationModel.setName(operation.getName());
            operationModel.setFaultTypes(getFaultTypes());
            operationModel.setNonBlocking(oneway);
            operationModel.setConversationSequence(Operation.ConversationSequence.NO_CONVERSATION);
            operationModel.setInputType(getInputType());
            operationModel.setOutputType(getOutputType());

            operationModel.setWrapperStyle(isWrapperStyle());
            if (isWrapperStyle()) {
                WrapperInfo wrapperInfo = getWrapper().getWrapperInfo();
                operationModel.setWrapper(wrapperInfo);
            }
        }
        return operationModel;
    }
    
    private XmlSchemaElement getElement(QName elementName) {
        XmlSchemaElement element = inlineSchemas.getElementByQName(elementName);
        if (element == null) {
            XSDefinition definition = wsdlFactory.createXSDefinition();
            definition.setUnresolved(true);
            definition.setNamespace(elementName.getNamespaceURI());
            definition = resolver.resolveModel(XSDefinition.class, definition);
            if (definition.getSchema() != null) {
                element = definition.getSchema().getElementByName(elementName);
            }
        }
        return element;
    }
    
    private XmlSchemaType getType(QName typeName) {
        XmlSchemaType type = inlineSchemas.getTypeByQName(typeName);
        if (type == null) {
            XSDefinition definition = wsdlFactory.createXSDefinition();
            definition.setNamespace(typeName.getNamespaceURI());
            definition.setUnresolved(true);
            definition = resolver.resolveModel(XSDefinition.class, definition);
            if (definition.getSchema() != null) {
                type = definition.getSchema().getTypeByName(typeName);
            }
        }
        return type;
    }
    
    /**
     * Metadata for a WSDL part
     */
    public class WSDLPart {
        private Part part;

        private XmlSchemaElement element;

        private DataType<XMLType> dataType;

        public WSDLPart(Part part, Class javaType) throws InvalidWSDLException {
            this.part = part;
            QName elementName = part.getElementName();
            if (elementName != null) {
                element = WSDLOperation.this.getElement(elementName);
                if (element == null) {
                    throw new InvalidWSDLException("Element cannot be resolved: " + elementName.toString());
                }
            } else {
                // Create an faked XSD element to host the metadata
                element = new XmlSchemaElement();
                element.setName(part.getName());
                element.setQName(new QName(null, part.getName()));
                QName typeName = part.getTypeName();
                if (typeName != null) {
                    XmlSchemaType type = WSDLOperation.this.getType(typeName);
                    if (type == null) {
                        throw new InvalidWSDLException("Type cannot be resolved: " + typeName.toString());
                    }
                    element.setSchemaType(type);
                    element.setSchemaTypeName(type.getQName());
                }
            }
            dataType = new DataTypeImpl<XMLType>(dataBinding, javaType, new XMLType(getElementInfo(element)));
            // dataType.setMetadata(WSDLPart.class.getName(), this);
            // dataType.setMetadata(ElementInfo.class.getName(), getElementInfo(element));
        }

        /**
         * @return the element
         */
        public XmlSchemaElement getElement() {
            return element;
        }

        /**
         * @return the part
         */
        public Part getPart() {
            return part;
        }

        /**
         * @return the dataType
         */
        public DataType<XMLType> getDataType() {
            return dataType;
        }
    }

    /**
     * The "Wrapper Style" WSDL operation is defined by The Java API for
     * XML-Based Web Services (JAX-WS) 2.0 specification, section 2.3.1.2
     * Wrapper Style. <p/> A WSDL operation qualifies for wrapper style mapping
     * only if the following criteria are met:
     * <ul>
     * <li>(i) The operation�s input and output messages (if present) each
     * contain only a single part
     * <li>(ii) The input message part refers to a global element declaration
     * whose localname is equal to the operation name
     * <li>(iii) The output message part refers to a global element declaration
     * <li>(iv) The elements referred to by the input and output message parts
     * (henceforth referred to as wrapper elements) are both complex types
     * defined using the xsd:sequence compositor
     * <li>(v) The wrapper elements only contain child elements, they must not
     * contain other structures such as wildcards (element or attribute),
     * xsd:choice, substitution groups (element references are not permitted) or
     * attributes; furthermore, they must not be nillable.
     * </ul>
     */
    public class Wrapper {
        private XmlSchemaElement inputWrapperElement;

        private XmlSchemaElement outputWrapperElement;

        private List<XmlSchemaElement> inputElements;

        private List<XmlSchemaElement> outputElements;

//        private DataType<List<DataType<XMLType>>> unwrappedInputType;
//
//        private DataType<XMLType> unwrappedOutputType;

        private transient WrapperInfo wrapperInfo;

        private List<XmlSchemaElement> getChildElements(XmlSchemaElement element) throws InvalidWSDLException {
            if (element == null) {
                return null;
            }
            if (element.isNillable()) {
                // Wrapper element cannot be nillable
                return null;
            }
            XmlSchemaType type = element.getSchemaType();
            if (type == null) {
                String qName = element.getQName().toString();
                throw new InvalidWSDLException("The XML schema element does not have a type: " + qName);
            }
            if (!(type instanceof XmlSchemaComplexType)) {
                // Has to be a complexType
                return null;
            }
            XmlSchemaComplexType complexType = (XmlSchemaComplexType)type;
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
            XmlSchemaSequence sequence = (XmlSchemaSequence)complexType.getParticle();
            XmlSchemaObjectCollection items = sequence.getItems();
            List<XmlSchemaElement> childElements = new ArrayList<XmlSchemaElement>();
            for (int i = 0; i < items.getCount(); i++) {
                XmlSchemaObject schemaObject = items.getItem(i);
                if (!(schemaObject instanceof XmlSchemaElement)) {
                    return null;
                }
                XmlSchemaElement childElement = (XmlSchemaElement)schemaObject;
                if (childElement.getName() == null || childElement.getRefName() != null) {
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
         * @return a list of child XSD elements or null if if the request
         *         element is not wrapped
         */
        public List<XmlSchemaElement> getInputChildElements() throws InvalidWSDLException {
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
                Part part = (Part)parts.iterator().next();
                QName elementName = part.getElementName();
                if (elementName == null) {
                    return null;
                }
                if (!operation.getName().equals(elementName.getLocalPart())) {
                    return null;
                }
                inputWrapperElement = getElement(elementName);
                if (inputWrapperElement == null) {
                    throw new InvalidWSDLException("The element is not declared in a XML schema: " + elementName
                        .toString());
                }
                inputElements = getChildElements(inputWrapperElement);
                return inputElements;
            } else {
                return null;
            }
        }

        /**
         * Return a list of child XSD elements under the wrapped response
         * element
         * 
         * @return a list of child XSD elements or null if if the response
         *         element is not wrapped
         */
        public List<XmlSchemaElement> getOutputChildElements() throws InvalidWSDLException {
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
                Part part = (Part)parts.iterator().next();
                QName elementName = part.getElementName();
                if (elementName == null) {
                    throw new InvalidWSDLException("The element is not declared in the XML schema: " + part.getName());
                }
                outputWrapperElement = WSDLOperation.this.getElement(elementName);
                if (outputWrapperElement == null) {
                    return null;
                }
                outputElements = getChildElements(outputWrapperElement);
                // FIXME: Do we support multiple child elements for the
                // response?
                return outputElements;
            } else {
                return null;
            }
        }

        /**
         * @return the inputWrapperElement
         */
        public XmlSchemaElement getInputWrapperElement() {
            return inputWrapperElement;
        }

        /**
         * @return the outputWrapperElement
         */
        public XmlSchemaElement getOutputWrapperElement() {
            return outputWrapperElement;
        }

        /*
        public DataType<List<DataType<XMLType>>> getUnwrappedInputType() throws InvalidWSDLException {
            if (unwrappedInputType == null) {
                List<DataType<XMLType>> childTypes = new ArrayList<DataType<XMLType>>();
                for (XmlSchemaElement element : getInputChildElements()) {
                    DataType<XMLType> type =
                        new DataType<XMLType>(dataBinding, Object.class, new XMLType(getElementInfo(element)));
                    // type.setMetadata(ElementInfo.class.getName(), getElementInfo(element));
                    childTypes.add(type);
                }
                unwrappedInputType =
                    new DataType<List<DataType<XMLType>>>("idl:unwrapped.input", Object[].class, childTypes);
            }
            return unwrappedInputType;
        }

        public DataType<XMLType> getUnwrappedOutputType() throws InvalidServiceContractException {
            if (unwrappedOutputType == null) {
                List<XmlSchemaElement> elements = getOutputChildElements();
                if (elements != null && elements.size() > 0) {
                    if (elements.size() > 1) {
                        // We don't support output with multiple parts
                        throw new NotSupportedWSDLException("Multi-part output is not supported");
                    }
                    XmlSchemaElement element = elements.get(0);
                    unwrappedOutputType =
                        new DataType<XMLType>(dataBinding, Object.class, new XMLType(getElementInfo(element)));
                    // unwrappedOutputType.setMetadata(ElementInfo.class.getName(), getElementInfo(element));
                }
            }
            return unwrappedOutputType;
        }
        */

        public WrapperInfo getWrapperInfo() throws InvalidWSDLException {
            if (wrapperInfo == null) {
                ElementInfo in = getElementInfo(getInputWrapperElement());
                ElementInfo out = getElementInfo(getOutputWrapperElement());
                List<ElementInfo> inChildren = new ArrayList<ElementInfo>();
                for (XmlSchemaElement e : getInputChildElements()) {
                    inChildren.add(getElementInfo(e));
                }
                List<ElementInfo> outChildren = new ArrayList<ElementInfo>();
                if (out != null) {
                    for (XmlSchemaElement e : getOutputChildElements()) {
                        outChildren.add(getElementInfo(e));
                    }
                }
                wrapperInfo =
                    new WrapperInfo(dataBinding, in, out, inChildren, outChildren);
            }
            return wrapperInfo;
        }
    }

    private static ElementInfo getElementInfo(XmlSchemaElement element) {
        if (element == null) {
            return null;
        }
        return new ElementInfo(element.getQName(), getTypeInfo(element.getSchemaType()));
    }

    private static TypeInfo getTypeInfo(XmlSchemaType type) {
        if (type == null) {
            return null;
        }
        XmlSchemaType baseType = (XmlSchemaType)type.getBaseSchemaType();
        QName name = type.getQName();
        boolean simple = (type instanceof XmlSchemaSimpleType);
        if (baseType == null) {
            return new TypeInfo(name, simple, null);
        } else {
            return new TypeInfo(name, simple, getTypeInfo(baseType));
        }
    }

}
