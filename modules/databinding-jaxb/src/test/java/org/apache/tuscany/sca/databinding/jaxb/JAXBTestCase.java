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
package org.apache.tuscany.sca.databinding.jaxb;

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import com.example.ipo.jaxb.ObjectFactory;
import com.example.ipo.jaxb.PurchaseOrderType;

/**
 *
 * @version $Rev$ $Date$
 */
public class JAXBTestCase {
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
    
    private static ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();

    @Test
    public void testTransform() throws Exception {
        Reader2JAXB t0 = new Reader2JAXB();

        DataType targetDataType = new DataTypeImpl<Class>(PurchaseOrderType.class, null);

        TransformationContext tContext = new TransformationContextImpl();
        tContext.setTargetDataType(targetDataType);

        Object object1 = t0.transform(new StringReader(IPO_XML), tContext);

        DataType sourceDataType = new DataTypeImpl<Class>(PurchaseOrderType.class, null);

        TransformationContext tContext1 = new TransformationContextImpl();
        tContext1.setSourceDataType(sourceDataType);

        JAXB2Node t1 = new JAXB2Node(registry);
        Node node = t1.transform(object1, tContext1);

        Assert.assertNotNull(node);

        Node2JAXB t2 = new Node2JAXB();
        Object object2 = t2.transform(node, tContext);
        Assert.assertNotNull(object2);

    }

    @Test
    public void testTransform2() throws Exception {
        Reader2JAXB t0 = new Reader2JAXB();

        QName root = new QName("http://www.example.com/IPO", "purchaseOrder");
        DataType targetDataType = new DataTypeImpl<XMLType>(PurchaseOrderType.class, new XMLType(root, null));
        // targetDataType.setMetadata(JAXBContextHelper.JAXB_CONTEXT_PATH, contextPath);

        TransformationContext tContext = new TransformationContextImpl();
        tContext.setTargetDataType(targetDataType);
        Object object1 = t0.transform(new StringReader(IPO_XML), tContext);

        DataType sourceDataType = new DataTypeImpl<XMLType>(PurchaseOrderType.class, new XMLType(root, null));
        // sourceDataType.setMetadata(JAXBContextHelper.JAXB_CONTEXT_PATH, contextPath);

        TransformationContext tContext1 = new TransformationContextImpl();
        tContext1.setSourceDataType(sourceDataType);

        JAXB2Node t1 = new JAXB2Node(registry);
        Node node = t1.transform(object1, tContext1);

        Assert.assertNotNull(node);

        Node2JAXB t2 = new Node2JAXB();
        Object object2 = t2.transform(node, tContext);
        Assert.assertNotNull(object2);

    }    

    @Test
    public void testTransform3() throws Exception {

        DataType sourceDataType = new DataTypeImpl<Class>(PurchaseOrderType.class, null);

        TransformationContext tContext1 = new TransformationContextImpl();
        tContext1.setSourceDataType(sourceDataType);


        JAXB2Node t1 = new JAXB2Node(registry);
        PurchaseOrderType po = new ObjectFactory().createPurchaseOrderType();
        Node node = t1.transform(po, tContext1);

        Assert.assertNotNull(node);

    }
}
