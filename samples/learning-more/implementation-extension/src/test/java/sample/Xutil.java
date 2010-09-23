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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.w3c.dom.NodeList;

/**
 * Just for fun, a little bit of magic code and utility functions to help work with XML DOM.
 */
class Xutil {
    interface NodeBuilder {
        Node build(Document doc);
    }

    /**
     * Convert a name and a list of children to a document element.
     */
    static Element xdom(String ns, String name, final NodeBuilder... nodes) {
        return (Element)elem(ns, name, nodes).build(db.newDocument());
    }

    /**
     * Convert a name and children to an element.
     */
    static NodeBuilder elem(final String uri, final String n, final NodeBuilder... nodes) {
        return new NodeBuilder() {
            public Node build(Document doc) {
                final Element e = doc.createElementNS(uri, n);
                for(final NodeBuilder n: nodes)
                    e.appendChild(n.build(doc));
                return e;
            }
        };
    }

    static NodeBuilder elem(final String n, final NodeBuilder... nodes) {
        return elem(null, n, nodes);
    }

    /**
     * Convert a string to a text element.
     */
    static NodeBuilder text(final String t) {
        return new NodeBuilder() {
            public Node build(final Document doc) {
                return doc.createTextNode(t);
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

    /**
     * A pure Java FP-style alternative to xpath for DOM.
     */
    interface Mapper<T> {
        T map(final Element e);
    }
    
    static Mapper<Element> identity = new Mapper<Element>() {
        public Element map(Element e) {
            return e;
        };
    };
    
    interface Reducer<T> {
        T reduce(final T accum, final Element e);
    }
    
    static Reducer<String> print = new Reducer<String>() {
        public String reduce(String accum, Element e) {
            return accum + e.getTextContent();
        }
    };

    /**
     * Apply a mapper to a list of elements.
     */
    static <T> List<T> xmap(final Mapper<T> f, final Iterable<Element> l) {
        final List<T> v = new ArrayList<T>();
        for(Element e: l)
            v.add(f.map(e));
        return v;
    }

    /**
     * Apply a filter to a list of elements.
     */
    static List<Element> xfilter(final Mapper<Boolean> f, final Iterable<Element> l) {
        final List<Element> v = new ArrayList<Element>();
        for(Element e: l)
            if(f.map(e))
                v.add(e);
        return v;
    }

    /**
     * Perform a reduction over a list of elements.
     */
    static <T> T xreduce(final Reducer<T> f, final T initial, final Iterable<Element> l) {
        T accum = initial;
        for(Element e: l)
            accum = f.reduce(accum, e);
        return accum;
    }

    /**
     * Return a filter that selects elements by name.
     */
    static Mapper<Boolean> select(final String name) {
        return new Mapper<Boolean>() {
            public Boolean map(Element e) {
                return name.equals(e.getLocalName());
            }
        };
    }

    /**
     * Return the child elements of a node.
     */
    static Iterable<Element> elems(final Node parent) {
        final List<Element> l = new ArrayList<Element>();
        for (Node n: children(parent))
            if (n instanceof Element)
                l.add((Element)n);
        return l;
    }

    /**
     * An iterable over the children of a node.
     */
    private static Iterable<Node> children(Node parent) {
        final NodeList l = parent.getChildNodes();
        final int n = l.getLength();
        return new Iterable<Node>() {
            public Iterator<Node> iterator() {
                return new Iterator<Node>() {
                    int i = 0;
                    public boolean hasNext() {
                        return i < n;
                    }
                    public Node next() {
                        return l.item(i++);
                    }
                    public void remove() {
                    }
                };
            }  
        };
    }
}
