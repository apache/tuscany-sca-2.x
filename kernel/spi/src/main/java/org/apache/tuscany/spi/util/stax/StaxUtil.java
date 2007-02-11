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
package org.apache.tuscany.spi.util.stax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Multiplicity;

/**
 * Utility for stax operations.
 *
 * @version $Revision$ $Date$
 */
public abstract class StaxUtil {

    /**
     * XML input factory.
     */
    private static final XMLInputFactory XML_FACTORY =
        XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", StaxUtil.class.getClassLoader());

    private static final Map<String, Multiplicity> MULTIPLICITY = new HashMap<String, Multiplicity>(4);

    static {
        MULTIPLICITY.put("0..1", Multiplicity.ZERO_ONE);
        MULTIPLICITY.put("1..1", Multiplicity.ONE_ONE);
        MULTIPLICITY.put("0..n", Multiplicity.ZERO_N);
        MULTIPLICITY.put("1..n", Multiplicity.ONE_N);
    }

    private StaxUtil() {
    }

    /**
     * Convert a "multiplicity" attribute to the equivalent enum value.
     *
     * @param multiplicity the attribute to convert
     * @param def          the default value
     * @return the enum equivalent
     */
    public static Multiplicity multiplicity(String multiplicity, Multiplicity def) {
        return multiplicity == null ? def : MULTIPLICITY.get(multiplicity);
    }

    /**
     * Convert a "scope" attribute to the equivalent enum value. Returns CONVERSATIONAL if the value equals (ignoring
     * case) "conversational", otherwise returns NONCONVERSATIONAL.
     *
     * @param scope the attribute to convert
     * @return the enum equivalent
     */
    public static InteractionScope interactionScope(String scope) {
        if ("conversational".equalsIgnoreCase(scope)) {
            return InteractionScope.CONVERSATIONAL;
        } else {
            return InteractionScope.NONCONVERSATIONAL;
        }
    }

    public static Document createPropertyValue(XMLStreamReader reader, QName type, DocumentBuilder builder)
        throws XMLStreamException {
        Document doc = builder.newDocument();

        // root element has no namespace and local name "value"
        Element root = doc.createElementNS(null, "value");
        if (type != null) {
            Attr xsi = doc.createAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi");
            xsi.setValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            root.setAttributeNodeNS(xsi);

            String prefix = type.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                prefix = "ns";
            }
            Attr typeXmlns = doc.createAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + prefix);
            typeXmlns.setValue(type.getNamespaceURI());
            root.setAttributeNodeNS(typeXmlns);

            Attr xsiType = doc.createAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:type");
            xsiType.setValue(prefix + ":" + type.getLocalPart());
            root.setAttributeNodeNS(xsiType);
        }
        doc.appendChild(root);

        loadPropertyValue(reader, root);
        return doc;
    }

    /**
     * Load a property value specification from an StAX stream into a DOM Document. Only elements, text and attributes
     * are processed; all comments and other whitespace are ignored.
     *
     * @param reader the stream to read from
     * @param root   the DOM node to load
     */
    public static void loadPropertyValue(XMLStreamReader reader, Node root) throws XMLStreamException {
        Document document = root.getOwnerDocument();
        Node current = root;
        while (true) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    Element child = document.createElementNS(name.getNamespaceURI(), name.getLocalPart());

                    // add the attributes for this element
                    int count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String ns = reader.getAttributeNamespace(i);
                        String localPart = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        child.setAttributeNS(ns, localPart, value);
                    }

                    // push the new element and make it the current one
                    current.appendChild(child);
                    current = child;
                    break;
                case XMLStreamConstants.CDATA:
                    current.appendChild(document.createCDATASection(reader.getText()));
                    break;
                case XMLStreamConstants.CHARACTERS:
                    current.appendChild(document.createTextNode(reader.getText()));
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    // if we are back at the root then we are done
                    if (current == root) {
                        return;
                    }

                    // pop the element off the stack
                    current = current.getParentNode();
            }
        }
    }

    /**
     * Serializes the infoset in the stream reader.
     *
     * @param reader Stream reader.
     * @return Serialized XML.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static String serialize(XMLStreamReader reader) throws XMLStreamException {

        try {

            StringBuffer xml = new StringBuffer();

            int event = reader.getEventType();
            while (true) {

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        onStartElement(reader, xml);
                        onNsMappings(reader, xml);
                        onAttributes(reader, xml);
                        xml.append(">");
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (reader.isWhiteSpace()) {
                            break;
                        }
                        xml.append(reader.getText());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        onEndElement(reader, xml);
                        break;
                }

                if (!reader.hasNext()) {
                    break;
                }
                event = reader.next();

            }
            return xml.toString();

        } finally {
            reader.close();
        }

    }

    /**
     * Creates a stream reader to the serialized XML.
     *
     * @param xml Serialized XML to which reader is to be created.
     * @return XML stream reader instance.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static XMLStreamReader createReader(String xml) throws XMLStreamException {

        InputStream in = new ByteArrayInputStream(xml.getBytes());
        return XML_FACTORY.createXMLStreamReader(in);

    }

    /**
     * Creates a stream reader to the serialized XML.
     *
     * @param xml XML stream to which reader is to be created.
     * @return XML stream reader instance.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static XMLStreamReader createReader(InputStream xml) throws XMLStreamException {

        return XML_FACTORY.createXMLStreamReader(xml);

    }

    /**
     * Returns the qualified name of the document element.
     *
     * @param xml Serialized xml that needs to be checked.
     * @return Qualified name of the document element.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static QName getDocumentElementQName(String xml) throws XMLStreamException {

        XMLStreamReader reader = null;
        try {
            reader = createReader(xml);
            reader.next();
            return reader.getName();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    /*
     * Renders end element markup.
     */
    private static void onEndElement(XMLStreamReader reader, StringBuffer xml) {
        String name = getName(reader);
        xml.append("</");
        xml.append(name);
        xml.append(">");
    }

    /*
     * Gets the fully-qualified name of the element.
     */
    private static String getName(XMLStreamReader reader) {
        QName qname = reader.getName();
        String namePrefix = qname.getPrefix();
        String localPart = qname.getLocalPart();
        return namePrefix == null || "".equals(namePrefix) ? localPart : namePrefix + ":" + localPart;
    }

    /*
     * Render the attributes.
     */
    private static void onAttributes(XMLStreamReader reader, StringBuffer xml) {
        for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
            xml.append(" ");
            xml.append(reader.getAttributeLocalName(i));
            xml.append("=");
            xml.append("'");
            xml.append(reader.getAttributeValue(i));
            xml.append("'");
        }
    }

    /*
     * Renedr namespace mappings.
     */
    private static void onNsMappings(XMLStreamReader reader, StringBuffer xml) {
        for (int i = 0, n = reader.getNamespaceCount(); i < n; ++i) {
            String prefix = reader.getNamespacePrefix(i);
            prefix = prefix == null ? prefix = "xmlns" : "xmlns:" + prefix;
            xml.append(" ");
            xml.append(prefix);
            xml.append("=");
            xml.append("'");
            xml.append(reader.getNamespaceURI(i));
            xml.append("'");
        }
    }

    /*
     * Render start element.
     */
    private static void onStartElement(XMLStreamReader reader, StringBuffer xml) {
        xml.append("<");
        String name = getName(reader);
        xml.append(name);
    }

}
