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
package org.apache.tuscany.sca.interfacedef.wsdl.java2wsdl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.InvalidWSDLException;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLInterfaceIntrospectorImpl;
import org.apache.tuscany.sca.interfacedef.wsdl.interface2wsdl.Interface2WSDLGenerator;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLModelResolver;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.xml.XMLDocumentHelper;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.java2wsdl.Java2WSDLBuilder;
import org.osoa.sca.annotations.OneWay;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Utility methods to create WSDL objects from Java interfaces.
 *
 * @version $Rev$ $Date$
 */
public class Java2WSDLHelper {
    // the following 3 switches are temporary for debugging
    public static boolean newGenerator = true;  // external code sets this to force new generator
    public static boolean oldGenerator;  // external code sets this to force old generator
    public static boolean printWSDL;     // external code sets this to print generated WSDL

    private static void register(Map<String, String> map, DataType type) {
        if (type == null) {
            return;
        }
        Package pkg = type.getPhysical().getPackage();
        if (pkg != null) {
            String pkgName = pkg.getName();
            Object logical = type.getLogical();
            if (logical instanceof XMLType) {
                QName typeName = ((XMLType)logical).getTypeName();
                if (typeName != null && !XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(typeName.getNamespaceURI())) {
                    map.put(pkgName, typeName.getNamespaceURI());
                }
            }
        }
    }

    //FIXME: temp code to control whether the old or new path is used.
    // Will be removed when the new path can handle all cases.
    private static boolean useNewGenerator(JavaInterface iface) {
        /* restore the following code when the new generator supports exception mapping 
        // check databindings to see if new generator can be used
        boolean newGenerator = true;
        for (Operation op : iface.getOperations()) {
            DataType<List<DataType>> inputType = op.getInputType();
            for (DataType t : inputType.getLogical()) {
                newGenerator &= checkDataBinding(t);
            }
            DataType outputType = op.getOutputType();
            newGenerator &= checkDataBinding(outputType);
        }
        return newGenerator;
        */
        return iface.getQName() != null;  // use new generator for @WebService 
    }

    /*
    //FIXME: temp code to control whether the old or new path is used.
    // Will be removed when the new path can handle all databindings.
    private static boolean checkDataBinding(DataType dt) {
        boolean result = (dt == null ||
               "java:simpleType".equals(dt.getDataBinding()) ||
               "java:complexType".equals(dt.getDataBinding()) ||
               "java:array".equals(dt.getDataBinding()) ||
               "java.io.Externalizable".equals(dt.getDataBinding()) ||
               "org.osoa.sca.CallableReference".equals(dt.getDataBinding()) ||
               "org.apache.axiom.om.OMElement".equals(dt.getDataBinding()) ||
               "javax.xml.bind.JAXBElement".equals(dt.getDataBinding()));
        if (!result) {
            System.out.println("$$ unsupported databinding " + dt.getDataBinding());
        }
        return result;
    }
    */

    /**
     * Create a WSDLInterfaceContract from a JavaInterfaceContract
     */
    public static WSDLInterfaceContract createWSDLInterfaceContract(JavaInterfaceContract contract,
                                                                    ModelResolver resolver,
                                                                    DataBindingExtensionPoint dataBindings,
                                                                    WSDLFactory wsdlFactory,
                                                                    XSDFactory xsdFactory) {
        return createWSDLInterfaceContract(contract, false, resolver, dataBindings, wsdlFactory, xsdFactory);
    }
    
    /**
     * Create a WSDLInterfaceContract from a JavaInterfaceContract
     */
    public static WSDLInterfaceContract createWSDLInterfaceContract(JavaInterfaceContract contract,
                                                                    boolean requiresSOAP12,
                                                                    ModelResolver resolver,
                                                                    DataBindingExtensionPoint dataBindings,
                                                                    WSDLFactory wsdlFactory,
                                                                    XSDFactory xsdFactory) {

        WSDLInterfaceContract wsdlContract = wsdlFactory.createWSDLInterfaceContract();
        WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();
        wsdlContract.setInterface(wsdlInterface);

        final WSDLDefinition wsdlDefinition = wsdlFactory.createWSDLDefinition();
        JavaInterface iface = (JavaInterface)contract.getInterface();

        //FIXME: When Interface2WSDLGenerator supports all databindings, change this
        // code to use it in all cases instead of calling createDefinition()
        Definition def = null;
        boolean usedNewGenerator = false;
        if (newGenerator || (!oldGenerator && useNewGenerator(iface))) {
            usedNewGenerator = true;
            /*
            System.out.println("$$ new gen: " + iface.getName());
            */
            try {
                Interface2WSDLGenerator wsdlGenerator =
                    new Interface2WSDLGenerator(requiresSOAP12, resolver, dataBindings, xsdFactory);
                def = wsdlGenerator.generate(iface, wsdlDefinition);
            } catch (WSDLException e) {
                throw new RuntimeException(e);
            }

        } else {
            /*
            System.out.println("$$ old gen: " + iface.getName());
            */
            // Create a package2ns map
            Map<String, String> pkg2nsMap = new HashMap<String, String>();
            for (Operation op : iface.getOperations()) {
                DataType<List<DataType>> inputType = op.getInputType();
                for (DataType t : inputType.getLogical()) {
                    register(pkg2nsMap, t);
                }
                DataType outputType = op.getOutputType();
                register(pkg2nsMap, outputType);
            }
            def = createDefinition(pkg2nsMap, iface.getJavaClass(), requiresSOAP12);
        }

        // for debugging
        if (printWSDL) {
            try {
                System.out.println("Generated WSDL for Java interface " + iface.getName() + " class " + iface.getJavaClass().getName());
                WSDLWriter writer =  javax.wsdl.factory.WSDLFactory.newInstance().newWSDLWriter();
                writer.writeWSDL(def, System.out);
            } catch (WSDLException e) {
                throw new RuntimeException(e);
            }
        }

        wsdlDefinition.setDefinition(def);
        wsdlInterface.setWsdlDefinition(wsdlDefinition);
        wsdlInterface.setRemotable(true);
        wsdlInterface.setConversational(contract.getInterface().isConversational());
        wsdlInterface.setUnresolved(false);
        wsdlInterface.setRemotable(true);
        PortType portType = (PortType)def.getAllPortTypes().values().iterator().next();
        wsdlInterface.setPortType(portType);

        if (!usedNewGenerator) {
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            final XSDFactory finalFactory = xsdFactory;
            final Definition fdef = def;
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    readInlineSchemas(finalFactory, wsdlDefinition, fdef, new XmlSchemaCollection());
                    return null;
                }
            });
        }

        try {
            for (Operation op : iface.getOperations()) {
                javax.wsdl.Operation wsdlOp = portType.getOperation(op.getName(), null, null);
                wsdlInterface.getOperations().add(WSDLInterfaceIntrospectorImpl.getOperation(
                                                      wsdlOp, wsdlDefinition, resolver, xsdFactory));

                /*
                Operation clonedOp = (Operation)op.clone();
                clonedOp.setDataBinding(null);

                if (clonedOp.getInputType().getLogical().isEmpty()) {
                    // null args case needs a single input type for the wrapper
                    clonedOp.setInputType(opx.getInputType());
                }
                for (DataType<?> dt : clonedOp.getInputType().getLogical()) {
                    dt.setDataBinding(null);
                }

                if (clonedOp.getOutputType() != null) {
                    clonedOp.getOutputType().setDataBinding(null);
                }
                for (DataType<?> dt : clonedOp.getFaultTypes()) {
                    dt.setDataBinding(null);
                }
                clonedOp.setWrapperStyle(true);
                clonedOp.setWrapper(opx.getWrapper().getWrapperInfo());

                wsdlInterface.getOperations().add(clonedOp);
                */
            }
        } catch (InvalidWSDLException e) {
            throw new RuntimeException(e);
        }

        return wsdlContract;
    }

    private static Document promote(Element element) {
        Document doc = (Document)element.getOwnerDocument().cloneNode(false);
        Element schema = (Element)doc.importNode(element, true);
        doc.appendChild(schema);
        Node parent = element.getParentNode();
        while (parent instanceof Element) {
            Element root = (Element)parent;
            NamedNodeMap nodeMap = root.getAttributes();
            if (nodeMap != null) {  // nodename: #document value: null
                for (int i = 0; i < nodeMap.getLength(); i++) {
                    Attr attr = (Attr)nodeMap.item(i);
                    String name = attr.getName();
                    if ("xmlns".equals(name) || name.startsWith("xmlns:")) {
                        if (schema.getAttributeNode(name) == null) {
                            schema.setAttributeNodeNS((Attr)doc.importNode(attr, true));
                        }
                    }
                }
            }
            parent = parent.getParentNode();
        }
        doc.setDocumentURI(element.getOwnerDocument().getDocumentURI());
        return doc;
    }

    /**
     * Populate the inline schemas including those from the imported definitions
     * 
     * @param definition
     * @param schemaCollection
     */
    private static void readInlineSchemas(XSDFactory xsdFactory,
                                          WSDLDefinition wsdlDefinition,
                                          Definition definition,
                                          XmlSchemaCollection schemaCollection) {
        Types types = definition.getTypes();
        if (types != null) {
            int index = 0;
            for (Object ext : types.getExtensibilityElements()) {
                ExtensibilityElement extElement = (ExtensibilityElement)ext;
                Element element = null;
                if (WSDLModelResolver.XSD_QNAME_LIST.contains(extElement.getElementType())) {
                    if (extElement instanceof Schema) {
                        element = ((Schema)extElement).getElement();
                    } else if (extElement instanceof UnknownExtensibilityElement) {
                        element = ((UnknownExtensibilityElement)extElement).getElement();
                    }
                }
                if (element != null) {
                    Document doc = promote(element);
                    XSDefinition xsDefinition = xsdFactory.createXSDefinition();
                    xsDefinition.setUnresolved(true);
                    xsDefinition.setNamespace(element.getAttribute("targetNamespace"));
                    xsDefinition.setDocument(doc);
                    xsDefinition.setLocation(URI.create(doc.getDocumentURI() + "#" + index));
                    loadXSD(schemaCollection, xsDefinition);
                    wsdlDefinition.getXmlSchemas().add(xsDefinition);
                    index++;
                }
            }
        }
        for (Object imports : definition.getImports().values()) {
            List impList = (List)imports;
            for (Object i : impList) {
                javax.wsdl.Import anImport = (javax.wsdl.Import)i;
                // Read inline schemas 
                if (anImport.getDefinition() != null) {
                    readInlineSchemas(xsdFactory, wsdlDefinition, anImport.getDefinition(), schemaCollection);
                }
            }
        }
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
            definition.setSchemaCollection(schemaCollection);
            definition.setSchema(schema);
        }
    }

    /**
     * Create a WSDL4J Definition object from a Java interface
     */
    protected static Definition createDefinition(Map map, final Class<?> javaInterface, boolean requiresSOAP12) {

        final String className = javaInterface.getName();
        // Allow privileged access to get ClassLoader. Requires RuntimePermission read in security
        // policy.
        final ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return javaInterface.getClassLoader();
            }
        });
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        final Java2WSDLBuilder builder = AccessController.doPrivileged(new PrivilegedAction<Java2WSDLBuilder>() {
            public Java2WSDLBuilder run() {
                return new Java2WSDLBuilder(os, className, cl);
            }
        });
        if (map != null) {
            builder.setPkg2nsMap(map);
        }

        // builder.generateWSDL();
        // Allow privileged access to read properties. Requires PropertiesPermission read in
        // security policy.
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    builder.generateWSDL();
                    return null;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        

        try {

            WSDLReader reader = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);

            WSDLLocatorImpl locator = new WSDLLocatorImpl(new ByteArrayInputStream(os.toByteArray()));
            Definition definition = reader.readWSDL(locator);

            // processSOAPVersion(definition, requiresSOAP12);
            processNoArgAndVoidReturnMethods(definition, javaInterface);

            return definition;

        } catch (WSDLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processSOAPVersion(Definition definition, boolean requiresSOAP12) {
        if (requiresSOAP12) {
            removePort(definition, "SOAP11port_http");
        } else {
            removePort(definition, "SOAP12port_http");
        }
    }

    private static void removePort(Definition definition, String portNameSuffix) {
        Service service = (Service)definition.getServices().values().iterator().next();
        Map<?, ?> ports = service.getPorts();
        for (Object o : ports.keySet()) {
            if (((String)o).endsWith(portNameSuffix)) {
                Port p = (Port)ports.remove(o);
                definition.removeBinding(p.getBinding().getQName());
                break;
            }
        }
    }

    private static void processNoArgAndVoidReturnMethods(Definition definition, Class javaInterface) {
        String namespaceURI = definition.getTargetNamespace();
        String prefix = definition.getPrefix(namespaceURI);
        String xsPrefix = definition.getPrefix("http://www.w3.org/2001/XMLSchema");
        PortType portType = (PortType)definition.getAllPortTypes().values().iterator().next();

        Element schema = null;
        Document document = null;
        Types types = definition.getTypes();
        if (types != null) {
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema)ext).getElement();
                    if (element.getAttribute("targetNamespace").equals(namespaceURI)) {
                        schema = element;
                        document = schema.getOwnerDocument();
                        break;
                    }
                }
            }
        }
        if (document == null) {
            return;
        }

        // look at each operation in the port type to see if it needs fixing up
        for (Object oper : portType.getOperations()) {
            javax.wsdl.Operation operation = (javax.wsdl.Operation)oper;
            String opName = operation.getName();

            // if input message has no parts, add one containing an empty wrapper
            Input input = operation.getInput();
            if (input != null) {
                Message inputMsg = input.getMessage();
                if (inputMsg.getParts().isEmpty()) {
                    // create wrapper element and add it to the schema DOM
                    Element wrapper =
                        document.createElementNS("http://www.w3.org/2001/XMLSchema", xsPrefix + ":element");
                    wrapper.setAttribute("name", opName);
                    schema.appendChild(wrapper);
                    Element complexType =
                        document.createElementNS("http://www.w3.org/2001/XMLSchema", xsPrefix + ":complexType");
                    wrapper.appendChild(complexType);

                    // create new part for the wrapper and add it to the message
                    Part part = definition.createPart();
                    part.setName("parameters");
                    part.setElementName(new QName(namespaceURI, opName, prefix));
                    inputMsg.addPart(part);
                }
            }

            // if two-way operation has no output message, add one containing an empty wrapper
            if (input != null && operation.getOutput() == null) {
                boolean isOneWay = false;
                Method[] methods = javaInterface.getMethods();
                for (Method method : methods) {
                    if (method.getName().equals(opName) && method.getAnnotation(OneWay.class) != null) {
                        isOneWay = true;
                    }
                }
                if (!isOneWay) {
                    // create wrapper element and add it to the schema DOM
                    String msgName = opName + "Response";
                    Element wrapper =
                        document.createElementNS("http://www.w3.org/2001/XMLSchema", xsPrefix + ":element");
                    wrapper.setAttribute("name", msgName);
                    schema.appendChild(wrapper);
                    Element complexType =
                        document.createElementNS("http://www.w3.org/2001/XMLSchema", xsPrefix + ":complexType");
                    wrapper.appendChild(complexType);

                    // create new part for the wrapper
                    Part part = definition.createPart();
                    part.setName("parameters");
                    part.setElementName(new QName(namespaceURI, msgName, prefix));

                    // create new message for the part
                    Message outputMsg = definition.createMessage();
                    outputMsg.setQName(new QName(namespaceURI, msgName, prefix));
                    outputMsg.addPart(part);
                    outputMsg.setUndefined(false);
                    definition.addMessage(outputMsg);

                    // create output element for the operation
                    Output output = definition.createOutput();
                    output.setMessage(outputMsg);
                    output.setExtensionAttribute(new QName("http://www.w3.org/2006/05/addressing/wsdl", "Action"),
                                                 new QName("urn:" + msgName));
                    operation.setOutput(output);
                    operation.setStyle(OperationType.REQUEST_RESPONSE);

                    // add binding output element to bindings for this port type
                    for (Object bindObj : definition.getAllBindings().values()) {
                        Binding binding = (Binding)bindObj;
                        if (binding.getPortType().equals(portType)) {
                            BindingOperation op = binding.getBindingOperation(opName, null, null);
                            if (op != null && op.getBindingInput() != null && op.getBindingOutput() == null) {
                                BindingOutput bindingOut = definition.createBindingOutput();
                                for (Object extObj : op.getBindingInput().getExtensibilityElements()) {
                                    bindingOut.addExtensibilityElement((ExtensibilityElement)extObj);
                                }
                                op.setBindingOutput(bindingOut);
                            }
                        }
                    }
                }
            }
        }

    }

}

class WSDLLocatorImpl implements WSDLLocator {
    private InputStream inputStream;
    private String base = "http://";
    private String latestImportURI;

    public WSDLLocatorImpl(InputStream is) {
        this.inputStream = is;
    }

    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    public InputSource getBaseInputSource() {
        try {
            return XMLDocumentHelper.getInputSource(new URL(base), inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getBaseURI() {
        return base;
    }

    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        return null;
    }

    public String getLatestImportURI() {
        return latestImportURI;
    }

}
