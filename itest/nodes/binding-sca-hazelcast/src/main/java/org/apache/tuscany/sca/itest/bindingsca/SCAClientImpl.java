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

package org.apache.tuscany.sca.itest.bindingsca;

import java.net.URI;

import org.oasisopen.sca.client.SCAClientFactory;

/**
 * 
 */
public class SCAClientImpl implements Client {
    private Local local;
    private Remote remote;

    public SCAClientImpl(String domainURI) throws Exception {
        SCAClientFactory factory = SCAClientFactory.newInstance(URI.create(domainURI));
        local = factory.getService(Local.class, "LocalComponent/Local");
        remote = factory.getService(Remote.class, "RemoteComponent/Remote");
    }

    public String getName(String id) {
        Customer customer = remote.getCustomer(id);
        customer.dump("Client.getName()");
        return local.getName(customer);
    }

    public String create(String name) {
        String id = remote.generateId();
        Customer customer = remote.createCustomer(id, name);
        customer.dump("Client.create()");
        return remote.getId(customer);
    }

}
