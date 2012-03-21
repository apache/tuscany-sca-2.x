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

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.sca.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Compatibility;
import org.apache.tuscany.sca.interfacedef.IncompatibleInterfaceContractException;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSFaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSJavaInterfaceProcessor;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.xml.XSDModelResolver;
import org.junit.Ignore;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Tests generating WSDL from a Java interface that uses types with JAXB @XmlType annotations
 */
@Ignore("Fails presently due to a bug in the generated wsdl where the Class2 type is in the wrong namespace")
public class JAXBWSDLGeneratorTestCase extends TestCase {

    public void testCreateWSDLInterfaceContract() throws InvalidInterfaceException, IncompatibleInterfaceContractException {
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        org.apache.tuscany.sca.core.FactoryExtensionPoint modelFactories = new DefaultFactoryExtensionPoint(registry);
        WSDLFactory wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        XSDFactory xsdFactory = modelFactories.getFactory(XSDFactory.class);
        DocumentBuilderFactory documentBuilderFactory = modelFactories.getFactory(DocumentBuilderFactory.class);
        JavaInterfaceFactory factory = new DefaultJavaInterfaceFactory(registry);
        JavaInterfaceContract javaIC = factory.createJavaInterfaceContract();

        JavaInterface iface = factory.createJavaInterface(JaxbIface.class);
        
        DefaultDataBindingExtensionPoint dataBindings = new DefaultDataBindingExtensionPoint(registry);
        JAXWSFaultExceptionMapper faultExceptionMapper = new JAXWSFaultExceptionMapper(dataBindings, null);
        new JAXWSJavaInterfaceProcessor(registry).visitInterface(iface);
        new DataBindingJavaInterfaceProcessor(registry).visitInterface(iface);
        javaIC.setInterface(iface);
        WSDLInterfaceContract wsdlIC = BindingWSDLGenerator.createWSDLInterfaceContract(javaIC, false, new XSDModelResolver(null, null), dataBindings, wsdlFactory, xsdFactory, documentBuilderFactory, null);
        assertNotNull(wsdlIC);
        
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper interfaceContractMapper = utilities.getUtility(InterfaceContractMapper.class);

        // TODO: this call fails with an IncompatibleInterfaceContractExcetpion
        interfaceContractMapper.checkCompatibility(wsdlIC, javaIC, Compatibility.SUBSET, true, false);
    }

}

@Remotable
interface JaxbIface {
    String sayHello(JAXB_Class2 s);
}

