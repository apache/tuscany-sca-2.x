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

import static scatours.launcher.LauncherUtil.locate;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class InteractionLauncher {

    public static void main(String[] args) throws Exception {
        Node node1 =
            NodeFactory.getInstance().createNode("client.composite",
                                                       locate("common"),
                                                       locate("currency"),
                                                       locate("calendar"),
                                                       locate("shoppingcart"),
                                                       locate("interaction-client"));

        Node node2 =
            NodeFactory.getInstance().createNode("service.composite",
                                                       locate("common"),
                                                       locate("hotel"),
                                                       locate("flight"),
                                                       locate("interaction-service-remote"));

        node2.start();
        node1.start();

        Runnable localInteraction = ((Node)node1).getService(Runnable.class, "InteractionLocalClient/Runnable");
        localInteraction.run();

        Runnable remoteInteraction = ((Node)node1).getService(Runnable.class, "InteractionRemoteClient/Runnable");
        remoteInteraction.run();

        Runnable requestResponseInteraction =
            ((Node)node1).getService(Runnable.class, "InteractionRequestResponseClient/Runnable");
        requestResponseInteraction.run();

        Runnable onewayCallbackInteraction =
            ((Node)node1).getService(Runnable.class, "InteractionOneWayCallbackClient/Runnable");
        onewayCallbackInteraction.run();

        Runnable conversationalInteraction =
            ((Node)node1).getService(Runnable.class, "InteractionConversationClient/Runnable");
        conversationalInteraction.run();

        node1.stop();
        node2.stop();
    }
}
