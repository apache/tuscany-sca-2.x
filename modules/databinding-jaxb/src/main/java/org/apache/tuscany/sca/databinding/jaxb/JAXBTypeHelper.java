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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.databinding.XMLTypeHelper;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.util.JavaXMLMapper;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.osoa.sca.ServiceRuntimeException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class JAXBTypeHelper implements XMLTypeHelper {
    private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String ANYTYPE_NAME = "anyType";
    private static final QName ANYTYPE_QNAME = new QName(SCHEMA_NS, ANYTYPE_NAME);

    // private List<Class<?>> types = new ArrayList<Class<?>>();

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
            // types.add(javaType);
            if (logical instanceof XMLType) {
                xmlType = ((XMLType)logical).getTypeName();
            }
            if (xmlType == null) {
                xmlType = new QName(JAXBContextHelper.jaxbDecapitalize(javaType.getSimpleName()));
            }
            return new TypeInfo(xmlType, false, null);
        }
    }

    /*
    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver) {
        List<XSDefinition> definitions = new ArrayList<XSDefinition>();
        generateJAXBSchemas(definitions, factory);
        return definitions;
    }
    */

    public static Map<String, String> generateSchema(JAXBContext context) throws IOException {
        StringResolverImpl resolver = new StringResolverImpl();
        context.generateSchema(resolver);
        Map<String, String> xsds = new HashMap<String, String>();
        for (Map.Entry<String, StreamResult> xsd : resolver.getResults().entrySet()) {
            xsds.put(xsd.getKey(), xsd.getValue().getWriter().toString());
        }
        return xsds;
    }

    private static class XSDResolver implements URIResolver {
        private Map<String, String> xsds;

        public XSDResolver(Map<String, String> xsds) {
            super();
            this.xsds = xsds;
        }

        public InputSource resolveEntity(java.lang.String namespace,
                                         java.lang.String schemaLocation,
                                         java.lang.String baseUri) {
            String xsd = xsds.get(schemaLocation);
            if (xsd == null) {
                return null;
            }
            return new InputSource(new StringReader(xsd));
        }

    }

    /*
    private void generateJAXBSchemas1(List<XSDefinition> definitions, XSDFactory factory) {
        if (types.size() > 0) {
            try {
                XmlSchemaCollection collection = new XmlSchemaCollection();
                Class[] typesArray = new Class[types.size()];
                typesArray = types.toArray(typesArray);
                JAXBContext context = JAXBContextHelper.createJAXBContext(typesArray);
                Map<String, String> results = generateSchema(context);
                collection.setSchemaResolver(new XSDResolver(results));

                for (Map.Entry<String, String> entry : results.entrySet()) {
                    XSDefinition definition = factory.createXSDefinition();
                    int index = entry.getKey().lastIndexOf('#');
                    String ns = entry.getKey().substring(0, index);
                    String file = entry.getKey().substring(index + 1);
                    definition.setUnresolved(true);
                    definition.setNamespace(ns);
                    definition.setSchema(collection.read(new StringReader(entry.getValue()), null));
                    definition.setSchemaCollection(collection);
                    definition.setUnresolved(false);
                    definitions.add(definition);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    */

    private static class DOMResolverImpl extends SchemaOutputResolver {
        private Map<String, DOMResult> results = new HashMap<String, DOMResult>();

        @Override
        public Result createOutput(String ns, String file) throws IOException {
            DOMResult result = new DOMResult();
            // TUSCANY-2498: Set the system id to "" so that the xsd:import doesn't produce 
            // an illegal schemaLocation attr 
            result.setSystemId("");
            results.put(ns, result);
            return result;
        }

        public Map<String, DOMResult> getResults() {
            return results;
        }
    }

    /*
    private void generateJAXBSchemas(List<XSDefinition> definitions, XSDFactory factory) {
        if (types.size() > 0) {
            try {
                Class<?>[] typesArray = new Class<?>[types.size()];
                typesArray = types.toArray(typesArray);
                JAXBContext context = JAXBContext.newInstance(typesArray);
                generateSchemas(definitions, factory, context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    */

    private void generateSchemas(List<XSDefinition> definitions, XSDFactory factory, JAXBContext context)
        throws IOException {
        DOMResolverImpl resolver = new DOMResolverImpl();
        context.generateSchema(resolver);
        Map<String, DOMResult> results = resolver.getResults();
        for (Map.Entry<String, DOMResult> entry : results.entrySet()) {
            XSDefinition definition = factory.createXSDefinition();
            definition.setUnresolved(true);
            definition.setDocument((Document)entry.getValue().getNode());
            definition.setNamespace(entry.getKey());
            URI location = null;
            try {
                location = new URI(entry.getValue().getSystemId());
            } catch (URISyntaxException e) {
                // ignore: use null value
            }    
            definition.setLocation(location);
            definitions.add(definition);
        }
    }

    private static class StringResolverImpl extends SchemaOutputResolver {
        private Map<String, StreamResult> results = new HashMap<String, StreamResult>();

        @Override
        public Result createOutput(String ns, String file) throws IOException {
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            String sysId = ns + '#' + file;
            result.setSystemId(sysId);
            results.put(sysId, result);
            return result;
        }

        public Map<String, StreamResult> getResults() {
            return results;
        }
    }

    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, Interface intf) {
        try {
            JAXBContext context = JAXBContextHelper.createJAXBContext(intf, false);
            List<XSDefinition> definitions = new ArrayList<XSDefinition>();
            generateSchemas(definitions, factory, context);
            return definitions;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public List<XSDefinition> getSchemaDefinitions(XSDFactory factory, ModelResolver resolver, List<DataType> dataTypes) {
        try {

            JAXBContext context = JAXBContextHelper.createJAXBContext(dataTypes);
            List<XSDefinition> definitions = new ArrayList<XSDefinition>();
            generateSchemas(definitions, factory, context);
            return definitions;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
