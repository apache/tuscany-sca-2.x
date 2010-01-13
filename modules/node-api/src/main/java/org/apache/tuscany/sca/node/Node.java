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

import java.util.List;

import org.apache.tuscany.sca.node.configuration.NodeConfiguration;




/**
 * Represents an SCA processing node.
 * A node is loaded with an SCA composites. It can start and stop that composite.
 *
 * @version $Rev$ $Date$
 */
public interface Node extends Client {
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
     * Destroy the node.
     */
    void destroy();

    public List<String> getServiceNames();
}
