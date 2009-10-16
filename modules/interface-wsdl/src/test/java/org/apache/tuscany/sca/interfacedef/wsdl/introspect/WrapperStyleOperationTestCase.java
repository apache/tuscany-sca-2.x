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

import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLOperation;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.AbstractWSDLTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for WSDLOperation.
 *
 * @version $Rev$ $Date$
 */
public class WrapperStyleOperationTestCase extends AbstractWSDLTestCase {
    private static final QName PORTTYPE_NAME = new QName("http://example.com/stockquote.wsdl", "StockQuotePortType");

    @Test
    public final void testWrappedOperation() throws Exception {
        ProcessorContext context = new ProcessorContext();
        URL url = getClass().getResource("../xml/stockquote.wsdl");
        WSDLDefinition definition = (WSDLDefinition)documentProcessor.read(null, new URI("stockquote.wsdl"), url, context);
        resolver.addModel(definition, context);
        definition = resolver.resolveModel(WSDLDefinition.class, definition, context);
        PortType portType = definition.getDefinition().getPortType(PORTTYPE_NAME);
        WSDLInterface wi = wsdlFactory.createWSDLInterface(portType, definition, resolver, context.getMonitor());
        WSDLOperation op = (WSDLOperation) wi.getOperations().get(0);
        Assert.assertTrue(op.isWrapperStyle());
        Assert.assertEquals(1, op.getWrapper().getInputChildElements().size());
        Assert.assertEquals(1, op.getWrapper().getOutputChildElements().size());
    }

    @Test
    public final void testUnwrappedOperation() throws Exception {
        ProcessorContext context = new ProcessorContext();
        URL url = getClass().getResource("../xml/unwrapped-stockquote.wsdl");
        WSDLDefinition definition = (WSDLDefinition)documentProcessor.read(null, new URI("unwrapped-stockquote.wsdl"), url, context);
        resolver.addModel(definition, context);
        definition = resolver.resolveModel(WSDLDefinition.class, definition, context);
        PortType portType = definition.getDefinition().getPortType(PORTTYPE_NAME);
        WSDLInterface wi = wsdlFactory.createWSDLInterface(portType, definition, resolver, context.getMonitor());
        WSDLOperation op = (WSDLOperation) wi.getOperations().get(1);
        Assert.assertFalse(op.isWrapperStyle());
        op = (WSDLOperation) wi.getOperations().get(2);
        Assert.assertFalse(op.isWrapperStyle());
    }

}
