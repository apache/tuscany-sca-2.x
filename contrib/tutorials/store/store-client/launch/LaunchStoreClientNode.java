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

package launch;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.launcher.NodeLauncher;

import client.Shopper;

public class LaunchStoreClientNode {

    public static void main(String[] args) throws Exception {
        NodeLauncher nodeLauncher = NodeLauncher.newInstance();
        SCANode storeClientNode = nodeLauncher.createNodeFromURL("http://localhost:9990/node-config/StoreClientNode");
        storeClientNode.start();
        SCAClient client = (SCAClient)storeClientNode;
        
        Shopper shopper = client.getService(Shopper.class, "StoreClient");
        
        String total = shopper.shop("Orange", 5);
        System.out.println("Total: " + total);
        
        storeClientNode.stop();
    }
}
