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

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.interfacedef.wsdl.xml.WSDLDocumentProcessor;

/**
 * Test case for WSDLOperation
 */
public class WrapperStyleOperationTestCase extends TestCase {
    private static final QName PORTTYPE_NAME = new QName("http://example.com/stockquote.wsdl", "StockQuotePortType");

    private WSDLDocumentProcessor registry;
    private XMLSchemaRegistry schemaRegistry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new WSDLDocumentProcessor();
        this.schemaRegistry = new DefaultXMLSchemaRegistry();
    }

    public final void testWrappedOperation() throws Exception {
        URL url = getClass().getResource("../xml/stockquote.wsdl");
        Definition definition = registry.read( url).getDefinition();
        schemaRegistry.loadSchemas(definition);
        PortType portType = definition.getPortType(PORTTYPE_NAME);
        Operation operation = portType.getOperation("getLastTradePrice", null, null);
        WSDLOperation op = new WSDLOperation(operation, "org.w3c.dom.Node", schemaRegistry);
        Assert.assertTrue(op.isWrapperStyle());
        Assert.assertEquals(1, op.getWrapper().getInputChildElements().size());
        Assert.assertEquals(1, op.getWrapper().getOutputChildElements().size());
    }

    public final void testUnwrappedOperation() throws Exception {
        URL url = getClass().getResource("../xml/unwrapped-stockquote.wsdl");
        Definition definition = registry.read( url).getDefinition();
        schemaRegistry.loadSchemas(definition);
        PortType portType = definition.getPortType(PORTTYPE_NAME);
        Operation operation = portType.getOperation("getLastTradePrice1", null, null);
        WSDLOperation op = new WSDLOperation(operation, "org.w3c.dom.Node", schemaRegistry);
        Assert.assertFalse(op.isWrapperStyle());
        operation = portType.getOperation("getLastTradePrice2", null, null);
        op = new WSDLOperation(operation, "org.w3c.dom.Node", schemaRegistry);
        Assert.assertFalse(op.isWrapperStyle());
    }

}
