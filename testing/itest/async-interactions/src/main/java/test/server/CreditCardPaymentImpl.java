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

package test.server;

import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Service;

import test.client.CreditCardPaymentCallback;
import test.client.CreditCardPaymentCallbackSync;

import com.example.test.jaxb.server.CreditCardDetailsType;
import com.example.test.jaxws.server.AuthorizeFault;
import com.example.test.jaxws.server.CreditCardPayment;

/**
 * Mocked implementation of CreditCardPayment 
 */
@Service({CreditCardPayment.class, CreditCardPaymentRequestServer.class, CreditCardPaymentRequestServerSync.class})
public class CreditCardPaymentImpl implements CreditCardPayment, CreditCardPaymentRequestServer,
    CreditCardPaymentRequestServerSync {
    @Callback
    protected CreditCardPaymentCallback callback;
    
    @Callback
    protected CreditCardPaymentCallbackSync callbackSync;

    @Override
    public void authorizeRequestOneway(CreditCardDetailsType creditCard, float amount) {
        String status;
        try {
            status = authorize(creditCard, amount);
        } catch (AuthorizeFault e) {
            status = "FAIL: " + e.getFaultInfo().getErrorCode();
        }
        callback.authorizeResponseOneway(creditCard.getCreditCardNumber(), status);
    }
    
    @Override
    public String authorizeRequest(CreditCardDetailsType creditCard, float amount) {
        String status;
        try {
            status = authorize(creditCard, amount);
        } catch (AuthorizeFault e) {
            status = "FAIL: " + e.getFaultInfo().getErrorCode();
        }
        callbackSync.authorizeResponse(creditCard.getCreditCardNumber(), status);
        return "ACK";
    }

    @Override
    public String authorize(CreditCardDetailsType creditCard, float amount) throws AuthorizeFault {
        if (creditCard != null) {
            System.out.println("Checking card: name = " + creditCard.getCardOwner().getName()
                + " number = "
                + creditCard.getCreditCardNumber()
                + " for amount "
                + amount);
        } else {
            System.out.println("Checking card is null");
        }

        return "OK";
    }

}
