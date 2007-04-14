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

package org.apache.tuscany.interfacedef.wsdl.introspect;

import java.net.URL;
import java.util.List;

import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.contribution.resolver.ArtifactResolver;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.util.XMLType;
import org.apache.tuscany.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.interfacedef.wsdl.xml.WSDLDocumentProcessor;

/**
 * Test case for WSDLOperation
 */
public class WSDLOperationTestCase extends TestCase {
    private static final QName PORTTYPE_NAME =
        new QName("http://example.com/stockquote.wsdl", "StockQuotePortType");

    private WSDLDocumentProcessor processor;
    private ArtifactResolver resolver;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        processor = new WSDLDocumentProcessor();
        resolver = new DefaultArtifactResolver();
    }

    public final void testWrappedOperation() throws Exception {
        URL url = getClass().getResource("../xml/stockquote.wsdl");
        WSDLDefinition definition = processor.read(url);
        PortType portType = definition.getDefinition().getPortType(PORTTYPE_NAME);
        Operation operation = portType.getOperation("getLastTradePrice", null, null);

        WSDLOperation op = new WSDLOperation(operation, definition.getInlinedSchemas(), "org.w3c.dom.Node", resolver);

        DataType<List<DataType>> inputType = op.getInputType();
        Assert.assertEquals(1, inputType.getLogical().size());
        DataType<XMLType> type = inputType.getLogical().get(0);
        Assert.assertEquals(new QName("http://example.com/stockquote.xsd", "getLastTradePrice"), type.getLogical().getElementName());

        DataType<XMLType> outputType = op.getOutputType();
        Assert.assertEquals(new QName("http://example.com/stockquote.xsd", "getLastTradePriceResponse"),
                            outputType.getLogical().getElementName());
        Assert.assertTrue(op.isWrapperStyle());

        DataType<List<DataType>> unwrappedInputType = op.getWrapper().getWrapperInfo().getUnwrappedInputType();
        List<DataType> childTypes = unwrappedInputType.getLogical();
        Assert.assertEquals(1, childTypes.size());
        DataType<XMLType> childType = childTypes.get(0);
        Assert.assertEquals(new QName(null, "tickerSymbol"), childType.getLogical().getElementName());

        childType = op.getWrapper().getWrapperInfo().getUnwrappedOutputType();
        Assert.assertEquals(new QName(null, "price"), childType.getLogical().getElementName());
    }

    public final void testUnwrappedOperation() throws Exception {
        URL url = getClass().getResource("../xml/unwrapped-stockquote.wsdl");
        WSDLDefinition definition = processor.read(url);
        PortType portType = definition.getDefinition().getPortType(PORTTYPE_NAME);

        Operation operation = portType.getOperation("getLastTradePrice1", null, null);
        WSDLOperation op = new WSDLOperation(operation, definition.getInlinedSchemas(), "org.w3c.dom.Node", resolver);
        Assert.assertFalse(op.isWrapperStyle());
        Assert.assertEquals(1, op.getInputType().getLogical().size());

        operation = portType.getOperation("getLastTradePrice2", null, null);
        op = new WSDLOperation(operation, definition.getInlinedSchemas(), "org.w3c.dom.Node", resolver);
        Assert.assertFalse(op.isWrapperStyle());
        Assert.assertEquals(2, op.getInputType().getLogical().size());
    }

    public final void testInvalidWSDL() throws Exception {
        URL url = getClass().getResource("../xml/invalid-stockquote.wsdl");
        WSDLDefinition definition = processor.read(url);
        PortType portType = definition.getDefinition().getPortType(PORTTYPE_NAME);

        Operation operation = portType.getOperation("getLastTradePrice", null, null);
        WSDLOperation op = new WSDLOperation(operation, definition.getInlinedSchemas(), "org.w3c.dom.Node", resolver);

        try {
            op.isWrapperStyle();
            fail("InvalidWSDLException should have been thrown");
        } catch (InvalidWSDLException e) {
            // Expected
        }

    }

}
