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

package sample;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A little bit of magic code and utility functions to help work with DOM.
 */
class Xutil {
    static class NodeBuilder {
        String ns;
        String name;
        NodeBuilder[] children;
        String text;
    }

    /**
     * Convert a name and a list of children to a document element.
     */
    static Element dom(String ns, String name, final NodeBuilder... nodes) {
        return (Element)node(elem(ns, name, nodes), db.newDocument());
    }

    /**
     * Convert a name and children to an element.
     */
    static NodeBuilder elem(final String uri, final String n, final NodeBuilder... nodes) {
        return new NodeBuilder() {
            {
                this.ns = uri;
                this.name = n;
                this.children = nodes;
            }
        };
    }

    static NodeBuilder elem(final String n, final NodeBuilder... nodes) {
        return new NodeBuilder() {
            {
                this.name = n;
                this.children = nodes;
            }
        };
    }

    /**
     * Convert a string to a text element.
     */
    static NodeBuilder text(final String t) {
        return new NodeBuilder() {
            {
                this.text = t;
            }
        };
    }

    private final static DocumentBuilder db = db();

    private static DocumentBuilder db() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch(ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Element link(final Element e, final Document doc, final NodeBuilder... nodes) {
        for(final NodeBuilder c: nodes)
            e.appendChild(node(c, doc));
        return e;
    }

    private static Node node(NodeBuilder node, Document doc) {
        if(node.text != null)
            return doc.createTextNode(node.text);
        return link(doc.createElementNS(node.ns, node.name), doc, node.children);
    }

    /**
     * Convert an element to XML.
     */
    static TransformerFactory trf = TransformerFactory.newInstance();

    static String xml(final Node node) {
        try {
            final StreamResult r = new StreamResult(new StringWriter());
            trf.newTransformer().transform(new DOMSource(node), r);
            return r.getWriter().toString();
        } catch(TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Evaluate an xpath expression.
     */
    private static XPathFactory xpf = XPathFactory.newInstance();

    static String xpath(final String expr, final Node node) {
        final XPath xp = xpf.newXPath();
        try {
            return (String)xp.evaluate(expr, node, XPathConstants.STRING);
        } catch(XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

}
