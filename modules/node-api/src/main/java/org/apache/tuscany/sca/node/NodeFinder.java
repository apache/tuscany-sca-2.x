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

package org.apache.tuscany.sca.node;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeFinder {

    private static Map<URI, List<Node>> nodes = new HashMap<URI, List<Node>>();

    public static void addNode(URI domainName, Node node) {
        List<Node> domainNodes = nodes.get(domainName);
        if (domainNodes == null) {
            domainNodes = new ArrayList<Node>();
        }
        domainNodes.add(node);
        nodes.put(domainName, domainNodes);
    }

    public static Node removeNode(Node node) {
        for (List<Node> domainNodes : nodes.values()) {
            if (domainNodes.contains(node)) {
                domainNodes.remove(node);
                if (domainNodes.size() < 1) {
                    nodes.remove(domainNodes);
                }
                return node;
            }
        }
        return null;
    }

    public static List<Node> getNodes(URI domainURI) {
        return nodes.get(domainURI);
    }

}
