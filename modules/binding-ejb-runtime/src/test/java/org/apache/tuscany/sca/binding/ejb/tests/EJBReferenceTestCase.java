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
package org.apache.tuscany.sca.binding.ejb.tests;

import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

import account.Customer;

/**
 * Invokes the component which calls the reference using the EJB binding 
 *
 * @version $Rev$ $Date$
 */
public class EJBReferenceTestCase extends TestCase {
    private static final int MOCK_PORT = 8085;
    private Node node;

    @Override
    protected void setUp() throws Exception {
        System.setProperty("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
        System.setProperty("java.naming.provider.url", "ejbd://localhost:" + MOCK_PORT);
        System.setProperty("managed", "false");

        String contribution = ContributionLocationHelper.getContributionLocation(EJBReferenceTestCase.class);
        node = NodeFactory.newInstance().createNode("account/account.composite", new Contribution("account", contribution));
        node.start();

        // To capture the network traffic for the MockServer, uncomment the next line
        // new Thread(new SocketTracer(MOCK_PORT, OPENEJB_PORT)).start();

        // Start the mock server to simulate the remote EJB
        new Thread(new MockServer(MOCK_PORT)).start();
        
        // Wait enough for the server to be started
        Thread.sleep(500);
    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

    public void testCalculator() throws Exception {
        Customer customer = node.getService(Customer.class, "CustomerComponent");
        // This is one of the customer numbers in bank application running on Geronimo
        String accountNo = "1234567890";
        Double balance = customer.depositAmount(accountNo, new Double(100));
        // System.out.println("Balance amount for account " + accountNo + " is $" + balance);
        assertEquals(1200.0, balance);
    }
}
