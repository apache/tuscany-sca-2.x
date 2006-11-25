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
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.idl.wsdl.WSDLServiceContract;
import org.easymock.EasyMock;

public class Axis2ReferenceTestCase extends TestCase {

    public void testInvokeService() throws Exception {
        Axis2Reference axis2Reference = createAxis2Reference("testWebAppName", "testServiceName");
        Operation operation = new Operation<Type>("sayHi", null, null, null, false, null, NO_CONVERSATION);
        TargetInvoker targetInvoker = axis2Reference.createTargetInvoker(null, operation);
        assertNotNull(targetInvoker);
    }

    public void testAsyncTargetInvoker() throws Exception {
        Axis2Reference axis2Reference = createAxis2Reference("testWebAppName", "testServiceName");
        //Create a mocked InboundWire, make the call of ServiceExtension.getInterface() returns a Class
        InboundWire inboundWire = EasyMock.createNiceMock(InboundWire.class);
        JavaServiceContract contract = new JavaServiceContract(Greeter.class);
        contract.setCallbackClass(GreetingCallback.class);
        Operation<Type> callbackOp =
            new Operation<Type>("sayHiCallback", null, null, null, true, null, NO_CONVERSATION);
        HashMap<String, Operation<Type>> callbackOps = new HashMap<String, Operation<Type>>();
        callbackOps.put("sayHiCallback", callbackOp);
        contract.setCallbackOperations(callbackOps);
        EasyMock.expect(inboundWire.getServiceContract()).andReturn(contract).anyTimes();
        EasyMock.replay(inboundWire);

        axis2Reference.setInboundWire(inboundWire);
        Operation operation = new Operation<Type>("sayHi", null, null, null, true, null, NO_CONVERSATION);
        TargetInvoker asyncTargetInvoker = axis2Reference.createAsyncTargetInvoker(null, operation);
        assertNotNull(asyncTargetInvoker);
    }

    private Axis2Reference createAxis2Reference(String webAppName, String serviceName) throws Exception {
        //Create WebServiceBinding
        String wsdlLocation = "/wsdl/hello_world_doc_lit.wsdl";
        URL url = getClass().getResource(wsdlLocation);
        assertNotNull("Could not find wsdl " + url.toString(), url);

        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        InputSource input = new InputSource(url.openStream());
        Definition wsdlDef = reader.readWSDL(url.toString(), input);
        Service wsdlService = wsdlDef.getService(new QName("http://objectweb.org/hello_world_soap_http",
            "SOAPService"));
        Port port = wsdlService.getPort("SoapPort");
        WebServiceBinding wsBinding = new WebServiceBinding(wsdlDef, port, "uri", "portURI", wsdlService);
        //Create a mocked WireService, make the call of ServiceExtension.getServiceInstance() returns a proxy instance.
        WireService wireService = EasyMock.createNiceMock(WireService.class);
        EasyMock.replay(wireService);
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        // TODO figure out what to do with the service contract
        ServiceContract<?> contract = new WSDLServiceContract();
        contract.setInterfaceClass(Greeter.class);
        return new Axis2Reference(serviceName,
            parent,
            wireService,
            wsBinding,
            contract);
    }
}
