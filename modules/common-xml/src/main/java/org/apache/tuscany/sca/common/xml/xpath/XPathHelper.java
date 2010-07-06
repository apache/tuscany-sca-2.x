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

package org.apache.tuscany.sca.common.xml.xpath;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tuscany.sca.common.xml.stax.reader.NamespaceContextImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;

/**
 * Helper for XPath operations
 */
public class XPathHelper {
    private XPathFactory factory;

    /**
     * @param factory
     */
    public XPathHelper(XPathFactory factory) {
        super();
        this.factory = factory;
    }

    public XPathHelper(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.factory = factories.getFactory(XPathFactory.class);
    }

    public static XPathHelper getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilities.getUtility(XPathHelper.class);
    }

    public XPath newXPath() {
        return factory.newXPath();
    }

    public XPathExpression compile(NamespaceContext context, String expression) throws XPathExpressionException {
        XPath path = newXPath();
        context = getNamespaceContext(expression, context);
        return compile(path, context, expression);
    }

    public XPathExpression compile(XPath path, NamespaceContext context, String expression)
        throws XPathExpressionException {
        path.setNamespaceContext(context);
        return path.compile(expression);
    }

    /**
     * Take a snapshot of the given namespace context based on the prefixes found in the expression.
     * In StAX, the prefix/namespace mapping in the namespace context can change as the event moves  
     * @param expression
     * @param context
     * @return
     */
    public NamespaceContext getNamespaceContext(String expression, NamespaceContext context) {
        NamespaceContextImpl nsContext = new NamespaceContextImpl(null);

        boolean found = false;
        for (String prefix : getPrefixes(expression)) {
            String namespace = context.getNamespaceURI(prefix);
            if (namespace != null && !XMLConstants.NULL_NS_URI.equals(namespace)) {
                nsContext.register(prefix, namespace);
                if ( (namespace.equals("http://docs.oasis-open.org/ns/opencsa/sca/200912")) && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
                	found = true;
            }
        }
        
        if(!found) {
            nsContext.register("__sca", "http://docs.oasis-open.org/ns/opencsa/sca/200912");
        }
        return nsContext;
    }

    /**
     * Registers a prefix in an existing NamespaceContext
     * @param prefix
     * @param namespace
     * @param context
     */
    public void registerPrefix(String prefix, String namespace, NamespaceContext context) {
    	NamespaceContextImpl nsContext = (NamespaceContextImpl) context;
    	nsContext.register(prefix, namespace);
    }
    
    /**
     * Parse the XPath expression to collect all the prefixes for namespaces
     * @param expression
     * @return A collection of prefixes
     */
    private Collection<String> getPrefixes(String expression) {
        Collection<String> prefixes = new HashSet<String>();
        prefixes.add(XMLConstants.DEFAULT_NS_PREFIX);
        Pattern pattern = Pattern.compile("([^:]+):([^:]+)");
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String prefix = extractNCName(matcher.group(1), true);
            String local = extractNCName(matcher.group(2), false);
            if (prefix != null && local != null) {
                prefixes.add(prefix);
            }
        }
        return prefixes;
    }

    private String extractNCName(String str, boolean reverse) {
        if (str.length() < 1) {
            return null;
        }
        if (!reverse) {
            if (!XMLCharHelper.isNCNameStart(str.charAt(0))) {
                return null;
            }
            int i = 0, j = str.length();
            // Find the last non-NCName char
            for (; i < j; i++) {
                if (!XMLCharHelper.isNCName(str.charAt(i))) {
                    break;
                }
            }
            return str.substring(0, i);
        } else {
            int j = str.length() - 1;
            // Find the first non-NCName char
            for (; j >= 0; j--) {
                if (!XMLCharHelper.isNCName(str.charAt(j))) {
                    break;
                }
            }
            // j is before the first char of the prefix
            if (j != (str.length() - 1) && XMLCharHelper.isNCNameStart(str.charAt(j + 1))) {
                return str.substring(j + 1);
            } else {
                return null;
            }
        }
    }

}
