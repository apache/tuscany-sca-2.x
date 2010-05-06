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

package org.apache.tuscany.sca.databinding.sdo;

import java.beans.Introspector;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

public class SDOTypeHelper implements XMLTypeHelper {
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

    private TypeHelper typeHelper;
    private XSDHelper xsdHelper;
	
	private ProcessorContext context;
    // private Map<String, List<Type>> xsdTypesMap = new HashMap<String, List<Type>>();
    // private Map<String, List<Type>> typesMap = new HashMap<String, List<Type>>();

    public SDOTypeHelper( ProcessorContext context ) {
        super();
		this.context=context;
		//Should we use this.context to get helper objects ???
        typeHelper = SDOContextHelper.getDefaultHelperContext().getTypeHelper();
        xsdHelper = SDOContextHelper.getDefaultHelperContext().getXSDHelper();
    }
	//Should we remove this constructor???? otherwise we context gets created
    public SDOTypeHelper() {
    	this(null);
        /*
    	super();
		this.context=null;
		//Should we use this.context to get helper objects ???
        typeHelper = SDOContextHelper.getDefaultHelperContext().getTypeHelper();
        xsdHelper = SDOContextHelper.getDefaultHelperContext().getXSDHelper();
        */
    }
    public TypeInfo getTypeInfo(Class javaType, Object logical) {
        QName xmlType = JavaXMLMapper.getXMLType(javaType);
        if (xmlType != null) {
            return new TypeInfo(xmlType, true, null);
        } else {
            // introspect(javaType, xsdTypesMap, typesMap);
            if (logical instanceof XMLType) {
                xmlType = ((XMLType)logical).getTypeName();
            }
            if (xmlType == null) {
                xmlType =
                    new QName(JavaXMLMapper.getNamespace(javaType), Introspector.decapitalize(javaType.getSimpleName()));
            }
            return new TypeInfo(xmlType, false, null);
        }
    }

    private void introspect(Class javaType, Map<String, List<Type>> xsdTypesMap, Map<String, List<Type>> typesMap) {
        Type type = typeHelper.getType(javaType);
        if (type == null) {
            return;
        }
        if (xsdHelper.isXSD(type)) {
            addToMap(xsdTypesMap, type);
        } else {
            addToMap(typesMap, type);
        }
    }

    private void addToMap(Map<String, List<Type>> map, Type type) {
        List<Type> types = map.get(type.getURI());
        if (types == null) {
            types = new ArrayList<Type>();
            map.put(type.getURI(), types);
        }
        if (!types.contains(type)) {
            types.add(type);
        }
    }

    /*
    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver) {
        List<XSDefinition> definitions = new ArrayList<XSDefinition>();
        generateSDOSchemas(definitions, factory, typesMap);
        addResolvedXSDs(definitions, factory, resolver, xsdTypesMap);
        return definitions;
    }
    */

    private void generateSDOSchemas(List<XSDefinition> definitions, XSDFactory factory, Map<String, List<Type>> map) {
        for (Map.Entry<String, List<Type>> entry : map.entrySet()) {
            List<Type> types = entry.getValue();
            String ns = entry.getKey();
            generateSchema(definitions, factory, types, ns);
        }
    }

    private void generateSchema(List<XSDefinition> definitions, XSDFactory factory, List<Type> types, String ns) {
        String schema = xsdHelper.generate(types);
        DOMImplementationRegistry registry = null;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        DOMImplementation impl = registry.getDOMImplementation("XML 3.0");
        DOMImplementationLS ls = (DOMImplementationLS)impl.getFeature("LS", "3.0");
        LSParser parser = ls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, SCHEMA_NS);
        LSInput input = ls.createLSInput();
        input.setCharacterStream(new StringReader(schema));
        Document document = parser.parse(input);
        XSDefinition definition = factory.createXSDefinition();
        definition.setUnresolved(true);
        definition.setDocument(document);
        definition.setNamespace(ns);
        definitions.add(definition);
    }

    private void addResolvedXSDs(List<XSDefinition> definitions,
                                 XSDFactory factory,
                                 ModelResolver resolver,
                                 Map<String, List<Type>> map) {
        for (Map.Entry<String, List<Type>> entry : map.entrySet()) {
            XSDefinition definition = factory.createXSDefinition();
            definition.setUnresolved(true);
            definition.setNamespace(entry.getKey());
            //FIXME: set location URI
			
            XSDefinition resolved = resolver.resolveModel(XSDefinition.class, definition,context);
            if (resolved.getSchema() == null) {
                //FIXME: create a checked exception and propagate it back up to the activator
                throw new RuntimeException("No XSD found for namespace " + entry.getKey());
            }
            // make sure all the required types are defined in the resolved schema
            for (Type type : entry.getValue()) {
                String name = xsdHelper.getLocalName(type);
                QName typeName = null;
                if (name.endsWith("_._type")) {
                    // FIXME: Anonymous tyype
                    name = name.substring(0, name.length() - "_._type".length());
                    typeName = new QName(type.getURI(), name);
                    if (resolved.getXmlSchemaElement(typeName) == null) {
                        //FIXME: create a checked exception and propagate it back up to the activator
                        throw new RuntimeException("No XSD found for " + typeName.toString());
                    }
                } else {
                    typeName = new QName(type.getURI(), name);
                    if (resolved.getXmlSchemaType(typeName) == null) {
                        //FIXME: create a checked exception and propagate it back up to the activator
                        throw new RuntimeException("No XSD found for " + typeName.toString());
                    }
                }

            }
            definitions.add(resolved);
        }
    }

    private static List<DataType> getDataTypes(Interface intf) {
        List<DataType> dataTypes = new ArrayList<DataType>();
        for (Operation op : intf.getOperations()) {
            WrapperInfo wrapperInfo = op.getWrapper();
            
            if (wrapperInfo != null ) {
                DataType dt1 = wrapperInfo.getInputWrapperType();
                if (dt1 != null) {
                    dataTypes.add(dt1);
                }
                DataType dt2 = wrapperInfo.getOutputWrapperType();
                if (dt2 != null) {
                    dataTypes.add(dt2);
                }
            } else {
                for (DataType dt1 : op.getInputType().getLogical()) {
                    dataTypes.add(dt1);
                }
                DataType dt2 = op.getOutputType();
                if (dt2 != null) {
                    dataTypes.add(dt2);
                }
                for (DataType<DataType> dt3 : op.getFaultTypes()) {
                    DataType dt4 = dt3.getLogical();
                    if (dt4 != null) {
                        dataTypes.add(dt4);
                    }
                }
            }
        }
        return dataTypes;
    }

    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, Interface intf) {
        return getSchemaDefinitions(factory, resolver, getDataTypes(intf));
    }

    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, List<DataType> dataTypes) {
        Map<String, List<Type>> xsdTypesMap = new HashMap<String, List<Type>>();
        Map<String, List<Type>> typesMap = new HashMap<String, List<Type>>();
        for (DataType d : dataTypes) {
            if (SDODataBinding.NAME.equals(d.getDataBinding())) {
                introspect(d.getPhysical(), xsdTypesMap, typesMap);
            }
        }
        List<XSDefinition> definitions = new ArrayList<XSDefinition>();
        generateSDOSchemas(definitions, factory, typesMap);
        addResolvedXSDs(definitions, factory, resolver, xsdTypesMap);
        return definitions;
    }

}
