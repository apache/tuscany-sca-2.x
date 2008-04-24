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

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Rev$ $Date$
 */
public class Interface2WSDLGenerator {
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String SCHEMA_NAME = "schema";
    private static final QName SCHEMA_QNAME = new QName(SCHEMA_NS, SCHEMA_NAME);

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

    public Definition generate(Interface interfaze, WSDLDefinition wsdlDefinition) throws WSDLException {
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
        definition.addNamespace("xs", SCHEMA_NS);

        definition.setTargetNamespace(name.getNamespaceURI());
        definition.setQName(name);
        definition.addNamespace(name.getPrefix(), name.getNamespaceURI());

        PortType portType = definition.createPortType();
        portType.setQName(name);
        Binding binding = definitionGenerator.createBinding(definition, portType);
        ArrayList<Class> javaTypes = new ArrayList<Class>(); 
        Map<QName, List<ElementInfo>> wrappers = new HashMap<QName, List<ElementInfo>>();
        for (Operation op : interfaze.getOperations()) {
            javax.wsdl.Operation operation = generateOperation(definition, op, javaTypes, wrappers);
            portType.addOperation(operation);
            String action = ((JavaOperation)op).getAction();
            BindingOperation bindingOp = definitionGenerator.createBindingOperation(definition, operation, action);
            binding.addBindingOperation(bindingOp);
        }
        portType.setUndefined(false);
        definition.addPortType(portType);
        binding.setUndefined(false);
        definition.addBinding(binding);
        wsdlDefinition.setBinding(binding);

        Map<String, Element> schemas = new HashMap<String, Element>();
        Document schemaDoc = null;
        if (javaTypes.size() > 0) {
            try {
                // generate schema document for all required types
                Class[] types = new Class[javaTypes.size()];
                types = javaTypes.toArray(types);
                JAXBContext context = JAXBContext.newInstance(types);
                schemaDoc = (Document)JAXBContextHelper.generateSchema(context);

                // create a map entry for each schema in the document
                NodeList docNodes = schemaDoc.getChildNodes();
                for (int i = 0; i < docNodes.getLength(); i++) {
                    Node docNode = docNodes.item(i);
                    if (docNode.getNodeType() == Node.ELEMENT_NODE &&
                        SCHEMA_NS.equals(docNode.getNamespaceURI()) &&
                        SCHEMA_NAME.equals(docNode.getLocalName())) {
                        Schema schemaExt = createSchemaExt(definition);
                        schemaExt.setElement((Element)docNode);
                        String targetNS = ((Element)docNode).getAttribute("targetNamespace");
                        if (!"".equals(targetNS)) {
                            schemas.put(targetNS, (Element)docNode);
                        }
                    }
                }

                // remove global wrapper elements from generation list
                for (Map.Entry<String, Element> entry: schemas.entrySet()) {
                    String targetNS = entry.getKey();
                    NodeList childNodes = entry.getValue().getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node childNode = childNodes.item(i);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            String elementName = ((Element)childNode).getAttribute("name");
                            QName elementQName = new QName(targetNS, elementName); 
                            wrappers.remove(elementQName);  // it's OK if not found
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // generate schema for any wrappers that weren't generated by JAXB
        if (wrappers.size() > 0) {
            for (Map.Entry<QName, List<ElementInfo>> entry: wrappers.entrySet()) {
                String targetNS = entry.getKey().getNamespaceURI();
                Element schema = schemas.get(targetNS);
                if (schema == null) {
                    if (schemaDoc == null) {
                        schemaDoc = createDocument();
                    }
                    schema = schemaDoc.createElementNS(SCHEMA_NS, "xs:schema");
                    schema.setAttribute("elementFormDefault", "qualified");
                    schema.setAttribute("attributeFormDefault", "qualified");
                    schema.setAttribute("targetNamespace", targetNS);
                    schema.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xs", SCHEMA_NS);
                    Schema schemaExt = createSchemaExt(definition);
                    schemaExt.setElement(schema);
                    schemas.put(targetNS, schema);
                }
                Element wrapper = schemaDoc.createElementNS(SCHEMA_NS, "xs:element");
                schema.appendChild(wrapper);
                wrapper.setAttribute("name", entry.getKey().getLocalPart());
                Element complexType = schemaDoc.createElementNS(SCHEMA_NS, "xs:complexType");
                wrapper.appendChild(complexType);
                for (ElementInfo element: entry.getValue()) {
                    Element xsElement = schemaDoc.createElementNS(SCHEMA_NS, "xs:element"); 
                    xsElement.setAttribute("minOccurs", "0");
                    xsElement.setAttribute("name", element.getQName().getLocalPart());
                    QName typeName = element.getType().getQName();
                    xsElement.setAttribute("type", typeName.getLocalPart());
                    complexType.appendChild(xsElement);
                }
            }
        }

        return definition;
    }

    private DocumentBuilderFactory documentBuilderFactory;

    public Schema createSchemaExt(Definition definition) throws WSDLException {
        Types types = definition.getTypes();
        if (types == null) {
            types = definition.createTypes();
            definition.setTypes(types);
        }

        Schema schemaExt = (Schema)definition.getExtensionRegistry()
                               .createExtension(Types.class, SCHEMA_QNAME);
        types.addExtensibilityElement(schemaExt);

        return schemaExt;
    }

    public Document createDocument() {
        Document document;
        try {
            if (documentBuilderFactory == null) {
                documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setNamespaceAware(true);
            }
            document = documentBuilderFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        document.setDocumentURI("http://");
        return document;
    }

    protected QName getQName(Interface i) {
        JavaInterface ji = (JavaInterface)i;
        QName qname = ji.getQName();
        if (qname != null) {
            return qname;
        } else {
            Class<?> javaClass = ((JavaInterface)i).getJavaClass();
            return new QName(JavaInterfaceUtil.getNamespace(javaClass), javaClass.getSimpleName(), "tns");
        }
    }

    public javax.wsdl.Operation generateOperation(Definition definition,
                                                  Operation op,
                                                  ArrayList<Class> javaTypes,
                                                  Map<QName, List<ElementInfo>> wrappers)
                                              throws WSDLException {
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
            inputMsg.addPart(generateWrapperPart(definition, op, javaTypes, wrappers, true));
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
                outputMsg.addPart(generateWrapperPart(definition, op, javaTypes, wrappers, false));
            } else {
                outputMsg.addPart(generatePart(definition, op.getOutputType(), "return"));
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

    public Part generateWrapperPart(Definition definition,
                                    Operation operation,
                                    ArrayList<Class> javaTypes, 
                                    Map<QName, List<ElementInfo>> wrappers,
                                    boolean input) throws WSDLException {
        Part part = definition.createPart();
        String partName = input ? operation.getName() : (operation.getName() + "Response");
        part.setName(partName);
        WrapperInfo opWrapper = operation.getWrapper();
        if (opWrapper != null) {
            ElementInfo elementInfo =
                input ? opWrapper.getInputWrapperElement() : opWrapper.getOutputWrapperElement();
            List<ElementInfo> elements =
                input ? opWrapper.getInputChildElements() : opWrapper.getOutputChildElements();
            QName wrapperName = elementInfo.getQName();
            part.setElementName(wrapperName);
            wrappers.put(wrapperName, elements);

            Method method = ((JavaOperation)operation).getJavaMethod();
            if (input) {
                Class<?>[] paramTypes = method.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    Object logical = operation.getInputType().getLogical().get(i).getLogical();
                    TypeInfo typeInfo = getTypeInfo(paramTypes[i], logical);
                    if (!typeInfo.isSimpleType()) {
                        javaTypes.add(paramTypes[i]);
                    }
                    ElementInfo element = new ElementInfo(elements.get(i).getQName(), typeInfo);
                    elements.set(i, element);
                }
            } else {
                Class<?> returnType = method.getReturnType();
                if (returnType != Void.TYPE) {
                    Object logical = operation.getOutputType().getLogical();
                    TypeInfo typeInfo = getTypeInfo(returnType, logical);
                    if (!typeInfo.isSimpleType()) {
                        javaTypes.add(returnType);
                    }
                    ElementInfo element = new ElementInfo(elements.get(0).getQName(), typeInfo);
                    elements.set(0, element);
                }
            }
        }
        return part;
    }

    private TypeInfo getTypeInfo(Class javaType, Object logical) {
        QName xmlType = JavaXMLMapper.getXMLType(javaType);
        if (xmlType != null) {
            return new TypeInfo(xmlType, true, null);
        } else {
            if (logical instanceof XMLType) {
                xmlType = ((XMLType)logical).getTypeName();
            }
            if (xmlType == null) {
                xmlType = new QName(JavaInterfaceUtil.getNamespace(javaType),
                                    Introspector.decapitalize(javaType.getSimpleName()));
            }
            return new TypeInfo(xmlType, false, null);
        }
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

    // The following method isn't currently used and would need updating to be
    // usable.  Instead, we generate an inline schema DOM in this class and
    // convert it to an XMLSchemaCollection when this method returns to
    // Java2WSDLHelper.  It would be possible for this class to generate the
    // XMLSchemaCollection and add it to the WSDLDefinition that's passed into
    // this method.  This doesn't completely remove the need for DOM to
    // XMLSchemaCollection conversion, because a DOM is produced by the
    // JAXB schema generator.  We could do that DOM to XMLSchemaCollection
    // conversion in this class and then add all the schemas generated here
    // to the XMLSchemaCollection.  This would only be a worthwhile improvement
    // over the current code if we could omit the inline DOM schemas from the
    // generated wsdl4j Definition, and I'm not sure about all the implications
    // of going down this path.
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
