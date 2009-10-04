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
package test.sca.tests;

import static org.junit.Assert.assertEquals;
import mysca.test.myservice.MySimpleTotalService;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests to make sure that autowiring and recusive composite work together
 *
 */
@Ignore("TUSCANY-3296")
public class MultiLevelTestCase {
    private static Node node1;
    private static Node node2;
    private static Node node3;
    private static MySimpleTotalService myService1;
    private static MySimpleTotalService myService2;
    private static MySimpleTotalService myService3;

    @BeforeClass
    public static void setUp() throws Exception {
        String location = ContributionLocationHelper.getContributionLocation("TotalService1Auto.composite");
        Contribution contribution = new Contribution("c1", location);
        try {
            node1 = NodeFactory.newInstance().createNode("TotalService1Auto.composite", contribution);
        } catch (Exception e) {
            e.printStackTrace();
        }
        node2 = NodeFactory.newInstance().createNode("TotalService2Auto.composite", contribution);
        node3 = NodeFactory.newInstance().createNode("TotalService3Auto.composite", contribution);
        
        node1.start();
        node2.start();
        node3.start();

        myService1 = node1.getService(MySimpleTotalService.class, "TotalServiceComponentLevel1Auto");
        myService2 = node2.getService(MySimpleTotalService.class, "TotalServiceInRecursive2Auto/MyServiceLevel1Auto");
        myService3 = node3.getService(MySimpleTotalService.class, "TotalServiceInRecursive3Auto/MyServiceLevel2Auto");
    }

    @Test
    public void testLevel1() {
        assertEquals("Level 1", myService1.getLocation());
        assertEquals("2001", myService1.getYear());
    }

    @Test
    public void testLevel2() {
        assertEquals("Default 2", myService2.getLocation());
        assertEquals("1992", myService2.getYear());
    }

    @Test
    public void testLevel3() {
        assertEquals("Default 3", myService3.getLocation());
        assertEquals("1993", myService3.getYear());
    }
    
    @AfterClass
    public static void tearDown() {
        node1.stop();
        node2.stop();
        node3.stop();
    }
}
