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

import org.apache.tuscany.sca.node.Node2Exception;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.SCANode2Factory;
import org.apache.tuscany.sca.node.SCANode2Factory.SCAContribution;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * Bootstrap class for standalone SCA nodes.
 *  
 * @version $Rev$ $Date$
 */
public class NodeImplementationLauncherBootstrap {

    private SCANode2 node;

    /**
     * A node facade.
     */
    public static class NodeFacade implements SCANode2, SCAClient {
        private ClassLoader runtimeClassLoader;
        private SCANode2 delegate;
        
        private NodeFacade(SCANode2 delegate) {
            runtimeClassLoader = Thread.currentThread().getContextClassLoader();
            this.delegate = delegate;
        }
        
        public void start() throws Node2Exception {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                delegate.start();
            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
        
        public void stop() throws Node2Exception {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(runtimeClassLoader);
                delegate.stop();
            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
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
        SCANode2Factory nodeFactory = SCANode2Factory.newInstance();
        node = new NodeFacade(nodeFactory.createSCANode(configurationURI));
    }

    /**
     * Bootstrap a new SCA node.
     * 
     * @param compositeURI
     * @param uris
     * @param locations
     */
    public NodeImplementationLauncherBootstrap(String compositeURI, String[] uris, String[] locations) throws Exception {
        SCANode2Factory nodeFactory = SCANode2Factory.newInstance();
        SCAContribution[] contributions = new SCAContribution[uris.length];
        for (int i = 0; i < uris.length; i++) {
            contributions[i] = new SCAContribution(uris[i], locations[i]);
        }
        node = new NodeFacade(nodeFactory.createSCANode(compositeURI, contributions));
    }

    /**
     * Returns the SCA node.
     * 
     * @return
     */
    public SCANode2 getNode() {
        return node;
    }

}
