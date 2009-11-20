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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
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
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
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
        if (outputFactory != null) {
            this.outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        }
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

    private static InputStream openStream(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
            // See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5041014
            connection.setUseCaches(false);
        }
        InputStream is = connection.getInputStream();
        return is;
    }

    public XMLStreamReader createXMLStreamReader(URL url) throws XMLStreamException {
        try {
            return createXMLStreamReader(openStream(url));
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public String saveAsString(XMLStreamReader reader) throws XMLStreamException {
        StringWriter writer = new StringWriter();
        save(reader, writer);
        return writer.toString();
    }

    public void save(XMLStreamReader reader, OutputStream outputStream) throws XMLStreamException {
        XMLStreamWriter streamWriter = createXMLStreamWriter(outputStream);
        save(reader, streamWriter);
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
        return outputFactory.createXMLStreamWriter(outputStream);
    }

    public void save(XMLStreamReader reader, Writer writer) throws XMLStreamException {
        XMLStreamWriter streamWriter = createXMLStreamWriter(writer);
        save(reader, streamWriter);
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
        XMLStreamSerializer serializer = new XMLStreamSerializer(isReparingNamespaces());
        serializer.serialize(reader, writer);
        writer.flush();
    }

    public void saveAsSAX(XMLStreamReader reader, ContentHandler contentHandler) throws XMLStreamException,
        SAXException {
        new StAX2SAXAdapter(false).parse(reader, contentHandler);
    }

    /**
     * @param url
     * @param element
     * @param attribute
     * @param rootOnly
     * @return
     * @throws IOException
     * @throws XMLStreamException
     */
    public String readAttribute(URL url, QName element, String attribute) throws IOException, XMLStreamException {
        if (attribute == null) {
            attribute = "targetNamespace";
        }
        XMLStreamReader reader = createXMLStreamReader(url);
        try {
            return readAttributeFromRoot(reader, element, attribute);
        } finally {
            reader.close();
        }
    }

    public List<String> readAttributes(URL url, QName element, String attribute) throws IOException, XMLStreamException {
        if (attribute == null) {
            attribute = "targetNamespace";
        }
        XMLStreamReader reader = createXMLStreamReader(url);
        try {
            Attribute attr = new Attribute(element, attribute);
            return readAttributes(reader, attr)[0].getValues();
        } finally {
            reader.close();
        }
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

    private Attribute[] readAttributes(XMLStreamReader reader, AttributeFilter filter) throws XMLStreamException {
        XMLStreamReader newReader = inputFactory.createFilteredReader(reader, filter);
        while (filter.proceed() && newReader.hasNext()) {
            newReader.next();
        }
        return filter.attributes;
    }

    public Attribute[] readAttributes(URL url, Attribute... attributes) throws XMLStreamException {
        XMLStreamReader reader = createXMLStreamReader(url);
        try {
            return readAttributes(reader, attributes);
        } finally {
            reader.close();
        }
    }

    public Attribute[] readAttributes(XMLStreamReader reader, Attribute... attributes) throws XMLStreamException {
        return readAttributes(reader, new AttributeFilter(false, attributes));
    }

    private String readAttributeFromRoot(XMLStreamReader reader, Attribute filter) throws XMLStreamException {
        Attribute[] attrs = readAttributes(reader, new AttributeFilter(true, filter));
        List<String> values = attrs[0].getValues();
        if (values.isEmpty()) {
            return null;
        } else {
            return values.get(0);
        }
    }

    public String readAttributeFromRoot(XMLStreamReader reader, QName element, String attributeName)
        throws XMLStreamException {
        Attribute filter = new Attribute(element, attributeName);
        return readAttributeFromRoot(reader, filter);
    }

    public static class Attribute {
        private QName element;
        private String name;
        private List<String> values = new ArrayList<String>();

        /**
         * @param element
         * @param name
         */
        public Attribute(QName element, String name) {
            super();
            this.element = element;
            this.name = name;
        }

        public List<String> getValues() {
            return values;
        }

    }

    private static class AttributeFilter implements StreamFilter {
        private boolean proceed = true;
        private Attribute[] attributes;
        private boolean rootOnly;

        /**
         * @param rootOnly
         */
        public AttributeFilter(boolean rootOnly, Attribute... attributes) {
            super();
            this.rootOnly = rootOnly;
            this.attributes = attributes;
        }

        public boolean accept(XMLStreamReader reader) {
            if (attributes == null || attributes.length == 0) {
                proceed = false;
                return true;
            }
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                QName name = reader.getName();
                for (Attribute attr : attributes) {
                    if (attr.element.equals(name)) {
                        attr.values.add(reader.getAttributeValue(null, attr.name));
                    }
                }
                if (rootOnly) {
                    proceed = false;
                }
            }
            return true;
        }

        public boolean proceed() {
            return proceed;
        }

    }

    public XMLInputFactory getInputFactory() {
        return inputFactory;
    }

    private boolean isReparingNamespaces() {
        if (outputFactory == null) {
            return Boolean.TRUE;
        }
        return outputFactory.getProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES) == Boolean.TRUE;
    }

    public XMLOutputFactory getOutputFactory() {
        return outputFactory;
    }

    public String writeAttribute(XMLStreamWriter writer, QName name, String value) throws XMLStreamException {
        return writeAttribute(writer, name.getPrefix(), name.getLocalPart(), name.getNamespaceURI(), value);
    }

    public String writeAttribute(XMLStreamWriter writer,
                                 String prefix,
                                 String localName,
                                 String namespaceURI,
                                 String value) throws XMLStreamException {
        if (value == null) {
            return null;
        }
        XMLStreamSerializer serializer = new XMLStreamSerializer(isReparingNamespaces());
        return serializer.writeAttribute(writer, prefix, localName, namespaceURI, value);
    }

    public void writeStartElement(XMLStreamWriter writer, QName name) throws XMLStreamException {
        writeStartElement(writer, name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
    }

    public void writeStartElement(XMLStreamWriter writer, String prefix, String localName, String namespaceURI)
        throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer(isReparingNamespaces());
        serializer.writeStartElement(writer, prefix, localName, namespaceURI);
    }

    public String writeNamespace(XMLStreamWriter writer, String prefix, String namespaceURI) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer(isReparingNamespaces());
        return serializer.writeNamespace(writer, prefix, namespaceURI);
    }

}
