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

package scatours.payment.creditcard;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.tuscanyscatours.payment.creditcard.AuthorizeFault_Exception;
import com.tuscanyscatours.payment.creditcard.CreditCardDetailsType;
import com.tuscanyscatours.payment.creditcard.CreditCardPayment;
import com.tuscanyscatours.payment.creditcard.CreditCardTypeType;
import com.tuscanyscatours.payment.creditcard.ObjectFactory;
import com.tuscanyscatours.payment.creditcard.PayerType;

/**
 *
 */
public class CreditCardPaymentTestCase {
    private static SCANode node;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            node =
                SCANodeFactory.newInstance().createSCANode("creditcard.composite", new SCAContribution("creditcard", "./target/classes"));

            node.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testCreditCardPayment() {
        SCAClient client = (SCAClient)node;
        CreditCardPayment cc = client.getService(CreditCardPayment.class, "CreditCardPayment");

        ObjectFactory objectFactory = new ObjectFactory();
        CreditCardDetailsType ccDetails = objectFactory.createCreditCardDetailsType();
        ccDetails.setCreditCardType(CreditCardTypeType.fromValue("Visa"));
        PayerType ccOwner = objectFactory.createPayerType();
        ccOwner.setName("Fred");
        ccDetails.setCardOwner(ccOwner);

        try {
            System.out.println(cc.authorize(ccDetails, 100.00f));
        } catch (AuthorizeFault_Exception e) {
            System.err.println("Fault: " + e.getFaultInfo().getErrorCode());
        }
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

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
            node = null;
        }
    }

}
