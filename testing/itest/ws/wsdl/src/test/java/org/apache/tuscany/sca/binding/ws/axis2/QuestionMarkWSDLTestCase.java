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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/**
 * Test ?wsdl works and that the returned WSDL has the correct endpoint
 *
 * @version $Rev$ $Date$
 */
public class QuestionMarkWSDLTestCase extends TestCase {

    private Node node;

    /**
     * Tests ?wsdl works and returns the correct port endpoint from the WSDL
     */
    public void testWSDLPortEndpoint() throws Exception {
        InputStream inp = new URL("http://localhost:8085/services/HelloWorldWebService2?wsdl").openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inp));
        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL("http://localhost:8085/services/HelloWorldWebService2?wsdl");
        assertNotNull(definition);
        Service service = definition.getService(new QName("http://helloworld/HelloWorldService/HelloWorld",
                                                 "HelloWorldService"));        
        Port port = service.getPort("ep2SOAP11Port");

        String endpoint = getEndpoint(port);
        assertEquals("http://localhost:8085/services/HelloWorldWebService2", endpoint);
    }

    /**
     * Tests ?wsdl works and returns the correct port endpoint from binding.ws with a custom URI
     */
    public void testCustomEndpoint() throws Exception {
        InputStream inp = new URL("http://localhost:8085/foo/bar?wsdl").openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inp));
        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();

        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL("http://localhost:8085/foo/bar?wsdl");
        assertNotNull(definition);
        Service service = definition.getService(new QName("http://axis2.ws.binding.sca.tuscany.apache.org/",
                                                          "HelloWorldService"));
        Port port = service.getPort("ep1SOAP11Port");

        String endpoint = getEndpoint(port);
        // TODO - used to get the real host here but WSDL seems to have localhost in it atm?
        assertEquals("http://localhost:8085/foo/bar", endpoint);
    }

    protected String getEndpoint(Port port) {
        List wsdlPortExtensions = port.getExtensibilityElements();
        for (final Object extension : wsdlPortExtensions) {
            if (extension instanceof SOAPAddress) {
                return ((SOAPAddress) extension).getLocationURI();
            }
        }
        throw new RuntimeException("no SOAPAddress");
    }

    @Override
    protected void setUp() throws Exception {
        String contribution = "target/classes";
        node = NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/ws/axis2/questionmark-wsdl.composite", new Contribution("test", contribution));
        node.start();
    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
