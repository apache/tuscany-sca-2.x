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
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Operation;

/**
 * Test case for InterfaceWSDLIntrospectorImpl
 */
public class InterfaceWSDLIntrospectorImplTestCase extends TestCase {
    private static final QName PORTTYPE_NAME = new QName("http://example.com/stockquote.wsdl", "StockQuotePortType");

    private WSDLDefinitionRegistryImpl registry;
    private PortType portType;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new WSDLDefinitionRegistryImpl();
        registry.setSchemaRegistry(new XMLSchemaRegistryImpl());
        URL url = getClass().getResource("stockquote.wsdl");
        Definition definition = registry.loadDefinition(null, url);
        portType = definition.getPortType(PORTTYPE_NAME);
    }

    public final void testIntrospectPortType() throws InvalidServiceContractException {
        InterfaceWSDLIntrospector introspector = new InterfaceWSDLIntrospectorImpl(registry);
        WSDLServiceContract contract = introspector.introspect(portType);
        Assert.assertEquals(contract.getInterfaceName(), "StockQuotePortType");
        Map<String, Operation<QName>> operations = contract.getOperations();
        Assert.assertEquals(1, operations.size());
        Operation<QName> operation = operations.get("getLastTradePrice");
        Assert.assertNotNull(operation);
        DataType<List<DataType<QName>>> inputType = operation.getInputType();
        Assert.assertEquals(1, inputType.getLogical().size());
        DataType<QName> returnType = operation.getOutputType();
        Assert.assertNotNull(returnType);
        Assert.assertEquals(0, operation.getFaultTypes().size());
        WrapperStyleOperation op = 
            (WrapperStyleOperation) operation.getMetaData().get(WrapperStyleOperation.class.getName());
        Assert.assertNotNull(op);
        Assert.assertEquals(1, op.getInputChildElements().size());
        Assert.assertEquals(1, op.getOutputChildElements().size());
    }

    public final void testIntrospectPortTypePortType() throws InvalidServiceContractException {
        InterfaceWSDLIntrospector introspector = new InterfaceWSDLIntrospectorImpl(registry);
        WSDLServiceContract contract = introspector.introspect(portType, portType);
        Assert.assertEquals("StockQuotePortType", contract.getInterfaceName());
        Assert.assertEquals("StockQuotePortType", contract.getCallbackName());
    }

}
