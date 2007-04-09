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

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;

import loanapplication.message.Application;

/**
 * Defines the loan service contract.
 */
@Conversational
@Callback(LoanServiceCallback.class)
public interface LoanService {

    /**
     * Submits a new loan application. Calling this method will start a new conversation if one has not been previously
     * initiated.
     *
     * @param application the loan application
     */
    void apply(Application application);

    /**
     * Called after the loan has been approved and when the client is read to complete the process. Calling this method
     * will end the conversation.
     */
    @EndsConversation
    void secureLoan();

    /**
     * Called to cancel a loan application. Calling this method will end the conversation.
     */
    @EndsConversation
    void cancel();

}
