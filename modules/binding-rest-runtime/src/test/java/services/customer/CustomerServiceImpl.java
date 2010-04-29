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

package services.customer;

import java.util.HashMap;
import java.util.Map;

import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
public class CustomerServiceImpl implements CustomerService {
    private Map<String, Customer> customers = new HashMap<String, Customer>();

    @Init
    public void init() {
        customers.put("John", new Customer("John", "John", "john@domain.com"));
    }

    public Customer get() {
        return customers.values().iterator().next();
    }
    
    public void addCustomer(Customer customer) {
        customers.put(customer.getName(), customer);
    }
    
    public void updateCustomer(Customer customer) {
        if(customers.get(customer.getName()) != null) {
            customers.put(customer.getName(), customer);
        }
    }
}
