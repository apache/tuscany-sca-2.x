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
package calculator.warning;

import java.io.File;

import junit.framework.TestCase;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;

/**
 * This shows how to test the Calculator service component.
 */
public class PropertyAttributeMustSupplyNullTestCase extends TestCase {

    private CalculatorService calculatorService;
    private Node node;

    @Override
    protected void setUp() throws Exception {
        NodeFactory nodeFactory = NodeFactory.newInstance();
        node = nodeFactory.createNode(new File("src/main/resources/PropertyAttribute/CalculatorNullMustSupply.composite").toURL().toString(),
        		                 new Contribution("TestContribution", 
        		                                     new File("src/main/resources/PropertyAttribute").toURL().toString()));
        node.start();
        calculatorService = ((Client)node).getService(CalculatorService.class, "CalculatorServiceComponent");
    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

    public void testCalculator() throws Exception {
        ExtensionPointRegistry registry = ((NodeImpl)node).getExtensionPointRegistry();
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        Monitor monitor = monitorFactory.createMonitor();
        Problem problem = monitor.getLastProblem();
        
        assertNotNull(problem);
        assertEquals("PropertyMustSupplyNull", problem.getMessageId());
 
    }
}
