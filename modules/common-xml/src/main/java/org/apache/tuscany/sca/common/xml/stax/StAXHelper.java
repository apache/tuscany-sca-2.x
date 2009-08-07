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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

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
        // woodstox 3.2.4 fails due to http://jira.codehaus.org/browse/WSTX-144
        // this issue has been fixed in woodstox 3.2.9
        // We can use the commented code once we move to woodstox 3.2.9
        /*
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        Document document = domHelper.newDocument();
        DOMResult result = new DOMResult(document);
        XMLStreamWriter streamWriter = createXMLStreamWriter(result);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
        return result.getNode();
        */
        Document root = domHelper.newDocument();
        ContentHandler handler = domHelper.createContentHandler(root);
        try {
            saveAsSAX(reader, handler);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
        return root;
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

    /**
     * Returns the boolean value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    public static Boolean getAttributeAsBoolean(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    /**
     * Returns the QName value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    public static QName getAttributeAsQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getValueAsQName(reader, qname);
    }

    /**
     * Returns the value of an attribute as a list of QNames.
     * @param reader
     * @param name
     * @return
     */
    public static List<QName> getAttributeAsQNames(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value != null) {
            List<QName> qnames = new ArrayList<QName>();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                qnames.add(getValueAsQName(reader, tokens.nextToken()));
            }
            return qnames;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns a QName from a string.
     * @param reader
     * @param value
     * @return
     */
    public static QName getValueAsQName(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }

    /**
     * Returns the string value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    public static String getAttributeAsString(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }

    /**
     * Returns the value of xsi:type attribute
     * @param reader The XML stream reader
     * @return The QName of the type, if the attribute is not present, null is
     *         returned.
     */
    public static QName getXSIType(XMLStreamReader reader) {
        String qname = reader.getAttributeValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
        return getValueAsQName(reader, qname);
    }

    /**
     * Test if an attribute is explicitly set
     * @param reader
     * @param name
     * @return
     */
    public static boolean isAttributePresent(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name) != null;
    }

    /**
     * Advance the stream to the next END_ELEMENT event skipping any nested
     * content.
     * @param reader the reader to advance
     * @throws XMLStreamException if there was a problem reading the stream
     */
    public static void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (depth == 0) {
                    return;
                }
                depth--;
            }
        }
    }

}
