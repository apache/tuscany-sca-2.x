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

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.tuscany.sca.xsd.XSDFactory;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSerializer;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * @version $Rev$ $Date$
 */
public class Interface2WSDLGenerator {
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String SCHEMA_NAME = "schema";
    private static final QName SCHEMA_QNAME = new QName(SCHEMA_NS, SCHEMA_NAME);
    private static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";

    private WSDLFactory factory;
    private DataBindingExtensionPoint dataBindings;
    private WSDLDefinitionGenerator definitionGenerator;
    private boolean requiresSOAP12;
    private ModelResolver resolver;
    private XSDFactory xsdFactory;

    public Interface2WSDLGenerator(boolean requiresSOAP12,
                                   ModelResolver resolver,
                                   DataBindingExtensionPoint dataBindings,
                                   XSDFactory xsdFactory) throws WSDLException {
        this(requiresSOAP12, resolver, dataBindings, xsdFactory, WSDLFactory.newInstance());
    }

    public Interface2WSDLGenerator(boolean requiresSOAP12,
                                   ModelResolver resolver,
                                   DataBindingExtensionPoint dataBindings,
                                   XSDFactory xsdFactory,
                                   WSDLFactory factory) {
        super();
        this.requiresSOAP12 = requiresSOAP12; 
        this.resolver = resolver; 
        definitionGenerator = new WSDLDefinitionGenerator(requiresSOAP12);
        this.dataBindings = dataBindings;
        this.xsdFactory = xsdFactory;
        this.factory = factory;
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
        JavaInterface iface = (JavaInterface)interfaze;
        QName name = getQName(iface);
        Definition definition = factory.newDefinition();
        if (requiresSOAP12) {
            definition.addNamespace("soap12", "http://schemas.xmlsoap.org/wsdl/soap12/");
        } else {
            definition.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        }
        definition.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        definition.addNamespace("xs", SCHEMA_NS);

        String namespaceURI = name.getNamespaceURI();
        definition.setTargetNamespace(namespaceURI);
        definition.setQName(new QName(namespaceURI, name.getLocalPart() + "Service", name.getPrefix()));
        definition.addNamespace(name.getPrefix(), namespaceURI);

        PortType portType = definition.createPortType();
        portType.setQName(name);
        Binding binding = definitionGenerator.createBinding(definition, portType);
        Map<String, XMLTypeHelper> helpers = new HashMap<String, XMLTypeHelper>();
        Map<QName, List<ElementInfo>> wrappers = new HashMap<QName, List<ElementInfo>>();
        for (Operation op : interfaze.getOperations()) {
            javax.wsdl.Operation operation = generateOperation(definition, op, helpers, wrappers);
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

        // call each helper in turn to populate the wsdl.types element
        XmlSchemaCollection schemaCollection = new XmlSchemaCollection(); 
        int index = 0;
        for (XMLTypeHelper helper: new HashSet<XMLTypeHelper>(helpers.values())) {
            List<XSDefinition> xsDefinitions = helper.getSchemaDefinitions(xsdFactory, resolver);
            for (XSDefinition xsDef: xsDefinitions) {
                String nsURI = xsDef.getNamespace();
                Document document = xsDef.getDocument();
                XmlSchema schemaDef = xsDef.getSchema();
                if (document == null) {
                    try {
                        NamespaceMap prefixMap = new NamespaceMap();
                        prefixMap.add("xs", SCHEMA_NS);
                        prefixMap.add("tns", nsURI);
                        schemaDef.setNamespaceContext(prefixMap);
                        Document[] docs = schemaDef.getAllSchemas();
                        document = docs[0];
                        xsDef.setDocument(document);
                        for (int i = 1; i < docs.length; i++) {
                            Element schema = docs[i].getDocumentElement();
                            Schema schemaExt = createSchemaExt(definition);
                            schemaExt.setElement(schema);
                        }
                    } catch (XmlSchemaException e) {
                        throw new RuntimeException(e);
                    }
                }
                loadXSD(schemaCollection, xsDef);
                wsdlDefinition.getXmlSchemas().add(xsDef);
                Element schema = document.getDocumentElement();
                Schema schemaExt = createSchemaExt(definition);
                schemaExt.setElement(schema);
            }
        }

        // remove global wrapper elements with schema definitions from generation list
        for (QName wrapperName: new HashSet<QName>(wrappers.keySet())) {
            if (wsdlDefinition.getXmlSchemaElement(wrapperName) != null) {
                wrappers.remove(wrapperName);
            }
        }

        // generate schema elements for wrappers that aren't defined in the schemas
        if (wrappers.size() > 0) {
            int i = 0;
            Map<String, XSDefinition> wrapperXSDs = new HashMap<String, XSDefinition>();
            Map<Element, Map<String, String>> prefixMaps = new HashMap<Element, Map<String, String>>();
            for (Map.Entry<QName, List<ElementInfo>> entry: wrappers.entrySet()) {
                String targetNS = entry.getKey().getNamespaceURI();
                Document schemaDoc = null;
                Element schema = null;
                XSDefinition xsDef = wrapperXSDs.get(targetNS);
                if (xsDef != null) {
                    schemaDoc = xsDef.getDocument();
                    schema = schemaDoc.getDocumentElement();
                } else {
                    schemaDoc = createDocument();
                    schema = schemaDoc.createElementNS(SCHEMA_NS, "xs:schema");
                    schema.setAttribute("elementFormDefault", "qualified");
                    schema.setAttribute("attributeFormDefault", "qualified");
                    schema.setAttribute("targetNamespace", targetNS);
                    schema.setAttributeNS(XMLNS_NS, "xmlns:xs", SCHEMA_NS);
                    schemaDoc.appendChild(schema);
                    Schema schemaExt = createSchemaExt(definition);
                    schemaExt.setElement(schema);
                    prefixMaps.put(schema, new HashMap<String, String>());
                    xsDef = xsdFactory.createXSDefinition();
                    xsDef.setUnresolved(true);
                    xsDef.setNamespace(targetNS);
                    xsDef.setDocument(schemaDoc);
                    wrapperXSDs.put(targetNS, xsDef);
                }
                Element wrapper = schemaDoc.createElementNS(SCHEMA_NS, "xs:element");
                schema.appendChild(wrapper);
                wrapper.setAttribute("name", entry.getKey().getLocalPart());
                if (entry.getValue().size() == 1 && entry.getValue().get(0).getQName() == null) {
                    // special case for global fault element
                    QName typeName = entry.getValue().get(0).getType().getQName();
                    wrapper.setAttribute("type", typeName.getLocalPart());
                } else {
                    // normal wrapper containing type definition inline
                    Element complexType = schemaDoc.createElementNS(SCHEMA_NS, "xs:complexType");
                    wrapper.appendChild(complexType);
                    if (entry.getValue().size() > 0) {
                        Element sequence = schemaDoc.createElementNS(SCHEMA_NS, "xs:sequence");
                        complexType.appendChild(sequence);
                        for (ElementInfo element: entry.getValue()) {
                            Element xsElement = schemaDoc.createElementNS(SCHEMA_NS, "xs:element"); 
                            if (element.isMany()) {
                                xsElement.setAttribute("maxOccurs", "unbounded");
                            }
                            xsElement.setAttribute("minOccurs", "0");
                            xsElement.setAttribute("name", element.getQName().getLocalPart());
                            if (element.isNillable()) {
                                xsElement.setAttribute("nillable", "true");
                            }
                            QName typeName = element.getType().getQName();
                            String nsURI = typeName.getNamespaceURI();
                            if ("".equals(nsURI) || targetNS.equals(nsURI)) {
                                xsElement.setAttribute("type", typeName.getLocalPart());
                            } else if (SCHEMA_NS.equals(nsURI)) {
                                xsElement.setAttribute("type", "xs:" + typeName.getLocalPart());
                            } else {
                                Map<String, String> prefixMap = prefixMaps.get(schema);
                                String prefix = prefixMap.get(nsURI);
                                if (prefix == null) {
                                    prefix = "ns" + i++;
                                    prefixMap.put(nsURI, prefix);
                                    schema.setAttributeNS(XMLNS_NS, "xmlns:" + prefix, nsURI);
                                }
                                xsElement.setAttribute("type", prefix + ":" + typeName.getLocalPart());
                            }
                            sequence.appendChild(xsElement);
                        }
                    }
                }
            }
 
            // resolve XSDefinitions containing generated wrappers
            for (XSDefinition xsDef: wrapperXSDs.values()) {
                loadXSD(schemaCollection, xsDef);
                wsdlDefinition.getXmlSchemas().add(xsDef);
            }
        }

        return definition;
    }

    private static void loadXSD(XmlSchemaCollection schemaCollection, XSDefinition definition) {
        if (definition.getSchema() != null) {
            return;
        }
        if (definition.getDocument() != null) {
            String uri = null;
            if (definition.getLocation() != null) {
                uri = definition.getLocation().toString();
            }
            XmlSchema schema = schemaCollection.read(definition.getDocument(), uri, null);
            if (definition.getSchemaCollection() == null) {
                definition.setSchemaCollection(schemaCollection);
            }
            if (definition.getSchema() == null) {
                definition.setSchema(schema);
            }
        }
    }

    public Schema createSchemaExt(Definition definition) throws WSDLException {
        Types types = definition.getTypes();
        if (types == null) {
            types = definition.createTypes();
            definition.setTypes(types);
        }

        Schema schemaExt = createSchema(definition);
        types.addExtensibilityElement(schemaExt);

        return schemaExt;
    }

    public Schema createSchema(Definition definition) throws WSDLException {
        return (Schema)definition.getExtensionRegistry().createExtension(Types.class, SCHEMA_QNAME);
    }

    private DocumentBuilderFactory documentBuilderFactory;

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
        // document.setDocumentURI("http://");
        return document;
    }

    protected QName getQName(Interface interfaze) {
        JavaInterface iface = (JavaInterface)interfaze;
        QName qname = iface.getQName();
        if (qname != null) {
            return qname;
        } else {
            Class<?> javaClass = iface.getJavaClass();
            return new QName(JavaXMLMapper.getNamespace(javaClass), javaClass.getSimpleName(), "tns");
        }
    }

    public javax.wsdl.Operation generateOperation(Definition definition,
                                                  Operation op,
                                                  Map<String, XMLTypeHelper> helpers,
                                                  Map<QName, List<ElementInfo>> wrappers)
                                              throws WSDLException {
        javax.wsdl.Operation operation = definition.createOperation();
        operation.setName(op.getName());
        operation.setUndefined(false);

        Input input = definition.createInput();
        Message inputMsg = definition.createMessage();
        String namespaceURI = definition.getQName().getNamespaceURI();
        QName inputMsgName = new QName(namespaceURI, op.getName());
        inputMsg.setQName(inputMsgName);
        inputMsg.setUndefined(false);
        definition.addMessage(inputMsg);

        // FIXME: By default, java interface is mapped to doc-lit-wrapper style WSDL
        if (op.getWrapper() != null) {
            // Generate doc-lit-wrapper style
            inputMsg.addPart(generateWrapperPart(definition, op, helpers, wrappers, true));
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
            Message outputMsg = definition.createMessage();
            QName outputMsgName = new QName(namespaceURI, op.getName() + "Response");
            outputMsg.setQName(outputMsgName);
            outputMsg.setUndefined(false);
            definition.addMessage(outputMsg);

            if (op.getWrapper() != null) {
                outputMsg.addPart(generateWrapperPart(definition, op, helpers, wrappers, false));
            } else {
                outputMsg.addPart(generatePart(definition, op.getOutputType(), "return"));
            }
            output.setMessage(outputMsg);

            operation.setOutput(output);
            operation.setStyle(OperationType.REQUEST_RESPONSE);
        } else {
            operation.setStyle(OperationType.ONE_WAY);
        }

        for (DataType<DataType> faultType: op.getFaultTypes()) {
            Fault fault = definition.createFault();
            QName faultName = ((XMLType)faultType.getLogical().getLogical()).getElementName();
            fault.setName(faultName.getLocalPart());
            Message faultMsg = definition.getMessage(faultName);
            if (faultMsg == null) {
                faultMsg = definition.createMessage();
                faultMsg.setQName(faultName);
                faultMsg.setUndefined(false);
                definition.addMessage(faultMsg);
                faultMsg.addPart(generatePart(definition, faultType.getLogical(), faultName.getLocalPart()));
            }
            fault.setMessage(faultMsg);
            operation.addFault(fault);
            List<ElementInfo> elements = null;
            if (faultType.getLogical().getPhysical() != faultType.getPhysical()) {
                // create special wrapper for type indirection to real fault bean
                elements = new ArrayList<ElementInfo>(1);
                DataType logical = faultType.getLogical();
                elements.add(getElementInfo(logical.getPhysical(), logical, null, helpers));
             } else {
                // convert synthesized fault bean to a wrapper type
                elements = new ArrayList<ElementInfo>();
                for (DataType<XMLType> propDT: op.getFaultBeans().get(faultName)) {
                    XMLType logical = propDT.getLogical();
                    elements.add(getElementInfo(propDT.getPhysical(), propDT, logical.getElementName(), helpers));
                }
            }
            wrappers.put(faultName, elements);
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
                                    Map<String, XMLTypeHelper> helpers, 
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
                    DataType dataType = operation.getInputType().getLogical().get(i);
                    elements.set(i, getElementInfo(paramTypes[i], dataType, elements.get(i).getQName(), helpers));
                }
            } else {
                Class<?> returnType = method.getReturnType();
                if (returnType != Void.TYPE) {
                    DataType dataType = operation.getOutputType();
                    elements.set(0, getElementInfo(returnType, dataType, elements.get(0).getQName(), helpers));
                }
            }
        }
        return part;
    }

    private ElementInfo getElementInfo(Class javaType,
                                       DataType dataType,
                                       QName name,
                                       Map<String, XMLTypeHelper> helpers) {
        String db = dataType.getDataBinding();
        while ("java:array".equals(db)) {
            dataType = (DataType)dataType.getLogical();
            db = dataType.getDataBinding();
        }
        DataBinding dataBinding = dataBindings.getDataBinding(db);
        if (dataBinding == null) {
            throw new RuntimeException("no data binding for " + db);
        }
        XMLTypeHelper helper = helpers.get(db);
        if (helper == null) {
            Class helperClass = dataBinding.getXMLTypeHelperClass();
            for (XMLTypeHelper xth : helpers.values()) { 
                if (xth.getClass() == helperClass) {
                    helper = xth;
                    break;
                }
            }
            if (helper == null) {
                try {
                    helper = (XMLTypeHelper)helperClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            helpers.put(db, helper);
        }
        TypeInfo typeInfo = helper.getTypeInfo(javaType.isArray() ? javaType.getComponentType() : javaType,
                                               dataType.getLogical());
        ElementInfo element = new ElementInfo(name, typeInfo);
        element.setMany(javaType.isArray());
        element.setNillable(!javaType.isPrimitive());
        return element;
    }

    /*
    // currently not using the next three methods
    public XmlSchemaType getXmlSchemaType(DataType type) {
        return null;
    }

    // FIXME: WE need to add databinding-specific Java2XSD generation
    public Element generateXSD(DataType dataType) {
        DataBinding dataBinding = dataBindings.getDataBinding(dataType.getDataBinding());
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
    */

    public WSDLFactory getFactory() {
        return factory;
    }

    public void setFactory(WSDLFactory factory) {
        this.factory = factory;
    }

}
