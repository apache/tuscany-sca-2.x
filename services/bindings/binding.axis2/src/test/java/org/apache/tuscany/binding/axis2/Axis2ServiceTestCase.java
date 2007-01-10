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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.easymock.EasyMock;

public class Axis2ServiceTestCase extends TestCase {

    public void testInvokeService() throws Exception {
        TestServletHost tomcatHost = new TestServletHost();
        Axis2ServiceBinding axis2Service = createAxis2Service("testServiceName", tomcatHost, false);
        axis2Service.start();

        if (true) return;
        Servlet servlet = tomcatHost.getMapping("testWebAppName/serviceBindings/testServiceName");
        assertNotNull(servlet);

        //Create mocked HttpRequest and HttpResponse object to test the Axis2Servlet
        //To be done:

    }

    public void testAsyncMessageReceiver() throws Exception {

        TestServletHost tomcatHost = new TestServletHost();
        Axis2ServiceBinding axis2Service = createAxis2Service("testServiceName", tomcatHost, true);
        axis2Service.start();
    }

    private Axis2ServiceBinding createAxis2Service(String serviceName, ServletHost tomcatHost, boolean callback)
        throws Exception {
        //Create WebServiceBindingDefinition
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
        WebServiceBindingDefinition wsBinding = new WebServiceBindingDefinition(wsdlDef, port, "uri", "portURI", wsdlService);

        //Create a mocked WireService, make the call of ServiceBindingExtension.getServiceInstance() returns a proxy instance.
        WireService wireService = EasyMock.createNiceMock(WireService.class);
        wireService.createProxy(EasyMock.isA(Class.class), EasyMock.isA(InboundWire.class));
        EasyMock.expectLastCall().andReturn(null);
        EasyMock.replay(wireService);

        //Create a mocked InboundWire, make the call of ServiceBindingExtension.getInterface() returns a Class
        InboundWire inboundWire = EasyMock.createNiceMock(InboundWire.class);
        JavaServiceContract contract = new JavaServiceContract(Greeter.class);
        Map<String, Operation<Type>> opMap = new HashMap<String, Operation<Type>>();
        for (Method m : Greeter.class.getMethods()) {
            opMap.put(m.getName(), new Operation<Type>(m.getName(), null, null, null));
        }
        contract.setOperations(opMap);
        EasyMock.expect(inboundWire.getServiceContract()).andReturn(contract).anyTimes();
        if (callback) {
            contract.setCallbackName("");
        }
        EasyMock.replay(inboundWire);

        OutboundWire outboundWire = EasyMock.createNiceMock(OutboundWire.class);
        Map<Operation<?>, OutboundInvocationChain> map = new HashMap<Operation<?>, OutboundInvocationChain>();
        EasyMock.expect(outboundWire.getInvocationChains()).andReturn(map).once();
        EasyMock.replay(outboundWire);

        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
        ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();
        Axis2ServiceBinding axis2Service =
            new Axis2ServiceBinding(serviceName,
                contract,
                null,
                null,
                wsBinding,
                tomcatHost,
                configurationContext, new WorkContextImpl());
        axis2Service.setInboundWire(inboundWire);
        axis2Service.setOutboundWire(outboundWire);

        return axis2Service;
    }

    protected class TestServletHost implements ServletHost {
        private Map<String, Servlet> mappings = new HashMap<String, Servlet>();

        public void registerMapping(String mapping, Servlet servlet) {
            mappings.put(mapping, servlet);
        }

        public Servlet unregisterMapping(String mapping) {
            return mappings.remove(mapping);
        }

        public Servlet getMapping(String mapping) {
            return mappings.get(mapping);
        }

        public boolean isMappingRegistered(String mapping) {
            return mappings.containsKey(mapping);
        }

    }

}
