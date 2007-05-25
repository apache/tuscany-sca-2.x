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
package mortgage;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of the LoanApproval service.
 */
@Service(LoanApproval.class)
// Service declaration
public class LoanApprovalImpl implements LoanApproval {
    private CreditCheck[] creditCheck;
    private MortgageCalculator mortgageCalculator;
    private InterestRateQuote interestRateQuote;

    // Reference declaration using a protected or public field
    @Reference
    public RiskAssessment riskAssessment;

    private int minimumCreditScore = 650;

    // Property declaration using a setter method
    @Property(name = "minimumCreditScore")
    public void setMinimumCreditScore(int minimumCreditScore) {
        this.minimumCreditScore = minimumCreditScore;
    }

    // Reference declaration using a setter method
    @Reference
    public void setCreditCheck(CreditCheck[] creditCheck) {
        this.creditCheck = creditCheck;
    }

    @Reference
    public void setInterestRateQuote(InterestRateQuote interestRateQuote) {
        this.interestRateQuote = interestRateQuote;
    }

    @Reference
    public void setMortgageCalculator(MortgageCalculator mortgageCalculator) {
        this.mortgageCalculator = mortgageCalculator;
    }

    public boolean approve(Customer customer, double loanAmount, int years) {
        int score = 0;
        for (int i = 0; i < creditCheck.length; i++) {
            score += creditCheck[0].getCreditScore(customer.getSsn());
        }
        if (score != 0) {
            score = score / creditCheck.length;
        }
        if (score < minimumCreditScore) {
            return false;
        }
        float rate = interestRateQuote.getRate(customer.getState(), loanAmount, years);
        double monthlyPayment = mortgageCalculator.getMonthlyPayment(loanAmount, years, rate);
        double ratio = monthlyPayment / customer.getMonthlyIncome();
        return riskAssessment.assess(score, ratio);
    }
}
