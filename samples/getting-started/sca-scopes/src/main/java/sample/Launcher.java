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
package sample;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

public class Launcher {
	
    public static void main(String[] args) {
        Node node = startRuntime();         
        Client client = node.getService(Client.class, "Client");
        client.run();                                           
        stopRuntime(node);
    }

    /**
     * Starts a Tuscany node with the predefined contribution.
     *
     * @return the running node
     */
    private static Node startRuntime() {
        String location = ContributionLocationHelper.getContributionLocation("scopes.composite");
        Node node = NodeFactory.newInstance().createNode("scopes.composite", new Contribution("c1", location));
        node.start();
        return node;
    }

    /**
     * Stops a Tuscany node.
     *
     * @param node the node to stop
     */
    private static void stopRuntime(Node node) {
        node.stop();
    }

}
