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
package org.apache.tuscany.databinding.axiom;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;

public class OMElementTestCase extends TestCase {
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

    public final void testStringTransform() {
        String2OMElement t1 = new String2OMElement();
        OMElement element = t1.transform(IPO_XML, null);
        OMElement2String t2 = new OMElement2String();
        String xml = t2.transform(element, null);
        Assert.assertNotNull(xml);
        Assert.assertNotNull(xml.indexOf("<ipo:comment>") != -1);
    }

    public final void testStringTransform2() {
        String str =
            "<p0:firstName xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:p0=\"http://helloworld\">Robert</p0:firstName>";
        String2OMElement t1 = new String2OMElement();
        OMElement element = t1.transform(str, null);
        OMElement2String t2 = new OMElement2String();
        String xml = t2.transform(element, null);
        Assert.assertNotNull(xml);
        Assert.assertNotNull(xml.indexOf("<ipo:comment>") != -1);
    }

    public final void testStAXTransform() {
        String2OMElement t1 = new String2OMElement();
        OMElement element = t1.transform(IPO_XML, null);

        OMElement2XMLStreamReader t2 = new OMElement2XMLStreamReader();
        XMLStreamReader reader = t2.transform(element, null);

        XMLStreamReader2OMElement t3 = new XMLStreamReader2OMElement();
        OMElement element2 = t3.transform(reader, null);

        Assert.assertEquals(element2.getQName(), element.getQName());
        Assert.assertEquals(new QName("http://www.example.com/IPO", "purchaseOrder"), element2.getQName());
    }

    public final void testCopy() {
        String2OMElement t1 = new String2OMElement();
        OMElement element = t1.transform(IPO_XML, null);
        OMElement copy = (OMElement)new AxiomDataBinding().copy(element);
        assertNotSame(element, copy);
        assertEquals(new QName("http://www.example.com/IPO", "purchaseOrder"), copy.getQName());
    }

}
