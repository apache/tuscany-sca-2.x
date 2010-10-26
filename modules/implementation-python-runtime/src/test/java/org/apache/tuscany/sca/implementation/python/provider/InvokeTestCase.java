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

package org.apache.tuscany.sca.implementation.python.provider;

import static org.apache.tuscany.sca.node.ContributionLocationHelper.getContributionLocation;
import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.jabsorb.client.Client;
import org.jabsorb.client.Session;
import org.jabsorb.client.TransportRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the Python implementation provider.
 * 
 * @version $Rev$ $Date$
 */
public class InvokeTestCase {
    static Node node;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            final String loc = getContributionLocation("domain-test.composite");
            node = NodeFactory.newInstance().createNode("domain-test.composite", new Contribution("c", loc));
            node.start();
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        node.stop();
    }

    @Test
    public void testService() throws Exception {
        final Session s = TransportRegistry.i().createSession("http://localhost:8080/python");
        final Client c = new Client(s);
        final Object px = c.openProxy("", EchoTest.class);
        final Object r = c.invoke(px, EchoTest.class.getMethod("echo", String.class, String.class), new Object[] {"Hey", "There"});
        c.closeProxy(px);
        s.close();
        assertEquals("Hey There", r);
    }

    @Test
    public void testReference() throws Exception {
        final Session s = TransportRegistry.i().createSession("http://localhost:8080/client");
        final Client c = new Client(s);
        final Object px = c.openProxy("", EchoTest.class);
        final Object r = c.invoke(px, EchoTest.class.getMethod("echo", String.class, String.class), new Object[] {"Hey", "There"});
        c.closeProxy(px);
        s.close();
        assertEquals("Hey There", r);
    }

    @Test
    public void testLocal() throws Exception {
        final Session s = TransportRegistry.i().createSession("http://localhost:8080/java-client");
        final Client c = new Client(s);
        final Object px = c.openProxy("", EchoTest.class);
        final Object r = c.invoke(px, EchoTest.class.getMethod("echo", String.class, String.class), new Object[] {"Hey", "There"});
        c.closeProxy(px);
        s.close();
        assertEquals("Hey There", r);
    }

}
