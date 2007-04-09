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

import java.net.URL;

import javax.xml.namespace.NamespaceContext;

import junit.framework.TestCase;

import org.apache.tuscany.databinding.xml.String2Node;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.easymock.EasyMock;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 */
public class PropertyHelperTestCase extends TestCase {
    private static final String IPO_XML =
        "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
            + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + "  xmlns:ipo=\"http://www.example.com/IPO\""
            + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\""
            + "  orderDate=\"1999-12-01\">"
            + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">"
            + "    <name>Helen Zoe</name>"
            + "    <street>47 Eden Street</street>"
            + "    <city>Cambridge</city>"
            + "    <postcode>CB1 1JR</postcode>"
            + "  </shipTo>"
            + "  <billTo xsi:type=\"ipo:USAddress\">"
            + "    <name>Robert Smith</name>"
            + "    <street>8 Oak Avenue</street>"
            + "    <city>Old Town</city>"
            + "    <state>PA</state>"
            + "    <zip>95819</zip>"
            + "  </billTo>"
            + "  <items>"
            + "    <item partNum=\"833-AA\">"
            + "      <productName>Lapis necklace</productName>"
            + "      <quantity>1</quantity>"
            + "      <USPrice>99.95</USPrice>"
            + "      <ipo:comment>Want this for the holidays</ipo:comment>"
            + "      <shipDate>1999-12-05</shipDate>"
            + "    </item>"
            + "  </items>"
            + "</ipo:purchaseOrder>";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testXPath() throws Exception {
        String2Node t = new String2Node();
        Node node = t.transform(IPO_XML, null);

        Document doc = PropertyHelper.evaluate(null, node, "/ipo:purchaseOrder/billTo");
        assertNotNull(doc);

        NamespaceContext context = EasyMock.createMock(NamespaceContext.class);
        EasyMock.expect(context.getNamespaceURI("ipo")).andReturn("http://www.example.com/IPO").anyTimes();
        EasyMock.replay(context);
        doc = PropertyHelper.evaluate(context, node, "/ipo:purchaseOrder/items");
        assertNotNull(doc);
        doc = PropertyHelper.evaluate(context, node, "/ipo:purchaseOrder/billTo");
        assertNotNull(doc);
        doc = PropertyHelper.evaluate(context, node, "/");
        assertNotNull(doc);
        doc = PropertyHelper.evaluate(context, node, "/ipo:purchaseOrder/billTo1");
        assertNull(doc);
    }

    public void testFile() throws Exception {
        URL url = getClass().getResource("ipo.xml");
        Document doc = PropertyHelper.loadFromFile(url.toExternalForm(), null);
        assertNotNull(doc);

        DeploymentContext context = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(context.getClassLoader()).andReturn(getClass().getClassLoader());
        EasyMock.replay(context);
        doc = PropertyHelper.loadFromFile("org/apache/tuscany/core/property/ipo.xml", context);
        assertNotNull(doc);
    }

}
