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

package org.apache.tuscany.sca.implementation.node.launcher;

import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 * Bootstrap class for the SCA node daemon.
 *  
 * @version $Rev$ $Date$
 */
public class NodeImplementationDaemonBootstrap {
    private SCANode node;

    /**
     * A node wrappering an instance of a node daemon.
     */
    public static class NodeFacade implements SCANode {
        private ClassLoader threadContextClassLoader;
        private ClassLoader runtimeClassLoader;
        private SCANode daemon;
        
        private NodeFacade() {
            runtimeClassLoader = Thread.currentThread().getContextClassLoader();
        }
        
        public void start() {
            threadContextClassLoader = Thread.currentThread().getContextClassLoader();
            boolean started = false;
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                SCANodeFactory factory = SCANodeFactory.newInstance();
                daemon = factory.createSCANodeFromClassLoader("NodeDaemon.composite", threadContextClassLoader);
                started = true;
            } finally {
                if (!started) {
                    Thread.currentThread().setContextClassLoader(threadContextClassLoader);
                }
            }
        }
        
        public void stop() {
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                daemon.stop();
            } finally {
                Thread.currentThread().setContextClassLoader(threadContextClassLoader);
            }
        }
    }
    
    /**
     * Constructs a new daemon bootstrap.
     */
    public NodeImplementationDaemonBootstrap() throws Exception {
        node = new NodeFacade();
    }
    
    /**
     * Returns the node representing the daemon.
     * @return
     */
    public SCANode getNode() {
        return node;
    }

}
