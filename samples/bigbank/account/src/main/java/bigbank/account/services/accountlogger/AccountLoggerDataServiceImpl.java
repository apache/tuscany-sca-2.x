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
package bigbank.account.services.accountlogger;

import java.rmi.RemoteException;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import bigbank.account.services.accountdata.AccountDataService;
import bigbank.account.services.accountdata.CustomerIdService;

import com.bigbank.account.AccountLog;
import com.bigbank.account.AccountReport;
import com.bigbank.account.CustomerProfileData;
import com.bigbank.account.StockSummary;

/**
 * This class implements the Account Data Logger service component.
 */
@Service(AccountDataService.class)
public class AccountLoggerDataServiceImpl implements AccountDataService {

    private CustomerIdService accountDataService; // TODO fix this!

    @Reference
    public void setAccountDataService(CustomerIdService accountDataService) {
        this.accountDataService = accountDataService;
    }

    private AccountLoggerService accountLoggerService;

    @Reference
    public void setAccountLoggerService(AccountLoggerService accountLoggerService) {
        this.accountLoggerService = accountLoggerService;
    }

    public StockSummary purchaseStock(int id, StockSummary stock) throws RemoteException {
        accountLoggerService.logPurchaseStock(id, stock);

        return accountDataService.purchaseStock(id, stock);
    }

    public CustomerProfileData getCustomerProfile(String logonID) throws RemoteException {

        return accountDataService.getCustomerProfile(logonID);
    }

    public float deposit(String account, float amount) throws RemoteException {
        try {
            if (!(accountDataService instanceof CustomerIdService)) {
                throw new RemoteException("Can't use data service as customer id service");
            }
            int id = accountDataService.getCustomerIdByAccount(account);
            // int id = accountDataService.getCustomerIdByAccount(account);
            accountLoggerService.logDeposit(id, account, amount);

            return accountDataService.deposit(account, amount);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
        }
    }

    public CustomerProfileData createAccount(CustomerProfileData customerProfile, boolean createSavings, boolean createChecking)
            throws RemoteException {

        return accountDataService.createAccount(customerProfile, createSavings, createChecking);
    }

    public StockSummary sellStock(int purchaseLotNumber, int quantity) throws RemoteException {
        try {
            int id = accountDataService.getCustomerIdByPurchaseLotNumber(purchaseLotNumber);
            StockSummary ss = accountDataService.sellStock(purchaseLotNumber, quantity);
            if (ss != null) {
                if (!(accountDataService instanceof CustomerIdService)) {
                    throw new RemoteException("Can't use data service as customer id service");
                }
                accountLoggerService.logSellStock(id, ss, quantity);
            }
            return ss;
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass() + " " + e.getMessage(), e);
        }
    }

    public float withdraw(String account, float amount) throws RemoteException {
        if (!(accountDataService instanceof CustomerIdService)) {
            throw new RemoteException("Can't use data service as customer id service");
        }
        int id = accountDataService.getCustomerIdByAccount(account);
        // int id = accountDataService.getCustomerIdByAccount(account);
        accountLoggerService.logWithdrawal(id, account, amount);

        return accountDataService.withdraw(account, amount);
    }

    public AccountReport getAccountReport(final int customerID) throws RemoteException {

        return accountDataService.getAccountReport(customerID);
    }

    public AccountLog getAccountLog(final int customerID) throws RemoteException {
        return accountLoggerService.getAccountLog(customerID);
    }
}
