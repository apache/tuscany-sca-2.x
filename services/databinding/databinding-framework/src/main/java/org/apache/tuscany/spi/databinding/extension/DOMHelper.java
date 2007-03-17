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
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    static {
        FACTORY.setNamespaceAware(true);
    }

    private DOMHelper() {
    }

    public static Document newDocument() throws ParserConfigurationException {
        return newDocumentBuilder().newDocument();
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
        String qname =
            (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name
                .getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

}
