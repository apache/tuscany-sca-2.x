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

package org.apache.tuscany.sca.contribution.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * Default implementation of an XMLInputFactory that creates validating
 * XMLStreamReaders.
 *
 * @version $Rev$ $Date$
 */
public class DefaultValidatingXMLInputFactory extends ValidatingXMLInputFactory implements LSResourceResolver {

    private XMLInputFactory inputFactory;
    private DocumentBuilderFactory documentBuilderFactory;
    private DOMImplementationLS ls;
    private ValidationSchemaExtensionPoint schemas;
    private Monitor monitor;
    private boolean initialized;
    private boolean hasSchemas;
    private Schema aggregatedSchema;

    public DefaultValidatingXMLInputFactory(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factoryExtensionPoint = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.inputFactory = factoryExtensionPoint.getFactory(XMLInputFactory.class);
        this.documentBuilderFactory = factoryExtensionPoint.getFactory(DocumentBuilderFactory.class);
        this.schemas = registry.getExtensionPoint(ValidationSchemaExtensionPoint.class);
        this.monitor =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(MonitorFactory.class).createMonitor();
    }

    /**
     * Constructs a new XMLInputFactory.
     *
     * @param inputFactory
     * @param schemas
     */
    public DefaultValidatingXMLInputFactory(XMLInputFactory inputFactory, ValidationSchemaExtensionPoint schemas, Monitor monitor) {
        this.inputFactory = inputFactory;
        this.schemas = schemas;
        this.monitor = monitor;
    }

    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Throwable ex) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "contribution-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      ex);
            monitor.problem(problem);
        }
    }

    private void warn(String message, Object model, Throwable ex) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "contribution-validation-messages",
                                      Severity.WARNING,
                                      model,
                                      message,
                                      ex);
            monitor.problem(problem);
        }
    }

    /**
     * Initialize the registered schemas and create an aggregated schema for
     * validation.
     */
    private void initializeSchemas() {
        if (initialized) {
            return;
        }
        initialized = true;

        // Load the XSDs registered in the validation schema extension point
        try {
            List<String> uris = schemas.getSchemas();
            int n = uris.size();
            if (n ==0) {
                return;
            } else {
                hasSchemas = true;
            }
            final Source[] sources = new Source[n];
            for (int i =0; i < n; i++) {
                final String uri = uris.get(i);
                // Allow privileged access to open URL stream. Requires FilePermission in security policy.
                final URL url = new URL( uri );
                InputStream urlStream;
                try {
                    urlStream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                        public InputStream run() throws IOException {
                            URLConnection connection = url.openConnection();
                            connection.setUseCaches(false);
                            return connection.getInputStream();
                        }
                    });
                } catch (PrivilegedActionException e) {
                	error("PrivilegedActionException", url, (IOException)e.getException());
                    throw (IOException)e.getException();
                }
                sources[i] = new StreamSource(urlStream, uri);
            }

            // Create an aggregated validation schemas from all the XSDs
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            /*
            // Set the feature to avoid DTD processing
            try {
                schemaFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
                schemaFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                schemaFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (SAXException e) {
                // Ignore
            }
            */

            DOMImplementation impl = null;
            try {
                impl = documentBuilderFactory.newDocumentBuilder().getDOMImplementation();
            } catch (ParserConfigurationException e) {
                // Ignore
            }
            if (impl instanceof DOMImplementationLS) {
                ls = (DOMImplementationLS)impl;
                schemaFactory.setResourceResolver(this);
            }
            // Allow privileged access to check files. Requires FilePermission
            // in security policy.
            try {
                aggregatedSchema = AccessController.doPrivileged(new PrivilegedExceptionAction<Schema>() {
                    public Schema run() throws SAXException {
                        return schemaFactory.newSchema(sources);
                    }
                });
            } catch (PrivilegedActionException e) {
            	warn("PrivilegedActionException", schemaFactory, (SAXException)e.getException());
            	hasSchemas = false;
                throw (SAXException)e.getException();
            }

        } catch (SAXException e) {
//            IllegalStateException ie = new IllegalStateException(e);
//            error("IllegalStateException", schemas, ie);
//            throw ie;
        } catch (Throwable e) {
            //FIXME Log this, some old JDKs don't support XMLSchema validation
            warn(e.getMessage(), schemas, e);
            hasSchemas = false;
        }
    }

    @Override
    public XMLEventReader createFilteredReader(XMLEventReader arg0, EventFilter arg1) throws XMLStreamException {
        return inputFactory.createFilteredReader(arg0, arg1);
    }

    @Override
    public XMLStreamReader createFilteredReader(XMLStreamReader arg0, StreamFilter arg1) throws XMLStreamException {
        return inputFactory.createFilteredReader(arg0, arg1);
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream arg0, String arg1) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0, arg1);
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    @Override
    public XMLEventReader createXMLEventReader(Reader arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    @Override
    public XMLEventReader createXMLEventReader(Source arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    @Override
    public XMLEventReader createXMLEventReader(String arg0, InputStream arg1) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0, arg1);
    }

    @Override
    public XMLEventReader createXMLEventReader(String arg0, Reader arg1) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0, arg1);
    }

    @Override
    public XMLEventReader createXMLEventReader(XMLStreamReader arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream arg0, String arg1) throws XMLStreamException {
        initializeSchemas();
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
        }else {
            return inputFactory.createXMLStreamReader(arg0, arg1);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream arg0) throws XMLStreamException {
        initializeSchemas();
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Reader arg0) throws XMLStreamException {
        initializeSchemas();
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Source arg0) throws XMLStreamException {
        initializeSchemas();
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String arg0, InputStream arg1) throws XMLStreamException {
        initializeSchemas();
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0, arg1);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String arg0, Reader arg1) throws XMLStreamException {
        initializeSchemas();
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0, arg1);
        }
    }

    @Override
    public XMLEventAllocator getEventAllocator() {
        return inputFactory.getEventAllocator();
    }

    @Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        return inputFactory.getProperty(arg0);
    }

    @Override
    public XMLReporter getXMLReporter() {
        return inputFactory.getXMLReporter();
    }

    @Override
    public XMLResolver getXMLResolver() {
        return inputFactory.getXMLResolver();
    }

    @Override
    public boolean isPropertySupported(String arg0) {
        return inputFactory.isPropertySupported(arg0);
    }

    @Override
    public void setEventAllocator(XMLEventAllocator arg0) {
        inputFactory.setEventAllocator(arg0);
    }

    @Override
    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
        inputFactory.setProperty(arg0, arg1);
    }

    @Override
    public void setXMLReporter(XMLReporter arg0) {
        inputFactory.setXMLReporter(arg0);
    }

    @Override
    public void setXMLResolver(XMLResolver arg0) {
        inputFactory.setXMLResolver(arg0);
    }

    /**
     * Cache for public XSDs and DTDs
     */
    private static Map<String, URL> cachedXSDs = new HashMap<String, URL>();
    static {
        cachedXSDs
            .put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                 DefaultValidatingXMLInputFactory.class
                     .getResource("/org/apache/tuscany/sca/assembly/xsd/oasis-200401-wss-wssecurity-secext-1.0.xsd"));
        cachedXSDs
            .put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
                 DefaultValidatingXMLInputFactory.class
                     .getResource("/org/apache/tuscany/sca/assembly/xsd/oasis-200401-wss-wssecurity-utility-1.0.xsd"));
        cachedXSDs.put("http://www.w3.org/2005/08/addressing", DefaultValidatingXMLInputFactory.class
            .getResource("/org/apache/tuscany/sca/assembly/xsd/ws-addr.xsd"));
        cachedXSDs.put("http://www.w3.org/ns/ws-policy", DefaultValidatingXMLInputFactory.class
            .getResource("/org/apache/tuscany/sca/assembly/xsd/ws-policy.xsd"));
        cachedXSDs.put("http://www.w3.org/ns/wsdl-instance", DefaultValidatingXMLInputFactory.class
            .getResource("/org/apache/tuscany/sca/assembly/xsd/wsdli.xsd"));
        cachedXSDs.put("http://www.w3.org/XML/1998/namespace", DefaultValidatingXMLInputFactory.class
            .getResource("/org/apache/tuscany/sca/assembly/xsd/xml.xsd"));
        cachedXSDs.put("http://www.w3.org/2000/09/xmldsig#", DefaultValidatingXMLInputFactory.class
            .getResource("/org/apache/tuscany/sca/assembly/xsd/xmldsig-core-schema.xsd"));

        cachedXSDs.put("-//W3C//DTD XMLSCHEMA 200102//EN", DefaultValidatingXMLInputFactory.class
            .getResource("/org/apache/tuscany/sca/assembly/xsd/XMLSchema.dtd"));
        cachedXSDs.put("datatypes", DefaultValidatingXMLInputFactory.class
            .getResource("/org/apache/tuscany/sca/assembly/xsd/datatypes.dtd"));
    };

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        String key = null;
        if("http://www.w3.org/2001/XMLSchema".equals(type)) {
            key = namespaceURI;
        } else if("http://www.w3.org/TR/REC-xml".equals(type)) {
            key = publicId;
        }
        URL url = cachedXSDs.get(key);
        if (url != null) {
            systemId = url.toString();
        }

        LSInput input = ls.createLSInput();
        input.setBaseURI(baseURI);
        input.setPublicId(publicId);
        input.setSystemId(systemId);
        return input;
    }

}
