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
package loanapplication;


import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class LoanClientImpl implements LoanClient {
    
    private LoanService loanService;
    
    @Reference
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    public void applyForLoan(String customerName, float amount) {
        loanService.apply(new LoanApplication(customerName, amount));
    }
    
    public boolean isApproved() {
        if (loanService.getLoanStatus() == null) {
            return false;
        }
        return loanService.getLoanStatus().equals("approved");
    }
    
    public boolean isCancelled() {
        if (loanService.getLoanStatus() == null) {
            return false;
        }
        return loanService.getLoanStatus().equals("cancelled");
    }
    
    public String displayLoan() {
        return loanService.display();
    }
    
    public void cancelLoan() {
        loanService.cancelApplication();
    }
    
    public void closeLoan() {
        loanService.close();
    }
}
