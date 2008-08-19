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

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * Bootstrap class for standalone SCA nodes.
 *  
 * @version $Rev$ $Date$
 */
public class NodeImplementationLauncherBootstrap {

    private SCANode node;

    /**
     * A node facade.
     */
    public static class NodeFacade implements SCANode, SCAClient {
        private ClassLoader threadContextClassLoader;
        private ClassLoader runtimeClassLoader;
        private SCANode delegate;
        
        private NodeFacade(SCANode delegate) {
            runtimeClassLoader = Thread.currentThread().getContextClassLoader();
            this.delegate = delegate;
        }
        
        public void start() {
            threadContextClassLoader = Thread.currentThread().getContextClassLoader();
            boolean started = false;
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                delegate.start();
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
                delegate.stop();
            } finally {
                Thread.currentThread().setContextClassLoader(threadContextClassLoader);
            }
        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            return (R)((SCAClient)delegate).cast(target);
        }

        public <B> B getService(Class<B> businessInterface, String serviceName) {
            return (B)((SCAClient)delegate).getService(businessInterface, serviceName);
        }

        public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName) {
            return (ServiceReference<B>)((SCAClient)delegate).getServiceReference(businessInterface, referenceName);
        }
    }
    
    /**
     * Bootstrap a new SCA node.
     * 
     * @param configurationURI
     */
    public NodeImplementationLauncherBootstrap(String configurationURI) throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        node = new NodeFacade(nodeFactory.createSCANodeFromURL(configurationURI));
    }

    /**
     * Bootstrap a new SCA node.
     * 
     * @param configurationURI
     * @param contributionClassLoader
     */
    public NodeImplementationLauncherBootstrap(String compositeURI, ClassLoader contributionClassLoader) throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        node = new NodeFacade(nodeFactory.createSCANodeFromClassLoader(compositeURI, contributionClassLoader));
    }

    /**
     * Bootstrap a new SCA node.
     * 
     * @param compositeURI
     * @param uris
     * @param locations
     */
    public NodeImplementationLauncherBootstrap(String compositeURI, String[] uris, String[] locations) throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        SCAContribution[] contributions = new SCAContribution[uris.length];
        for (int i = 0; i < uris.length; i++) {
            contributions[i] = new SCAContribution(uris[i], locations[i]);
        }
        node = new NodeFacade(nodeFactory.createSCANode(compositeURI, contributions));
    }

    /**
     * Bootstrap a new SCA node.
     * 
     * @param compositeURI
     * @param uris
     * @param locations
     */
    public NodeImplementationLauncherBootstrap(String compositeURI, String compositeContent, String[] uris, String[] locations) throws Exception {
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        SCAContribution[] contributions = new SCAContribution[uris.length];
        for (int i = 0; i < uris.length; i++) {
            contributions[i] = new SCAContribution(uris[i], locations[i]);
        }
        node = new NodeFacade(nodeFactory.createSCANode(compositeURI, compositeContent, contributions));
    }

    /**
     * Returns the SCA node.
     * 
     * @return
     */
    public SCANode getNode() {
        return node;
    }

}
