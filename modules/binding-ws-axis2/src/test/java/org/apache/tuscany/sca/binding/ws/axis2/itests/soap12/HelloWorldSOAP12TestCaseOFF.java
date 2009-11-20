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

package org.apache.tuscany.sca.binding.ws.axis2.itests.soap12 ;

import junit.framework.TestCase;

import org.apache.tuscany.sca.binding.ws.axis2.itests.HelloWorld;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class HelloWorldSOAP12TestCaseOFF extends TestCase {

    private Node node;

    public void testHelloWorld() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClient");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }
    public void testHelloWorldSOAP() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientSOAP");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }
    public void testHelloWorldSOAP11() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientSOAP11");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }
    public void testHelloWorldSOAP12() throws Exception {
        HelloWorld helloWorld = node.getService(HelloWorld.class, "HelloWorldClientSOAP12");
        assertEquals("Hello petra", helloWorld.getGreetings("petra"));
    }

    @Override
    protected void setUp() throws Exception {
        String contribution = ContributionLocationHelper.getContributionLocation(getClass());
        node = NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/ws/axis2/itests/soap12/HelloWorldSOAP12.composite", new Contribution("test", contribution));
    }
   
    @Override
    protected void tearDown() throws Exception {
        node.stop();
        node.destroy();
    }

}
