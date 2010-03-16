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
package scatours;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case to run the test code in the introducing-client contribution
 * that tests components and services in the introducing-tours contribution
 * and the introducing-trips contribution.
 */
public class IntroducingTestCase {

    private SCANode node;

    @Before
    public void startServer() throws Exception {
        node =
            SCANodeFactory.newInstance()
                .createSCANode(null,
                               new SCAContribution("introducing-tours",
                                                   "../../contributions/introducing-tours/target/classes"),
                               new SCAContribution("introducing-trips",
                                                   "../../contributions/introducing-trips/target/classes"),
                               new SCAContribution("introducing-client",
                                                   "../../contributions/introducing-client/target/classes"));
        node.start();
    }

    @Test
    public void testClient() throws Exception {
        Runnable proxy = ((SCAClient)node).getService(Runnable.class, "TestClient/Runnable");
        proxy.run();
    }

    @After
    public void stopServer() throws Exception {
        node.stop();
    }
}
