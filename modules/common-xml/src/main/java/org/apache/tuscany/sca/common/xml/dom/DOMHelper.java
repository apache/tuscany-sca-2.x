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
package org.apache.tuscany.sca.common.xml.dom;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import org.apache.tuscany.sca.common.xml.dom.impl.SAX2DOMAdapter;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Helper for DOM
 *
 * @version $Rev$ $Date$
 */
public class DOMHelper {
    private DocumentBuilderFactory documentBuilderFactory;
    private TransformerFactory transformerFactory;

    public DOMHelper(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        documentBuilderFactory = factories.getFactory(DocumentBuilderFactory.class);
        documentBuilderFactory.setNamespaceAware(true);
        transformerFactory = factories.getFactory(TransformerFactory.class);
    }

    /**
     * @param documentBuilderFactory
     * @param transformerFactory
     */
    public DOMHelper(DocumentBuilderFactory documentBuilderFactory, TransformerFactory transformerFactory) {
        super();
        this.documentBuilderFactory = documentBuilderFactory;
        this.transformerFactory = transformerFactory;
    }

    public Document newDocument() {
        return newDocumentBuilder().newDocument();

    }

    public DocumentBuilder newDocumentBuilder() {
        try {
            return documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Document load(String xmlString) throws IOException, SAXException {
        DocumentBuilder builder = newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlString));
        return builder.parse(is);
    }

    public NodeContentHandler createContentHandler(Node root) {
        if (root == null) {
            root = newDocument();
        }
        return new SAX2DOMAdapter(root);
    }

    public String saveAsString(Node node) {
        Transformer transformer = newTransformer();
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        try {
            transformer.transform(new DOMSource(node), result);
        } catch (TransformerException e) {
            throw new IllegalArgumentException(e);
        }
        return result.getWriter().toString();
    }

    private Transformer newTransformer() {
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
        return transformer;
    }

    public void saveAsSAX(Node node, ContentHandler contentHandler) {
        Transformer transformer = newTransformer();
        SAXResult result = new SAXResult(contentHandler);
        try {
            transformer.transform(new DOMSource(node), result);
        } catch (TransformerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static QName getQName(Node node) {
        String ns = node.getNamespaceURI();
        String prefix = node.getPrefix();
        String localName = node.getLocalName();
        if (localName == null) {
            localName = node.getNodeName();
        }
        if (ns == null) {
            ns = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        return new QName(ns, localName, prefix);
    }

    public static Element createElement(Document document, QName name) {
        String prefix = name.getPrefix();
        String qname =
            (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name.getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

    /**
     * Wrap an element as a DOM document
     * @param node
     * @return
     */
    public static Document promote(Node node) {
        if (node instanceof Document) {
            return (Document)node;
        }
        Element element = (Element)node;
        Document doc = element.getOwnerDocument();
        if (doc.getDocumentElement() == element) {
            return doc;
        }
        doc = (Document)element.getOwnerDocument().cloneNode(false);
        Element schema = (Element)doc.importNode(element, true);
        doc.appendChild(schema);
        Node parent = element.getParentNode();
        while (parent instanceof Element) {
            Element root = (Element)parent;
            NamedNodeMap nodeMap = root.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); i++) {
                Attr attr = (Attr)nodeMap.item(i);
                String name = attr.getName();
                if ("xmlns".equals(name) || name.startsWith("xmlns:")) {
                    if (schema.getAttributeNode(name) == null) {
                        schema.setAttributeNodeNS((Attr)doc.importNode(attr, true));
                    }
                }
            }
            parent = parent.getParentNode();
        }
        return doc;
    }

    public static interface NodeContentHandler extends ContentHandler, LexicalHandler {
        Node getNode();
    }

}
