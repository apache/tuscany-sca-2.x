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
        invokeJSONPEndpoint("http://IBM-B4ADCA311EA:8085/JSONPComponent2/HelloWorldService/jsonp2/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP3() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp uri="jsonp3"/>
        invokeJSONPEndpoint("http://IBM-B4ADCA311EA:8085/JSONPComponent3/HelloWorldService/jsonp3/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP4() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp uri="/jsonp4"/>
        invokeJSONPEndpoint("http://IBM-B4ADCA311EA:8085/jsonp4/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP5() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp5a" uri="/jsonp5b"/>
        invokeJSONPEndpoint("http://IBM-B4ADCA311EA:8085/jsonp5b/sayHello?name=petra&callback=foo");
    }
    @Test
    public void testJSONP6() throws MalformedURLException, IOException {
        // <tuscany:binding.jsonp name="jsonp6a" uri="jsonp6b"/>
        invokeJSONPEndpoint("http://IBM-B4ADCA311EA:8085/JSONPComponent6/HelloWorldService/jsonp6a/jsonp6b/sayHello?name=petra&callback=foo");
    }

    private void invokeJSONPEndpoint(String s) throws MalformedURLException, IOException {
        URL url = new URL(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String response = br.readLine();
        Assert.assertEquals("foo(\"Hello petra\");", response);
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
