/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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

package org.apache.tuscany.spi.databinding.extension;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Helper for DOM
 */
public final class DOMHelper {
    private final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static {
        factory.setNamespaceAware(true);
    }

    private DOMHelper() {
    }

    public static Document newDocument() throws ParserConfigurationException {
        DocumentBuilder builder = newDocumentBuilder();
        Document document = builder.newDocument();
        return document;
    }

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder;
    }

    public static QName getQName(Node node) {
        String ns = node.getNamespaceURI();
        if (ns == null) {
            ns = "";
        }
        // node.getLocalName() will return null if it is created using DOM Level 1 method 
        // such as createElement()
        QName name = new QName(ns, node.getNodeName());
        return name;
    }
    
    public static Element createElement(Document document, QName name) {
        String prefix = name.getPrefix();
        String qname =
                (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name.getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

}
