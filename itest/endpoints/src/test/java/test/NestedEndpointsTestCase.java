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

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.tuscany.sca.http.jetty.JettyServer;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NestedEndpointsTestCase {

    private static Node node;

    @Test
    public void testJSONP1() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp />
        Utils.invokeJSONPEndpoint("http://localhost:8085/NestedComponent1/JSONPComponent1/HelloWorldService/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP2() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp2"/>
        Utils.invokeJSONPEndpoint("http://localhost:8085/NestedComponent1/JSONPComponent2/HelloWorldService/jsonp2/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP3() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp uri="jsonp3"/>
        Utils.invokeJSONPEndpoint("http://localhost:8085/NestedComponent1/JSONPComponent3/HelloWorldService/jsonp3/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP4() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp uri="/jsonp4"/>
        Utils.invokeJSONPEndpoint("http://localhost:8085/jsonp4/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP5() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp5a" uri="/jsonp5b"/>
        Utils.invokeJSONPEndpoint("http://localhost:8085/jsonp5b/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP6() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp6a" uri="jsonp6b"/>
        Utils.invokeJSONPEndpoint("http://localhost:8085/NestedComponent1/JSONPComponent6/HelloWorldService/jsonp6a/jsonp6b/sayHello?name=petra&callback=foo");
    }

    @Test
    public void testWS1() throws MalformedURLException, Exception {
        // <tuscany:binding.WS />
        Utils.invokeWSEndpoint("http://localhost:8085/NestedComponent1/WSComponent1/HelloWorldService");
    }
    @Test
    public void testWS2() throws MalformedURLException, Exception {
        // <tuscany:binding.WS name="WS2"/>
        Utils.invokeWSEndpoint("http://localhost:8085/NestedComponent1/WSComponent2/HelloWorldService/ws2");
    }
    @Test
    public void testWS3() throws MalformedURLException, Exception {
        // <tuscany:binding.WS uri="WS3"/>
        Utils.invokeWSEndpoint("http://localhost:8085/NestedComponent1/WSComponent3/HelloWorldService/ws3");
    }
    @Test
    public void testWS4() throws MalformedURLException, Exception {
        // <tuscany:binding.WS uri="/WS4"/>
        Utils.invokeWSEndpoint("http://localhost:8085/ws4");
    }
    @Test
    public void testWS5() throws MalformedURLException, Exception {
        // <tuscany:binding.WS name="WS5a" uri="/WS5b"/>
        Utils.invokeWSEndpoint("http://localhost:8085/ws5b");
    }
    @Test
    public void testWS6() throws Exception {
        // <tuscany:binding.ws name="ws6a" uri="ws6b"/>
        Utils.invokeWSEndpoint("http://localhost:8085/NestedComponent1/WSComponent6/HelloWorldService/ws6a/ws6b");
    }

    @BeforeClass
    public static void init() throws Exception {
        JettyServer.portDefault = 8085;
        node = NodeFactory.newInstance().createNode("nested.composite").start();
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

}
