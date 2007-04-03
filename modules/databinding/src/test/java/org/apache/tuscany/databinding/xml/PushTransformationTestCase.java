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
package org.apache.tuscany.databinding.xml;

import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.impl.PipedTransformer;
import org.apache.tuscany.databinding.xml.Node2String;
import org.apache.tuscany.databinding.xml.SAX2DOMPipe;
import org.apache.tuscany.databinding.xml.String2XMLStreamReader;
import org.apache.tuscany.databinding.xml.XMLStreamReader2SAX;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class PushTransformationTestCase extends TestCase {
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

    public void testTransformation() {
        String2XMLStreamReader t1 = new String2XMLStreamReader();
        XMLStreamReader reader = t1.transform(IPO_XML, null);
        XMLStreamReader2SAX t2 = new XMLStreamReader2SAX();
        PipedTransformer<XMLStreamReader, ContentHandler, Node> t3 =
            new PipedTransformer<XMLStreamReader, ContentHandler, Node>(t2, new SAX2DOMPipe());
        Node node = t3.transform(reader, null);
        Assert.assertNotNull(node);
        Node2String t4 = new Node2String();
        String xml = t4.transform(node, null);
        Assert.assertTrue(xml != null && xml.indexOf("<shipDate>1999-12-05</shipDate>") != -1);
    }

}
