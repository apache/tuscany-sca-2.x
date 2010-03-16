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

package com.tuscanyscatours.customer.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Requires;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.customer.Customer;
import com.tuscanyscatours.customer.CustomerNotFoundException;
import com.tuscanyscatours.customer.CustomerRegistry;
import com.tuscanyscatours.payment.creditcard.CreditCardDetailsType;
import com.tuscanyscatours.payment.creditcard.CreditCardTypeType;
import com.tuscanyscatours.payment.creditcard.ObjectFactory;
import com.tuscanyscatours.payment.creditcard.PayerType;

/**
 * An in-memory customer registry implementation
 */
@Service(CustomerRegistry.class)
@Scope("COMPOSITE")
@EagerInit
@Requires("{http://www.osoa.org/xmlns/sca/1.0}managedTransaction.global")
public class CustomerRegistryImpl implements CustomerRegistry {
    private volatile static int idGenerator = 0;
    private Map<String, Customer> customers = new HashMap<String, Customer>();

    @Init
    public void init() {
        // Load the customers
        ObjectFactory factory = new ObjectFactory();
        CreditCardDetailsType cc = factory.createCreditCardDetailsType();
        PayerType john = factory.createPayerType();
        john.setName("John Smith");
        cc.setCardOwner(john);
        cc.setCreditCardNumber("1111-2222-3333-4444");
        cc.setCreditCardType(CreditCardTypeType.VISA);
        cc.setCVV2("1234");
        cc.setExpMonth(1);
        cc.setExpYear(2012);
        createCustomer("John Smith", "john@xyz.com", cc);
    }

    @Destroy
    public void destroy() {
        // Save the customers
    }

    public Customer createCustomer(String name, String email, CreditCardDetailsType creditCard) {
        Customer customer = new Customer();
        customer.setId("c-" + idGenerator++);
        customer.setName(name);
        customer.setEmail(email);
        customer.setCreditCard(creditCard);
        customers.put(customer.getId(), customer);
        return customer;
    }

    public boolean deleteCustomer(String id) {
        return customers.remove(id) != null;
    }

    public Collection<Customer> getAllCustomers() {
        return customers.values();
    }

    public Customer getCustomer(String id) throws CustomerNotFoundException {
        Customer customer = customers.get(id);

        if (customer == null) {
            throw new CustomerNotFoundException("Customer " + id + " not found");
        }

        return customer;
    }

    public boolean updateCustomer(Customer customer) {
        Customer current = null;
        try {
            current = getCustomer(customer.getId());
        } catch (Exception ex) {
            return false;
        }

        current.setEmail(customer.getEmail());
        current.setName(customer.getName());
        current.setCreditCard(customer.getCreditCard());
        return true;
    }

}
