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

import junit.framework.Assert;

import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.wsdl.AbstractWSDLTestCase;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.policy.Intent;

/**
 * Test case for InterfaceWSDLIntrospectorImpl.
 *
 * @version $Rev$ $Date$
 */
public class WSDLPolicyAnnotatedInterfaceIntrospectorTestCase extends AbstractWSDLTestCase {
    private static final QName PORTTYPE_NAME = new QName("http://example.com/stockquote.wsdl", "StockQuotePortType");
    private static final QName INTENT = new QName("http://example.com/stockquote.wsdl", "PolicyIntent");

    private PortType portType;
    private WSDLDefinition definition;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        URL url = getClass().getResource("/policy/stockquote_policy.wsdl");
        definition = (WSDLDefinition)documentProcessor.read(null, new URI("stockquote.wsdl"), url);
        resolver.addModel(definition);
        definition = resolver.resolveModel(WSDLDefinition.class, definition);
        portType = definition.getDefinition().getPortType(PORTTYPE_NAME);
    }

    @SuppressWarnings("unchecked")
    public final void testIntrospectPortType() throws InvalidInterfaceException {
        WSDLInterface contract = wsdlFactory.createWSDLInterface(portType, definition, resolver);
        Assert.assertEquals(contract.getName().getLocalPart(), "StockQuotePortType");
        Assert.assertTrue(contract.isConversational());
        
        boolean foundIntent = false;
        
        for(Intent intent : contract.getRequiredIntents()) {
            if(INTENT.equals(intent.getName())) {
                foundIntent = true;
            }
        }
        
        Assert.assertTrue(foundIntent);
        
        for(Operation operation : contract.getOperations()) {
            if(operation.getName().equals("cancel")) {
                Assert.assertEquals(operation.getConversationSequence(), ConversationSequence.CONVERSATION_END);
            }
        }
        
    }
}
