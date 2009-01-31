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

package org.apache.tuscany.sca.itest;

import junit.framework.Assert;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.equinox.launcher.Contribution;
import org.apache.tuscany.sca.node.equinox.launcher.NodeLauncher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import calculator.CalculatorService;

public class MultipleDefinitionsTestCase {
    
    private static NodeLauncher launcher;
    private static Node node;     
    private static CalculatorService calculatorService;  
    
    public static void main(String[] args) throws Exception {
        setUpBeforeClass();
        tearDownAfterClass();
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        launcher = NodeLauncher.newInstance();
        node = launcher.createNode("Calculator.composite", 
                                   new Contribution("contrib2", "./src/test/resources/contrib2"),
                                   new Contribution("contrib1", "./src/test/resources/contrib1"),
                                   new Contribution("contrib3", "./target/classes"));
        node.start();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
            node.destroy();
        }
        if (launcher != null) {
            launcher.destroy();
        }
    }

    @Test
    public void testPolicies() {
        Assert.assertEquals(20.0, calculatorService.add(10, 10));
        Assert.assertEquals(100.0, calculatorService.multiply(10, 10));
        Assert.assertEquals(1.0, calculatorService.divide(10, 10));
        Assert.assertEquals(0.0, calculatorService.subtract(10, 10));
    }   
}
