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
import java.io.StringReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.tuscany.sca.assembly.xsd.Constants;
import org.apache.tuscany.sca.common.xml.XMLDocumentHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.extensibility.ClassLoaderContext;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Default implementation of an XMLInputFactory that creates validating
 * XMLStreamReaders.
 *
 * @version $Rev$ $Date$
 */
public class DefaultValidatingXMLInputFactory extends ValidatingXMLInputFactory implements LSResourceResolver {
    private ExtensionPointRegistry registry;
    private XMLInputFactory inputFactory;
    private DocumentBuilderFactory documentBuilderFactory;
    private DOMImplementationLS ls;
    private ValidationSchemaExtensionPoint schemas;
    private MonitorFactory monitorFactory;
    private boolean initialized;
    private boolean hasSchemas;
    private Schema aggregatedSchema;
    private StAXHelper helper;

    public DefaultValidatingXMLInputFactory(ExtensionPointRegistry registry) {
        this.registry = registry;
        FactoryExtensionPoint factoryExtensionPoint = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.inputFactory = factoryExtensionPoint.getFactory(XMLInputFactory.class);
        this.documentBuilderFactory = factoryExtensionPoint.getFactory(DocumentBuilderFactory.class);
        this.schemas = registry.getExtensionPoint(ValidationSchemaExtensionPoint.class);
        this.monitorFactory =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(MonitorFactory.class);
        this.helper = StAXHelper.getInstance(registry);
    }

    /**
     * Constructs a new XMLInputFactory.
     *
     * @param inputFactory
     * @param schemas
     */
    // FOR Test only
    public DefaultValidatingXMLInputFactory(XMLInputFactory inputFactory, ValidationSchemaExtensionPoint schemas) {
        this.inputFactory = inputFactory;
        this.schemas = schemas;
        this.registry = new DefaultExtensionPointRegistry();
    }
    

    /**
     * Report a exception.
     *
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Throwable ex) {
        Monitor.error(monitor, this, "contribution-validation-messages", message, ex);
    }

    private void warn(Monitor monitor, String message, Object model, Throwable ex) {
        Monitor.warning(monitor, this, "contribution-validation-messages", message, ex);
    }
    
    public static final QName XSD = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "schema");

    private Collection<? extends Source> aggregate(URL... urls) throws IOException, XMLStreamException {
        if (urls.length == 1) {
            return Collections.singletonList(new SAXSource(XMLDocumentHelper.getInputSource(urls[0])));
        }
        Map<String, Collection<URL>> map = new HashMap<String, Collection<URL>>();

        for (URL url : urls) {
            String tns = helper.readAttribute(url, XSD, "targetNamespace");
            Collection<URL> collection = map.get(tns);
            if (collection == null) {
                collection = new HashSet<URL>();
                map.put(tns, collection);
            }
            collection.add(url);
        }
        List<Source> sources = new ArrayList<Source>();
        for (Map.Entry<String, Collection<URL>> e : map.entrySet()) {
            if (e.getValue().size() == 1) {
                sources.add(new SAXSource(XMLDocumentHelper.getInputSource(e.getValue().iterator().next())));
            } else {
                StringBuffer xsd = new StringBuffer("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\"");
                if (e.getKey() != null) {
                    xsd.append(" targetNamespace=\"").append(e.getKey()).append("\"");
                }
                xsd.append(">");
                for (URL url : e.getValue()) {
                    xsd.append("<include schemaLocation=\"").append(url).append("\"/>");
                }
                xsd.append("</schema>");
                SAXSource source = new SAXSource(new InputSource(new StringReader(xsd.toString())));
                sources.add(source);
            }
        }
        return sources;
    }

    /**
     * Initialize the registered schemas and create an aggregated schema for
     * validation.
     * @param monitor TODO
     */
    private synchronized void initializeSchemas(Monitor monitor) {
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
            
            URL[] urls = new URL[uris.size()];
            for (int i = 0; i < urls.length; i++) {
                urls[i] = new URL(uris.get(i));
            }
            final Collection<? extends Source> sources = aggregate(urls);            

            final SchemaFactory schemaFactory = newSchemaFactory();
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
                        return schemaFactory.newSchema(sources.toArray(new Source[sources.size()]));
                    }
                });
            } catch (PrivilegedActionException e) {
            	warn(monitor, "PrivilegedActionException", schemaFactory, (SAXException)e.getException());
            	hasSchemas = false;
                throw (SAXException)e.getException();
            }

        } catch (SAXException e) {
//            IllegalStateException ie = new IllegalStateException(e);
//            error("IllegalStateException", schemas, ie);
//            throw ie;
        } catch (Throwable e) {
            //FIXME Log this, some old JDKs don't support XMLSchema validation
            warn(monitor, e.getMessage(), schemas, e);
            hasSchemas = false;
        }
    }

    /**
     * For OSGi:
     * Create a SchemaFactory in the context of service provider classloaders
     * @return
     */
    private SchemaFactory newSchemaFactory() {
        ClassLoader cl =
            ClassLoaderContext.setContextClassLoader(getClass().getClassLoader(),
                                                     registry.getServiceDiscovery(),
                                                     SchemaFactory.class,
                                                     TransformerFactory.class,
                                                     SAXParserFactory.class,
                                                     DocumentBuilderFactory.class
                                                     );
        try {
            // Create an aggregated validation schemas from all the XSDs
            return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        } finally {
            if (cl != null) {
                Thread.currentThread().setContextClassLoader(cl);
            }
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
        Monitor monitor = monitorFactory.getContextMonitor();
        initializeSchemas(monitor);
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
        }else {
            return inputFactory.createXMLStreamReader(arg0, arg1);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream arg0) throws XMLStreamException {
        Monitor monitor = monitorFactory.getContextMonitor();
        initializeSchemas(monitor);
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Reader arg0) throws XMLStreamException {
        Monitor monitor = monitorFactory.getContextMonitor();
        initializeSchemas(monitor);
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Source arg0) throws XMLStreamException {
        Monitor monitor = monitorFactory.getContextMonitor();
        initializeSchemas(monitor);
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String arg0, InputStream arg1) throws XMLStreamException {
        Monitor monitor = monitorFactory.getContextMonitor();
        initializeSchemas(monitor);
        if (hasSchemas) {
            return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
        } else {
            return inputFactory.createXMLStreamReader(arg0, arg1);
        }
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String arg0, Reader arg1) throws XMLStreamException {
        Monitor monitor = monitorFactory.getContextMonitor();
        initializeSchemas(monitor);
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

    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        String key = null;
        if("http://www.w3.org/2001/XMLSchema".equals(type)) {
            key = namespaceURI;
        } else if("http://www.w3.org/TR/REC-xml".equals(type)) {
            key = publicId;
        }
        URL url = Constants.CACHED_XSDS.get(key);
        if (url != null && !Constants.SCA11_NS.equals(namespaceURI)) {
            systemId = url.toString();
        } else if (url != null && systemId == null) {
            systemId = url.toString();
        } 

        LSInput input = ls.createLSInput();
        input.setBaseURI(baseURI);
        input.setPublicId(publicId);
        input.setSystemId(systemId);
        return input;
    }

}
