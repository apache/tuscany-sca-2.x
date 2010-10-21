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
package test.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

import com.example.test.jaxb.client.AuthorizeResponse;
import com.example.test.jaxb.client.CreditCardDetailsType;
import com.example.test.jaxb.client.ObjectFactory;
import com.example.test.jaxb.client.PayerType;
import com.example.test.jaxws.client.AuthorizeFault;
import com.example.test.jaxws.client.CreditCardPayment;

/**
 * Mocked implementation of CreditCardPaymentClient
 */
@Service(CreditCardClient.class)
public class CreditCardPaymentClientImpl implements CreditCardClient, CreditCardPaymentCallback,
    CreditCardPaymentCallbackSync {

    @Reference
    private CreditCardPayment proxy;

    @Reference
    private CreditCardPaymentRequestClient asyncProxy;

    @Reference
    private CreditCardPaymentRequestClientSync syncProxy;

    @Override
    public String authorize(String creditCardNumber, String holder, float amount) {
        CreditCardDetailsType creditCard = createCreditCard(creditCardNumber, holder);

        try {
            return proxy.authorize(creditCard, amount);
        } catch (AuthorizeFault e) {
            return "FAIL: " + e.getFaultInfo().getErrorCode();
        }

    }

    private CreditCardDetailsType createCreditCard(String creditCardNumber, String holder) {
        ObjectFactory factory = new ObjectFactory();
        CreditCardDetailsType creditCard = factory.createCreditCardDetailsType();
        creditCard.setCreditCardNumber(creditCardNumber);
        PayerType payer = factory.createPayerType();
        payer.setName(holder);
        creditCard.setCardOwner(payer);
        return creditCard;
    }

    public String authorizeAsync(String creditCardNumber, String holder, float amount) {
        CreditCardDetailsType creditCard = createCreditCard(creditCardNumber, holder);

        Response<AuthorizeResponse> respone = proxy.authorizeAsync(creditCard, amount);
        try {
            return respone.get().getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL: " + e.getMessage();
        }

    }

    public String authorizeAsyncWithCallback(String creditCardNumber, String holder, float amount) {
        CreditCardDetailsType creditCard = createCreditCard(creditCardNumber, holder);

        Future<?> respone = proxy.authorizeAsync(creditCard, amount, new AsyncHandler<AuthorizeResponse>() {

            @Override
            public void handleResponse(Response<AuthorizeResponse> res) {
                try {
                    System.out.println(Thread.currentThread() + " Response has arrived: " + res.get().getStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        while (!(respone.isDone() || respone.isCancelled())) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "FAIL: " + e.getMessage();
            }
        }

        System.out.println(Thread.currentThread() + " The task is done");
        try {
            return ((AuthorizeResponse)respone.get()).getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL: " + e.getMessage();
        }
    }

    /**
     * A map that host the result for a given credit card
     */
    private static Map<String, String> statusMap = new HashMap<String, String>();

    public String authorizeSCAAsyncWithCallback(String creditCardNumber, String holder, float amount) {
        CreditCardDetailsType creditCard = createCreditCard(creditCardNumber, holder);
        asyncProxy.authorizeRequestOneway(creditCard, amount);
        synchronized (statusMap) {
            while (true) {
                String status = statusMap.remove("ASYNC:" + creditCardNumber);
                if (status != null) {
                    System.out.println("Response found for " + creditCardNumber + " :" + status);
                    return status;
                } else {
                    try {
                        statusMap.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return "FAIL: " + e.getMessage();
                    }
                }
            }
        }
    }

    @Override
    public void authorizeResponseOneway(String creditCardNumber, String status) {
        System.out.println("SCA one callback: CreditCard: " + creditCardNumber + " Status: " + status);
        synchronized (statusMap) {
            statusMap.put("ASYNC:" + creditCardNumber, status);
            statusMap.notifyAll();
        }
    }

    public String authorizeSCAWithCallback(String creditCardNumber, String holder, float amount) {
        CreditCardDetailsType creditCard = createCreditCard(creditCardNumber, holder);
        syncProxy.authorizeRequest(creditCard, amount);
        synchronized (statusMap) {
            while (true) {
                String status = statusMap.remove("SYNC:" + creditCardNumber);
                if (status != null) {
                    System.out.println("Response found for " + creditCardNumber + " :" + status);
                    return status;
                } else {
                    try {
                        statusMap.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return "FAIL: " + e.getMessage();
                    }
                }
            }
        }
    }

    @Override
    public String authorizeResponse(String creditCardNumber, String status) {
        System.out.println("SCA synchronous callback: CreditCard: " + creditCardNumber + " Status: " + status);
        synchronized (statusMap) {
            statusMap.put("SYNC:" + creditCardNumber, status);
            statusMap.notifyAll();
        }
        return "ACK";
    }

}
