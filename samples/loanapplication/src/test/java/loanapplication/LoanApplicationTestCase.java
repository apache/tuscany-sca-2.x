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

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCADomain;

public class LoanApplicationTestCase extends TestCase {

    private LoanClient loanClient;
    private SCADomain domain;

    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("loanapplication.composite");

        loanClient = domain.getService(LoanClient.class, "LoanClientComponent");
    }
    
    protected void tearDown() throws Exception {
    	domain.close();
    }

    public void test() throws Exception {
        try {
            loanClient.applyForLoan("John Doe", 1000.0f);
            System.out.println("Applied: " + loanClient.displayLoan());
            System.out.println("Loan approved: " + loanClient.isApproved());
            loanClient.cancelLoan();
            System.out.println("Sleeping to let cancel complete ...");
            Thread.sleep(500);
            if (!loanClient.isCancelled()) {
                fail("Loan should be cancelled");
            }
            System.out.println("Cancelled: " + loanClient.displayLoan());
            loanClient.closeLoan();
            
            /* This is a mistake, after @EndsConversation, a new conversation is
             * started automatically, so we should not get TargetNotFoundException.
             * Keep this for the timeout case, where we should get the exception
            try {
                System.out.println("Trying to use the closed loan in the ended conversation ...");            
                System.out.println("Closed: " + loanClient.displayLoan());
                fail("Target should not be found");
            } catch(TargetNotFoundException e) {
                System.out.println("Target not found as expected");            
            }
            */

            // Now check that a new conversation's loan is not cancelled
            
            if (loanClient.isCancelled()) {
                fail("Loan should not be cancelled");
            }
        } catch(Throwable e) {
            e.printStackTrace();
            if (e instanceof Exception) {
                throw (Exception)e;
            }
            if (e instanceof Error) {
                throw (Error)e;
            }
        }
    }
}
