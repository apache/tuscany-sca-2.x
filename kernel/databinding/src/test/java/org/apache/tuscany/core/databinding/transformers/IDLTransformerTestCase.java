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
package org.apache.tuscany.core.databinding.transformers;

import static org.apache.tuscany.spi.databinding.DataBinding.IDL_INPUT;
import static org.apache.tuscany.spi.databinding.DataBinding.IDL_OUTPUT;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.core.databinding.transformers.Input2InputTransformer;
import org.apache.tuscany.core.databinding.transformers.Output2OutputTransformer;
import org.apache.tuscany.databinding.impl.DataBindingRegistryImpl;
import org.apache.tuscany.databinding.impl.MediatorImpl;
import org.apache.tuscany.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.databinding.impl.TransformerRegistryImpl;
import org.apache.tuscany.databinding.javabeans.DOMNode2JavaBeanTransformer;
import org.apache.tuscany.databinding.javabeans.JavaBean2DOMNodeTransformer;
import org.apache.tuscany.databinding.xml.DOMDataBinding;
import org.apache.tuscany.databinding.xml.Node2String;
import org.apache.tuscany.databinding.xml.String2Node;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.ElementInfo;
import org.apache.tuscany.spi.model.TypeInfo;
import org.apache.tuscany.spi.model.WrapperInfo;
import org.apache.tuscany.spi.model.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IDLTransformerTestCase extends TestCase {
    private static final String IPO_XML =
        "<?xml version=\"1.0\"?>" + "<order1"
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
            + "</order1>";

    private static final String URI_ORDER_XSD = "http://example.com/order.xsd";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTransform() throws Exception {
        List<DataType<XMLType>> types0 = new ArrayList<DataType<XMLType>>();
        DataType<XMLType> wrapperType =
            new DataType<XMLType>(null, Object.class, new XMLType(new QName(URI_ORDER_XSD, "checkOrderStatus"), null));
        types0.add(wrapperType);
        DataType<List<DataType<XMLType>>> inputType0 =
            new DataType<List<DataType<XMLType>>>(IDL_INPUT, Object[].class, types0);

        List<DataType<XMLType>> types1 = new ArrayList<DataType<XMLType>>();
        DataType<XMLType> customerIdType =
            new DataType<XMLType>(null, Object.class, new XMLType(new QName(URI_ORDER_XSD, "customerId"), null));
        DataType<XMLType> orderType =
            new DataType<XMLType>(null, Object.class, new XMLType(new QName(URI_ORDER_XSD, "order"), null));
        DataType<XMLType> flagType =
            new DataType<XMLType>(null, Object.class, new XMLType(new QName(URI_ORDER_XSD, "flag"), null));
        types1.add(customerIdType);
        types1.add(orderType);
        types1.add(flagType);

        DataType<XMLType> statusType =
            new DataType<XMLType>(null, Object.class, new XMLType(new QName(URI_ORDER_XSD, "status"), null));
        DataType<XMLType> responseType =
            new DataType<XMLType>(null, Object.class, new XMLType(new QName(URI_ORDER_XSD, "checkOrderStatusResponse"),
                                                                  null));

        org.apache.tuscany.spi.model.Operation<XMLType> op =
            new org.apache.tuscany.spi.model.Operation<XMLType>("checkOrderStatus", inputType0, responseType, null);
        op.setDataBinding(DOMDataBinding.NAME);

        inputType0.setOperation(op);
        op.setWrapperStyle(true);
        ElementInfo inputElement =
            new ElementInfo(new QName(URI_ORDER_XSD, "checkOrderStatus"), new TypeInfo(null, false, null));
        wrapperType.setMetadata(ElementInfo.class.getName(), inputElement);

        ElementInfo customerId =
            new ElementInfo(new QName("", "customerId"), SimpleTypeMapperExtension.XSD_SIMPLE_TYPES.get("string"));
        ElementInfo order =
            new ElementInfo(new QName("", "order"), new TypeInfo(new QName(URI_ORDER_XSD), false, null));
        ElementInfo flag =
            new ElementInfo(new QName("", "flag"), SimpleTypeMapperExtension.XSD_SIMPLE_TYPES.get("int"));

        customerIdType.setMetadata(ElementInfo.class.getName(), customerId);
        orderType.setMetadata(ElementInfo.class.getName(), order);
        flagType.setMetadata(ElementInfo.class.getName(), flag);

        customerIdType.setOperation(op);
        orderType.setOperation(op);
        flagType.setOperation(op);

        List<ElementInfo> inputElements = new ArrayList<ElementInfo>();
        inputElements.add(customerId);
        inputElements.add(order);
        inputElements.add(flag);

        ElementInfo statusElement =
            new ElementInfo(new QName("", "status"), SimpleTypeMapperExtension.XSD_SIMPLE_TYPES.get("string"));

        statusType.setMetadata(ElementInfo.class.getName(), statusElement);
        statusType.setOperation(op);

        List<ElementInfo> outputElements = new ArrayList<ElementInfo>();
        outputElements.add(statusElement);

        ElementInfo outputElement =
            new ElementInfo(new QName(URI_ORDER_XSD, "checkOrderStatusResponse"), new TypeInfo(null, false, null));

        responseType.setMetadata(ElementInfo.class.getName(), inputElement);
        responseType.setOperation(op);

        WrapperInfo wrapperInfo =
            new WrapperInfo(DOMDataBinding.NAME, inputElement, outputElement, inputElements, outputElements);
        op.setWrapper(wrapperInfo);
        op.setDataBinding(DOMDataBinding.NAME);

        MediatorImpl m = new MediatorImpl();
        TransformerRegistryImpl tr = new TransformerRegistryImpl();
        tr.registerTransformer(new String2Node());
        tr.registerTransformer(new Node2String());
        tr.registerTransformer(new DOMNode2JavaBeanTransformer());
        tr.registerTransformer(new JavaBean2DOMNodeTransformer());
        m.setTransformerRegistry(tr);
        DataBindingRegistry dataBindingRegistry = new DataBindingRegistryImpl();
        dataBindingRegistry.register(new DOMDataBinding());
        m.setDataBindingRegistry(dataBindingRegistry);

        Object[] source = new Object[] {"cust001", IPO_XML, Integer.valueOf(1)};
        Input2InputTransformer t = new Input2InputTransformer();
        t.setMediator(m);

        TransformationContext context = new TransformationContextImpl();
        List<DataType<Class>> types = new ArrayList<DataType<Class>>();
        types.add(new DataType<Class>(Object.class.getName(), String.class, String.class));
        types.add(new DataType<Class>("java.lang.String", String.class, String.class));
        types.add(new DataType<Class>(Object.class.getName(), int.class, int.class));
        DataType<List<DataType<Class>>> inputType1 =
            new DataType<List<DataType<Class>>>(IDL_INPUT, Object[].class, types);
        context.setSourceDataType(inputType1);
        context.setTargetDataType(op.getInputType());
        Object[] results = t.transform(source, context);
        assertEquals(1, results.length);
        assertTrue(results[0] instanceof Element);
        Element element = (Element)results[0];
        assertEquals("http://example.com/order.xsd", element.getNamespaceURI());
        assertEquals("checkOrderStatus", element.getLocalName());

        TransformationContext context1 = new TransformationContextImpl();
        DataType<DataType> sourceType = new DataType<DataType>(IDL_OUTPUT, Object.class, op.getOutputType());
        sourceType.setOperation(op.getOutputType().getOperation());

        context1.setSourceDataType(sourceType);
        DataType<DataType> targetType =
            new DataType<DataType>(IDL_OUTPUT, Object.class, new DataType<Class>("java.lang.Object", String.class,
                                                                                 String.class));
        context1.setTargetDataType(targetType);

        Document factory = DOMHelper.newDocument();
        Element responseElement =
            factory.createElementNS("http://example.com/order.wsdl", "p:checkOrderStatusResponse");
        Element status = factory.createElement("status");
        responseElement.appendChild(status);
        status.appendChild(factory.createTextNode("shipped"));
        Output2OutputTransformer t2 = new Output2OutputTransformer();
        t2.setMediator(m);
        Object st = t2.transform(responseElement, context1);
        assertEquals("shipped", st);

    }

}
