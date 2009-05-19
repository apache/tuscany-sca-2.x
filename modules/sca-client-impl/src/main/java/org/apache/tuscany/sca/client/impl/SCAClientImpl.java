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

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFinder;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClient;

public class SCAClientImpl implements SCAClient {

    public <T> T getService(Class<T> serviceInterface, String serviceName, URI domainURI)
        throws NoSuchServiceException, NoSuchDomainException {
        if (domainURI == null) {
            domainURI = URI.create(Node.DEFAULT_DOMAIN_URI);
        }
        Node node = NodeFinder.getNode(domainURI);
        if (node == null) {
            throw new NoSuchDomainException(domainURI.toString());
        }

        T service = node.getService(serviceInterface, serviceName);
        if (service == null) {
            throw new NoSuchServiceException(serviceName);
        }

        return service;
    }

}
