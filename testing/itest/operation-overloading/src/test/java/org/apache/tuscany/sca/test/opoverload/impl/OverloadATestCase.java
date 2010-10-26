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
package org.apache.tuscany.sca.test.opoverload.impl;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
public class OverloadATestCase {

    private static Node node;
    private static OverloadASourceTarget overloadA;

    /**
     * Method prefixed with 'test' is a test method where testing logic is written using various assert methods. This
     * test verifies the values compared are same as the values retrieved from the SCA runtime.
     */
    @Test
    public void testOperationAall() {
        String[] result = overloadA.operationAall();
        assertEquals(5, result.length);
        assertEquals(OverloadASourceTarget.opName, result[0]);
        assertEquals(OverloadASourceTarget.opName + 11, result[1]);
        assertEquals(OverloadASourceTarget.opName + "eleven", result[2]);
        assertEquals(OverloadASourceTarget.opName + 3 + "three", result[3]);
        assertEquals(OverloadASourceTarget.opName + "four" + 4, result[4]);
    }

    @Test
    public void testOperationAInt() {
        String result = overloadA.operationA(29);
        assertEquals(OverloadASourceTarget.opName + 29, result);
    }

    @Test
    public void testOperationAString() {
        String result = overloadA.operationA("rick:-)");
        assertEquals(OverloadASourceTarget.opName + "rick:-)", result);
    }

    @Test
    public void testOperationAIntString() {
        String result = overloadA.operationA(123, "Tuscany");
        assertEquals(OverloadASourceTarget.opName + 123 + "Tuscany", result);
    }

    @Test
    public void testOperationStringInt() {
        String result = overloadA.operationA("StringInt", 77);
        assertEquals(OverloadASourceTarget.opName + "StringInt" + 77, result);
    }

    /**
     * setUp() is a method in JUnit Frame Work which is executed before all others methods in the class extending
     * unit.framework.TestCase. So this method is used to create a test Embedded SCA node, to start the SCA node and
     * to get a reference to the contribution service
     */

    @BeforeClass
    public static void setUp() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("OperationOverload.composite");
        node = NodeFactory.newInstance().createNode("OperationOverload.composite", new Contribution("c1", location));
        node.start();
        overloadA = node.getService(OverloadASourceTarget.class, "OverloadASourceComponent");
    }

    /**
     * tearDown() is a method in JUnit Frame Work which is executed after all other methods in the class extending
     * unit.framework.TestCase. So this method is used to close the SCA node.
     */

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }
}
