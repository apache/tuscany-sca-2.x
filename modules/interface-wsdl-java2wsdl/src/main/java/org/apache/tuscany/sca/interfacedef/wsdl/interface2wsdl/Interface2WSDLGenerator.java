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

package org.apache.tuscany.sca.interfacedef.wsdl.interface2wsdl;

import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.w3c.dom.Element;

/**
 * @version $Rev$ $Date$
 */
public class Interface2WSDLGenerator {
    private WSDLFactory factory;
    private DataBindingExtensionPoint dataBindingExtensionPoint;
    private WSDLDefinitionGenerator definitionGenerator = new WSDLDefinitionGenerator();

    public Interface2WSDLGenerator() throws WSDLException {
        super();
        this.factory = WSDLFactory.newInstance();
    }

    public Interface2WSDLGenerator(WSDLFactory factory, DataBindingExtensionPoint dataBindingExtensionPoint) {
        super();
        this.factory = factory;
        this.dataBindingExtensionPoint = dataBindingExtensionPoint;
    }

    public Definition generate(Interface interfaze) throws WSDLException {
        if (interfaze == null) {
            return null;
        }
        if (!interfaze.isRemotable()) {
            throw new IllegalArgumentException("Interface is not remotable");
        }
        if (interfaze instanceof WSDLInterface) {
            return ((WSDLInterface)interfaze).getWsdlDefinition().getDefinition();
        }
        QName name = getQName(interfaze);
        Definition definition = factory.newDefinition();
        definition.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        definition.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");

        definition.setTargetNamespace(name.getNamespaceURI());
        definition.setQName(name);
        definition.addNamespace(name.getPrefix(), name.getNamespaceURI());

        PortType portType = definition.createPortType();
        portType.setQName(name);
        for (Operation op : interfaze.getOperations()) {
            javax.wsdl.Operation operation = generateOperation(definition, op);
            portType.addOperation(operation);
        }
        portType.setUndefined(false);
        definition.addPortType(portType);
        definitionGenerator.createBinding(definition, portType);

        Types types = definitionGenerator.createTypes(definition);

        return definition;
    }

    protected QName getQName(Interface i) {
        // FIXME: We need to add the name information into the Interface model 
        Class<?> javaClass = ((JavaInterface)i).getJavaClass();
        return new QName(JavaInterfaceUtil.getNamespace(javaClass), javaClass.getSimpleName(), "tns");
    }

    public javax.wsdl.Operation generateOperation(Definition definition, Operation op) {
        javax.wsdl.Operation operation = definition.createOperation();
        operation.setName(op.getName());
        operation.setUndefined(false);

        Input input = definition.createInput();
        input.setName("input");
        Message inputMsg = definition.createMessage();
        QName inputMsgName = new QName(definition.getQName().getNamespaceURI(), op.getName() + "_InputMessage");
        inputMsg.setQName(inputMsgName);
        inputMsg.setUndefined(false);
        definition.addMessage(inputMsg);

        // FIXME: By default, java interface is mapped to doc-lit-wrapper style WSDL
        if (op.getWrapper() != null) {
            // Generate doc-lit-wrapper style
            inputMsg.addPart(generateWrapperPart(definition, op, true));
        } else {
            // Bare style
            int i = 0;
            for (DataType d : op.getInputType().getLogical()) {
                inputMsg.addPart(generatePart(definition, d, "arg" + i));
                i++;
            }
        }
        input.setMessage(inputMsg);
        operation.setInput(input);

        if (!op.isNonBlocking()) {
            Output output = definition.createOutput();
            output.setName("output");
            Message outputMsg = definition.createMessage();
            QName outputMsgName = new QName(definition.getQName().getNamespaceURI(), op.getName() + "_OutputMessage");
            outputMsg.setQName(outputMsgName);
            outputMsg.setUndefined(false);
            definition.addMessage(outputMsg);

            if (op.getWrapper() != null) {
                inputMsg.addPart(generateWrapperPart(definition, op, false));
            } else {
                inputMsg.addPart(generatePart(definition, op.getOutputType(), "return"));
            }
            output.setMessage(outputMsg);

            operation.setOutput(output);
            operation.setStyle(OperationType.REQUEST_RESPONSE);
        } else {
            operation.setStyle(OperationType.ONE_WAY);
        }

        operation.setUndefined(false);
        return operation;
    }

    public Part generatePart(Definition definition, DataType arg, String partName) {
        Part part = definition.createPart();
        part.setName(partName);
        if (arg != null && arg.getLogical() instanceof XMLType) {
            XMLType xmlType = (XMLType)arg.getLogical();
            part.setElementName(xmlType.getElementName());
            if (xmlType.getElementName() == null) {
                part.setTypeName(xmlType.getTypeName());
            }
        }
        return part;
    }

    public Part generateWrapperPart(Definition definition, Operation operation, boolean input) {
        Part part = definition.createPart();
        String partName = input ? operation.getName() : (operation.getName() + "Response");
        part.setName(partName);
        if (operation.getWrapper() != null) {
            ElementInfo elementInfo =
                input ? operation.getWrapper().getInputWrapperElement() : operation.getWrapper()
                    .getOutputWrapperElement();
            part.setElementName(elementInfo.getQName());
        }
        return part;
    }

    public XmlSchemaType getXmlSchemaType(DataType type) {
        return null;
    }

    // FIXME: WE need to add databinding-specific Java2XSD generation
    public Element generateXSD(DataType dataType) {
        DataBinding dataBinding = dataBindingExtensionPoint.getDataBinding(dataType.getDataBinding());
        if (dataBinding != null) {
            // return dataBinding.generateSchema(dataType);
        }
        return null;
    }

    public void generateWrapperElements(Operation op) {
        XmlSchemaCollection collection = new XmlSchemaCollection();
        String ns = getQName(op.getInterface()).getNamespaceURI();
        XmlSchema schema = new XmlSchema(ns, collection);
        schema.setAttributeFormDefault(new XmlSchemaForm(XmlSchemaForm.QUALIFIED));
        schema.setElementFormDefault(new XmlSchemaForm(XmlSchemaForm.QUALIFIED));

        XmlSchemaElement inputElement = new XmlSchemaElement();
        inputElement.setQName(new QName(ns, op.getName()));
        XmlSchemaComplexType inputType = new XmlSchemaComplexType(schema);
        inputType.setName("");
        XmlSchemaSequence inputSeq = new XmlSchemaSequence();
        inputType.setParticle(inputSeq);
        List<DataType> argTypes = op.getInputType().getLogical();
        for (DataType argType : argTypes) {
            XmlSchemaElement child = new XmlSchemaElement();
            Object logical = argType.getLogical();
            if (logical instanceof XMLType) {
                child.setName(((XMLType)logical).getElementName().getLocalPart());
                XmlSchemaType type = getXmlSchemaType(argType);
                child.setType(type);
            }
            inputSeq.getItems().add(child);
        }
        inputElement.setType(inputType);

        XmlSchemaElement outputElement = new XmlSchemaElement();
        outputElement.setQName(new QName(ns, op.getName() + "Response"));
        XmlSchemaComplexType outputType = new XmlSchemaComplexType(schema);
        outputType.setName("");
        XmlSchemaSequence outputSeq = new XmlSchemaSequence();
        outputType.setParticle(outputSeq);
        DataType returnType = op.getOutputType();
        XmlSchemaElement child = new XmlSchemaElement();
        Object logical = returnType.getLogical();
        if (logical instanceof XMLType) {
            child.setName(((XMLType)logical).getElementName().getLocalPart());
            XmlSchemaType type = getXmlSchemaType(returnType);
            child.setType(type);
        }
        outputSeq.getItems().add(child);
        outputElement.setType(outputType);

        schema.getElements().add(inputElement.getQName(), inputElement);
        schema.getElements().add(outputElement.getQName(), outputElement);

    }

    public WSDLFactory getFactory() {
        return factory;
    }

    public void setFactory(WSDLFactory factory) {
        this.factory = factory;
    }

}
