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
package itest.helloworld;


import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.domain.node.DomainNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.NoSuchServiceException;

public class HelloworldDomainNodeTestCase {

    DomainNode node;
    
    @Before
    public void setUp() throws Exception {
        node = new DomainNode("../../../samples/helloworld/target/sample-helloworld.zip");
    }

    @Test
    public void testHelloworld() throws NoSuchServiceException {
        assertEquals(1, node.getServiceNames().size());
        assertEquals("HelloworldComponent/Helloworld", node.getServiceNames().get(0));
        Helloworld service = node.getService(Helloworld.class, "HelloworldComponent");
        assertEquals("Hello petra", service.sayHello("petra"));
    }

    @After
    public void stop() {
        node.stop();
    }
}
