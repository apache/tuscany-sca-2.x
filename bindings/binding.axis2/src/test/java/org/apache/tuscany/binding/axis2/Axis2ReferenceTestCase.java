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
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.xml.sax.InputSource;

import org.apache.tuscany.idl.wsdl.WSDLServiceContract;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

public class Axis2ReferenceTestCase extends TestCase {

    public void testInvokeService() throws Exception {
//TODO put back in
if(true) return;        
        Axis2Reference axis2Reference = createAxis2Reference("testWebAppName", "testServiceName");
        Method operation = Greeter.class.getMethod("sayHi");
        TargetInvoker targetInvoker = axis2Reference.createTargetInvoker(operation);
        assertNotNull(targetInvoker);
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
        wsBinding.setWebAppName(webAppName);
        //Create a mocked WireService, make the call of ServiceExtension.getServiceInstance() returns a proxy instance.
        WireService wireService = EasyMock.createNiceMock(WireService.class);
        EasyMock.replay(wireService);
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        // TODO figure out what to do with the service contract
        ServiceContract contract = new WSDLServiceContract();
        contract.setInterfaceClass(Greeter.class);
        return new Axis2Reference(serviceName, parent, wireService, wsBinding, contract);
    }
}
