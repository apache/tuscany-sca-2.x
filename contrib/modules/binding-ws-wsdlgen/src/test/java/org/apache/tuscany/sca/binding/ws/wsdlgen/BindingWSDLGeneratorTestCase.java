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
package org.apache.tuscany.sca.binding.ws.wsdlgen;

import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.sca.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSFaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSJavaInterfaceProcessor;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.xml.XSDModelResolver;
import org.osoa.sca.annotations.Remotable;

/**
 *
 * @version $Rev$ $Date$
 */
public class BindingWSDLGeneratorTestCase extends TestCase {

    public void testCreateWSDLInterfaceContract() throws InvalidInterfaceException {
        DefaultModelFactoryExtensionPoint modelFactories = new DefaultModelFactoryExtensionPoint();
        WSDLFactory wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        XSDFactory xsdFactory = modelFactories.getFactory(XSDFactory.class);
        DefaultJavaInterfaceFactory factory = new DefaultJavaInterfaceFactory();
        JavaInterfaceContract javaIC = factory.createJavaInterfaceContract();
        JavaInterface iface = factory.createJavaInterface(HelloWorld.class);
        DefaultDataBindingExtensionPoint dataBindings = new DefaultDataBindingExtensionPoint();
        JAXWSFaultExceptionMapper faultExceptionMapper = new JAXWSFaultExceptionMapper(dataBindings, null);
        new JAXWSJavaInterfaceProcessor(dataBindings, faultExceptionMapper, null).visitInterface(iface);
        new DataBindingJavaInterfaceProcessor(dataBindings).visitInterface(iface);
        javaIC.setInterface(iface);
        WSDLInterfaceContract wsdlIC = BindingWSDLGenerator.createWSDLInterfaceContract(javaIC, false, new XSDModelResolver(null, null), dataBindings, wsdlFactory, xsdFactory, null);
        assertNotNull(wsdlIC);
        WSDLInterface wsdlInterface = (WSDLInterface)wsdlIC.getInterface();
        assertNotNull(wsdlInterface);
        assertEquals(1, wsdlInterface.getOperations().size());
        assertEquals("sayHello", wsdlInterface.getOperations().get(0).getName());
        assertNotNull(wsdlInterface.getPortType());
 
        JavaInterfaceContract javaIC2 = factory.createJavaInterfaceContract();
        JavaInterface iface2 = factory.createJavaInterface(TestJavaInterface.class);
        new JAXWSJavaInterfaceProcessor(dataBindings, faultExceptionMapper, null).visitInterface(iface2);
        new DataBindingJavaInterfaceProcessor(dataBindings).visitInterface(iface2);
        javaIC2.setInterface(iface2);
        WSDLInterfaceContract wsdlIC2 = BindingWSDLGenerator.createWSDLInterfaceContract(javaIC2, false, new XSDModelResolver(null, null), dataBindings, wsdlFactory, xsdFactory, null);
        assertNotNull(wsdlIC2);
    }

}

@Remotable
interface HelloWorld {
    String sayHello(String s);
}
