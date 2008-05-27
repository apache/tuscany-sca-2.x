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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.tuscany.sca.xsd.XSDFactory;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

public class SDOTypeHelper implements XMLTypeHelper {
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

    private TypeHelper typeHelper;
    private XSDHelper xsdHelper;
    private Map<String, List<Type>> xsdTypesMap = new HashMap<String, List<Type>>();
    private Map<String, List<Type>> typesMap = new HashMap<String, List<Type>>();
    
    public SDOTypeHelper() {
        super();
        typeHelper = SDOContextHelper.getDefaultHelperContext().getTypeHelper();
        xsdHelper = SDOContextHelper.getDefaultHelperContext().getXSDHelper();
    }

    public TypeInfo getTypeInfo(Class javaType, Object logical) {
        QName xmlType = JavaXMLMapper.getXMLType(javaType);
        if (xmlType != null) {
            return new TypeInfo(xmlType, true, null);
        } else {
            Type type = typeHelper.getType(javaType);
            if (xsdHelper.isXSD(type)) {
                List<Type> xsdTypes = xsdTypesMap.get(type.getURI());
                if (xsdTypes == null) {
                    xsdTypes = new ArrayList<Type>();
                    xsdTypesMap.put(type.getURI(), xsdTypes);
                }
                if (!xsdTypes.contains(type)) {
                    xsdTypes.add(type);
                }
            } else {
                List<Type> types = typesMap.get(type.getURI());
                if (types == null) {
                    types = new ArrayList<Type>();
                    typesMap.put(type.getURI(), types);
                }
                if (!types.contains(type)) {
                    types.add(type);
                }
            }
            if (logical instanceof XMLType) {
                xmlType = ((XMLType)logical).getTypeName();
            }
            if (xmlType == null) {
                xmlType = new QName(JavaXMLMapper.getNamespace(javaType),
                                    Introspector.decapitalize(javaType.getSimpleName()));
            }
            return new TypeInfo(xmlType, false, null);
        }
    }

    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver) {
        List<XSDefinition> definitions = new ArrayList<XSDefinition>();
        generateSDOSchemas(definitions, factory);
        addResolvedXSDs(definitions, factory, resolver);
        return definitions;
    }

    private void generateSDOSchemas(List<XSDefinition> definitions, XSDFactory factory) {
        for (Map.Entry<String, List<Type>> entry: typesMap.entrySet()) {
            String schema = xsdHelper.generate(entry.getValue());
            DOMImplementationRegistry registry = null;
            try {
                registry = DOMImplementationRegistry.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
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
            definition.setNamespace(entry.getKey());
            definitions.add(definition);
        }
    }

    private void addResolvedXSDs(List<XSDefinition> definitions, XSDFactory factory, ModelResolver resolver) {
        for (Map.Entry<String, List<Type>> entry: xsdTypesMap.entrySet()) {
            XSDefinition definition = factory.createXSDefinition();
            definition.setUnresolved(true);
            definition.setNamespace(entry.getKey());
            //FIXME: set location URI
            XSDefinition resolved = resolver.resolveModel(XSDefinition.class, definition);
            if (resolved.getSchema() == null) {
                //FIXME: create a checked exception and propagate it back up to the activator
                throw new RuntimeException("No XSD found for namespace " + entry.getKey());
            }
            // make sure all the required types are defined in the resolved schema
            for (Type type: entry.getValue()) {
                QName typeName = new QName(type.getURI(), type.getName());
                if (resolved.getXmlSchemaType(typeName) == null) {
                    //FIXME: create a checked exception and propagate it back up to the activator
                    throw new RuntimeException("No XSD found for " + typeName.toString());
                }
            }
            definitions.add(resolved);
        }
    }

}
