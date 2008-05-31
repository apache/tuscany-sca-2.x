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

package org.apache.tuscany.sca.databinding.jaxb;

import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.w3c.dom.Document;

public class JAXBTypeHelper implements XMLTypeHelper {
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String ANYTYPE_NAME = "anyType";
    private static final QName ANYTYPE_QNAME = new QName(SCHEMA_NS, ANYTYPE_NAME);

    private List<Class> types = new ArrayList<Class>();
    
    public JAXBTypeHelper() {
        super();
    }

    public TypeInfo getTypeInfo(Class javaType, Object logical) {
        QName xmlType = JavaXMLMapper.getXMLType(javaType);
        if (xmlType != null) {
            return new TypeInfo(xmlType, true, null);
        } else if (javaType.isInterface()) {
            return new TypeInfo(ANYTYPE_QNAME, true, null);
        } else {
            types.add(javaType);
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
        generateJAXBSchemas(definitions, factory);
        return definitions;
    }
    
    public static Map<String, DOMResult> generateSchema(JAXBContext context) throws IOException {
        SchemaOutputResolverImpl resolver = new SchemaOutputResolverImpl();
        context.generateSchema(resolver);
        return resolver.getResults();
    }

    private void generateJAXBSchemas(List<XSDefinition> definitions, XSDFactory factory) {
        if (types.size() > 0) {
            try {
                Class[] typesArray = new Class[types.size()];
                typesArray = types.toArray(typesArray);
                JAXBContext context = JAXBContext.newInstance(typesArray);
                SchemaOutputResolverImpl resolver = new SchemaOutputResolverImpl();
                context.generateSchema(resolver);
                Map<String, DOMResult> results = resolver.getResults();
                for (Map.Entry<String, DOMResult> entry: results.entrySet()) {
                    XSDefinition definition = factory.createXSDefinition();
                    definition.setUnresolved(true);
                    definition.setDocument((Document)entry.getValue().getNode());
                    definition.setNamespace(entry.getKey());
                    definitions.add(definition);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class SchemaOutputResolverImpl extends SchemaOutputResolver {
        private Map<String, DOMResult> results = new HashMap<String, DOMResult>();

        @Override
        public Result createOutput(String ns, String file) throws IOException {
            DOMResult result = new DOMResult();
            result.setSystemId("sca:dom");
            results.put(ns, result);
            return result;
        }

        public Map<String, DOMResult> getResults() {
            return results;
        }
    }

}
