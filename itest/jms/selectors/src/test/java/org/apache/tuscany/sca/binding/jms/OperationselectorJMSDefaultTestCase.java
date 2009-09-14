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
package org.apache.tuscany.sca.binding.jms;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.binding.jms.operationselector.jmsdefault.HelloWorldReference;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * This shows how to test the JMS binding using a simple HelloWorld application.
 */
public class OperationselectorJMSDefaultTestCase {

    private static SCANode node;

    @Before
    public void init() {
        SCANodeFactory factory = SCANodeFactory.newInstance();
        node = factory.createSCANode("jmsdefault/helloworld.composite", 
                                     new SCAContribution("test", "./target/classes"));

        node.start();
    }

    @Test
    public void testHelloWorldCreate() throws Exception {
        HelloWorldReference helloWorldReference = ((SCAClient)node).getService(HelloWorldReference.class, "HelloWorldReferenceComponent");
        
        assertEquals("Hello One Fred Bloggs Hello Two Fred Bloggs Hello Three Fred Bloggs Hello Four Fred Bloggs", helloWorldReference.getGreetings("Fred Bloggs"));
        
    }

    @After
    public void end() {
        if (node != null) {
            node.stop();
        }
    }
}
