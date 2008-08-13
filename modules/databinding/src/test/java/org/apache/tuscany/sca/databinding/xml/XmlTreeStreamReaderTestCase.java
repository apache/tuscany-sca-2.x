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

package org.apache.tuscany.sca.databinding.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class XmlTreeStreamReaderTestCase {
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

    private static final String XML_RESULT =
        "<?xml version='1.0' encoding='UTF-8'?>" + "<p1:e1 xmlns:p1=\"http://ns\">"
            + "<p2:e11 xmlns:p2=\"http://ns1\">MyText</p2:e11>"
            + "<p1:e12><p1:e121 /></p1:e12>"
            + "<ipo:purchaseOrder xmlns:ipo=\"http://www.example.com/IPO\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\" orderDate=\"1999-12-01\">  "
            + "<shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">    "
            + "<name>Helen Zoe</name>    <street>47 Eden Street</street>    "
            + "<city>Cambridge</city>    <postcode>CB1 1JR</postcode>  </shipTo>  "
            + "<billTo xsi:type=\"ipo:USAddress\">    <name>Robert Smith</name>    "
            + "<street>8 Oak Avenue</street>    <city>Old Town</city>    <state>PA</state>    "
            + "<zip>95819</zip>  </billTo>  <items>    <item partNum=\"833-AA\">      "
            + "<productName>Lapis necklace</productName>      <quantity>1</quantity>      "
            + "<USPrice>99.95</USPrice>      <ipo:comment>Want this for the holidays</ipo:comment>      "
            + "<shipDate>1999-12-05</shipDate>    </item>  </items></ipo:purchaseOrder></p1:e1>";
    private XmlNodeImpl root;

    @Before
    public void setUp() throws Exception {
        root = new XmlNodeImpl();
        root.name = new QName("http://ns", "e1", "p1");

        XmlNodeImpl e11 = new XmlNodeImpl();
        e11.name = new QName("http://ns1", "e11", "p2");

        XmlNodeImpl e12 = new XmlNodeImpl();
        e12.name = new QName("http://ns", "e12");

        root.children.add(e11);
        root.children.add(e12);

        XmlNodeImpl e121 = new XmlNodeImpl();
        e121.name = new QName("http://ns", "e121");
        e12.children.add(e121);

        XmlNodeImpl e111 = new XmlNodeImpl();
        e111.value = "MyText";
        e11.children.add(e111);

        XmlNodeImpl e13 = new XmlNodeImpl();
        e13.value = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(IPO_XML));
        root.children.add(e13);

    }

    @Test
    public void testIterator() {
        List<QName> elements = new ArrayList<QName>();
        XmlNodeIterator i = new XmlNodeIterator(root);
        for (; i.hasNext();) {
            XmlNode e = i.next();
            elements.add(e.getName());
        }
        // System.out.println(elements);
        QName[] names =
            {new QName("http://ns", "e1"), new QName("http://ns1", "e11"), null, null, new QName("http://ns1", "e11"),
             new QName("http://ns", "e12"), new QName("http://ns", "e121"), new QName("http://ns", "e121"),
             new QName("http://ns", "e12"), null, null, new QName("http://ns", "e1")};
        Assert.assertEquals(Arrays.asList(names), elements);
    }

    @Test
    public void testReader() throws Exception {
        XmlTreeStreamReaderImpl reader = new XmlTreeStreamReaderImpl(root);
        XMLStreamReader2String t = new XMLStreamReader2String();
        String xml = t.transform(reader, null);
        XMLAssert.assertXMLEqual(XML_RESULT, xml);
    }

    private static class XmlNodeImpl implements XmlNode {
        private List<XmlNode> children = new ArrayList<XmlNode>();
        private List<XmlNode> attrs = new ArrayList<XmlNode>();
        private Map<String, String> namespaces = new HashMap<String, String>();
        private QName name;
        private Object value = "123";

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlNode#attributes()
         */
        public List<XmlNode> attributes() {
            return attrs;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlNode#children()
         */
        public Iterator<XmlNode> children() {
            return children.iterator();
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getName()
         */
        public QName getName() {
            return name;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getValue()
         */
        public <T> T getValue() {
            return (T)value;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlNode#namespaces()
         */
        public Map<String, String> namespaces() {
            return namespaces;
        }

        @Override
        public String toString() {
            return String.valueOf(name);
        }

        public Type getType() {
            if (value instanceof XMLStreamReader) {
                return Type.READER;
            }
            return name == null ? Type.CHARACTERS : Type.ELEMENT;
        }

    }
}
