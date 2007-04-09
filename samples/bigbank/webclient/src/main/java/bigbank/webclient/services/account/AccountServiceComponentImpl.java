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
package bigbank.webclient.services.account;

import java.rmi.RemoteException;

import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.bigbank.account.AccountLog;
import com.bigbank.account.AccountReport;
import com.bigbank.account.AccountService;
import com.bigbank.account.CustomerProfileData;
import com.bigbank.account.StockSummary;

/**
 */
@Service(AccountService.class)
public class AccountServiceComponentImpl implements AccountService {

    private AccountService accountService;

    @Reference
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 
     */
    public AccountServiceComponentImpl() {
        super();
    }

    /**
     * @see bigbank.account.services.account.AccountService#getAccountReport(java.lang.String)
     */
    public AccountReport getAccountReport(int customerID) {
        try {
            return accountService.getAccountReport(customerID);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    public StockSummary purchaseStock(int customerID, StockSummary stockSummary) throws RemoteException {
        try {
            return accountService.purchaseStock(customerID, stockSummary);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    public CustomerProfileData getCustomerProfile(String param2) throws RemoteException {
        try {
            return accountService.getCustomerProfile(param2);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    public float deposit(String account, float amount) throws RemoteException {
        try {
            return accountService.deposit(account, amount);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    public StockSummary sellStock(int purchaseLotNumber, int quantity) throws RemoteException {
        try {
            return accountService.sellStock(purchaseLotNumber, quantity);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    public float withdraw(String account, float amount) throws RemoteException {
        try {
            return accountService.withdraw(account, amount);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

    public CustomerProfileData createAccount(CustomerProfileData customerProfile, boolean createSavings, boolean createCheckings)
            throws RemoteException {

        return accountService.createAccount(customerProfile, createSavings, createCheckings);
    }

    public AccountLog getAccountLog(int customerID) throws RemoteException {
        try {
            return accountService.getAccountLog(customerID);
        } catch (Exception e) {
            throw new ServiceUnavailableException(e);
        }
    }

}
