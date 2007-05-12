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

public class Axis2ServiceTestCase extends TestCase {

    public void testInvokeService() throws Exception {
//        TestServletHost tomcatHost = new TestServletHost();
//        Axis2ServiceBinding axis2Service = createAxis2Service("testServiceName", tomcatHost, false);
//        axis2Service.start();
//
//        if (true) return;
//        Servlet servlet = tomcatHost.getMapping("testWebAppName/serviceBindings/testServiceName");
//        assertNotNull(servlet);
//
//        //Create mocked HttpRequest and HttpResponse object to test the Axis2Servlet
//        //To be done:

    }

    public void testAsyncMessageReceiver() throws Exception {

//        TestServletHost tomcatHost = new TestServletHost();
//        Axis2ServiceBinding axis2Service = createAxis2Service("testServiceName", tomcatHost, true);
//        axis2Service.start();
    }

//    @SuppressWarnings("unchecked")
//    private Axis2ServiceBinding createAxis2Service(String serviceName, ServletHost tomcatHost, boolean callback)
//        throws Exception {
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
//        WebServiceBindingDefinition wsBinding = new WebServiceBindingDefinition(wsdlDef, port, "uri", "portURI", wsdlService);
//
//        //Create a mocked WireService, make the call of ServiceBindingExtension.getServiceInstance() returns a proxy instance.
////        WireService wireService = EasyMock.createNiceMock(WireService.class);
////        wireService.createProxy(EasyMock.isA(Class.class), EasyMock.isA(Wire.class));
////        EasyMock.expectLastCall().andReturn(null);
////        EasyMock.replay(wireService);
//
//        //Create a mocked InboundWire, make the call of ServiceBindingExtension.getInterface() returns a Class
//        Wire inboundWire = EasyMock.createNiceMock(Wire.class);
//        JavaServiceContract contract = new JavaServiceContract(Greeter.class);
//        Map<String, Operation<Type>> opMap = new HashMap<String, Operation<Type>>();
//        for (Method m : Greeter.class.getMethods()) {
//            opMap.put(m.getName(), new Operation<Type>(m.getName(), null, null, null));
//        }
//        contract.setOperations(opMap);
//        EasyMock.expect(inboundWire.getTargetContract()).andReturn(contract).anyTimes();
//        if (callback) {
//            contract.setCallbackName("");
//        }
//        EasyMock.replay(inboundWire);
//
//        Wire outboundWire = EasyMock.createNiceMock(Wire.class);
//        Map<Operation<?>, InvocationChain> map = new HashMap<Operation<?>, InvocationChain>();
//        EasyMock.expect(outboundWire.getInvocationChains()).andReturn(map).once();
//        EasyMock.replay(outboundWire);
//
//        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
//        ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();
//        Axis2ServiceBinding axis2Service =
//            new Axis2ServiceBinding(URI.create(serviceName),
//                contract,
//                null,
//                wsBinding,
//                tomcatHost,
//                configurationContext, null);
//        axis2Service.setWire(inboundWire);
////        axis2Service.setOutboundWire(outboundWire);
//
//        return axis2Service;
//    }

}
