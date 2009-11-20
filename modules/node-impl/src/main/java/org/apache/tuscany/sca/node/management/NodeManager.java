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

package org.apache.tuscany.sca.node.management;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.impl.NodeImpl;

/**
 * MBean implementation for the node
 */
public class NodeManager implements NodeManagerMBean {
    private NodeImpl node;
    private ObjectName name;

    public NodeManager(NodeImpl node) {
        this.node = node;
        try {
            this.name = getName(node);
        } catch (MalformedObjectNameException e) {
            // Ignore
        }
    }

    public String getURI() {
        return node.getURI();
    }

    public String getDomainURI() {
        return node.getConfiguration().getDomainURI();
    }

    public ObjectName getName() {
        return name;
    }

    private static ObjectName getName(NodeImpl node) throws MalformedObjectNameException {
        String name =
            Node.class.getPackage().getName() + ":type="
                + Node.class.getSimpleName()
                + ",uri="
                + ObjectName.quote(node.getURI());
        return ObjectName.getInstance(name);

    }
}
