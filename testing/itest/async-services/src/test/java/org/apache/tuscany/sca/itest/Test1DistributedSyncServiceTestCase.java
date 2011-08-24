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

import java.net.URI;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasisopen.sca.NoSuchServiceException;

public class Test1DistributedSyncServiceTestCase {

    private static final URI DOMAIN = URI.create("uri:Test1DistributedTestCase?bind=127.0.0.1:8765");
    protected static Node clientNode, serviceNode;

    @BeforeClass
    public static void setUp() throws Exception {
        serviceNode = TuscanyRuntime.runComposite(DOMAIN, "test1SyncService.composite", "target/classes");
        clientNode = TuscanyRuntime.runComposite(DOMAIN, "test1Client.composite", "target/classes");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (clientNode != null) clientNode.stop();
        if (serviceNode != null) serviceNode.stop();
    }

    @Test
    public void testReference() throws NoSuchServiceException {
        Service1 test = clientNode.getService(Service1.class, "Client");
        assertEquals("Service1NonAsyncServerImpl.operation1 foo" + 
                     "Service1NonAsyncServerImpl.operation1 fooTypeOne" + 
                     "Service1NonAsyncServerImpl.operation1 fooTypeTwo", 
                     test.operation1("foo"));
    }

}
