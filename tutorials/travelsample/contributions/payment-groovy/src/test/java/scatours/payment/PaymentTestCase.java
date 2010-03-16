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

package scatours.payment;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tuscanyscatours.payment.Payment;

/**
 * 
 */
public class PaymentTestCase {

    private static Node paymentNode;
    private static Node creditCardNode;
    private static Node emailGatewayNode;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        creditCardNode =
            NodeFactory.getInstance()
                .createNode("creditcard.composite",
                               new Contribution("creditcard", "../creditcard-payment-jaxb/target/classes"));

        creditCardNode.start();

        emailGatewayNode =
            NodeFactory.getInstance().createNode("emailgateway.composite",
                                                       new Contribution("emailgateway",
                                                                           "../emailgateway/target/classes"),
                                                       new Contribution("emailgateway-test",
                                                                           "../emailgateway/target/test-classes"));

        emailGatewayNode.start();

        paymentNode =
            NodeFactory.getInstance().createNode(
                                                       new Contribution("payment-groovy", "./target/classes"),
                                                       new Contribution("payment-groovy-test",
                                                                           "./target/test-classes"));

        paymentNode.start();
    }

    @Test
    public void testPayment() {
        Node client = (Node)paymentNode;
        Payment payment = client.getService(Payment.class, "PaymentClient");
        System.out.println("Result = " + payment.makePaymentMember("Fred", 100.00f));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        paymentNode.stop();
        creditCardNode.stop();
        emailGatewayNode.stop();
    }

}
