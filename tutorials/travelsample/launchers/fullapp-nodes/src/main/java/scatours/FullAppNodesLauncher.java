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

import java.io.IOException;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class FullAppNodesLauncher {

    public static void main(String[] args) throws Exception {
        Node nodeCreditcard =
            NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/creditcard");
        nodeCreditcard.start();

        Node nodePayment =
            NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/payment");
        nodePayment.start();

        Node nodeShoppingcart =
            NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/shoppingcart");
        nodeShoppingcart.start();

        Node nodeCurrency =
            NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/currency");
        nodeCurrency.start();

        Node nodePackagedtrip =
            NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/packagedtrip");
        nodePackagedtrip.start();

        Node nodeBespoketrip =
            NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/bespoketrip");
        nodeBespoketrip.start();

        Node nodeFrontend =
            NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/coordination");
        nodeFrontend.start();

        Node nodeUI = NodeFactory.getInstance().createNodeFromURL("http://localhost:9990/node-config/ui");
        nodeUI.start();

        System.out.println("Point your browser at - http://localhost:8080/scatours/ ");
        System.out.println("Nodes started - Press enter to shutdown.");

        try {
            System.in.read();
        } catch (IOException e) {
        }

        nodeCreditcard.stop();
        nodePayment.stop();
        nodeShoppingcart.stop();
        nodeCurrency.stop();
        nodePackagedtrip.stop();
        nodeBespoketrip.stop();
        nodeFrontend.stop();
        nodeUI.stop();
    }
}
