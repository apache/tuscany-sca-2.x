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

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.equinox.launcher.Contribution;
import org.apache.tuscany.sca.node.equinox.launcher.ContributionLocationHelper;
import org.apache.tuscany.sca.node.equinox.launcher.NodeLauncher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This shows how to test the Calculator composition.
 */
public class CalculatorTestCase {

    private static NodeLauncher launcher;
    private static Node node;   
    
    public static void main(String[] args) throws Exception {
    	setUpBeforeClass();
    	tearDownAfterClass();
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        launcher = NodeLauncher.newInstance();
        String location = ContributionLocationHelper.getContributionLocation(CalculatorClient.class);
        node = launcher.createNode("Calculator.composite", new Contribution("test", location));
        System.out.println("SCA Node API ClassLoader: " + node.getClass().getClassLoader());
        node.start();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
        }
        if (launcher != null) {
            launcher.destroy();
        }
    }

    @Test
    public void testDummy() throws Exception {
    }
}
