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

package org.apache.tuscany.sca.interfacedef.wsdl.introspect;

import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.contribution.resolver.ModelResolver;
import org.apache.tuscany.contribution.resolver.DefaultModelResolver;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.util.XMLType;
import org.apache.tuscany.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.introspect.DefaultWSDLInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLDocumentProcessor;

/**
 * Test case for InterfaceWSDLIntrospectorImpl
 */
public class DefaultWSDLInterfaceIntrospectorTestCase extends TestCase {
    private static final QName PORTTYPE_NAME = new QName("http://example.com/stockquote.wsdl", "StockQuotePortType");

    private WSDLDocumentProcessor registry;
    private PortType portType;
    private ModelResolver resolver;
    private WSDLDefinition definition;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new WSDLDocumentProcessor(new DefaultWSDLFactory(), null);
        resolver = new DefaultModelResolver(getClass().getClassLoader());
        URL url = getClass().getResource("../xml/stockquote.wsdl");
        definition = registry.read(null, new URI("stockquote.wsdl"), url);
        portType = definition.getDefinition().getPortType(PORTTYPE_NAME);
    }

    public final void testIntrospectPortType() throws InvalidInterfaceException {
        DefaultWSDLInterfaceIntrospector introspector = new DefaultWSDLInterfaceIntrospector(new DefaultWSDLFactory());
        WSDLInterface contract = introspector.introspect(portType, definition.getInlinedSchemas(), resolver);
        Assert.assertEquals(contract.getName().getLocalPart(), "StockQuotePortType");
        List<Operation> operations = contract.getOperations();
        Assert.assertEquals(1, operations.size());
        Operation operation = operations.get(0);
        Assert.assertEquals("getLastTradePrice", operation.getName());
        DataType<List<DataType>> inputType = operation.getInputType();
        Assert.assertEquals(1, inputType.getLogical().size());
        DataType<XMLType> returnType = operation.getOutputType();
        Assert.assertNotNull(returnType);
        Assert.assertEquals(0, operation.getFaultTypes().size());
        // Assert.assertEquals(1,
        // operation.getWrapper().getInputChildElements().size());
        // Assert.assertEquals(1,
        // operation.getWrapper().getOutputChildElements().size());
    }

}
