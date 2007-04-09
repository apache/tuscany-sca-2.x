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

package org.apache.tuscany.core.property;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tuscany.databinding.xml.InputStream2Node;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The property factory backed by the DataBindingframework
 */
public final class PropertyHelper {

    private static final XPathFactory FACTORY = XPathFactory.newInstance();

    private PropertyHelper() {
    }

    public static Document evaluate(NamespaceContext nsContext, Node node, String xPathExpression)
        throws XPathExpressionException, ParserConfigurationException {
        XPath path = FACTORY.newXPath();
        if (nsContext != null) {
            path.setNamespaceContext(nsContext);
        } else {
            path.setNamespaceContext(new DOMNamespeceContext(node));
        }
        XPathExpression expression = path.compile(xPathExpression);
        Node result = (Node) expression.evaluate(node, XPathConstants.NODE);
        if (result == null) {
            return null;
        }

        // TODO: How to wrap the result into a Document?
        Document document = DOMHelper.newDocument();
        if (result instanceof Document) {
            return document;
        } else {
            document.appendChild(document.importNode(result, true));
            return document;
        }
    }

    public static Document loadFromFile(String file, DeploymentContext deploymentContext)
        throws LoaderException {
        try {
            URI uri = URI.create(file);
            URL url = null;
            if (!uri.isAbsolute()) {
                url = deploymentContext.getClassLoader().getResource(file);
            } else {
                url = uri.toURL();
            }
            InputStream is = url.openStream();
            try {
                InputStream2Node transformer = new InputStream2Node();
                return (Document) transformer.transform(is, null);
            } finally {
                is.close();
            }
        } catch (Exception e) {
            throw new LoaderException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void processProperties(CompositeComponentType<?, ?, Property<?>> parent,
                                         ComponentDefinition<? extends Implementation<?>> componentDefinition,
                                         DeploymentContext deploymentContext) throws LoaderException {
        Map<String, PropertyValue<?>> propertyValues = componentDefinition.getPropertyValues();

        for (PropertyValue propValue : propertyValues.values()) {
            Document node = propValue.getValue();
            String source = propValue.getSource();
            String file = propValue.getFile();
            if (source != null) {
                try {
                    // $<name>/...
                    int index = source.indexOf('/');
                    if (index == -1) {
                        // Tolerating $prop
                        source = source + "/";
                        index = source.length() - 1;
                    }
                    if (source.charAt(0) == '$') {
                        String name = source.substring(1, index);
                        Property<?> compositeProp = parent.getProperties().get(name);
                        if (compositeProp == null) {
                            throw new InvalidValueException("Source cannot be resolved to a composite property");
                        }
                        Document document = compositeProp.getDefaultValue();
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
                        node = evaluate(null, document, xpath);
                        if (node != null) {
                            propValue.setValue(node);
                        }
                        /*Property<?> prop =
                      (Property<?>)componentDefinition.getImplementation().getComponentType()
                          .getProperties().get(propValue.getName());
                  propValue
                      .setValueFactory(new SimplePropertyObjectFactory(prop, propValue.getValue()));*/
                    } else {
                        throw new InvalidValueException("Source has an invalid value");
                    }
                } catch (Exception e) {
                    throw new LoaderException(e);
                }
            } else if (file != null) {
                node = loadFromFile(propValue.getFile(), deploymentContext);
                propValue.setValue(node);
                Property<?> prop =
                    (Property<?>) componentDefinition.getImplementation().getComponentType().getProperties()
                        .get(propValue.getName());
                propValue.setValueFactory(new SimplePropertyObjectFactory(prop, propValue.getValue()));
            }
        }
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

        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }

    }

}
