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

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oasisopen.sca.NoSuchServiceException;

public class Test1TestCase {

    protected TuscanyRuntime runtime;
    protected Node serviceNode;
    protected Node clientNode;

    @Before
    public void setUp() throws Exception {
        serviceNode = TuscanyRuntime.runComposite("service.composite", "target/classes");
        clientNode = TuscanyRuntime.runComposite("client.composite", "target/classes");
    }

    @After
    public void tearDown() throws Exception {
        serviceNode.stop();
        clientNode.stop();
    }

    @Test
    public void testReference() throws NoSuchServiceException {
        Helloworld client = clientNode.getService(Helloworld.class, "HelloworldClient");
        assertEquals("client: Hello Ariana", client.sayHello("Ariana"));
    }

}
