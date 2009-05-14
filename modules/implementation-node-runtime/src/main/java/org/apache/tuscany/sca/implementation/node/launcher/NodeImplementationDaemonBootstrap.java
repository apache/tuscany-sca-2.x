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

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.oasisopen.sca.CallableReference;
import org.oasisopen.sca.ServiceReference;

/**
 * Bootstrap class for the SCA node daemon.
 *
 * @version $Rev$ $Date$
 */
public class NodeImplementationDaemonBootstrap {
    private Node node;

    /**
     * A node wrappering an instance of a node daemon.
     */
    public static class NodeFacade implements Node {
        private ClassLoader threadContextClassLoader;
        private ClassLoader runtimeClassLoader;
        private Node daemon;

        private NodeFacade() {
            runtimeClassLoader = Thread.currentThread().getContextClassLoader();
        }

        public Node start() {
            threadContextClassLoader = Thread.currentThread().getContextClassLoader();
            boolean started = false;
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                NodeFactory factory = NodeFactory.newInstance();
                String contribution = ContributionLocationHelper.getContributionLocation(getClass());
                daemon = factory.createNode("NodeDaemon.composite", new Contribution("node-runtime", contribution));
                started = true;
                return this;
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

        public void destroy() {
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                daemon.destroy();
            } finally {
                Thread.currentThread().setContextClassLoader(threadContextClassLoader);
            }
        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public <B> B getService(Class<B> businessInterface, String serviceName) {
            throw new UnsupportedOperationException();
        }

        public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String serviceName) {
            throw new UnsupportedOperationException();
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
    public Node getNode() {
        return node;
    }

}
