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

package org.apache.tuscany.sca.client.impl;

import java.net.URI;
import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFinder;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClient;

public class SCAClientImpl implements SCAClient {

    public <T> T getService(Class<T> serviceInterface, String serviceName, URI domainURI)
        throws NoSuchServiceException, NoSuchDomainException {
        if (domainURI == null) {
            domainURI = URI.create(Node.DEFAULT_DOMAIN_URI);
        }
        List<Node> nodes = NodeFinder.getNodes(domainURI);
        if (nodes == null || nodes.size() < 1) {
            throw new NoSuchDomainException(domainURI.toString());
        }
        
        for (Node n : nodes) {
            if (n instanceof NodeImpl) {
                for ( Endpoint2 e : ((NodeImpl)n).getServiceEndpoints()) {
                    // TODO: implement more complete matching
                    if (serviceName.equals(e.getComponent().getName())) {
                       return n.getService(serviceInterface, serviceName);
                   }
                }
            }
        }

        throw new NoSuchServiceException(serviceName);
    }

}
