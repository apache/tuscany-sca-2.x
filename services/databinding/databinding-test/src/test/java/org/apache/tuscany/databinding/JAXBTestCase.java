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
package org.apache.tuscany.databinding;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.jaxb.JAXB2Node;
import org.apache.tuscany.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.databinding.jaxb.XMLStreamReader2JAXB;
import org.apache.tuscany.databinding.xmlbeans.Node2XmlObject;
import org.apache.tuscany.databinding.xmlbeans.XmlObject2XMLStreamReader;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.model.DataType;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

public class JAXBTestCase extends TestCase {
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

    private String contextPath = "com.example.ipo.jaxb";

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTransform() throws Exception {
        // URL/Stream/Reader to XmlObject
        XmlObject object = XmlObject.Factory.parse(new StringReader(IPO_XML));

        // XmlObject to XMLStreamReader
        XmlObject2XMLStreamReader t1 = new XmlObject2XMLStreamReader();
        XMLStreamReader reader = t1.transform(object, null);

        DataType targetDataType = new DataType<Class>(Object.class, null);
        targetDataType.setMetadata(JAXBContextHelper.JAXB_CONTEXT_PATH, contextPath);

        TransformationContext tContext = createMock(TransformationContext.class);
        expect(tContext.getTargetDataType()).andReturn(targetDataType).anyTimes();
        replay(tContext);

        // XMLStreamReader to JAXB
        XMLStreamReader2JAXB t2 = new XMLStreamReader2JAXB();
        Object object2 = t2.transform(reader, tContext);

        DataType sourceDataType = new DataType<Class>(Object.class, null);
        sourceDataType.setMetadata(JAXBContextHelper.JAXB_CONTEXT_PATH, contextPath);

        TransformationContext tContext1 = createMock(TransformationContext.class);
        expect(tContext1.getSourceDataType()).andReturn(sourceDataType).anyTimes();
        replay(tContext1);

        JAXB2Node t3 = new JAXB2Node();
        Node node = t3.transform(object2, tContext1);

        Node2XmlObject t4 = new Node2XmlObject();
        XmlObject object3 = t4.transform(node, null);

        Assert.assertNotNull(object3);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
