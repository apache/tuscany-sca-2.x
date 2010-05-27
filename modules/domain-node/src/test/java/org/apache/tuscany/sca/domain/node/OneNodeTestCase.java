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

package org.apache.tuscany.sca.domain.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import itest.nodes.Helloworld;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 * This shows how to test the Calculator service component.
 */
@Ignore("TUSCANY-3391")
public class OneNodeTestCase{

    private static DomainNode domain;
    private static String serviceContributionUri;
    
    @Before
    public void setUpBeforeClass() throws Exception {
        domain = new DomainNode();
        serviceContributionUri = domain.addContribution("target/test-classes/itest-nodes-helloworld-service-2.0-M5-SNAPSHOT.jar");
        domain.addContribution("target/test-classes/itest-nodes-helloworld-client-2.0-M5-SNAPSHOT.jar");
    }

    @Test
    public void testService() throws Exception {
        Helloworld service = SCAClientFactory.newInstance(URI.create("vm://defaultDomain")).getService(Helloworld.class, "HelloworldService");
        assertNotNull(service);
        assertEquals("Hello Petra", service.sayHello("Petra"));
    }

    @Test
    public void testClient() throws Exception {
        Helloworld client = SCAClientFactory.newInstance(URI.create("vm://defaultDomain")).getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
    }
    
    @Test
    public void testRemovingServiceContribution() throws Exception {
        domain.removeContribution(serviceContributionUri);
        try {
            SCAClientFactory.newInstance(URI.create("vm://defaultDomain")).getService(Helloworld.class, "HelloworldService");
        // FIXME: should this be NoSuchServiceException or ServiceNotFoundException?
        } catch (NoSuchServiceException e) {
            // expected
        }

        Helloworld client = SCAClientFactory.newInstance(URI.create("vm://defaultDomain")).getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        try {
            assertEquals("Hi Hello Petra", client.sayHello("Petra"));
            fail();
        } catch (Exception e) {
            // FIXME: this gives an NPE, would be better to be something like ServiceNotFoundException
            // expected
        }
    }

    @Test
    public void testStoppingDomainNode() throws Exception {
        domain.stop();
        try {
            SCAClientFactory.newInstance(URI.create("vm://defaultDomain")).getService(Helloworld.class, "HelloworldClient");
            fail();
        } catch (NoSuchDomainException e) {
            // expected
        }
    }

    @After
    public void tearDownAfterClass() throws Exception {
        if (domain != null) {
            domain.stop();
        }
    }
}
