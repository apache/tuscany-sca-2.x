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
package org.apache.tuscany.databinding.impl;

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.databinding.DefaultTransformerExtensionPoint;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.xml.Node2String;
import org.apache.tuscany.databinding.xml.Node2Writer;
import org.apache.tuscany.databinding.xml.SAX2DOMPipe;
import org.apache.tuscany.databinding.xml.String2SAX;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Test case for MediatorImpl
 */
public class MediatorImplTestCase extends TestCase {
    private static final String IPO_XML =
        "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
            + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + "  xmlns:ipo=\"http://www.example.com/IPO\""
            + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\""
            + "  orderDate=\"1999-12-01\">"
            + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">"
            + " <name>Helen Zoe</name>"
            + " <street>47 Eden Street</street>"
            + " <city>Cambridge</city>"
            + " <postcode>CB1 1JR</postcode>"
            + "  </shipTo>"
            + "  <billTo xsi:type=\"ipo:USAddress\">"
            + " <name>Robert Smith</name>"
            + " <street>8 Oak Avenue</street>"
            + " <city>Old Town</city>"
            + "<state>PA</state>"
            + " <zip>95819</zip>"
            + "  </billTo>"
            + "  <items>"
            + " <item partNum=\"833-AA\">"
            + " <productName>Lapis necklace</productName>"
            + "   <quantity>1</quantity>"
            + "<USPrice>99.95</USPrice>"
            + "   <ipo:comment>Want this for the holidays</ipo:comment>"
            + "   <shipDate>1999-12-05</shipDate>"
            + " </item>"
            + "  </items>"
            + "</ipo:purchaseOrder>";

    private DefaultMediator mediator;

    protected void setUp() throws Exception {
        super.setUp();

        DataBindingExtensionPoint dataBindingRegistry = new DefaultDataBindingExtensionPoint();
        TransformerExtensionPoint registry = new DefaultTransformerExtensionPoint(dataBindingRegistry);

        registry.addTransformer(new String2SAX());
        registry.addTransformer(new SAX2DOMPipe());
        registry.addTransformer(new Node2String());
        registry.addTransformer(new Node2Writer());

        mediator = new DefaultMediator(dataBindingRegistry, registry);
    }

    private TransformationContext createTransformationContext(Class sourceType, Class targetType) {
        TransformationContext context = new TransformationContextImpl();
        DataType sourceDataType = new DataTypeImpl<Class>(sourceType.getName(), sourceType, sourceType);
        DataType targetDataType = new DataTypeImpl<Class>(targetType.getName(), targetType, targetType);
        context.setSourceDataType(sourceDataType);
        context.setTargetDataType(targetDataType);
        return context;
    }

    public void testTransform1() {
        TransformationContext context = createTransformationContext(String.class, Node.class);
        Object node =
            mediator.mediate(IPO_XML, context.getSourceDataType(), context.getTargetDataType(), null);
        Assert.assertTrue(node instanceof Document);
        Element root = ((Document)node).getDocumentElement();
        Assert.assertEquals(root.getNamespaceURI(), "http://www.example.com/IPO");
        Assert.assertEquals(root.getLocalName(), "purchaseOrder");
    }

    public void testTransform2() {
        TransformationContext context = createTransformationContext(String.class, Writer.class);
        Writer writer = new StringWriter();
        mediator.mediate(IPO_XML, writer, context.getSourceDataType(), context.getTargetDataType(), null);
        String str = writer.toString();
        Assert.assertTrue(str != null && str.indexOf("<shipDate>1999-12-05</shipDate>") != -1);
    }

}
