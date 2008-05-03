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
import java.util.List;

import javax.xml.XMLConstants;
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

import org.apache.tuscany.sca.monitor.Monitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Default implementation of an XMLInputFactory that creates validating
 * XMLStreamReaders.
 *
 * @version $Rev$ $Date$
 */
public class DefaultValidatingXMLInputFactory extends ValidatingXMLInputFactory {
    
    private XMLInputFactory inputFactory;
    private ValidationSchemaExtensionPoint schemas;
    private Monitor monitor;
    private boolean initialized;
    private Schema aggregatedSchema;

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
                    throw (IOException)e.getException();
                }
                sources[i] = new StreamSource(urlStream, uri);
            }
            
            // Create an aggregated validation schemas from all the XSDs
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // Allow privileged access to check files. Requires FilePermission
            // in security policy.
            try {
                aggregatedSchema = AccessController.doPrivileged(new PrivilegedExceptionAction<Schema>() {
                    public Schema run() throws SAXException {
                        return schemaFactory.newSchema(sources);
                    }
                });
            } catch (PrivilegedActionException e) {
                throw (SAXException)e.getException();
            }

        } catch (Error e) {
            // FIXME Log this, some old JDKs don't support XMLSchema validation
            //e.printStackTrace();
        } catch (SAXParseException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            //FIXME Log this, some old JDKs don't support XMLSchema validation
            e.printStackTrace();
        }
    }

    public XMLEventReader createFilteredReader(XMLEventReader arg0, EventFilter arg1) throws XMLStreamException {
        return inputFactory.createFilteredReader(arg0, arg1);
    }

    public XMLStreamReader createFilteredReader(XMLStreamReader arg0, StreamFilter arg1) throws XMLStreamException {
        return inputFactory.createFilteredReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(InputStream arg0, String arg1) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(InputStream arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    public XMLEventReader createXMLEventReader(Reader arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    public XMLEventReader createXMLEventReader(Source arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    public XMLEventReader createXMLEventReader(String arg0, InputStream arg1) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(String arg0, Reader arg1) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(XMLStreamReader arg0) throws XMLStreamException {
        return inputFactory.createXMLEventReader(arg0);
    }

    public XMLStreamReader createXMLStreamReader(InputStream arg0, String arg1) throws XMLStreamException {
        initializeSchemas();
        return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
    }

    public XMLStreamReader createXMLStreamReader(InputStream arg0) throws XMLStreamException {
        initializeSchemas();
        return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
    }

    public XMLStreamReader createXMLStreamReader(Reader arg0) throws XMLStreamException {
        initializeSchemas();
        return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
    }

    public XMLStreamReader createXMLStreamReader(Source arg0) throws XMLStreamException {
        initializeSchemas();
        return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0), aggregatedSchema, monitor);
    }

    public XMLStreamReader createXMLStreamReader(String arg0, InputStream arg1) throws XMLStreamException {
        initializeSchemas();
        return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
    }

    public XMLStreamReader createXMLStreamReader(String arg0, Reader arg1) throws XMLStreamException {
        initializeSchemas();
        return new ValidatingXMLStreamReader(inputFactory.createXMLStreamReader(arg0, arg1), aggregatedSchema, monitor);
    }

    public XMLEventAllocator getEventAllocator() {
        return inputFactory.getEventAllocator();
    }

    public Object getProperty(String arg0) throws IllegalArgumentException {
        return inputFactory.getProperty(arg0);
    }

    public XMLReporter getXMLReporter() {
        return inputFactory.getXMLReporter();
    }

    public XMLResolver getXMLResolver() {
        return inputFactory.getXMLResolver();
    }

    public boolean isPropertySupported(String arg0) {
        return inputFactory.isPropertySupported(arg0);
    }

    public void setEventAllocator(XMLEventAllocator arg0) {
        inputFactory.setEventAllocator(arg0);
    }

    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
        inputFactory.setProperty(arg0, arg1);
    }

    public void setXMLReporter(XMLReporter arg0) {
        inputFactory.setXMLReporter(arg0);
    }

    public void setXMLResolver(XMLResolver arg0) {
        inputFactory.setXMLResolver(arg0);
    }

}
