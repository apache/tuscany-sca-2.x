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

import org.apache.activemq.broker.BrokerService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests using the JMS binding with an external JMS broker
 */
public class ExternalBrokerTestCase {

    private static SCADomain scaDomain;
    private BrokerService broker;

    @Before
    public void init() throws Exception {
        startBroker();
        scaDomain = SCADomain.newInstance("http://localhost", "/", "external/client.composite", "external/service.composite");
    }

    @Test
    public void testHelloWorldCreate() throws Exception {
        HelloWorldService helloWorldService = scaDomain.getService(HelloWorldService.class, "HelloWorldClient");
        assertEquals("jmsHello Petra", helloWorldService.sayHello("Petra"));
    }

    @After
    public void end() throws Exception {
        if (scaDomain != null) {
            scaDomain.close();
        }
        stopBroker();
    }

    protected void startBroker() throws Exception {
        broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();
    }
    protected void stopBroker() throws Exception {
        if (broker != null) {
            broker.stop();
        }
    }

}
