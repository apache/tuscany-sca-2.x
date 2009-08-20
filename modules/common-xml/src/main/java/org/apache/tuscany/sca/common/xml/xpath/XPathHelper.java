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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        path.setNamespaceContext(getNamespaceContext(expression, context));
        return path.compile(expression);
    }

    private NamespaceContext getNamespaceContext(String expression, NamespaceContext context) {
        NamespaceContextImpl nsContext = new NamespaceContextImpl(null);

        for (String prefix : getPrefixes(expression)) {
            String namespace = context.getNamespaceURI(prefix);
            if (namespace != null && !XMLConstants.NULL_NS_URI.equals(namespace)) {
                nsContext.register(prefix, namespace);
            }
        }
        return nsContext;
    }

    private Collection<String> getPrefixes(String expression) {
        List<String> prefixes = new ArrayList<String>();
        prefixes.add("");
        String[] segments = expression.split(":");
        for (int i = 0; i < segments.length - 1; i++) {
            String prefix = segments[i];
            if(prefix.length()<1) {
                continue;
            }
            int j = prefix.length() -1;
            for (; j >= 0; j--) {
                if (XMLCharHelper.isNCName(prefix.charAt(j))) {
                    continue;
                }
                j--;
                break;
            }
            if (j != (prefix.length() - 1) && XMLCharHelper.isNCNameStart(prefix.charAt(j + 1))) {
                prefixes.add(prefix.substring(j + 1));
            }
            break;

        }
        return prefixes;
    }

}
