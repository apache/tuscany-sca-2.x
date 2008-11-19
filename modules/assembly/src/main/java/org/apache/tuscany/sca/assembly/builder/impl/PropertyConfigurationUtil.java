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
package org.apache.tuscany.sca.assembly.builder.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Utility class to deal with processing of component properties that are taking values from the parent 
 * composite's properties or an external file.
 *
 * @version $Rev$ $Date$
 */
abstract class PropertyConfigurationUtil {
    
    private static Document evaluate(Document node, XPathExpression expression, DocumentBuilderFactory documentBuilderFactory)
        throws XPathExpressionException, ParserConfigurationException {

        Node value = node.getDocumentElement();
        Node result = (Node)expression.evaluate(value, XPathConstants.NODE);
        if (result == null) {
            return null;
        }

        // TODO: How to wrap the result into a Document?
        Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        if (result instanceof Document) {
            return (Document)result;
        } else {
            //Element root = document.createElementNS(null, "value");
            //document.appendChild(root);
            document.appendChild(document.importNode(result, true));
            return document;
        }
    }
    
    private static Document loadFromFile(String file, TransformerFactory transformerFactory) throws MalformedURLException, IOException,
        TransformerException, ParserConfigurationException {
        URI uri = URI.create(file);
        // URI resolution for relative URIs is done when the composite is resolved.
        URL url = uri.toURL();
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        InputStream is = null;
        try {
            is = connection.getInputStream();
    
            Source streamSource = new SAXSource(new InputSource(is));
            DOMResult result = new DOMResult();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(streamSource, result);
            
            Document document = (Document)result.getNode();
            
            // TUSCANY-2377, Add a fake value element so it's consistent with
            // the DOM tree loaded from inside SCDL
            Element root = document.createElementNS(null, "value");
            root.appendChild(document.getDocumentElement());
            document.appendChild(root);
            return document;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    static void sourceComponentProperties(Map<String, Property> compositeProperties,
                                                 Component componentDefinition,
                                                 DocumentBuilderFactory documentBuilderFactory,
                                                 TransformerFactory transformerFactory) throws CompositeBuilderException,
                                                                               ParserConfigurationException,
                                                                               XPathExpressionException,
                                                                               TransformerException,
                                                                               IOException {

        List<ComponentProperty> componentProperties = componentDefinition.getProperties();
        for (ComponentProperty aProperty : componentProperties) {
            String source = aProperty.getSource();
            String file = aProperty.getFile();
            if (source != null) {
                // $<name>/...
                int index = source.indexOf('/');
                if (index == -1) {
                    // Tolerating $prop
                    source = source + "/";
                    index = source.length() - 1;
                }
                if (source.charAt(0) == '$') {
                    String name = source.substring(1, index);
                    Property compositeProp = compositeProperties.get(name);
                    if (compositeProp == null) {
                        throw new CompositeBuilderException("The 'source' cannot be resolved to a composite property: " + source);
                    }

                    Document compositePropDefValues = (Document)compositeProp.getValue();

                    // FIXME: How to deal with namespaces?
                    Document node = evaluate(compositePropDefValues, aProperty.getSourceXPathExpression(), documentBuilderFactory);

                    if (node != null) {
                        aProperty.setValue(node);
                    }
                } else {
                    throw new CompositeBuilderException("The 'source' has an invalid value: " + source);
                }
            } else if (file != null) {
                aProperty.setValue(loadFromFile(aProperty.getFile(), transformerFactory));

            }
        }
    }
    
    private static class DOMNamespaceContext implements NamespaceContext {
        private Node node;

        /**
         * @param node
         */
        public DOMNamespaceContext(Node node) {
            super();
            this.node = node;
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix is null");
            } else if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
                return XMLConstants.XML_NS_URI;
            } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
                return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            }
            String ns = node.lookupNamespaceURI(prefix);
            return ns == null ? XMLConstants.NULL_NS_URI : ns;
        }

        public String getPrefix(String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException("Namespace URI is null");
            } else if (XMLConstants.XML_NS_URI.equals(namespaceURI)) {
                return XMLConstants.XML_NS_PREFIX;
            } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
                return XMLConstants.XMLNS_ATTRIBUTE;
            }
            return node.lookupPrefix(namespaceURI);
        }

        public Iterator<?> getPrefixes(String namespaceURI) {
            // Not implemented
            if (namespaceURI == null) {
                throw new IllegalArgumentException("Namespace URI is null");
            } else if (XMLConstants.XML_NS_URI.equals(namespaceURI)) {
                return Arrays.asList(XMLConstants.XML_NS_PREFIX).iterator();
            } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
                return Arrays.asList(XMLConstants.XMLNS_ATTRIBUTE).iterator();
            }
            String prefix = getPrefix(namespaceURI);
            if (prefix == null) {
                return Collections.emptyList().iterator();
            }
            return Arrays.asList(prefix).iterator();
        }

    }
    
}
