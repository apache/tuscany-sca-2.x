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
package org.apache.tuscany.core.loader;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @version $Rev$ $Date$
 */
public final class PropertyUtils {

    private PropertyUtils() {
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
     * @throws javax.xml.stream.XMLStreamException
     *
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

}
