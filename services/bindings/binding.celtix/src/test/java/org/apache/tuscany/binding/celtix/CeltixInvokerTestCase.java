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
import org.apache.tuscany.binding.celtix.io.SCADataBindingCallback;
import org.easymock.classextension.EasyMock;
import org.objectweb.celtix.Bus;
import org.objectweb.celtix.bindings.BindingManager;
import org.objectweb.celtix.bus.bindings.soap.SOAPBindingFactory;
import org.objectweb.celtix.bus.bindings.soap.SOAPClientBinding;
import org.objectweb.celtix.context.ObjectMessageContextImpl;
import org.objectweb.celtix.ws.addressing.EndpointReferenceType;

/**
 * @version $Rev$ $Date$
 */
public class CeltixInvokerTestCase extends TestCase {

    public void testProcessingInputWithoutInOut() throws Exception {
        String wsdlLocation = "/wsdl/hello_world_doc_lit.wsdl";
        String operationName = "greetMe";
        ObjectMessageContextImpl inputCtx = new ObjectMessageContextImpl();
        CeltixInvoker invoker = createCeltixInvoker(wsdlLocation,
            operationName, inputCtx);

        Object[] args = new Object[1];
        args[0] = new String("hello");
        invoker.invokeTarget(args, CeltixInvoker.NONE);

        // Check the input object after processing is correct
        // Should be no change for input if only IN parameters involved
        Object[] myrtn = (Object[]) inputCtx.getMessageObjects();

        assertEquals("hello", myrtn[0]);
    }

    public void testProcessingInputWithInOut() throws Exception {
        String wsdlLocation = "/wsdl/hello_world_doc_lit_inout.wsdl";
        String operationName = "greetMe";
        ObjectMessageContextImpl inputCtx = new ObjectMessageContextImpl();
        CeltixInvoker invoker = createCeltixInvoker(wsdlLocation,
            operationName, inputCtx);

        Object[] args = new Object[1];
        String inputvalue = new String("hello");
        args[0] = inputvalue;
        Object result = invoker.invokeTarget(args, CeltixInvoker.NONE);

        // Check the input object after processing is correct
        // input should be wrapped as Holder type if it is INOUT parameter
        Object[] myrtn = (Object[]) inputCtx.getMessageObjects();

        //FIXME: this does not work for the wrapped doc/lit case due to a bug in Celtix
        //assertTrue("input is not Holder type", myrtn[0] instanceof Holder);
    }

    // NOTE: For convenience this method presumes the soap service name is
    // SOAPService and port name is SoapPort
    private CeltixInvoker createCeltixInvoker(String wsdlLocation,
                                              String operationName,
                                              ObjectMessageContextImpl inputCtx)
        throws Exception {

        // Make following call to return a mocked SOAPClientBinding:
        // bus.getBindingManager().getBindingFactory(bindingId).createClientBinding(reference)
        SOAPClientBinding clientBinding = EasyMock
            .createMock(SOAPClientBinding.class);
        clientBinding.createObjectContext();
        EasyMock.expectLastCall().andReturn(inputCtx);
        clientBinding.invoke(EasyMock.isA(ObjectMessageContextImpl.class),
            EasyMock.isA(SCADataBindingCallback.class));
        EasyMock.expectLastCall().andReturn(new ObjectMessageContextImpl());
        EasyMock.replay(clientBinding);

        SOAPBindingFactory bindingFactory = EasyMock.createNiceMock(SOAPBindingFactory.class);
        bindingFactory.createClientBinding(EasyMock.isA(EndpointReferenceType.class));
        EasyMock.expectLastCall().andReturn(clientBinding);
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

        // Create WSDL Definition
        URL url = getClass().getResource(wsdlLocation);
        assertNotNull("Could not find wsdl " + url.toString(), url);

        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        InputSource input = new InputSource(url.openStream());
        Definition wsdlDef = reader.readWSDL(url.toString(), input);
        QName qName = new QName("http://objectweb.org/hello_world_soap_http", "SOAPService");
        Service wsdlService = wsdlDef.getService(qName);
        Port port = wsdlService.getPort("SoapPort");

        return new CeltixInvoker(operationName, bus, port, wsdlService, wsdlDef, null);
    }

}
