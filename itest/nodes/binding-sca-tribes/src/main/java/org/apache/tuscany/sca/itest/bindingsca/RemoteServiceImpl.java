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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.oasisopen.sca.annotation.AllowsPassByReference;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

/**
 * 
 */
@Service(Remote.class)
@Scope("COMPOSITE")
public class RemoteServiceImpl implements Remote {
    private Map<String, Customer> customers = new HashMap<String, Customer>();

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    @AllowsPassByReference
    public String getId(Customer customer) {
        customer.dump("Remote.getId()");
        return customer.getId();
    }

    public Customer getCustomer(String id) {
        Customer customer = customers.get(id);
        customer.dump("Remote.getCustomer()");
        return customer;
    }

    public Customer createCustomer(String id, String name) {
        Customer customer = new Customer(id, name);
        customer.dump("Remote.createCustomer()");
        customers.put(id, customer);
        return customer;
    }

}
