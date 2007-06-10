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

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;
import static org.junit.Assert.*;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import org.apache.tuscany.sca.binding.jms.HelloWorldService;

import org.apache.activemq.broker.BrokerService;


/**
 * This shows how to test the JMS binding using a simple HelloWorld application.
 */
public class JMSTestCase {

    private static BrokerService        broker;
    private static HelloWorldService    helloWorldService;
    private static SCADomain            scaDomain; 

    /*
     * This test is a bit strange for two reasons
     * 1/ starting and stopping the broker repeatedly for multiple tests 
     *    sometimes leads to orphaned lock files being left on disc
     * 2/ it doesn't seem possible to load a single composite file
     *    at the moment so I've put all the components for the test
     *    in one. This makes it very difficult to test for failure 
     *    cases at this level.
     * For these reasons setup happens at a class level at the moment
     */
    
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        // start the activemq broker
        broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.start();
        
        // start the SCA runtime
        scaDomain = SCADomain.newInstance("JMSBindingTest.composite");
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        scaDomain.close();
        
        broker.stop();
        broker.waitUntilStopped();
    }
/*
    @Test
    public void testHelloWorldMinimal() throws Exception {
        helloWorldService = scaDomain.getService(HelloWorldService.class, "HelloWorldMinimalClientComponent");
        assertEquals("Hello Fred", helloWorldService.sayHello("Fred"));
    }
*/
    @Test
    public void testHelloWorldCreate() throws Exception {
        helloWorldService = scaDomain.getService(HelloWorldService.class, "HelloWorldCreateClientComponent");
        assertEquals("ServiceA says Hello Fred-A ServiceB says Hello Fred-B", helloWorldService.sayHello("Fred"));
    }
    


}
