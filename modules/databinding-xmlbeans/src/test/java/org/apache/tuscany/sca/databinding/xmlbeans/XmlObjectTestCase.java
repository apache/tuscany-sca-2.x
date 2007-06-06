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
package org.apache.tuscany.sca.databinding.xmlbeans;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

import com.example.ipo.xmlbeans.PurchaseOrderDocument;

public class XmlObjectTestCase extends TestCase {
    private static final String IPO_XML = "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
    + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "  xmlns:ipo=\"http://www.example.com/IPO\""
    + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\"" + "  orderDate=\"1999-12-01\">"
    + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">" + "    <name>Helen Zoe</name>" + "    <street>47 Eden Street</street>"
    + "    <city>Cambridge</city>" + "    <postcode>CB1 1JR</postcode>" + "  </shipTo>" + "  <billTo xsi:type=\"ipo:USAddress\">"
    + "    <name>Robert Smith</name>" + "    <street>8 Oak Avenue</street>" + "    <city>Old Town</city>" + "    <state>PA</state>"
    + "    <zip>95819</zip>" + "  </billTo>" + "  <items>" + "    <item partNum=\"833-AA\">"
    + "      <productName>Lapis necklace</productName>" + "      <quantity>1</quantity>" + "      <USPrice>99.95</USPrice>"
    + "      <ipo:comment>Want this for the holidays</ipo:comment>" + "      <shipDate>1999-12-05</shipDate>" + "    </item>" + "  </items>"
    + "</ipo:purchaseOrder>";

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testXmlObject() throws Exception {
        // URL/Stream/Reader to XmlObject
        XmlObject object = XmlObject.Factory.parse(new StringReader(IPO_XML));

        // XmlObject to XMLStreamReader
        XmlObject2XMLStreamReader t1 = new XmlObject2XMLStreamReader();
        XMLStreamReader reader = t1.transform(object, null);

        // XMLStreamReader to XmlObject
        XMLStreamReader2XmlObject t2 = new XMLStreamReader2XmlObject();
        XmlObject object2 = t2.transform(reader, null);

        // XmlObject to Node
        XmlObject2Node t3 = new XmlObject2Node();
        Node node = t3.transform(object2, null);

        // Node to XmlObject
        Node2XmlObject t4 = new Node2XmlObject();
        XmlObject object3 = t4.transform(node, null);
        Assert.assertNotNull(object3);
    }
    
    public void testGeneratedXmlObject() throws Exception {
        // URL xmlFile = getClass().getClassLoader().getResource("ipo.xml");
        // URL/Stream/Reader to XmlObject
        PurchaseOrderDocument object = PurchaseOrderDocument.Factory.parse(new StringReader(IPO_XML));

        // XmlObject to XMLStreamReader
        XmlObject2XMLStreamReader t1 = new XmlObject2XMLStreamReader();
        XMLStreamReader reader = t1.transform(object, null);

        // XMLStreamReader to XmlObject
        XMLStreamReader2XmlObject t2 = new XMLStreamReader2XmlObject();
        PurchaseOrderDocument object2 = (PurchaseOrderDocument) t2.transform(reader, null);

        // XmlObject to Node
        XmlObject2Node t3 = new XmlObject2Node();
        Node node = t3.transform(object2, null);

        // Node to XmlObject
        Node2XmlObject t4 = new Node2XmlObject();
        PurchaseOrderDocument object3 = (PurchaseOrderDocument) t4.transform(node, null);
        Assert.assertNotNull(object3);
    }   

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
