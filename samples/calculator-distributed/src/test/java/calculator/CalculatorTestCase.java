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
package calculator;

import junit.framework.Assert;

import org.apache.activemq.broker.BrokerService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This shows how to test the Calculator service component in a 
 * distributed runtime
 */
public class CalculatorTestCase {

    private static BrokerService broker;
    private static CalculatorNode nodeA;
    private static SCADomain domainA;
    private static CalculatorNode nodeB;
    private static SCADomain domainB;
    private static CalculatorNode nodeC;
    private static SCADomain domainC;    
    private static CalculatorService calculatorService;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println("Setting but distributed nodes");
        
        // start the activemq broker
        broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.start();        
        
        // start the node that runs the 
        // calculator component
        nodeA = new CalculatorNode("domainA","nodeA");
        domainA = nodeA.startDomain();
        
        // start the node that runs the 
        // add component
        nodeB = new CalculatorNode("domainA","nodeB");
        domainB = nodeB.startDomain();
        
        // start the node that runs the 
        // subtract component
        nodeC = new CalculatorNode("domainA","nodeC");
        domainC = nodeC.startDomain();        
        
        calculatorService = domainA.getService(CalculatorService.class, "CalculatorServiceComponent");
   }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the domains
        nodeA.stopDomain();
        nodeB.stopDomain();
        nodeC.stopDomain();
        
        // stop the ActiveMQ broker
        broker.stop();
    }

    @Test
    public void testCalculator() throws Exception {
        
        // Calculate
        Assert.assertEquals(calculatorService.add(3, 2), 5.0);
        Assert.assertEquals(calculatorService.subtract(3, 2), 1.0);
        Assert.assertEquals(calculatorService.multiply(3, 2), 6.0);
        Assert.assertEquals(calculatorService.divide(3, 2), 1.5);

    }
}
