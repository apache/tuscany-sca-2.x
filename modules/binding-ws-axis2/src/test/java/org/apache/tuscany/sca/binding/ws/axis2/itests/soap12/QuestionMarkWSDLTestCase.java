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

package org.apache.tuscany.sca.binding.ws.axis2.itests.soap12;

import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis2.transport.http.server.HttpUtils;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * Test ?wsdl works and that the returned WSDL has the correct endpoint
 *
 * @version $Rev$ $Date$
 */
public class QuestionMarkWSDLTestCase extends TestCase {

    private static boolean newGenerator = true;
    private SCADomain domain;

    /**
     * Tests ?wsdl returns a soap 1.1 port by default
     */
    public void testSOAPDefault() throws Exception {
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL("http://localhost:8085/ep1?wsdl");
        assertNotNull(definition);
        Service service = definition.getService(new QName(
                              "http://itests.axis2.ws.binding.sca.tuscany.apache.org" + (newGenerator ? "/" : ""),
                              newGenerator ? "HelloWorldService" : "HelloWorld"));
        Port port = service.getPort(newGenerator ? "HelloWorldPort" : "HelloWorldSOAP11port_http");

        String endpoint = getSOAP11Endpoint(port);
        String ip = HttpUtils.getIpAddress();
        assertEquals("http://" + ip + ":8085/ep1", endpoint);
    }

    /**
     * Tests ?wsdl returns a soap 1.1 port when binding uses requires="soap.1_1"
     */
    public void testSOAP11Endpoint() throws Exception {
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL("http://localhost:8085/ep2?wsdl");
        assertNotNull(definition);
        Service service = definition.getService(new QName(
                              "http://itests.axis2.ws.binding.sca.tuscany.apache.org" + (newGenerator ? "/" : ""),
                              newGenerator ? "HelloWorldService" : "HelloWorld"));
        Port port = service.getPort(newGenerator ? "HelloWorldPort" : "HelloWorldSOAP11port_http");

        String endpoint = getSOAP11Endpoint(port);
        String ip = HttpUtils.getIpAddress();
        assertEquals("http://" + ip + ":8085/ep2", endpoint);
    }

    /**
     * Tests ?wsdl returns a soap 1.2 port when binding uses requires="soap.1_2"
     */
    public void testSOAP12Endpoint() throws Exception {
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL("http://localhost:8085/ep3?wsdl");
        assertNotNull(definition);
        Service service = definition.getService(new QName(
                              "http://itests.axis2.ws.binding.sca.tuscany.apache.org" + (newGenerator ? "/" : ""),
                              newGenerator ? "HelloWorldService" : "HelloWorld"));
        Port port = service.getPort(newGenerator ? "HelloWorldPort" : "HelloWorldSOAP12port_http");

        String endpoint = getSOAP12Endpoint(port);
        String ip = HttpUtils.getIpAddress();
        assertEquals("http://" + ip + ":8085/ep3", endpoint);
    }

    protected String getSOAP11Endpoint(Port port) {
        List wsdlPortExtensions = port.getExtensibilityElements();
        for (final Object extension : wsdlPortExtensions) {
            if (extension instanceof SOAPAddress) {
                return ((SOAPAddress) extension).getLocationURI();
            }
        }
        throw new RuntimeException("no SOAPAddress");
    }

    protected String getSOAP12Endpoint(Port port) {
        List wsdlPortExtensions = port.getExtensibilityElements();
        for (final Object extension : wsdlPortExtensions) {
            if (extension instanceof SOAP12Address) {
                return ((SOAP12Address) extension).getLocationURI();
            }
        }
        throw new RuntimeException("no SOAPAddress");
    }

    @Override
    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("org/apache/tuscany/sca/binding/ws/axis2/itests/soap12/questionmark-wsdl.composite");
    }

    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }

}
