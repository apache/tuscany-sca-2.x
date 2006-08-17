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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.axiom.OMElement2String;
import org.apache.tuscany.databinding.axiom.OMElement2XMLStreamReader;
import org.apache.tuscany.databinding.axiom.String2OMElement;
import org.apache.tuscany.databinding.axiom.XMLStreamReader2OMElement;
import org.apache.tuscany.databinding.impl.TransformerRegistryImpl;
import org.apache.tuscany.databinding.jaxb.JAXB2Node;
import org.apache.tuscany.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.databinding.jaxb.Node2JAXB;
import org.apache.tuscany.databinding.jaxb.XMLStreamReader2JAXB;
import org.apache.tuscany.databinding.sdo.DataObject2XMLStreamReader;
import org.apache.tuscany.databinding.sdo.XMLDocument2XMLStreamReader;
import org.apache.tuscany.databinding.sdo.XMLStreamReader2DataObject;
import org.apache.tuscany.databinding.sdo.XMLStreamReader2XMLDocument;
import org.apache.tuscany.databinding.xml.Node2String;
import org.apache.tuscany.databinding.xml.String2Node;
import org.apache.tuscany.databinding.xml.String2XMLStreamReader;
import org.apache.tuscany.databinding.xml.XMLStreamReader2String;
import org.apache.tuscany.databinding.xmlbeans.Node2XmlObject;
import org.apache.tuscany.databinding.xmlbeans.XMLStreamReader2XmlObject;
import org.apache.tuscany.databinding.xmlbeans.XmlObject2Node;
import org.apache.tuscany.databinding.xmlbeans.XmlObject2XMLStreamReader;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

public class TransformationTestCase extends TestCase {
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

    private TransformerRegistry registry;

    private String contextPath = "com.example.ipo.jaxb";

    protected void setUp() throws Exception {
        super.setUp();
        registry = new TransformerRegistryImpl();

        List<Transformer> transformers = new ArrayList<Transformer>();

        // Adding JAXB transformers
        transformers.add(new JAXB2Node());
        transformers.add(new Node2JAXB());
        transformers.add(new XMLStreamReader2JAXB());

        // Adding XMLBeans transformers
        transformers.add(new XmlObject2Node());
        transformers.add(new XmlObject2XMLStreamReader());
        transformers.add(new Node2XmlObject());
        transformers.add(new XMLStreamReader2XmlObject());

        transformers.add(new DataObject2XMLStreamReader());
        transformers.add(new XMLStreamReader2DataObject());

        transformers.add(new XMLDocument2XMLStreamReader());
        transformers.add(new XMLStreamReader2XMLDocument());

        transformers.add(new String2XMLStreamReader());
        transformers.add(new XMLStreamReader2String());

        transformers.add(new String2Node());
        transformers.add(new Node2String());

        transformers.add(new OMElement2String());
        transformers.add(new String2OMElement());

        transformers.add(new XMLStreamReader2OMElement());
        transformers.add(new OMElement2XMLStreamReader());

        for (Transformer transformer : transformers) {
            registry.registerTransformer(transformer);
        }

        // System.out.println(registry);

        URL xsdFile = getClass().getClassLoader().getResource("ipo.xsd");
        XSDHelper.INSTANCE.define(xsdFile.openStream(), null);
    }

    @SuppressWarnings("unchecked")
    // XMLBeans --> SDO
    public void testTransformation1() throws Exception {
        URL xsdFile = getClass().getClassLoader().getResource("ipo.xsd");
        XSDHelper.INSTANCE.define(xsdFile.openStream(), null);

        // URL/Stream/Reader to XmlObject
        XmlObject object = XmlObject.Factory.parse(new StringReader(IPO_XML));

        List<Transformer> path = registry.getTransformerChain(XmlObject.class.getName(), DataObject.class.getName());
        System.out.println("Path: " + path);

        TransformationContext tContext = createTransformationContext();

        Object result = object;
        for (Transformer transformer : path) {
            result = ((PullTransformer) transformer).transform(result, tContext);
        }
        System.out.println("Result: " + result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof DataObject);

    }

    @SuppressWarnings("unchecked")
    // SDO --> DOM
    public void testTransformation2() throws Exception {
        // System.out.println(registry);

        URL xmlFile = getClass().getClassLoader().getResource("ipo.xml");
        // URL/Stream/Reader to XmlObject
        XMLDocument object = XMLHelper.INSTANCE.load(xmlFile.openStream());

        List<Transformer> path = registry.getTransformerChain(XMLDocument.class.getName(), Node.class.getName());
        System.out.println("Path: " + path);

        TransformationContext tContext = createTransformationContext();

        Object result = object;
        for (Transformer transformer : path) {
            result = ((PullTransformer) transformer).transform(result, tContext);
        }
        System.out.println("Result: " + result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Node);

    }

    @SuppressWarnings("unchecked")
    // SDO --> JAXB
    public void testTransformation3() throws Exception {
        // System.out.println(registry);

        URL xmlFile = getClass().getClassLoader().getResource("ipo.xml");
        // URL/Stream/Reader to XmlObject
        XMLDocument object = XMLHelper.INSTANCE.load(xmlFile.openStream());

        List<Transformer> path = registry.getTransformerChain(XMLDocument.class.getName(), Object.class.getName());
        System.out.println("Path: " + path);

        TransformationContext tContext = createTransformationContext();

        Object result = object;
        for (Transformer transformer : path) {
            result = ((PullTransformer) transformer).transform(result, tContext);
        }
        System.out.println("Result: " + result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Object);

    }

    private TransformationContext createTransformationContext() {
        DataBinding targetContext = createMock(DataBinding.class);
        expect(targetContext.getAttribute(JAXBContextHelper.JAXB_CONTEXT_PATH)).andReturn(contextPath).anyTimes();
        replay(targetContext);

        TransformationContext tContext = createMock(TransformationContext.class);
        expect(tContext.getTargetDataBinding()).andReturn(targetContext).anyTimes();

        DataBinding sourceContext = createMock(DataBinding.class);
        expect(sourceContext.getAttribute(JAXBContextHelper.JAXB_CONTEXT_PATH)).andReturn(contextPath).anyTimes();
        replay(sourceContext);

        expect(tContext.getSourceDataBinding()).andReturn(sourceContext).anyTimes();
        replay(tContext);
        return tContext;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
