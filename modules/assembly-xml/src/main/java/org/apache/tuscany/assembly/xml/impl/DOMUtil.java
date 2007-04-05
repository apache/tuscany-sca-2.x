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

package org.apache.tuscany.assembly.xml.impl;

import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @version $Rev$ $Date$
 */
public final class DOMUtil {
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    static {
        FACTORY.setNamespaceAware(true);
    }

    private DOMUtil() {
    }

    public static Document newDocument() throws ContributionReadException {
        try {
            return newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new ContributionReadException(e);
        }
    }

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return FACTORY.newDocumentBuilder();
    }

    public static QName getQName(Node node) {
        String ns = node.getNamespaceURI();
        if (ns == null) {
            ns = "";
        }
        // node.getLocalName() will return null if it is created using DOM Level
        // 1 method
        // such as createElement()
        return new QName(ns, node.getNodeName());
    }

    public static Element createElement(Document document, QName name) {
        String prefix = name.getPrefix();
        String qname = (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name
            .getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

    public static void declareNamespace(Element element, String prefix, String ns) {
        String qname = null;
        if ("".equals(prefix)) {
            qname = "xmlns";
        } else {
            qname = "xmlns:" + prefix;
        }
        Node node = element;
        boolean declared = false;
        while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attrs = node.getAttributes();
            if (attrs == null) {
                break;
            }
            Node attr = attrs.getNamedItem(qname);
            if (attr != null) {
                declared = ns.equals(attr.getNodeValue());
                break;
            }
            node = node.getParentNode();
        }
        if (!declared) {
            Attr attr = element.getOwnerDocument().createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, qname);
            attr.setValue(ns);
            element.setAttributeNodeNS(attr);
        }
    }

    /**
     * Load a property value specification from an StAX stream into a DOM
     * Document. Only elements, text and attributes are processed; all comments
     * and other whitespace are ignored.
     * 
     * @param reader the stream to read from
     * @param root the DOM node to load
     * @throws javax.xml.stream.XMLStreamException
     */
    public static void loadDOM(XMLStreamReader reader, Node root) throws XMLStreamException {
        Document document = root.getOwnerDocument();
        Node current = root;
        while (true) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    Element child = createElement(document, name);

                    // push the new element and make it the current one
                    current.appendChild(child);
                    current = child;

                    declareNamespace(child, name.getPrefix(), name.getNamespaceURI());

                    int count = reader.getNamespaceCount();
                    for (int i = 0; i < count; i++) {
                        String prefix = reader.getNamespacePrefix(i);
                        String ns = reader.getNamespaceURI(i);
                        declareNamespace(child, prefix, ns);
                    }

                    // add the attributes for this element
                    count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String ns = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        String localPart = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        child.setAttributeNS(ns, localPart, value);
                        declareNamespace(child, prefix, ns);
                    }

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
}
