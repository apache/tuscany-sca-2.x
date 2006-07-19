/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.databinding.jaxb;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.w3c.dom.Node;

public class JAXBTestCase extends TestCase {
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

    private String contextPath = "com.example.ipo.jaxb";

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTransform() throws Exception {
        JAXBContext context = JAXBContext.newInstance(contextPath);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object object1 = unmarshaller.unmarshal(new StringReader(IPO_XML));

        JAXB2Node t1 = new JAXB2Node(contextPath);
        Node node = t1.transform(object1, null);

        Assert.assertNotNull(node);

        Node2JAXB t2 = new Node2JAXB(contextPath);
        Object object2 = t2.transform(node, null);
        Assert.assertNotNull(object2);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
