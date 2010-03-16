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
import org.junit.Test;

import com.tuscanyscatours.payment.creditcard.CreditCardDetailsType;
import com.tuscanyscatours.payment.creditcard.CreditCardPayment;
import com.tuscanyscatours.payment.creditcard.CreditCardPaymentFactory;
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
        SCANodeFactory factory = SCANodeFactory.newInstance();
        node = factory.createSCANode(null, new SCAContribution("creditcard-sdo", "./target/classes"));
        node.start();
    }

    @Test
    public void testCreditCardPayment() {
        SCAClient client = (SCAClient)node;
        CreditCardPayment cc = client.getService(CreditCardPayment.class, "CreditCardPayment");

        CreditCardPaymentFactory factory = CreditCardPaymentFactory.INSTANCE;
        CreditCardDetailsType ccDetails = factory.createCreditCardDetailsType();
        ccDetails.setCreditCardType("Visa");
        ccDetails.setCreditCardNumber("1111-2222-3333-4444");
        ccDetails.setExpMonth(9);
        ccDetails.setExpYear(2010);
        PayerType ccOwner = factory.createPayerType();
        ccOwner.setName("Fred");
        ccDetails.setCardOwner(ccOwner);

        System.out.println(cc.authorize(ccDetails, 100.00f));
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
