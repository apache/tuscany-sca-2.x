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
package itest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.Test;

/**
 * Tests if a non-default domain name gets used as part of the service endpoint URI
 */
public class EndpointURITestCase{

    @Test
    public void testDefault() throws Exception {
        Node node = TuscanyRuntime.runComposite(null, "src/test/resources/helloworld-ws.jar");
        try {

            assertEquals(1, node.getInstalledContributionURIs().size());
            
            assertNotNull(getWsdlUrl("http://localhost:8080/HelloworldComponent/Helloworld"));
        
        } finally {
            node.stop();
        }
    }

    @Test
    public void testDomainRoot() throws Exception {
        Node node = TuscanyRuntime.runComposite(URI.create("myDomain"), null, "src/test/resources/helloworld-ws.jar");
        try {

            assertEquals(1, node.getInstalledContributionURIs().size());
            
            assertNotNull(getWsdlUrl("http://localhost:8080/myDomain/HelloworldComponent/Helloworld"));
        
        } finally {
            node.stop();
        }
    }

    String getWsdlUrl(String uri) throws Exception {

        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL(uri + "?wsdl");

        Service service = (Service)definition.getServices().values().iterator().next();
        Port port = (Port)service.getPorts().values().iterator().next();

        String wsdlURL = getEndpoint(port);
        System.out.println("WSDL URL: "+ wsdlURL);
        return wsdlURL;
    }

    String getEndpoint(Port port) {
        List<?> wsdlPortExtensions = port.getExtensibilityElements();
        for (final Object extension : wsdlPortExtensions) {
            if (extension instanceof SOAPAddress) {
                return ((SOAPAddress) extension).getLocationURI();
            }
        }
        throw new RuntimeException("no SOAPAddress");
    }

}
