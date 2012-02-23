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

package org.apache.tuscany.sca.binding.ws.wsdlgen;

import java.lang.reflect.Method;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.ParameterMode;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContent;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSerializer.XmlSchemaSerializerException;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Rev$ $Date$
 */
public class Interface2WSDLGenerator {
    private static final Logger logger = Logger.getLogger(Interface2WSDLGenerator.class.getName());
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String SCHEMA_NAME = "schema";
    private static final QName SCHEMA_QNAME = new QName(SCHEMA_NS, SCHEMA_NAME);
    private static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";
    
    private static final String ANYTYPE_NAME = "anyType";
    private static final QName ANYTYPE_QNAME = new QName(SCHEMA_NS, ANYTYPE_NAME);


    private WSDLFactory factory;
    private DataBindingExtensionPoint dataBindings;
    private WSDLDefinitionGenerator definitionGenerator;
    private DocumentBuilderFactory documentBuilderFactory;
    private boolean requiresSOAP12;
    private ModelResolver resolver;
    private XSDFactory xsdFactory;
    private Monitor monitor;

    public Interface2WSDLGenerator(boolean requiresSOAP12,
                                   ModelResolver resolver,
                                   DataBindingExtensionPoint dataBindings,
                                   XSDFactory xsdFactory,
                                   DocumentBuilderFactory documentBuilderFactory,
                                   Monitor monitor) throws WSDLException {
        super();
        this.requiresSOAP12 = requiresSOAP12; 
        this.resolver = resolver;
        this.documentBuilderFactory = documentBuilderFactory;
        definitionGenerator = new WSDLDefinitionGenerator(requiresSOAP12);
        this.dataBindings = dataBindings;
        this.xsdFactory = xsdFactory;
        this.monitor = monitor;
        try{
            this.factory = AccessController.doPrivileged(new PrivilegedExceptionAction<WSDLFactory>() {
                public WSDLFactory run() throws WSDLException{
                    WSDLFactory factory =  WSDLFactory.newInstance();
                    return factory;
                 }
            });
        } catch (PrivilegedActionException e){
            throw (WSDLException) e.getException();
        }
        
    }

    /**
     * Log a warning message.
     * @param problem
     */
    private static void logWarning(Problem problem) {
        Logger problemLogger = Logger.getLogger(problem.getSourceClassName(), problem.getResourceBundleName());
        if (problemLogger != null){
            problemLogger.logp(Level.WARNING, problem.getSourceClassName(), null, problem.getMessageId(), problem.getMessageParams());
        } else {
            logger.severe("Can't get logger " + problem.getSourceClassName()+ " with bundle " + problem.getResourceBundleName());
        }
    }

    /**
     * Report a warning.
     * @param message
     * @param binding
     * @param parameters
     */
    private void warning(String message, Interface interfaze, String... messageParameters) {
        Problem problem = monitor.createProblem(this.getClass().getName(), "wsdlgen-validation-messages", Severity.WARNING, interfaze, message, (Object[])messageParameters);
        if (monitor != null) {
            monitor.problem(problem);
        } else {
            logWarning(problem);
        }
    }

    /**
     * Report a fatal error.
     * @param message
     * @param binding
     * @param parameters
     */
    private void fatal(String message, Interface interfaze, String... messageParameters) {
        Problem problem = monitor.createProblem(this.getClass().getName(), "wsdlgen-validation-messages", Severity.ERROR, interfaze, message, (Object[])messageParameters);
        throw new WSDLGenerationException(problem.toString(), null, problem);
    }
    
    private XMLTypeHelper getTypeHelper(DataType type, Map<String, XMLTypeHelper> helpers) {
        if (type == null) {
            return null;
        }
        String db = type.getDataBinding();
        if (db == null) {
            return null;
        }
        // TUSCANY-3800
        while ("java:array".equals(db)) {
            type = (DataType)type.getLogical();
            db = type.getDataBinding();
         }        
        return helpers.get(db);
    }
    
    private boolean inputTypesCompatible(DataType wrapperType, DataType<List<DataType>> inputType, Map<String, XMLTypeHelper> helpers) {
        XMLTypeHelper wrapperHelper = getTypeHelper(wrapperType, helpers);
        for (DataType dt : inputType.getLogical()) {
            if (getTypeHelper(dt, helpers) != wrapperHelper) {
                return false;
            }
        }
        return true;
    }
    
    private boolean outputTypeCompatible(DataType wrapperType, DataType outputType, Map<String, XMLTypeHelper> helpers) {
        // TUSCANY-3283 - use same algorithm as input types as we now support 
        //                multiple output values so the real output types will 
        //                be wrapped in an "idl:output" data type
        /*
        if (getTypeHelper(outputType, helpers) != getTypeHelper(wrapperType, helpers)) {
            return false;
        } else {
            return true;
        }
        */
        return inputTypesCompatible(wrapperType, outputType, helpers);
    }
    
    private void addDataType(Map<XMLTypeHelper, List<DataType>> map, DataType type, Map<String, XMLTypeHelper> helpers) {
        if (type == null) {
            return;
        }
        String db = type.getDataBinding();
        if (db == null) {
            return;
        }
        if ("java:array".equals(db)) {
            DataType dt = (DataType)type.getLogical();
            db = dt.getDataBinding();
        }
        XMLTypeHelper helper = helpers.get(db);
        List<DataType> types = map.get(helper);
        if (types == null) {
            types = new ArrayList<DataType>();
            map.put(helper, types);
        }
        types.add(type);
    }
    
    private Map<XMLTypeHelper, List<DataType>> getDataTypes(Interface intf, boolean useWrapper, Map<String, XMLTypeHelper> helpers) {
        Map<XMLTypeHelper, List<DataType>> dataTypes = new HashMap<XMLTypeHelper, List<DataType>>();
        for (Operation op : intf.getOperations()) {
            WrapperInfo inputWrapper = op.getInputWrapper();
            DataType dt1 = null;
            boolean useInputWrapper = useWrapper & inputWrapper != null;
            if (useInputWrapper) {
                dt1 = inputWrapper.getWrapperType();
                useInputWrapper &= inputTypesCompatible(dt1, op.getInputType(), helpers);
            }
            if (useInputWrapper) {
                addDataType(dataTypes, dt1, helpers);
            } else {
                for (DataType dt : op.getInputType().getLogical()) {
                    addDataType(dataTypes, dt, helpers);
                }
            }
            
            WrapperInfo outputWrapper = op.getOutputWrapper();
            DataType dt2 = null;
            boolean useOutputWrapper = useWrapper & outputWrapper != null;
            if (useOutputWrapper) {
                dt2 = outputWrapper.getWrapperType();
                useOutputWrapper &= outputTypeCompatible(dt2, op.getOutputType(), helpers);
            }
            if (useOutputWrapper) {
                addDataType(dataTypes, dt2, helpers);
            } else {
                if (op.getOutputType().getLogical().size() != 0) {
                    dt2 = op.getOutputType().getLogical().get(0);
                } 
                addDataType(dataTypes, dt2, helpers);
            }
            
            for (DataType<DataType> dt3 : op.getFaultTypes()) {
                DataType dt4 = dt3.getLogical();
                addDataType(dataTypes, dt4, helpers);
            }
        }
        // Adding classes referenced by @XmlSeeAlso in the java interface
        if (intf instanceof JavaInterface) {
            JavaInterface javaInterface = (JavaInterface)intf;
            Class<?>[] seeAlso = getSeeAlso(javaInterface.getJavaClass());
            if (seeAlso != null) {
                for (Class<?> cls : seeAlso) {
                    DataType dt = new DataTypeImpl<XMLType>(JAXBDataBinding.NAME, cls, XMLType.UNKNOWN);
                    addDataType(dataTypes, dt, helpers);
                }
            }
            seeAlso = getSeeAlso(javaInterface.getCallbackClass());
            if (seeAlso != null) {
                for (Class<?> cls : seeAlso) {
                    DataType dt = new DataTypeImpl<XMLType>(JAXBDataBinding.NAME, cls, XMLType.UNKNOWN);
                    addDataType(dataTypes, dt, helpers);
                }
            }
        }
        return dataTypes;
    }
    
    private static Class<?>[] getSeeAlso(Class<?> interfaze) {
        if (interfaze == null) {
            return null;
        }
        XmlSeeAlso seeAlso = interfaze.getAnnotation(XmlSeeAlso.class);
        if (seeAlso == null) {
            return null;
        } else {
            return seeAlso.value();
        }
    }


    public Definition generate(Interface interfaze, WSDLDefinition wsdlDefinition) throws WSDLException {
        if (interfaze == null) {
            return null;
        }
        if (interfaze instanceof WSDLInterface) {
            return ((WSDLInterface)interfaze).getWsdlDefinition().getDefinition();
        }
        JavaInterface iface = (JavaInterface)interfaze;
        if (!interfaze.isRemotable()) {
            fatal("InterfaceNotRemotable", interfaze, iface.getName());
        }
        QName name = getQName(iface);
        Definition definition = factory.newDefinition();
        if (requiresSOAP12) {
            definition.addNamespace("SOAP12", "http://schemas.xmlsoap.org/wsdl/soap12/");
        } else {
            definition.addNamespace("SOAP", "http://schemas.xmlsoap.org/wsdl/soap/");
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
            // Removed improper defaulting of SOAP action when using doc/lit BARE.
            // The correct default is "" (empty string).
            if (action == null) {
                action = "";
            }
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

        // TUSCANY-3283 - "true" here means also generate the wrapper types using JAXB
        Map<XMLTypeHelper, List<DataType>> dataTypes = getDataTypes(interfaze, true, helpers);
        for (Map.Entry<XMLTypeHelper, List<DataType>> en: dataTypes.entrySet()) {
            XMLTypeHelper helper = en.getKey();
            if (helper == null) {
                continue;
            }
            List<XSDefinition> xsDefinitions = helper.getSchemaDefinitions(xsdFactory, resolver, en.getValue());
            
            // TUSCANY-3283 - move the nonamespace types into the namespace of the interface
            //                as per JAXWS
            mergeNoNamespaceSchema(namespaceURI, xsDefinitions);
            
            for (XSDefinition xsDef: xsDefinitions) {
                //addSchemaExtension(xsDef, schemaCollection, wsdlDefinition, definition);
                loadXSD(schemaCollection, xsDef);
                wsdlDefinition.getXmlSchemas().add(xsDef);
            }
        }
        
        // remove global wrapper elements with schema definitions from generation list
        for (QName wrapperName: new HashSet<QName>(wrappers.keySet())) {
            if (wsdlDefinition.getXmlSchemaElement(wrapperName) != null) {
                wrappers.remove(wrapperName);
            }
        }
        
        // below we might generate wrapper schema into a DOM. If the schema are in a namespace
        // that is already been loaded then we need to throw away the schema collection and reload
        // it because you can't load a DOM into a schema collection if the schema for the namespace
        // has already been loaded
        boolean reloadSchema = false;

        // generate schema elements for wrappers that aren't defined in the schemas
        // TUSCANY-3283 - as we're generating wrappers with JAXB it won't 
        //                go through here for all wrappers. It will just have to do the ones
        //                where there is no JAXB mapping for the child types, e.g. SDO DataObject
        if (wrappers.size() > 0) {
            int i = 0;
            int index = 0;
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
                    // TUSCANY-3283 - if we have to generate a new schema check to see if the 
                    //                WSDL doc already has a schema in this namespace                       
                    xsDef = wsdlDefinition.getSchema(targetNS);
                    if (xsDef != null) {
                        schemaDoc = xsDef.getDocument();
                        schema = schemaDoc.getDocumentElement();
                        //wrapperXSDs.put(targetNS, xsDef);
                        Map<String, String> prefixMap = prefixMaps.get(schema);
                        if (prefixMap == null){
                            prefixMap = new HashMap<String, String>();
                            prefixMaps.put(schema, prefixMap);
                            String [] prefixes = xsDef.getSchema().getNamespaceContext().getDeclaredPrefixes();
                            for (int j = 0; j < prefixes.length; j++){
                                prefixMap.put(xsDef.getSchema().getNamespaceContext().getNamespaceURI(prefixes[j]),
                                             prefixes[j]);
                            }
                        } 
                        reloadSchema = true;
                    } else {                    
                        schemaDoc = createDocument();
                        schema = schemaDoc.createElementNS(SCHEMA_NS, "xs:schema");
                        // The elementFormDefault should be set to unqualified, see TUSCANY-2388
                        schema.setAttribute("elementFormDefault", "unqualified");
                        schema.setAttribute("attributeFormDefault", "qualified");
                        schema.setAttribute("targetNamespace", targetNS);
                        schema.setAttributeNS(XMLNS_NS, "xmlns:xs", SCHEMA_NS);
                        schemaDoc.appendChild(schema);
                        // TUSCANY-3283 - the extension is created at the bottom
                        //Schema schemaExt = createSchemaExt(definition);
                        //schemaExt.setElement(schema);
                        prefixMaps.put(schema, new HashMap<String, String>());
                        xsDef = xsdFactory.createXSDefinition();
                        xsDef.setUnresolved(true);
                        xsDef.setNamespace(targetNS);
                        xsDef.setDocument(schemaDoc);
                        // TUSCANY-2465: Set the system id to avoid schema conflict
                        xsDef.setLocation(URI.create("xsd_" + index + ".xsd"));
                        index++;
                        wrapperXSDs.put(targetNS, xsDef);
                        wsdlDefinition.getXmlSchemas().add(xsDef);
                    }
                }
                Element wrapper = schemaDoc.createElementNS(SCHEMA_NS, "xs:element");
                schema.appendChild(wrapper);
                wrapper.setAttribute("name", entry.getKey().getLocalPart());
                if (entry.getValue().size() == 1 && entry.getValue().get(0).getQName() == null) {
                    // special case for global fault element
                    QName typeName = entry.getValue().get(0).getType().getQName();
                    String nsURI = typeName.getNamespaceURI();
                    if ("".equals(nsURI)) {
                        wrapper.setAttribute("type", typeName.getLocalPart());
                        addSchemaImport(schema, "", schemaDoc);
                    } else if (targetNS.equals(nsURI)) {
                        wrapper.setAttribute("type", typeName.getLocalPart());
                    } else if (SCHEMA_NS.equals(nsURI)) {
                        wrapper.setAttribute("type", "xs:" + typeName.getLocalPart());
                    } else {
                        Map<String, String> prefixMap = prefixMaps.get(schema);
                        String prefix = prefixMap.get(nsURI);
                        if (prefix == null) {
                            prefix = "ns" + i++;
                            prefixMap.put(nsURI, prefix);
                            schema.setAttributeNS(XMLNS_NS, "xmlns:" + prefix, nsURI);
                            addSchemaImport(schema, nsURI, schemaDoc);
                        }
                        wrapper.setAttribute("type", prefix + ":" + typeName.getLocalPart());
                    }                    
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
                            if ("".equals(nsURI)) {
                                xsElement.setAttribute("type", typeName.getLocalPart());
                                addSchemaImport(schema, "", schemaDoc);
                            } else if (SCHEMA_NS.equals(nsURI)) {
                                xsElement.setAttribute("type", "xs:" + typeName.getLocalPart());
                            } else {
                                Map<String, String> prefixMap = prefixMaps.get(schema);
                                String prefix = prefixMap.get(nsURI);
                                if (prefix == null) {
                                    if (targetNS.equals(nsURI)) {
									    prefix = "tns";
									} else {
                                        prefix = "ns" + i++;
                                        addSchemaImport(schema, nsURI, schemaDoc);
									}
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
        }
        
        if (reloadSchema){
            schemaCollection = new XmlSchemaCollection();
            for (XSDefinition xsDef: wsdlDefinition.getXmlSchemas()){
                xsDef.setSchema(null);
                xsDef.setSchemaCollection(null);
            }
        }
        
        for (XSDefinition xsDef: wsdlDefinition.getXmlSchemas()){
            addSchemaExtension(xsDef, schemaCollection, wsdlDefinition, definition);
        }

        return definition;
    }
    
    // TUSCANY-3283 - merge the nonamespace schema into the default namespace schema 
    private void mergeNoNamespaceSchema(String toNamespace, List<XSDefinition> xsDefinitions){
        String fromNamespace = ""; 
        Document fromDoc = null;
        XSDefinition fromXsDef = null;
        Document toDoc = null;
        List<Document> relatedDocs = new ArrayList<Document>();
        
        for (XSDefinition xsDef: xsDefinitions) {
            if(xsDef.getNamespace().equals("")){
                fromXsDef = xsDef;
                fromDoc = xsDef.getDocument();
            } else if(xsDef.getNamespace().equals(toNamespace)){
                toDoc = xsDef.getDocument();
            } else {
                relatedDocs.add(xsDef.getDocument());
            }
        }
        
        if (fromDoc != null && toDoc != null){
            try {
                List<XmlSchema> resultingDocs = mergeSchema(fromNamespace, fromDoc, toNamespace, toDoc, relatedDocs);
                
                for (XmlSchema schema : resultingDocs){
                    for (XSDefinition xsDef: xsDefinitions) {
                        if (xsDef.getNamespace().equals(schema.getTargetNamespace())){
                            Document doc = schema.getSchemaDocument();
                            // just for debugging
                            //printDOM(doc);
                            xsDef.setDocument(doc);
                            //xsDef.setSchema(schema);
                        }
                    }
                }
                
                xsDefinitions.remove(fromXsDef);
            } catch (XmlSchemaSerializerException ex){
                throw new WSDLGenerationException(ex);
            }
        }
    }
       
    // TUSCANY-3283 - merge the nonamespace schema into the default namespace schema 
    private List<XmlSchema> mergeSchema(String fromNamespace, 
                                        Document fromDoc, 
                                        String toNamespace, 
                                        Document toDoc, 
                                        List<Document> relatedDocs) throws XmlSchemaSerializerException{
        // Read all the input DOMs into a schema collection so we can manipulate them
        XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
        schemaCollection.read(fromDoc.getDocumentElement());
        schemaCollection.read(toDoc.getDocumentElement());
        
        for(Document doc : relatedDocs){
            schemaCollection.read(doc.getDocumentElement());
        }
        
        XmlSchema fromSchema = null;
        XmlSchema toSchema = null;
        List<XmlSchema> resultSchema = new ArrayList<XmlSchema>();
        XmlSchema schemas[] = schemaCollection.getXmlSchemas();
        for (int i=0; i < schemas.length; i++){
            XmlSchema schema = schemas[i];

            if (schema.getTargetNamespace() == null){
                fromSchema = schema;
            } else if (schema.getTargetNamespace().equals(toNamespace)){
                toSchema = schema; 
                resultSchema.add(schema);
            } else if (schema.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")){
                // do nothing as we're not going to print out the
                // schema schema
            } else {
                resultSchema.add(schema);
            }
        }
        
        if (fromSchema == null || toSchema == null){
            return resultSchema;
        }
        
        // copy all the FROM items to the TO schema
        XmlSchemaObjectCollection fromItems = fromSchema.getItems();
        XmlSchemaObjectCollection toItems = toSchema.getItems();
        List<XmlSchemaObject> movedItems = new ArrayList<XmlSchemaObject>();
       
        Iterator<XmlSchemaObject> iter = fromItems.getIterator();
        while(iter.hasNext()){
            // don't copy import for TO namespace
            XmlSchemaObject obj = iter.next();
            if (obj instanceof XmlSchemaImport &&
                ((XmlSchemaImport)obj).getNamespace().equals(toNamespace)){
                // do nothing
            } else {
                toItems.add(obj);
                movedItems.add(obj);
            }
        }
        
        // check that all types in the TO namespace are now referred to correctly across the schema
        for(XmlSchemaObject obj : movedItems){
            fixUpMovedTypeReferences(fromNamespace, toNamespace, obj, resultSchema);
        }
        
        return resultSchema;
    }
    
    // TUSCANY-3283 - fix up any references to types moved to the default namespace schema
    public void fixUpMovedTypeReferences(String fromNamespace, String toNamespace, XmlSchemaObject fixUpObj, List<XmlSchema> relatedSchema){
        
        if (!(fixUpObj instanceof XmlSchemaComplexType)){
            return;
        }
        
        for (XmlSchema schema : relatedSchema){
            int importRemoveIndex = -1;
            for (int i = 0; i < schema.getItems().getCount(); i++){
                XmlSchemaObject obj = schema.getItems().getItem(i);
                
                processXMLSchemaObject(toNamespace, obj, fixUpObj);
                
                // remove FROM imports
                if (obj instanceof XmlSchemaImport &&
                    ((XmlSchemaImport)obj).getNamespace().equals(fromNamespace)){
                    importRemoveIndex = i;
                }
            }

            if (importRemoveIndex >= 0){
                schema.getItems().removeAt(importRemoveIndex);
            }
        }
    }
    
    // TUSCANY-3283 - iterate down the schema tree looking for references to the item being moved
    public void processXMLSchemaObject(String toNamespace, XmlSchemaObject obj,  XmlSchemaObject fixUpObj){
        if (obj instanceof XmlSchemaComplexType){
            processXMLSchemaObject(toNamespace, ((XmlSchemaComplexType)obj).getParticle(), fixUpObj);
            processXMLSchemaObject(toNamespace, ((XmlSchemaComplexType)obj).getContentModel(), fixUpObj);
        } else if (obj instanceof XmlSchemaComplexContent){
            processXMLSchemaObject(toNamespace, ((XmlSchemaComplexContent)obj).getContent(), fixUpObj);            
        } else if (obj instanceof XmlSchemaElement){
            XmlSchemaElement element = (XmlSchemaElement)obj;
            if(element.getSchemaType() == fixUpObj){
                QName name = element.getSchemaTypeName();
                QName newName = new QName(toNamespace, name.getLocalPart());
                element.setSchemaTypeName(newName);
            }
            ((XmlSchemaElement)obj).getSchemaType();
        } else if (obj instanceof XmlSchemaGroupBase){
            XmlSchemaObjectCollection items = ((XmlSchemaGroupBase)obj).getItems();
            Iterator<XmlSchemaObject> iter = items.getIterator();
            while(iter.hasNext()){
                processXMLSchemaObject(toNamespace, iter.next(), fixUpObj);
            }
        } else if (obj instanceof XmlSchemaComplexContentExtension){
            XmlSchemaComplexContentExtension extension = (XmlSchemaComplexContentExtension)obj;
            QName name = extension.getBaseTypeName();
            QName newName = new QName(toNamespace, name.getLocalPart());
            extension.setBaseTypeName(newName);
        }
        // TODO - what other structure items will be generated by JAXB?
    }    
    
    /*
     * TUSCANY-3283 - Just used when debugging DOM problems
     */
    private void printDOM(Document document){
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source source = new DOMSource(document);
            Result result = new StreamResult(System.out);
            transformer.transform(source, result);
            System.out.println("\n");
            System.out.flush();
        } catch (Exception ex){
            ex.toString();
        }
    }
    

    private static void addSchemaImport(Element schema, String nsURI, Document schemaDoc) {
        Element imp = schemaDoc.createElementNS(SCHEMA_NS, "xs:import");
        if (!"".equals(nsURI)) {
            imp.setAttribute("namespace", nsURI);
        }
        // Scan all xs:import elements to match namespace
        NodeList childNodes = schema.getElementsByTagNameNS(SCHEMA_NS, "import");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode instanceof Element) {
                String ns = ((Element)childNode).getAttributeNS(SCHEMA_NS, "namespace");
                if (nsURI.equals(ns)) {
                    // The xs:import with the same namespace has been declared
                    return;
                }
            }
        }
        // Try to find the first node after the import elements
        Node firstNodeAfterImport = null;
        if (childNodes.getLength() > 0) {
            firstNodeAfterImport = childNodes.item(childNodes.getLength() - 1).getNextSibling();
        } else {
            firstNodeAfterImport = schema.getFirstChild();
        }

        if (firstNodeAfterImport == null) {
            schema.appendChild(imp);
        } else {
            schema.insertBefore(imp, firstNodeAfterImport);
        }
    }

    private void addSchemaExtension(XSDefinition xsDef,
                                    XmlSchemaCollection schemaCollection,
                                    WSDLDefinition wsdlDefinition,
                                    Definition definition) throws WSDLException {
        if (xsDef.getAggregatedDefinitions() != null) {
            for (XSDefinition xsd: xsDef.getAggregatedDefinitions()) {
                addSchemaExtension(xsd, schemaCollection, wsdlDefinition, definition);
            }
        } else {
            String nsURI = xsDef.getNamespace();
            Document document = xsDef.getDocument();
            if (document == null) {
                try {
                    NamespaceMap prefixMap = new NamespaceMap();
                    prefixMap.add("xs", SCHEMA_NS);
                    prefixMap.add("tns", nsURI);
                    XmlSchema schemaDef = xsDef.getSchema();
                    schemaDef.setNamespaceContext(prefixMap);
                    Document[] docs = schemaDef.getAllSchemas();
                    document = docs[docs.length-1];
                    document.setDocumentURI(xsDef.getLocation().toString());
                    xsDef.setDocument(document);
                } catch (XmlSchemaException e) {
                    throw new RuntimeException(e);
                }
            }
            loadXSD(schemaCollection, xsDef);
            //wsdlDefinition.getXmlSchemas().add(xsDef);
            Element schema = document.getDocumentElement();
            Schema schemaExt = createSchemaExt(definition);
            schemaExt.setDocumentBaseURI(document.getDocumentURI());
            schemaExt.setElement(schema);
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

    public Document createDocument() {
        Document document;
        try {
            if (documentBuilderFactory == null) {
                documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setNamespaceAware(true);
            }
            document = documentBuilderFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new WSDLGenerationException(ex);
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

        List<ElementInfo> elements = null;
        // FIXME: By default, java interface is mapped to doc-lit-wrapper style WSDL
        if (op.getInputWrapper() != null) {
            // Generate doc-lit-wrapper style
            inputMsg.addPart(generateWrapperPart(definition, op, helpers, wrappers, true));
        } else {
            // Bare style
            int i = 0;
            for (DataType d : op.getInputType().getLogical()) {
                inputMsg.addPart(generatePart(definition, d, "arg" + i));
                elements = new ArrayList<ElementInfo>();
                ElementInfo element = getElementInfo(d.getPhysical(), d, null, helpers);
                elements.add(element);
                QName elementName = ((XMLType)d.getLogical()).getElementName();
                wrappers.put(elementName, elements);
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

            if (op.getOutputWrapper() != null) {
                outputMsg.addPart(generateWrapperPart(definition, op, helpers, wrappers, false));
            } else {
                
                if ((op.getOutputType() != null) && ( op.getOutputType().getLogical().size() != 0)) {
                	DataType outputType = op.getOutputType().getLogical().get(0);
                    outputMsg.addPart(generatePart(definition, outputType, "return"));
                    elements = new ArrayList<ElementInfo>();
                    ElementInfo element = getElementInfo(outputType.getPhysical(), outputType, null, helpers);
                    elements.add(element);
                    QName elementName = ((XMLType)outputType.getLogical()).getElementName();
                    wrappers.put(elementName, elements);
                }
            }
            output.setMessage(outputMsg);

            operation.setOutput(output);
            operation.setStyle(OperationType.REQUEST_RESPONSE);
        } else {
            operation.setStyle(OperationType.ONE_WAY);
        }

        for (DataType<DataType> faultType: op.getFaultTypes()) {
            Fault fault = definition.createFault();
            // TUSCANY-3778 - use the definition namespace not the element namespace to create the fault message
            QName faultName = ((XMLType)faultType.getLogical().getLogical()).getElementName();
            QName faultMsgName = new QName(namespaceURI, faultName.getLocalPart());
            fault.setName(faultName.getLocalPart());
            Message faultMsg = definition.getMessage(faultMsgName);
            if (faultMsg == null) {
                faultMsg = definition.createMessage();
                faultMsg.setQName(faultMsgName);
                faultMsg.setUndefined(false);
                definition.addMessage(faultMsg);
                faultMsg.addPart(generatePart(definition, faultType.getLogical(), faultName.getLocalPart()));
            }
            fault.setMessage(faultMsg);
            operation.addFault(fault);
            if (faultType.getLogical().getPhysical() != faultType.getPhysical()) {
                // create special wrapper for type indirection to real fault bean
                DataType logical = faultType.getLogical();
                elements = new ArrayList<ElementInfo>();
                elements.add(getElementInfo(logical.getPhysical(), logical, null, helpers));
             } else {
                // convert synthesized fault bean to a wrapper type
                for (DataType<XMLType> propDT: op.getFaultBeans().get(faultName)) {
                    XMLType logical = propDT.getLogical();
                    elements = new ArrayList<ElementInfo>();
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
            QName elementName = xmlType.getElementName();
            part.setElementName(elementName);
            addNamespace(definition, elementName);
            if (xmlType.getElementName() == null) {
                QName typeName = xmlType.getTypeName();
                part.setTypeName(typeName);
                addNamespace(definition, typeName);
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
        
        WrapperInfo inputWrapper = operation.getInputWrapper();
        WrapperInfo outputWrapper = operation.getOutputWrapper();
        
        if ((inputWrapper != null) && (outputWrapper != null)) {
            ElementInfo elementInfo =
                input ? inputWrapper.getWrapperElement() : outputWrapper.getWrapperElement();
            List<ElementInfo> elements =
                input ? inputWrapper.getChildElements() : outputWrapper.getChildElements();
            QName wrapperName = elementInfo.getQName();
            part.setElementName(wrapperName);
            addNamespace(definition, wrapperName);
            wrappers.put(wrapperName, elements);

            // FIXME: [rfeng] Ideally, we should try to register the wrappers only. But we are
            // expriencing the problem that we cannot handle XSD imports 
            /*
            Class<?> wrapperClass = input ? opWrapper.getInputWrapperClass() : opWrapper.getOutputWrapperClass();
            DataType wrapperDT = input ? opWrapper.getInputWrapperType() : opWrapper.getOutputWrapperType();
            if (wrapperClass != null) {
                getElementInfo(wrapperClass, wrapperDT, wrapperName, helpers);
                return part;
            }
            */

            Method method = ((JavaOperation)operation).getJavaMethod();
            
            /*
             * Making this change, though not understanding
             * whether we can assume JAXWSJavaInterfaceProcessor was already used to process
             */
            if (input) {
                List<DataType> inputDTs = operation.getInputType().getLogical();
                for (int i = 0; i < inputDTs.size(); i++) {
                    DataType nextInput = inputDTs.get(i);
                    elements.set(i, getElementInfo(nextInput.getPhysical(), nextInput, elements.get(i).getQName(), helpers));
                }

            } else {
                List<DataType> outputDTs = operation.getOutputType().getLogical();
                for (int i = 0; i < outputDTs.size(); i++) {
                    DataType nextOutput = outputDTs.get(i);
                    elements.set(i, getElementInfo(nextOutput.getPhysical(), nextOutput, elements.get(i).getQName(), helpers));
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
        XMLTypeHelper helper = helpers.get(db);
        if (helper == null) {
            DataBinding dataBinding = dataBindings.getDataBinding(db);
            if (dataBinding == null) {
                QName element = name;
                if (element == null || dataType.getLogical() instanceof XMLType) {
                    XMLType xmlType = (XMLType)dataType.getLogical();
                    if (xmlType.getElementName() != null) {
                        element = xmlType.getElementName();
                    }
                }
                return new ElementInfo(element, new TypeInfo(ANYTYPE_QNAME, false, null));
                // throw new ServiceRuntimeException("No data binding for " + db);
            }

            helper = dataBinding.getXMLTypeHelper();
            if (helper == null) {
                // Default to JAXB
                helper = helpers.get(JAXBDataBinding.NAME);
                if (helper == null) {
                    helper = dataBindings.getDataBinding(JAXBDataBinding.NAME).getXMLTypeHelper();
                    helpers.put(JAXBDataBinding.NAME, helper);
                }
            }
            helpers.put(db, helper);
        }
        // TUSCANY-3616 - don't revert a byte[] to a byte type but retain the mapping to base64Binary
        //                which is carried in the dataType and the original javaType
        TypeInfo typeInfo = null;
        ElementInfo element = null;
        if (byte[].class == javaType){
            typeInfo = helper.getTypeInfo(javaType, dataType.getLogical());
            element = new ElementInfo(name, typeInfo);
            element.setMany(false);
        } else {
            typeInfo = helper.getTypeInfo(javaType.isArray() ? javaType.getComponentType() : javaType, dataType.getLogical());
            element = new ElementInfo(name, typeInfo);
            element.setMany(javaType.isArray());
        }

        // TUSCANY-3298: Check the "many" flag set by databinding introspection
        Object logical = dataType.getLogical();
        if (logical instanceof XMLType && ((XMLType)logical).isMany()) {
            element.setMany(true);
        }
        
        element.setNillable(!javaType.isPrimitive());
        return element;
    }

    private static void addNamespace(Definition definition, QName name) {
        String namespace = name.getNamespaceURI();
        if (definition.getPrefix(namespace) == null) {
            definition.addNamespace("ns" + definition.getNamespaces().size(), namespace);
        }
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
