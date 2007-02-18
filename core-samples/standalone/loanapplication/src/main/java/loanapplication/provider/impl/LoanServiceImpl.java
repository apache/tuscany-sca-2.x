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
package loanapplication.provider.impl;

import static loanapplication.provider.LoanServiceCallback.DECLINED;
import static loanapplication.provider.LoanServiceCallback.APPROVED;

import java.util.UUID;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;

import loanapplication.provider.LoanService;
import loanapplication.provider.LoanServiceCallback;
import loanapplication.provider.CreditService;
import loanapplication.message.Application;

/**
 * The loan service implementation
 */
@Scope("CONVERSATIONAL")
public class LoanServiceImpl implements LoanService {
    private String loanNumber;
    private CreditService creditService;
    private LoanServiceCallback callback;

    public LoanServiceImpl(@Reference CreditService creditService) {
        this.creditService = creditService;
    }

    @Callback
    public void setCallback(LoanServiceCallback callback) {
        this.callback = callback;
    }

    public void apply(Application application) {
        String id = application.getCustomerID();
        System.out.println("Application received: "+ id);
        loanNumber = UUID.randomUUID().toString();
        int rating = creditService.getCreditRating(id);
        if (rating > 500){
            callback.creditResult(APPROVED);
        } else {
            callback.creditResult(DECLINED);
        }
    }

    @OneWay
    public void secureLoan() {
        System.out.println("Loan secured: "+ loanNumber);
    }

    @OneWay
    public void cancel() {
        System.out.println("Loan cancelled: "+ loanNumber);
    }
}
