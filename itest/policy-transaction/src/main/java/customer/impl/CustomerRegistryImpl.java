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

package customer.impl;

import java.util.HashMap;
import java.util.Map;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

import customer.Customer;
import customer.CustomerRegistry;

/**
 * 
 */
@Service(CustomerRegistry.class)
@Scope("COMPOSITE")
public class CustomerRegistryImpl implements CustomerRegistry {
    private Map<String, Customer> customers = new HashMap<String, Customer>();
    
    @Init
    public void init() {
        Customer c1 = new Customer();
        c1.setId("001");
        c1.setName("John Smith");
        customers.put(c1.getId(), c1);
        
        Customer c2 = new Customer();
        c2.setId("002");
        c2.setName("Jane Smith");
        customers.put(c2.getId(), c2);
    }
    
    public Customer find(String id) {
        return customers.get(id);
    }
    
    @Destroy
    public void destroy() {
        customers.clear();
    }

}
