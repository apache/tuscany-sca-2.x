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

import java.net.URL;

import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.oasisopen.sca.CallableReference;
import org.oasisopen.sca.ServiceReference;

/**
 * Bootstrap class for standalone SCA nodes.
 *
 * @version $Rev$ $Date$
 */
public class NodeImplementationLauncherBootstrap {

    private Node node;

    /**
     * A node facade.
     */
    public static class NodeFacade implements Node, Client {
        private ClassLoader threadContextClassLoader;
        private ClassLoader runtimeClassLoader;
        private Node delegate;

        private NodeFacade(Node delegate) {
            runtimeClassLoader = Thread.currentThread().getContextClassLoader();
            this.delegate = delegate;
        }

        public Node start() {
            threadContextClassLoader = Thread.currentThread().getContextClassLoader();
            boolean started = false;
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                delegate.start();
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
                delegate.stop();
            } finally {
                Thread.currentThread().setContextClassLoader(threadContextClassLoader);
            }
        }

        public void destroy() {
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                delegate.destroy();
            } finally {
                Thread.currentThread().setContextClassLoader(threadContextClassLoader);
            }
        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            return (R)((Client)delegate).cast(target);
        }

        public <B> B getService(Class<B> businessInterface, String serviceName) {
            return (B)((Client)delegate).getService(businessInterface, serviceName);
        }

        public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
            return (ServiceReference<B>)((Client)delegate).getServiceReference(businessInterface, referenceName);
        }
    }

    /**
     * Bootstrap a new SCA node.
     *
     * @param configurationURI
     */
    public NodeImplementationLauncherBootstrap(String configurationURI) throws Exception {
        NodeFactory nodeFactory = NodeFactory.newInstance();
        node = new NodeFacade(nodeFactory.createNode(new URL(configurationURI)));
    }

    /**
     * Bootstrap a new SCA node.
     *
     * @param compositeURI
     * @param uris
     * @param locations
     */
    public NodeImplementationLauncherBootstrap(String compositeURI, String[] uris, String[] locations) throws Exception {
        NodeFactory nodeFactory = NodeFactory.newInstance();
        Contribution[] contributions = new Contribution[uris.length];
        for (int i = 0; i < uris.length; i++) {
            contributions[i] = new Contribution(uris[i], locations[i]);
        }
        node = new NodeFacade(nodeFactory.createNode(compositeURI, contributions));
    }

    /**
     * Bootstrap a new SCA node.
     *
     * @param compositeURI
     * @param uris
     * @param locations
     */
    public NodeImplementationLauncherBootstrap(String compositeURI, String compositeContent, String[] uris, String[] locations) throws Exception {
        NodeFactory nodeFactory = NodeFactory.newInstance();
        Contribution[] contributions = new Contribution[uris.length];
        for (int i = 0; i < uris.length; i++) {
            contributions[i] = new Contribution(uris[i], locations[i]);
        }
        node = new NodeFacade(nodeFactory.createNode(compositeURI, compositeContent, contributions));
    }

    /**
     * Returns the SCA node.
     *
     * @return
     */
    public Node getNode() {
        return node;
    }

}
