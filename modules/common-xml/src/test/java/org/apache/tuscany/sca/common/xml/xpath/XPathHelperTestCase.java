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

import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class XPathHelperTestCase {
    private static XPathHelper xpathHelper;
    private static StAXHelper staxHelper;
    private static DOMHelper domHelper;

    private static String XML =
        "<r:root name=\"root\" xmlns:r=\"http://root\">" + "<c:child xmlns:c=\"http://child\" name=\"child\">"
            + "<c:child1 xmlns:c=\"http://child1\" name=\"child1\"/>"
            + "</c:child>"
            + "</r:root>";

    private static String XPATH =
        "<policySet attachTo=\"//c:child1[@name='child1']\" xmlns:c=\"http://child1\" xmlns=\"http://p\">" + "<child xmlns:c=\"http://c2\"/></policySet>";

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        xpathHelper = XPathHelper.getInstance(registry);
        domHelper = DOMHelper.getInstance(registry);
        staxHelper = StAXHelper.getInstance(registry);
    }

    @Test
    public void testNewXPath() {
        XPath path = xpathHelper.newXPath();
        Assert.assertNotNull(path);
    }

    @Test
    public void testCompile() throws Exception {
        XMLStreamReader reader = staxHelper.createXMLStreamReader(XPATH);
        reader.nextTag();
        String xpath = reader.getAttributeValue(null, "attachTo");
        XPathExpression expression = xpathHelper.compile(reader.getNamespaceContext(), xpath);
        // Advance the reader so that the namespace context changes its prefix/namespace mapping
        reader.nextTag();
        reader.close();

        Document doc = domHelper.load(XML);
        NodeList nodes = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
        Assert.assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        Assert.assertTrue(node instanceof Element);
        Assert.assertEquals(node.getNodeName(), "c:child1");
    }

    @Test
    public void testCompile2() throws Exception {
        XMLStreamReader reader = staxHelper.createXMLStreamReader(XPATH);
        reader.nextTag();
        String xpathExp = reader.getAttributeValue(null, "attachTo");
        XPath xpath = xpathHelper.newXPath();
        // Compile the expression without taking a snapshot of the namespace context
        XPathExpression expression = xpathHelper.compile(xpath, reader.getNamespaceContext(), xpathExp);
        // Advance the reader so that the namespace context changes its prefix/namespace mapping
        reader.nextTag();
        reader.close();

        Document doc = domHelper.load(XML);
        NodeList nodes = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
        Assert.assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        Assert.assertTrue(node instanceof Element);
        Assert.assertEquals(node.getNodeName(), "c:child1");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        xpathHelper = null;
    }

}
