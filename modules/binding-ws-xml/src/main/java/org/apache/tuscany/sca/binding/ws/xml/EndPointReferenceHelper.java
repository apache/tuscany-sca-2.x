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

package org.apache.tuscany.sca.binding.ws.xml;

import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper methods to read and write a wsa:endpointReference
 * TODO: almost direct copy of code for Assembly properties
 *       must be able to move to a common utility
 *
 * @version $Rev$ $Date$
 */
public class EndPointReferenceHelper {
    
    /**
     * Read a wsa:endpointReference into a DOM Element
     */
    public static Element readEndPointReference(XMLStreamReader reader) {
        try {

            return loadElement(reader);

        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Write a wsa:endpointReference from a DOM Element
     */
    public static void writeEndPointReference(Element element, XMLStreamWriter writer)  {
        try {

            saveElement(element, writer);

        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private static Element loadElement(XMLStreamReader reader) throws XMLStreamException, ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node root = document;
        Node current = root;
        while (true) {
            switch (reader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    Element child = createElement(document, name);

                    // push the new element and make it the current one
                    current.appendChild(child);
                    current = child;

                    int count = reader.getNamespaceCount();
                    for (int i = 0; i < count; i++) {
                        String prefix = reader.getNamespacePrefix(i);
                        String ns = reader.getNamespaceURI(i);
                        declareNamespace(child, prefix, ns);
                    }

                    if(!"".equals(name.getNamespaceURI()))
                    declareNamespace(child, name.getPrefix(), name.getNamespaceURI());

                    // add the attributes for this element
                    count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String ns = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        String qname = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        if (prefix != null && prefix.length() != 0) {
                            qname = prefix + ":" + qname;
                        }
                        child.setAttributeNS(ns, qname, value);
                        if (ns != null) {
                            declareNamespace(child, prefix, ns);
                        }
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
                    if ("EndpointReference".equals(reader.getName().getLocalPart())) {
                        return document.getDocumentElement();
                    }

                    // pop the element off the stack
                    current = current.getParentNode();
            }
            if ( reader.hasNext()) reader.next();
        }
    }

    private static Element createElement(Document document, QName name) {
        String prefix = name.getPrefix();
        String qname =
            (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name.getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

    private static void declareNamespace(Element element, String prefix, String ns) {
        if (ns == null) {
            ns = "";
        }
        if (prefix == null) {
            prefix = "";
        }
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
            org.w3c.dom.Attr attr = element.getOwnerDocument().createAttributeNS(XMLNS_ATTRIBUTE_NS_URI, qname);
            attr.setValue(ns);
            element.setAttributeNodeNS(attr);
        }
    }
    
    private static void saveElement(Element element, XMLStreamWriter writer) throws XMLStreamException{

        XMLStreamReader reader =
            XMLInputFactory.newInstance().createXMLStreamReader(new DOMSource(element));

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    writer.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());

                    int namespaces = reader.getNamespaceCount();
                    for (int i = 0; i < namespaces; i++) {
                        String prefix = reader.getNamespacePrefix(i);
                        String ns = reader.getNamespaceURI(i);
                        writer.writeNamespace(prefix, ns);
                    }

                    if (!"".equals(name.getNamespaceURI())) {
                        writer.writeNamespace(name.getPrefix(), name.getNamespaceURI());
                    }

                    // add the attributes for this element
                    namespaces = reader.getAttributeCount();
                    for (int i = 0; i < namespaces; i++) {
                        String ns = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        String qname = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);

                        writer.writeAttribute(prefix, ns, qname, value);
                    }

                    break;
                case XMLStreamConstants.CDATA:
                    writer.writeCData(reader.getText());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    writer.writeCharacters(reader.getText());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    writer.writeEndElement();
                    break;
            }
        }
    }
}
