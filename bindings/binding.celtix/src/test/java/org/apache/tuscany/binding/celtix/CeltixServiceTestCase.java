/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this 
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under 
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.apache.tuscany.binding.celtix;

import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import junit.framework.TestCase;

import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireService;

import org.easymock.classextension.EasyMock;
import org.objectweb.celtix.Bus;
import org.objectweb.celtix.bindings.BindingManager;
import org.objectweb.celtix.bindings.DataBindingCallback;
import org.objectweb.celtix.bindings.ServerBindingEndpointCallback;
import org.objectweb.celtix.bindings.ServerDataBindingCallback;
import org.objectweb.celtix.bus.bindings.soap.SOAPBindingFactory;
import org.objectweb.celtix.bus.bindings.soap.SOAPServerBinding;
import org.objectweb.celtix.context.ObjectMessageContextImpl;
import org.objectweb.celtix.ws.addressing.EndpointReferenceType;

/**
 * @version $Rev$ $Date$
 */
public class CeltixServiceTestCase extends TestCase {

    public void testGetDataBindingCallback() throws Exception {
        CeltixService celtixService = createCeltixService();

        QName operationName = new QName("greetMe");
        ObjectMessageContextImpl ctx = new ObjectMessageContextImpl();
        ctx.setMessageObjects(new String[]{"Celtix"});
        ServerDataBindingCallback callback1 = celtixService.getDataBindingCallback(operationName,
                ctx, DataBindingCallback.Mode.PARTS);
        assertNotNull(callback1);

        callback1.invoke(ctx);
        Object rtn = (String)ctx.getReturn();
        assertEquals("Hello Celtix", rtn);

    }

    private CeltixService createCeltixService() throws Exception {
        //Make following call to return a mocked SOAPClientBinding:
        //bus.getBindingManager().getBindingFactory(bindingId).createClientBinding(reference)
        SOAPServerBinding serverBinding = EasyMock.createMock(SOAPServerBinding.class);
        serverBinding.activate();
        EasyMock.replay(serverBinding);

        ServerBindingEndpointCallback callback = EasyMock.createNiceMock(ServerBindingEndpointCallback.class);

        SOAPBindingFactory bindingFactory = EasyMock.createNiceMock(SOAPBindingFactory.class);
        bindingFactory.createServerBinding(EasyMock.isA(EndpointReferenceType.class),
                                           EasyMock.isA(ServerBindingEndpointCallback.class));
        EasyMock.expectLastCall().andReturn(serverBinding);
        EasyMock.replay(bindingFactory);

        BindingManager bindingManager = EasyMock.createNiceMock(BindingManager.class);
        String bindingId = "http://schemas.xmlsoap.org/wsdl/soap/";
        bindingManager.getBindingFactory(bindingId);
        EasyMock.expectLastCall().andReturn(bindingFactory);

        Bus bus = EasyMock.createNiceMock(Bus.class);
        bus.getBindingManager();
        EasyMock.expectLastCall().andReturn(bindingManager);
        EasyMock.replay(bindingManager);
        EasyMock.replay(bus);

        //Create WSDL Definition
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

        //Create mocked InboundWire, for ServiceExtension.getInterface()
        InboundWire<Greeter> inboundWire = EasyMock.createNiceMock(InboundWire.class);
        inboundWire.getBusinessInterface();
        EasyMock.expectLastCall().andReturn(Greeter.class).anyTimes();
        EasyMock.replay(inboundWire);

        //Create mocked WireService, for ServiceExtension.getServiceInstance()
        WireService wireService = EasyMock.createNiceMock(WireService.class);
        wireService.createProxy(EasyMock.isA(InboundWire.class));
        EasyMock.expectLastCall().andReturn(new GreeterImpl()).anyTimes();
        EasyMock.replay(wireService);

        CeltixService celtixService = new CeltixService("name", Greeter.class, null, wireService, wsBinding, bus);
        //Not sure how InboundWire is set to CeltixService, is the following way correct?
        celtixService.setInboundWire(inboundWire);
        celtixService.start();

        return celtixService;
    }

}
