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

package org.apache.tuscany.sca.binding.hazelcast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.sca.runtime.RuntimeEndpoint;


public class EndpointStash {

    // TODO a better way of sharing these endpoints than a static
    //   the issue is that the ServiceInvoker needs to get hold of the Endpoint
    //   for a URI. The ServiceInvoker runs in the context of a Hazelcast spawned 
    //   thread so can only get to Tuscany via a static. The Hazelcast endpoint
    //   registry does actually have these endpoints so perhaps an alternative could be
    //   to use NodeFactory.getNodeFactories to get at the Hazelcast endpoint registry.
    private static Map<String, RuntimeEndpoint> endpoints = new ConcurrentHashMap<String, RuntimeEndpoint>();
    
    public static void addEndpoint(RuntimeEndpoint endpoint) {
        endpoints.put(endpoint.getURI(), endpoint);
    }
    
    public static RuntimeEndpoint getEndpoint(String uri) {
        for (RuntimeEndpoint ep : endpoints.values()) {
            if (ep.matches(uri)) {
                return ep;
            }
        }
        return null;
    }

    public static void removeEndpoint(String uri) {
        endpoints.remove(uri);
    }
}
