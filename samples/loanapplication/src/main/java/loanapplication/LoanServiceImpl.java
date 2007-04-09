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

import org.osoa.sca.annotations.AllowsPassByReference;
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Scope;

@Scope("CONVERSATION")
@AllowsPassByReference
public class LoanServiceImpl implements LoanService {
    
    private LoanApplication application;
    private String status;
    private int termLocked = 0;
    @ConversationID
    protected String conversationID;

    public void apply(LoanApplication application) {
        this.application = application;
        status = "open";
    }
    
    public void lockCurrentRate(int termInYears) {
        termLocked = termInYears;
        status = "locked";
    }
    
    public void cancelApplication() {
        status = "cancelled";
    }
    
    public String getLoanStatus() {
        return status;
    }
    
    public String display() {
        return "Loan application: " + application + ", term: "
        + termLocked + ", status: " + status
        + ", conversationID: " + conversationID;
    }
    
    public void close() {
        this.application = null;
        this.status = "closed";
    }
}
