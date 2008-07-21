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

package catalog.util;

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Rev$ $Date$
 */
public class XPathHelper {

    public static Node selectNode(String xpath, Node root, Map<String, String> namespaces)
        throws XPathExpressionException {
        XPath path = XPathFactory.newInstance().newXPath();
        NamespaceContextImpl context = new NamespaceContextImpl(null, namespaces);
        path.setNamespaceContext(context);
        XPathExpression exp = path.compile(xpath);
        return (Node)exp.evaluate(root, XPathConstants.NODE);
    }

    public static NodeList selectNodes(String xpath, Node root, Map<String, String> namespaces)
        throws XPathExpressionException {
        XPath path = XPathFactory.newInstance().newXPath();
        NamespaceContextImpl context = new NamespaceContextImpl(null, namespaces);
        path.setNamespaceContext(context);
        XPathExpression exp = path.compile(xpath);
        return (NodeList)exp.evaluate(root, XPathConstants.NODESET);
    }
}
