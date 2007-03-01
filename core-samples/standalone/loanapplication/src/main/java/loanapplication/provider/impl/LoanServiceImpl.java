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

import java.io.Serializable;
import java.util.UUID;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import loanapplication.message.Application;
import loanapplication.provider.CreditService;
import loanapplication.provider.LoanService;
import loanapplication.provider.LoanServiceCallback;
import loanapplication.provider.RateService;

/**
 * The loan service implementation
 */
@Scope("CONVERSATION")
public class LoanServiceImpl implements LoanService, Serializable {
    private static final long serialVersionUID = 7801583139613235069L;
    // the loan number, demonstrates the use of conversational state
    private String loanNumber;
    private CreditService creditService;
    private RateService rateService;
    private LoanServiceCallback callback;

    /**
     * Instantiates a new component instance, passing in references to the credit and rate services
     *
     * @param creditService the credit service
     * @param rateService   the rate service
     */
    public LoanServiceImpl(@Reference(name = "creditService", required = true)CreditService creditService,
                           @Reference(name = "rateService", required = true)RateService rateService) {
        this.creditService = creditService;
        this.rateService = rateService;
    }

    /**
     * A setter method for injecting the client callback reference. The reference will be injected by the runtime
     *
     * @param callback the client callback reference
     */
    @Callback
    public void setCallback(LoanServiceCallback callback) {
        this.callback = callback;
    }

    public void apply(Application application) {
        String id = application.getCustomerID();
        loanNumber = UUID.randomUUID().toString();
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Application received for customer");
        System.out.println("Assigned loan number: " + loanNumber);
        System.out.println("---------------------------------------------------------------------");
        int rating = creditService.getCreditScore(id);
        if (rating > 500) {
            // approve the loan
            callback.creditScoreResult(rating);
            rateService.getRate(application.getType());
            callback.applicationResult(LoanServiceCallback.APPROVED);
        } else {
            // reject the loan
            callback.creditScoreResult(rating);
            callback.applicationResult(LoanServiceCallback.DECLINED);
        }
    }

    public void secureLoan() {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Loan secured");
        System.out.println("Loan number: " + loanNumber);
        System.out.println("---------------------------------------------------------------------");
    }

    public void cancel() {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Loan cancelled");
        System.out.println("Loan number: " + loanNumber);
        System.out.println("---------------------------------------------------------------------");
    }
}
