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

package org.apache.tuscany.sca.itest.policy.transaction;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import customer.Customer;
import customer.CustomerRegistry;

public class CustomerTestCase {
    private static Node node;

    @BeforeClass
    public static void init() throws Exception {
        try {
            String location = ContributionLocationHelper.getContributionLocation("Customer.composite");
            node = NodeFactory.newInstance().createNode("Customer.composite",new Contribution("c1", location));
            node.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (node != null) {
            node.stop();
        }
    }

    @Test
    public void testCustomer() {
        CustomerRegistry customerRegistry = node.getService(CustomerRegistry.class, "CustomerRegistryComponent");
        Customer customer = customerRegistry.find("001");
        Assert.assertEquals(customer.getName(), "John Smith");
    }

    public static void main(String[] args) throws Exception {
        CustomerTestCase.init();
        CustomerTestCase tester = new CustomerTestCase();
        tester.testCustomer();
        CustomerTestCase.destroy();
    }
}
