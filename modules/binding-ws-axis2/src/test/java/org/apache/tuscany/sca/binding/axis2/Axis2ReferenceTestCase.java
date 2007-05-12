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
package org.apache.tuscany.sca.binding.axis2;

import junit.framework.TestCase;

public class Axis2ReferenceTestCase extends TestCase {

    public void testInvokeService() throws Exception {
//        Axis2ReferenceBinding axis2Reference = createAxis2Reference("testWebAppName", "testServiceName");
//        ServiceContract contract = new JavaServiceContract();
//        Operation operation = new Operation<Type>("sayHi", null, null, null, false, null, NO_CONVERSATION);
//        TargetInvoker targetInvoker = axis2Reference.createTargetInvoker(contract, operation);
//        assertNotNull(targetInvoker);
//        assertFalse(targetInvoker instanceof Axis2AsyncTargetInvoker);
    }

    public void testAsyncTargetInvoker() throws Exception {
//        Axis2ReferenceBinding axis2Reference = createAxis2Reference("testWebAppName", "testServiceName");
//        //Create a mocked InboundWire, make the call of ServiceBindingExtension.getInterface() returns a Class
//        Wire inboundWire = EasyMock.createNiceMock(Wire.class);
//        JavaServiceContract contract = new JavaServiceContract(Greeter.class);
//        contract.setCallbackName("");
//        contract.setCallbackClass(GreetingCallback.class);
//        Operation<Type> callbackOp =
//            new Operation<Type>("sayHiCallback", null, null, null, true, null, NO_CONVERSATION);
//        HashMap<String, Operation<Type>> callbackOps = new HashMap<String, Operation<Type>>();
//        callbackOps.put("sayHiCallback", callbackOp);
//        contract.setCallbackOperations(callbackOps);
//        EasyMock.expect(inboundWire.getTargetContract()).andReturn(contract).anyTimes();
//        EasyMock.replay(inboundWire);
//
//        axis2Reference.setWire(inboundWire);
//        Operation operation = new Operation<Type>("sayHi", null, null, null, true, null, NO_CONVERSATION);
//        TargetInvoker asyncTargetInvoker = axis2Reference.createTargetInvoker(contract, operation);
//        assertNotNull(asyncTargetInvoker);
//        assertTrue(asyncTargetInvoker instanceof Axis2AsyncTargetInvoker);
    }

//    @SuppressWarnings("unchecked")
//    private Axis2ReferenceBinding createAxis2Reference(String webAppName, String serviceName) throws Exception {
//        //Create WebServiceBindingDefinition
//        String wsdlLocation = "/wsdl/hello_world_doc_lit.wsdl";
//        URL url = getClass().getResource(wsdlLocation);
//        assertNotNull("Could not find wsdl " + url.toString(), url);
//
//        WSDLFactory factory = WSDLFactory.newInstance();
//        WSDLReader reader = factory.newWSDLReader();
//        reader.setFeature("javax.wsdl.verbose", false);
//        InputSource input = new InputSource(url.openStream());
//        Definition wsdlDef = reader.readWSDL(url.toString(), input);
//        Service wsdlService = wsdlDef.getService(new QName("http://objectweb.org/hello_world_soap_http",
//            "SOAPService"));
//        Port port = wsdlService.getPort("SoapPort");
//        WebServiceBindingDefinition wsBinding =
//            new WebServiceBindingDefinition(wsdlDef, port, "uri", "portURI", wsdlService);
//        //Create a mocked WireService, make the call of ServiceBindingExtension.getServiceInstance() returns a proxy instance.
//        // TODO figure out what to do with the service contract
//        ServiceContract<?> contract = new WSDLServiceContract();
//        contract.setInterfaceClass(Greeter.class);
//        return new Axis2ReferenceBinding(URI.create(serviceName),
//            wsBinding,
//            contract,
//            null,
//            null);
//    }
}
