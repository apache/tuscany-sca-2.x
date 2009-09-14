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

import org.apache.tuscany.sca.binding.jms.format.jmsdefault.helloworld.HelloWorldReference;
import org.apache.tuscany.sca.binding.jms.format.jmsdefault.helloworld.HelloWorldServiceImpl;
import org.apache.tuscany.sca.binding.jms.format.jmsdefault.helloworld.Person;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * This shows how to test the JMS binding using a simple HelloWorld application.
 */
public class FormatJMSDefaultTestCase {

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
        HelloWorldReference helloWorldService = ((SCAClient)node).getService(HelloWorldReference.class, "HelloWorldReferenceComponent");
        
        assertEquals("Hello Fred Bloggs Hello Fred Bloggs Hello Fred Bloggs Hello Fred Bloggs foo remote service exception, see nested exception foo remote service exception, see nested exception", helloWorldService.getGreetings("Fred Bloggs"));
        
        Person person = new Person();
        person.setFirstName("Fred");
        person.setLastName("Bloggs");
        assertEquals("Hello Fred Bloggs Hello Fred Bloggs Hello Fred Bloggs Hello Fred Bloggs", helloWorldService.getPersonGreetings(person));
        
        // this just makes sure that there are no exceptions thrown for this case
        helloWorldService.nullInVoidOut();
        Assert.assertEquals(4, HelloWorldServiceImpl.nullInVoidOutCalled);
    }

    @After
    public void end() {
        if (node != null) {
            node.stop();
        }
    }
}
