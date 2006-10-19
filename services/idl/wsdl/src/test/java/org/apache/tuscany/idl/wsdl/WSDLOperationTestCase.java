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

package org.apache.tuscany.idl.wsdl;

import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.spi.idl.ElementInfo;
import org.apache.tuscany.spi.model.DataType;

/**
 * Test case for WSDLOperation
 */
public class WSDLOperationTestCase extends TestCase {
    private static final QName PORTTYPE_NAME =
        new QName("http://example.com/stockquote.wsdl", "StockQuotePortType");

    private WSDLDefinitionRegistryImpl registry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new WSDLDefinitionRegistryImpl();
        registry.setSchemaRegistry(new XMLSchemaRegistryImpl());
    }

    public final void testWrappedOperation() throws Exception {
        URL url = getClass().getResource("stockquote.wsdl");
        Definition definition = registry.loadDefinition(null, url);
        PortType portType = definition.getPortType(PORTTYPE_NAME);
        Operation operation = portType.getOperation("getLastTradePrice", null, null);

        WSDLOperation op = new WSDLOperation(operation, "org.w3c.dom.Node", registry.getSchemaRegistry());

        DataType<List<DataType<QName>>> inputType = op.getInputType();
        Assert.assertEquals(1, inputType.getLogical().size());
        Assert.assertEquals(new QName("http://example.com/stockquote.xsd", "getLastTradePrice"), inputType
            .getLogical().get(0).getLogical());

        DataType<QName> outputType = op.getOutputType();
        Assert.assertEquals(new QName("http://example.com/stockquote.xsd", "getLastTradePriceResponse"),
                            outputType.getLogical());
        Assert.assertTrue(op.isWrapperStyle());

        DataType<List<DataType<QName>>> unwrappedInputType = op.getWrapper().getUnwrappedInputType();
        List<DataType<QName>> childTypes = unwrappedInputType.getLogical();
        Assert.assertEquals(1, childTypes.size());
        DataType<QName> childType = childTypes.get(0);
        Assert.assertEquals(new QName(null, "tickerSymbol"), childType.getLogical());
        ElementInfo element = (ElementInfo)childType.getMetadata(ElementInfo.class.getName());
        Assert.assertNotNull(element);

        childType = op.getWrapper().getUnwrappedOutputType();
        Assert.assertEquals(new QName(null, "price"), childType.getLogical());
        element = (ElementInfo)childType.getMetadata(ElementInfo.class.getName());
        Assert.assertNotNull(element);
    }

    public final void testUnwrappedOperation() throws Exception {
        URL url = getClass().getResource("unwrapped-stockquote.wsdl");
        Definition definition = registry.loadDefinition(null, url);
        PortType portType = definition.getPortType(PORTTYPE_NAME);

        Operation operation = portType.getOperation("getLastTradePrice1", null, null);
        WSDLOperation op = new WSDLOperation(operation, "org.w3c.dom.Node", registry.getSchemaRegistry());
        Assert.assertFalse(op.isWrapperStyle());
        Assert.assertEquals(1, op.getInputType().getLogical().size());

        operation = portType.getOperation("getLastTradePrice2", null, null);
        op = new WSDLOperation(operation, "org.w3c.dom.Node", registry.getSchemaRegistry());
        Assert.assertFalse(op.isWrapperStyle());
        Assert.assertEquals(2, op.getInputType().getLogical().size());
    }

    public final void testInvalidWSDL() throws Exception {
        URL url = getClass().getResource("invalid-stockquote.wsdl");
        Definition definition = registry.loadDefinition(null, url);
        PortType portType = definition.getPortType(PORTTYPE_NAME);

        Operation operation = portType.getOperation("getLastTradePrice", null, null);
        WSDLOperation op = new WSDLOperation(operation, "org.w3c.dom.Node", registry.getSchemaRegistry());

        try {
            op.isWrapperStyle();
            fail("InvalidWSDLException should have been thrown");
        } catch (InvalidWSDLException e) {
            // Expected
        }

    }

}
