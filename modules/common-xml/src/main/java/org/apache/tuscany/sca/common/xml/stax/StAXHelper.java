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
package org.apache.tuscany.sca.common.xml.stax;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.common.xml.stax.impl.StAX2SAXAdapter;
import org.apache.tuscany.sca.common.xml.stax.impl.XMLStreamSerializer;
import org.apache.tuscany.sca.common.xml.stax.reader.DOMXMLStreamReader;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Helper class for StAX
 */
public class StAXHelper {
    private final XMLInputFactory inputFactory;
    private final XMLOutputFactory outputFactory;
    private final DOMHelper domHelper;

    public StAXHelper(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        factories.getFactory(XMLInputFactory.class);
        inputFactory = factories.getFactory(XMLInputFactory.class);
        outputFactory = factories.getFactory(XMLOutputFactory.class);
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        domHelper = utilities.getUtility(DOMHelper.class);
    }
    
    public static StAXHelper getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilities.getUtility(StAXHelper.class);
    }


    /**
     * @param inputFactory
     * @param outputFactory
     * @param domHelper
     */
    public StAXHelper(XMLInputFactory inputFactory, XMLOutputFactory outputFactory, DOMHelper domHelper) {
        super();
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.domHelper = domHelper;
    }

    public XMLStreamReader createXMLStreamReader(InputStream inputStream) throws XMLStreamException {
        return inputFactory.createXMLStreamReader(inputStream);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return inputFactory.createXMLStreamReader(reader);
    }

    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return inputFactory.createXMLStreamReader(source);
    }

    public XMLStreamReader createXMLStreamReader(Node node) throws XMLStreamException {
        /*
        // DOMSource is not supported by the XMLInputFactory from JDK 6
        DOMSource source = new DOMSource(node);
        return createXMLStreamReader(source);
        */
        return new DOMXMLStreamReader(node);
    }

    public XMLStreamReader createXMLStreamReader(String string) throws XMLStreamException {
        StringReader reader = new StringReader(string);
        return createXMLStreamReader(reader);
    }

    public String saveAsString(XMLStreamReader reader) throws XMLStreamException {
        StringWriter writer = new StringWriter();
        save(reader, writer);
        return writer.toString();
    }

    public void save(XMLStreamReader reader, OutputStream outputStream) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        XMLStreamWriter streamWriter = createXMLStreamWriter(outputStream);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
        return outputFactory.createXMLStreamWriter(outputStream);
    }

    public void save(XMLStreamReader reader, Writer writer) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        XMLStreamWriter streamWriter = createXMLStreamWriter(writer);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
    }

    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        return outputFactory.createXMLStreamWriter(writer);
    }

    public Node saveAsNode(XMLStreamReader reader) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        Document document = domHelper.newDocument();
        DOMResult result = new DOMResult(document);
        XMLStreamWriter streamWriter = createXMLStreamWriter(result);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
        return result.getNode();
    }

    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return outputFactory.createXMLStreamWriter(result);
    }

    public void save(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        serializer.serialize(reader, writer);
        writer.flush();
    }

    public void saveAsSAX(XMLStreamReader reader, ContentHandler contentHandler) throws XMLStreamException,
        SAXException {
        new StAX2SAXAdapter(false).parse(reader, contentHandler);
    }

}
