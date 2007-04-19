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
package org.apache.tuscany.assembly.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.Property;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Util class to deal with processing of component properties that are take values from the parent 
 * composite's properties or thro an external file
 */
public class PropertyUtil {
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
    private static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    
    public static Document evaluate(NamespaceContext nsContext, Node node, String xPathExpression)
        throws XPathExpressionException, ParserConfigurationException {
        XPath path = XPATH_FACTORY.newXPath();
        
        if (nsContext != null) {
            path.setNamespaceContext(nsContext);
        } else {
            path.setNamespaceContext(new DOMNamespeceContext(node));
        }
        
        XPathExpression expression = path.compile(xPathExpression);
        Node result = (Node)expression.evaluate(node, XPathConstants.NODE);
        if (result == null) {
            return null;
        }

        // TODO: How to wrap the result into a Document?
        Document document = DOC_BUILDER_FACTORY.newDocumentBuilder().newDocument();
        if (result instanceof Document) {
            return (Document)result;
        } else {
            //Element root = document.createElementNS(null, "value");
            //document.appendChild(root);
            document.appendChild(document.importNode(result, true));
            return document;
        }
    }
    
    public static Document loadFromFile(String file) throws MalformedURLException, IOException,
        TransformerException, ParserConfigurationException {
        URI uri = URI.create(file);
        URL url = null;
        if (!uri.isAbsolute()) {
            url = Thread.currentThread().getContextClassLoader().getResource(file);
        } else {
            url = uri.toURL();
        }
        InputStream is = url.openStream();

        Source streamSource = new SAXSource(new InputSource(is));
        DOMResult result = new DOMResult();
        javax.xml.transform.Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.transform(streamSource, result);
        is.close();
        
        return (Document)result.getNode();
    }
    
    public static void processProperties(Composite composite, Component componentDefinition) throws InvalidValueException,
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
                    Property compositeProp = getPropertyByName(composite.getProperties(), name);
                    if (compositeProp == null) {
                        InvalidValueException ex =
                            new InvalidValueException("The 'source' cannot be resolved to a composite property - " + source);
                        throw ex;
                    }

                    boolean prependValue = false;
                    DocumentBuilder builder =
                        DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document compositePropDefValues = (Document)compositeProp.getValue();

                    // Adding /value because the document root is "value"
                    String path = source.substring(index);
                    String xpath = null;

                    if ("/".equals(path)) {
                        // trailing / is not legal for xpath
                        xpath = "/value";
                    } else {
                        xpath = "/value" + path;
                    }

                    // FIXME: How to deal with namespaces?
                    Document node = evaluate(null, compositePropDefValues, xpath);

                    if (node != null) {
                        aProperty.setValue(node);
                    }
                } else {
                    InvalidValueException ex =
                        new InvalidValueException("The 'source' has an invalid value - " + source);
                    throw ex;
                }
            } else if (file != null) {
                aProperty.setValue(loadFromFile(aProperty.getFile()));
                
            }
        }
    }
    
    public static void sourceComponentProperties(Map<String, Property> compositeProperties,
                                                 Component componentDefinition) throws InvalidValueException,
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
                        InvalidValueException ex =
                            new InvalidValueException(
                                                      "The 'source' cannot be resolved to a composite property - " + source);
                        throw ex;
                    }

                    boolean prependValue = false;
                    DocumentBuilder builder =
                        DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document compositePropDefValues = (Document)compositeProp.getValue();

                    // Adding /value because the document root is "value"
                    String path = source.substring(index);
                    String xpath = null;

                    if ("/".equals(path)) {
                        // trailing / is not legal for xpath
                        xpath = "/value";
                    } else {
                        xpath = "/value" + path;
                    }

                    // FIXME: How to deal with namespaces?
                    Document node = evaluate(null, compositePropDefValues, xpath);

                    if (node != null) {
                        aProperty.setValue(node);
                    }
                } else {
                    InvalidValueException ex =
                        new InvalidValueException("The 'source' has an invalid value - " + source);
                    throw ex;
                }
            } else if (file != null) {
                aProperty.setValue(loadFromFile(aProperty.getFile()));

            }
        }
    }
    
    private static Property getPropertyByName(List<Property> properties, String propertyName) {
        for (Property property : properties) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        
        return null;
    }

    private static class DOMNamespeceContext implements NamespaceContext {
        private Node node;

        /**
         * @param node
         */
        public DOMNamespeceContext(Node node) {
            super();
            this.node = node;
        }

        public String getNamespaceURI(String prefix) {
            return node.lookupNamespaceURI(prefix);
        }

        public String getPrefix(String namespaceURI) {
            return node.lookupPrefix(namespaceURI);
        }

        public Iterator<?> getPrefixes(String namespaceURI) {
            return null;
        }

    }
    
    public static void printNode(Node node)  {
        try {
            javax.xml.transform.Transformer transformer =
                TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(sw));
            
            System.out.println(sw.toString());
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        
    }

}
