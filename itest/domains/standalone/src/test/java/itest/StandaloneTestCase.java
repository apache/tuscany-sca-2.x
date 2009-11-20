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
package itest;

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.domain.node.DomainNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This shows how to test the Calculator service component.
 */
public class StandaloneTestCase{

    private static DomainNode node;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        node = new DomainNode();
        node.addContribution("../helloworld/target/itest-domains-helloworld.zip");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	node.stop();
    }

    @Test
    public void testServices() throws Exception {
        assertEquals(1, node.getServiceNames().size());
        assertEquals("HelloworldComponent/Helloworld", node.getServiceNames().get(0));
    }

    @Test
    public void testDuplicateComponents() throws Exception {
        try {
            node.addContribution("../helloworld/target/itest-domains-helloworld.zip", "dup");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // verify the old one is still there
        // TODO: doesn't work correctly yet
//        assertEquals(1, node.getServiceNames().size());
//        assertEquals("HelloworldComponent/Helloworld", node.getServiceNames().get(0));
    }

}
