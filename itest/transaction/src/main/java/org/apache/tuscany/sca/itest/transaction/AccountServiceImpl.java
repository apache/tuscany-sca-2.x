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

import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$
 */
public abstract class AccountServiceImpl implements AccountService {
    protected Map<String, Float> accounts = new HashMap<String, Float>();

    /**
     * @see org.apache.tuscany.sca.itest.transaction.AccountService#deposit(String, float)
     */
    public void deposit(String accountNumber, float amount) throws AccountNotFoundException {
        float balance = getBalance(accountNumber);
        balance += amount;
        save(accountNumber, balance);
        accounts.put(accountNumber, balance);
    }

    /**
     * @see org.apache.tuscany.sca.itest.transaction.AccountService#getBalance(String)
     */
    public float getBalance(String accountNumber) throws AccountNotFoundException {
        Float balance = accounts.get(accountNumber);
        if (balance == null) {
            balance = load(accountNumber);
            accounts.put(accountNumber, balance);
        }
        return balance;
    }

    /**
     * @see org.apache.tuscany.sca.itest.transaction.AccountService#withdraw(String, float)
     */
    public void withdraw(String accountNumber, float amount) throws OverDraftException, AccountNotFoundException {
        float balance = getBalance(accountNumber);
        if (amount > balance) {
            throw new OverDraftException("Insufficient fund");
        }
        balance -= amount;
        save(accountNumber, balance);
        accounts.put(accountNumber, balance);
    }

    protected abstract float load(String accountNumber) throws AccountNotFoundException;

    protected abstract void save(String accountNumber, float balance) throws AccountNotFoundException;

}
