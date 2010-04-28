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

import org.apache.tuscany.sca.node.configuration.NodeConfiguration;
import org.oasisopen.sca.ServiceReference;




/**
 * Represents an SCA processing node.
 * A node is loaded with an SCA composites. It can start and stop that composite.
 *
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public interface Node {
    String DEFAULT_DOMAIN_URI = NodeConfiguration.DEFAULT_DOMAIN_URI;
    String DEFAULT_NODE_URI = NodeConfiguration.DEFAULT_NODE_URI;
    /**
     * Start the composite loaded in the node.
     * @return Return the node itself so that we can call NodeFactory.newInstance().createNode(...).start()
     */
    Node start();

    /**
     * Stop the composite loaded in the node.
     */
    void stop();

    /**
     * Cast a type-safe reference to a CallableReference. Converts a type-safe
     * reference to an equivalent CallableReference; if the target refers to a
     * service then a ServiceReference will be returned, if the target refers to
     * a callback then a CallableReference will be returned.
     *
     * @param target a reference proxy provided by the SCA runtime
     * @param <B> the Java type of the business interface for the reference
     * @param <R> the type of reference to be returned
     * @return a CallableReference equivalent for the proxy
     * @throws IllegalArgumentException if the supplied instance is not a
     *             reference supplied by the SCA runtime
     */
    <B, R extends ServiceReference<B>> R cast(B target) throws IllegalArgumentException;

    /**
     * Returns a proxy for a service provided by a component in the SCA domain.
     *
     * @param businessInterface the interface that will be used to invoke the
     *            service
     * @param serviceName the name of the service
     * @param <B> the Java type of the business interface for the service
     * @return an object that implements the business interface
     */
    <B> B getService(Class<B> businessInterface, String serviceName);

    /**
     * Returns a ServiceReference for a service provided by a component in the
     * SCA domain.
     *
     * @param businessInterface the interface that will be used to invoke the
     *            service
     * @param serviceName the name of the service
     * @param <B> the Java type of the business interface for the service
     * @return a ServiceReference for the designated service
     */
    <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String serviceName);    
}
