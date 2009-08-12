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

package org.apache.tuscany.sca.common.xml.stax;

import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.common.xml.stax.StAXHelper.Attribute;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * Test Case for StAXHelper
 *
 * @version $Rev$ $Date$
 */
public class StAXHelperTestCase {
    private static final String XML =
        "<a:foo xmlns:a='http://foo' name='foo' xmlns='http://foo1'><bar name='bar'>" + "<doo a:name='doo' xmlns:a='http://doo'/>"
            + "</bar><bar1 xmlns='http://bar1' name='bar1'/><bar2 xmlns='' name='bar2'/></a:foo>";
    public static final QName WSDL11 = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
    public static final QName WSDL20 = new QName("http://www.w3.org/ns/wsdl", "description");
    public static final QName XSD = new QName("http://www.w3.org/2001/XMLSchema", "schema");

    @Test
    public void testHelper() throws Exception {
        StAXHelper helper = new StAXHelper(new DefaultExtensionPointRegistry());
        XMLStreamReader reader = helper.createXMLStreamReader(XML);
        String xml = helper.saveAsString(reader);
        XMLAssert.assertXMLEqual(XML, xml);
        reader = helper.createXMLStreamReader(xml);
        assertNotNull(reader);

        Node node = helper.saveAsNode(reader);
        assertNotNull(node.getFirstChild());
        reader = helper.createXMLStreamReader(node);
        xml = helper.saveAsString(reader);
        XMLAssert.assertXMLEqual(XML, xml);
    }
    
    @Test
    public void testNoRepairingNamespaces() throws Exception {
        StAXHelper helper = new StAXHelper(new DefaultExtensionPointRegistry());
        helper.getOutputFactory().setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
        XMLStreamReader reader = helper.createXMLStreamReader(XML);
        String xml = helper.saveAsString(reader);
        XMLAssert.assertXMLEqual(XML, xml);
        reader = helper.createXMLStreamReader(xml);
        assertNotNull(reader);
    }
    
    @Test
    public void testIndex() throws Exception {
        StAXHelper helper = new StAXHelper(new DefaultExtensionPointRegistry());
        URL xsd = getClass().getResource("test.xsd");
        String tns = helper.readAttribute(xsd, XSD, "targetNamespace");
        Assert.assertEquals("http://www.example.org/test/", tns);

        List<String> tnsList = helper.readAttributes(xsd, XSD, "targetNamespace");
        Assert.assertEquals(1, tnsList.size());
        Assert.assertEquals("http://www.example.org/test/", tnsList.get(0));

        URL wsdl = getClass().getResource("test.wsdl");
        tns = helper.readAttribute(wsdl, WSDL11, "targetNamespace");
        Assert.assertEquals("http://www.example.org/test/wsdl", tns);

        tns = helper.readAttribute(wsdl, XSD, "targetNamespace");
        Assert.assertNull(tns);

        tnsList = helper.readAttributes(wsdl, XSD, "targetNamespace");
        Assert.assertEquals(2, tnsList.size());
        Assert.assertEquals("http://www.example.org/test/xsd1", tnsList.get(0));
        Assert.assertEquals("http://www.example.org/test/xsd2", tnsList.get(1));

        Attribute attr1 = new Attribute(WSDL11, "targetNamespace");
        Attribute attr2 = new Attribute(XSD, "targetNamespace");
        Attribute[] attrs = helper.readAttributes(wsdl, attr1, attr2);

        Assert.assertEquals(2, attrs.length);
        Assert.assertEquals("http://www.example.org/test/wsdl", attrs[0].getValues().get(0));
        Assert.assertEquals("http://www.example.org/test/xsd1", attrs[1].getValues().get(0));
        Assert.assertEquals("http://www.example.org/test/xsd2", attrs[1].getValues().get(1));

    }

}
