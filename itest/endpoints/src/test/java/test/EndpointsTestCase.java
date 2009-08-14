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
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import junit.framework.Assert;

import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EndpointsTestCase {

    private static Node node;

    @Test
    public void testJSONP1() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp />
        invokeJSONPEndpoint("http://localhost:8085/JSONPComponent1/HelloWorldService/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP2() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp2"/>
        invokeJSONPEndpoint("http://localhost:8085/JSONPComponent2/HelloWorldService/jsonp2/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP3() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp uri="jsonp3"/>
        invokeJSONPEndpoint("http://localhost:8085/JSONPComponent3/HelloWorldService/jsonp3/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP4() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp uri="/jsonp4"/>
        invokeJSONPEndpoint("http://localhost:8085/jsonp4/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP5() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp5a" uri="/jsonp5b"/>
        invokeJSONPEndpoint("http://localhost:8085/jsonp5b/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP6() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp6a" uri="jsonp6b"/>
        invokeJSONPEndpoint("http://localhost:8085/JSONPComponent6/HelloWorldService/jsonp6a/jsonp6b/sayHello?name=petra&callback=foo");
    }

    @Test
    public void testWS1() throws MalformedURLException, Exception {
        // <tuscany:binding.WS />
        invokeWSEndpoint("http://localhost:8085/WSComponent1/HelloWorldService");
    }
    @Test
    public void testWS2() throws MalformedURLException, Exception {
        // <tuscany:binding.WS name="WS2"/>
        invokeWSEndpoint("http://localhost:8085/WSComponent2/HelloWorldService/ws2");
    }
    @Test
    public void testWS3() throws MalformedURLException, Exception {
        // <tuscany:binding.WS uri="WS3"/>
        invokeWSEndpoint("http://localhost:8085/WSComponent3/HelloWorldService/ws3");
    }
    @Test
    public void testWS4() throws MalformedURLException, Exception {
        // <tuscany:binding.WS uri="/WS4"/>
        invokeWSEndpoint("http://localhost:8085/ws4");
    }
    @Test
    public void testWS5() throws MalformedURLException, Exception {
        // <tuscany:binding.WS name="WS5a" uri="/WS5b"/>
        invokeWSEndpoint("http://localhost:8085/ws5b");
    }
    @Test
    public void testWS6() throws Exception {
        // <tuscany:binding.ws name="ws6a" uri="ws6b"/>
        invokeWSEndpoint("http://localhost:8085/WSComponent6/HelloWorldService/ws6a/ws6b");
    }

    private void invokeJSONPEndpoint(String s) throws MalformedURLException, IOException {
        URL url = new URL(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String response = br.readLine();
        Assert.assertEquals("foo(\"Hello petra\");", response);
    }

    public void invokeWSEndpoint(String endpoint) throws Exception {
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL(endpoint + "?wsdl");
        Assert.assertNotNull(definition);
        Service service = (Service)definition.getServices().values().iterator().next();
        Port port = (Port)service.getPorts().values().iterator().next();

        Assert.assertEquals(new URL(endpoint).getPath(), new URL(getEndpoint(port)).getPath());
    }
    
    protected String getEndpoint(Port port) {
        List<?> wsdlPortExtensions = port.getExtensibilityElements();
        for (final Object extension : wsdlPortExtensions) {
            if (extension instanceof SOAPAddress) {
                return ((SOAPAddress) extension).getLocationURI();
            }
        }
        throw new RuntimeException("no SOAPAddress");
    }
    
    @BeforeClass
    public static void init() throws Exception {
        JettyServer.portDefault = 8085;
        node = NodeFactory.newInstance().createNode("helloworld.composite").start();
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

}
