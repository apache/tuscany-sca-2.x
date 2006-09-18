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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.databinding.DataBindingRegistry;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.idl.Input2InputTransformer;
import org.apache.tuscany.databinding.idl.Output2OutputTransformer;
import org.apache.tuscany.databinding.impl.DataBindingRegistryImpl;
import org.apache.tuscany.databinding.impl.MediatorImpl;
import org.apache.tuscany.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.databinding.impl.TransformerRegistryImpl;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistryImpl;
import org.apache.tuscany.idl.wsdl.WSDLOperation;
import org.apache.tuscany.idl.wsdl.XMLSchemaRegistryImpl;
import org.apache.tuscany.spi.model.DataType;

public class OMElementWrapperTransformerTestCase extends TestCase {
    private static final String IPO_XML =
            "<?xml version=\"1.0\"?>" + "<order1" + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + "  xmlns:ipo=\"http://www.example.com/IPO\""
                    + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\"" + "  orderDate=\"1999-12-01\">"
                    + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">" + "    <name>Helen Zoe</name>"
                    + "    <street>47 Eden Street</street>" + "    <city>Cambridge</city>"
                    + "    <postcode>CB1 1JR</postcode>" + "  </shipTo>" + "  <billTo xsi:type=\"ipo:USAddress\">"
                    + "    <name>Robert Smith</name>" + "    <street>8 Oak Avenue</street>"
                    + "    <city>Old Town</city>" + "    <state>PA</state>" + "    <zip>95819</zip>" + "  </billTo>"
                    + "  <items>" + "    <item partNum=\"833-AA\">" + "      <productName>Lapis necklace</productName>"
                    + "      <quantity>1</quantity>" + "      <USPrice>99.95</USPrice>"
                    + "      <ipo:comment>Want this for the holidays</ipo:comment>"
                    + "      <shipDate>1999-12-05</shipDate>" + "    </item>" + "  </items>" + "</order1>";

    private static final QName PORTTYPE_NAME = new QName("http://example.com/order.wsdl", "OrderPortType");

    private WSDLDefinitionRegistryImpl registry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new WSDLDefinitionRegistryImpl();
        registry.setSchemaRegistry(new XMLSchemaRegistryImpl());
    }

    public void testTransform() throws Exception {
        URL url = getClass().getClassLoader().getResource("order.wsdl");
        Definition definition = registry.loadDefinition(null, url);
        PortType portType = definition.getPortType(PORTTYPE_NAME);
        Operation operation = portType.getOperation("checkOrderStatus", null, null);
        WSDLOperation op = new WSDLOperation(operation, AxiomDataBinding.NAME, registry.getSchemaRegistry());
        Assert.assertTrue(op.isWrapperStyle());

        MediatorImpl m = new MediatorImpl();
        TransformerRegistryImpl tr = new TransformerRegistryImpl();
        tr.registerTransformer(new String2OMElement());
        tr.registerTransformer(new OMElement2String());
        m.setTransformerRegistry(tr);
        DataBindingRegistry dataBindingRegistry = new DataBindingRegistryImpl();
        dataBindingRegistry.register(new AxiomDataBinding());
        m.setDataBindingRegistry(dataBindingRegistry);

        Object[] source = new Object[] { "cust001", IPO_XML, Integer.valueOf(1) };
        Input2InputTransformer t = new Input2InputTransformer();
        t.setDataBindingRegistry(dataBindingRegistry);
        t.setMediator(m);

        TransformationContext context = new TransformationContextImpl();
        List<DataType<Class>> types = new ArrayList<DataType<Class>>();
        types.add(new DataType<Class>(null, String.class, String.class));
        types.add(new DataType<Class>("java.lang.String", String.class, String.class));
        types.add(new DataType<Class>(null, int.class, int.class));
        DataType<List<DataType<Class>>> inputType1 =
                new DataType<List<DataType<Class>>>("idl:input", Object[].class, types);
        context.setSourceDataType(inputType1);
        context.setTargetDataType(op.getInputType());
        Object[] results = t.transform(source, context);
        Assert.assertEquals(1, results.length);
        Assert.assertTrue(results[0] instanceof OMElement);
        OMElement element = (OMElement) results[0];
        Assert.assertEquals(new QName("http://example.com/order.xsd", "checkOrderStatus"), element.getQName());

        TransformationContext context1 = new TransformationContextImpl();
        DataType<DataType> sourceType = new DataType<DataType>("idl:output", Object.class, op.getOutputType());
        sourceType.setMetadata(WSDLOperation.class.getName(), op.getOutputType().getMetadata(
                WSDLOperation.class.getName()));
        
        context1.setSourceDataType(sourceType);
        DataType<DataType> targetType =
                new DataType<DataType>("idl:output", Object.class, new DataType<Class>("java.lang.String",
                        String.class, String.class));
        context1.setTargetDataType(targetType);

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement responseElement =
                factory.createOMElement(new QName("http://example.com/order.wsdl", "checkOrderStatusResponse"), null);
        OMElement status = factory.createOMElement(new QName(null, "status"), responseElement);
        factory.createOMText(status, "shipped");
        Output2OutputTransformer t2 = new Output2OutputTransformer();
        t2.setMediator(m);
        t2.setDataBindingRegistry(dataBindingRegistry);
        Object st = t2.transform(responseElement, context1);
        Assert.assertEquals("shipped", st);

    }
}
