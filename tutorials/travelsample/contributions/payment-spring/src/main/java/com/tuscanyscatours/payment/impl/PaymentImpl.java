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

package com.tuscanyscatours.payment.impl;

import com.tuscanyscatours.customer.Customer;
import com.tuscanyscatours.customer.CustomerNotFoundException;
import com.tuscanyscatours.customer.CustomerRegistry;
import com.tuscanyscatours.emailgateway.EmailGateway;
import com.tuscanyscatours.emailgateway.EmailType;
import com.tuscanyscatours.payment.Payment;
import com.tuscanyscatours.payment.creditcard.CreditCardPayment;

public class PaymentImpl implements Payment {

    private CreditCardPayment creditCardPayment;
    private CustomerRegistry customerRegistry;
    private EmailGateway emailGateway;
    private float transactionFee;

    public void setCreditCardPayment(CreditCardPayment creditCardPayment) {
        this.creditCardPayment = creditCardPayment;
    }

    public void setCustomerRegistry(CustomerRegistry customerRegistry) {
        this.customerRegistry = customerRegistry;
    }

    public void setEmailGateway(EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    public void setTransactionFee(Float transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String makePaymentMember(String customerId, float amount) {
        try {
            Customer customer = customerRegistry.getCustomer(customerId);

            amount += transactionFee;

            String status = creditCardPayment.authorize(customer.getCreditCard(), amount);

            com.tuscanyscatours.emailgateway.ObjectFactory emailFactory =
                new com.tuscanyscatours.emailgateway.ObjectFactory();
            EmailType email = emailFactory.createEmailType();
            email.setTitle("Payment Received");
            email.setTo(customerId);

            emailGateway.sendEmail(email);

            return status;
        } catch (CustomerNotFoundException ex) {
            return "Payment failed due to " + ex.getMessage();
        } catch (Throwable t) {
            return "Payment failed due to system error " + t.getMessage();
        }
    }

}
