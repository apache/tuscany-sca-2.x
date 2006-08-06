/**
 *
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.loader;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Multiplicity;

/**
 * Utility classes to support StAX-based loaders
 *
 * @version $Rev$ $Date$
 */
public final class StAXUtil {
    private static final Map<String, Multiplicity> MULTIPLICITY = new HashMap<String, Multiplicity>(4);

    static {
        MULTIPLICITY.put("0..1", Multiplicity.ZERO_ONE);
        MULTIPLICITY.put("1..1", Multiplicity.ONE_ONE);
        MULTIPLICITY.put("0..n", Multiplicity.ZERO_N);
        MULTIPLICITY.put("1..n", Multiplicity.ONE_N);
    }

    private StAXUtil() {
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

    public static Document createPropertyValue(XMLStreamReader reader,
                                               QName type,
                                               DocumentBuilder builder) throws XMLStreamException {
        Document doc = builder.newDocument();

        // root element has no namespace and local name "value"
        Element root = doc.createElementNS(null, "value");
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
}
