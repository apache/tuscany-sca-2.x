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
package org.apache.tuscany.sca.binding.jms.format;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.binding.jms.format.jmsbytes.helloworld.HelloWorldReference;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * This shows how to test the JMS binding using a simple HelloWorld application.
 */
public class FormatJMSTextTestCase {

    private static Node node;

    @Before
    public void init() {
        node = NodeFactory.newInstance().createNode().start();
    }

    @Test
    public void testHelloWorldCreate() throws Exception {
        HelloWorldReference helloWorldService = node.getService(HelloWorldReference.class, "HelloWorldReferenceComponent");
        
        System.out.println(helloWorldService.getGreetings("Fred Bloggs"));
        assertEquals("Hello Fred Bloggs " +
                "foo remote service exception, see nested exception", 
                helloWorldService.getGreetings("Fred Bloggs"));
        
    }

    @After
    public void end() {
        if (node != null) {
            node.stop();
        }
    }
}
