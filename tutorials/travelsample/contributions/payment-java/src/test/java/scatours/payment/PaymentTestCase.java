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

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.tuscanyscatours.payment.Payment;

public class PaymentTestCase {
    private static SCANode node;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        node =
            SCANodeFactory.newInstance()
                .createSCANode(null,
                               new SCAContribution("payment", "./target/classes"),
                               new SCAContribution("creditcard",
                                                   "../../contributions/creditcard-payment-jaxb/target/classes"));
        node.start();
    }

    @Test
    public void testPayment() {
        SCAClient client = (SCAClient)node;
        Payment payment = client.getService(Payment.class, "Payment");

        System.out.println("\n\nSuccessful Payment - Status = \n\n" + payment.makePaymentMember("c-0", 100.00f));
        System.out.println("\n\nFailed Payment - Status = \n\n" + payment.makePaymentMember("c-1", 100.00f));
    }

    @Test
    @Ignore
    public void testWaitForInput() {
        System.out.println("Press a key to end");
        try {
            System.in.read();
        } catch (Exception ex) {
        }
        System.out.println("Shutting down");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
            node = null;
        }
    }

}
