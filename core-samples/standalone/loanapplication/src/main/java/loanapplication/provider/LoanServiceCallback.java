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
package loanapplication.provider;

import loanapplication.message.LoanPackage;

/**
 * Defines the callback contract for loan service clients
 */
public interface LoanServiceCallback {
    int APPROVED = 1;
    int DECLINED = -1;

    /**
     * Called when the customer's credit score is received from the credit check process.
     *
     * @param code the customer's credit score.
     */
    void creditScoreResult(int code);

    /**
     * Called to the loan has been {@link LoanServiceCallback#APPROVED} or {@link LoanServiceCallback#DECLINED}.
     *
     * @param code if the loan application was approved or declined.
     */
    void applicationResult(int code);

    /**
     * Called after the loan has been secured to provide the completed loan information.
     *
     * @param loanPackage the loan information.
     */
    void loanPackage(LoanPackage loanPackage);

}
