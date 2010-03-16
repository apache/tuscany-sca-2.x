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
    private static SCANode node1;
    private static SCANode node2;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        node1 =
            SCANodeFactory.newInstance()
                .createSCANode("payment.composite",
                               new SCAContribution("payment", "./target/classes"));
        node2 =
            SCANodeFactory.newInstance()
                .createSCANode("creditcard.composite",        
                               new SCAContribution("creditcard",
                                                   "../../contributions/creditcard-payment-jaxb/target/classes"));
        node1.start();
        node2.start();
    }

    @Test
    public void testPayment() {
        SCAClient client = (SCAClient)node1;
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
        if (node1 != null) {
            node1.stop();
            node1 = null;
        }
        
        if (node2 != null) {
            node2.stop();
            node2 = null;
        }        
    }

}
