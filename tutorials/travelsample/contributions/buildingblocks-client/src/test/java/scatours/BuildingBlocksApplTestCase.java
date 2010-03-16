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

import org.apache.activemq.broker.BrokerService;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Building Blocks Composite Application scenario
 */
public class BuildingBlocksApplTestCase {

    private BrokerService jmsBroker;
    private SCANode node1, node2;

    @Before
    public void startServer() throws Exception {
        jmsBroker = new BrokerService();
        jmsBroker.setPersistent(false);
        jmsBroker.setUseJmx(false);
        jmsBroker.addConnector("tcp://localhost:61619");

        node1 = SCANodeFactory.newInstance().createSCANode("tours-appl.composite",
                    new SCAContribution("introducing-trips", "../introducing-trips/target/classes"),
                    new SCAContribution("buildingblocks", "../buildingblocks/target/classes"));

        node2 = SCANodeFactory.newInstance().createSCANode("tours-appl-client.composite",
                    new SCAContribution("buildingblocks-client", "./target/classes"));

        jmsBroker.start();
        node1.start();
        node2.start();
    }

    @Test
    public void testAppl() {
        Runnable client = ((SCAClient)node2).getService(Runnable.class, "ApplClient/Runnable");
        client.run();
    }

    @After
    public void stopServer() throws Exception {
        if (node2 != null) {
            node2.stop();
        }
        if (node1 != null) {
            node1.stop();
        }
        if (jmsBroker != null) {
            jmsBroker.stop();
        }
    }
}
