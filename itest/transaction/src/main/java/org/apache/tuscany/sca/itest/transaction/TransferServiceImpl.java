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

package org.apache.tuscany.sca.itest.transaction;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(TransferService.class)
public class TransferServiceImpl implements TransferService {
    @Reference
    protected AccountService savings;

    @Reference
    protected AccountService checking;

    /**
     * @see org.apache.tuscany.sca.itest.transaction.TransferService#transfer(java.lang.String, java.lang.String, float)
     */
    public void transfer(String from, String to, float amount) throws OverDraftException, AccountNotFoundException {
        if (from.startsWith("C")) {
            checking.withdraw(from, amount);
        } else {
            savings.withdraw(from, amount);
        }
        if (to.startsWith("C")) {
            checking.deposit(to, amount);
        } else {
            savings.deposit(to, amount);
        }
    }
    
    public float getBalance(String accountNumber) throws AccountNotFoundException {
        if(accountNumber.startsWith("C")) {
            return checking.getBalance(accountNumber);
        } else {
            return savings.getBalance(accountNumber);
        }
    }

    public String[] getAccounts() {
        return new String[] {"S001", "S002", "C001"};
    }

}
