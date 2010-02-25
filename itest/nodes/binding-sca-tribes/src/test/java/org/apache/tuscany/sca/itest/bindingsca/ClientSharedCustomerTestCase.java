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

package org.apache.tuscany.sca.itest.bindingsca;

import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Running the client api and service node with two different classloaders that share the Customer class
 */
public class ClientSharedCustomerTestCase {
    private static Client client;
    private static TestCaseRunner runner;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        runner = new TestCaseRunner(ServiceNode.class, Remote.class.getName(), RemoteServiceImpl.class.getName());
        runner.beforeClass();
        client = new SCAClientImpl(BindingSCATestCase.DOMAIN_URI);
        Thread.sleep(1000);
    }

    @Test
    public void testClient() throws Exception {
        BindingSCATestCase.runClient(client);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (runner != null) {
            runner.afterClass();
        }
        NodeFactory.getInstance().destroy();
    }
}
