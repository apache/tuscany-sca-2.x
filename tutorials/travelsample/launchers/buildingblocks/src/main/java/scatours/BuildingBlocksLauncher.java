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
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class BuildingBlocksLauncher {

    public static void main(String[] args) throws Exception {
        runImpl();
        runImplInclude();
        runAppl();
    }

    private static void runImpl() throws Exception {
        Node node =
            NodeFactory.getInstance().createNode("tours-impl-client.composite",
                                                       locate("buildingblocks"),
                                                       locate("buildingblocks-client"));

        node.start();

        Runnable client = ((Node)node).getService(Runnable.class, "ToursClient/Runnable");
        client.run();

        node.stop();
    }

    private static void runImplInclude() throws Exception {
        Node node =
            NodeFactory.getInstance().createNode("tours-impl-include-client.composite",
                                                       locate("introducing-trips"),
                                                       locate("buildingblocks"),
                                                       locate("buildingblocks-client"));

        node.start();

        Runnable client = ((Node)node).getService(Runnable.class, "ToursClient/Runnable");
        client.run();

        node.stop();
    }

    private static void runAppl() throws Exception {
        final BrokerService jmsBroker = new BrokerService();
        jmsBroker.setPersistent(false);
        jmsBroker.setUseJmx(false);
        jmsBroker.addConnector("tcp://localhost:61619");

        Node node1 =
            NodeFactory.getInstance().createNode("tours-appl.composite",
                                                       locate("introducing-trips"),
                                                       locate("buildingblocks"));

        Node node2 =
            NodeFactory.getInstance().createNode("tours-appl-client.composite",
                                                       locate("buildingblocks-client"));

        jmsBroker.start();
        node1.start();
        node2.start();

        Runnable client = ((Node)node2).getService(Runnable.class, "ApplClient/Runnable");
        client.run();

        node2.stop();
        node1.stop();
        jmsBroker.stop();
    }
}
