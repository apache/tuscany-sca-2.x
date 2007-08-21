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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;

import junit.framework.TestCase;

import org.apache.tuscany.sca.binding.ws.axis2.Java2WSDLHelper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;

public class Java2WSDLHelperTestCase extends TestCase {

    public void testCreateDefinition() {
        Definition definition = Java2WSDLHelper.createDefinition(HelloWorld.class);
        assertNotNull(definition);

        Map portTypes = definition.getPortTypes();
        assertEquals(1, portTypes.size());

        PortType portType = (PortType)portTypes.values().iterator().next();
        assertEquals("HelloWorldPortType", portType.getQName().getLocalPart());
        assertEquals("http://axis2.ws.binding.sca.tuscany.apache.org", portType.getQName().getNamespaceURI());

        List<?> ops = portType.getOperations();
        assertEquals(1, ops.size());

        Operation operation = (Operation)ops.get(0);
        assertEquals("sayHello", operation.getName());
    }


    public void testCreateWSDLInterfaceContract() throws InvalidInterfaceException {
        DefaultJavaInterfaceFactory factory = new DefaultJavaInterfaceFactory();
        JavaInterfaceContract javaIC = factory.createJavaInterfaceContract();
        javaIC.setInterface(factory.createJavaInterface(HelloWorld.class));
        WSDLInterfaceContract wsdlIC = Java2WSDLHelper.createWSDLInterfaceContract(javaIC);
        assertNotNull(wsdlIC);
        WSDLInterface wsdlInterface = (WSDLInterface)wsdlIC.getInterface();
        assertNotNull(wsdlInterface);
        assertEquals(1, wsdlInterface.getOperations().size());
        assertEquals("sayHello", wsdlInterface.getOperations().get(0).getName());
        assertNotNull(wsdlInterface.getPortType());
    }

}

interface HelloWorld {
    public String sayHello(String s);
}
