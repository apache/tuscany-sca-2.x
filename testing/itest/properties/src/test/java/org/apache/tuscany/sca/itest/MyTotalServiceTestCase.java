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

import static org.junit.Assert.assertEquals;
import mysca.test.myservice.impl.MyService;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyTotalServiceTestCase {
    private static MyService service1;
    private static MyService service2;
    private static MyService service3;

    private static Node node;

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the local
     *       property variables defined in the implementation file with the expected values 'RTP' and '2006'
     */
    @Test
    public void testPropertyDefault() {
        assertEquals("RTP", service1.getLocation());
        assertEquals("2006", service1.getYear());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using service2 from the SCA runtime environment with the expected values 'Raleigh'
     *       and '2008'. This overrides the local property values
     */
    @Test
    public void testPropertyOverride() {
        assertEquals("Raleigh", service2.getLocation());
        assertEquals("2008", service2.getYear());
    }

    /**
     * Method annotated with
     * 
     * @Test is a test method where testing logic is written using various assert methods. This test verifies the
     *       property value obtained using service3 from the SCA runtime environment with the expected values 'Durham'
     *       and '2009'
     */
    @Test
    public void testPropertyNestedOverride() {
        assertEquals("Durham", service3.getLocation());
        assertEquals("2009", service3.getYear());
    }

    /**
     * Method annotated with
     * 
     * @BeforeClass is used for one time set Up, it executes before every tests. This method is used to create a test
     *              Embedded SCA node, to start the SCA node and to get a reference to the 'outerABService' service
     */
    @BeforeClass
    public static void setUp() throws Exception {

        String location = ContributionLocationHelper.getContributionLocation("Outer.composite");
        node = NodeFactory.newInstance().createNode("Outer.composite", new Contribution("c1", location));
        node.start();
        service1 = node.getService(MyService.class, "MyServiceComponent/MyService");
        service2 = node.getService(MyService.class, "MyServiceComponentNew/MyService");
        service3 = node.getService(MyService.class, "MySimpleServiceInRecursiveAnother");
    }

    /**
     * Method annotated with
     * 
     * @AfterClass is used for one time Tear Down, it executes after every tests. This method is used to close the
     *             node, close any previously opened connections etc
     */
    @AfterClass
    public static void tearDown() {
        node.stop();
    }
}
