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
package bigbank.account.services.accountdata;

import java.rmi.RemoteException;
import java.util.List;

import org.osoa.sca.annotations.Service;

import com.bigbank.account.AccountFactory;
import com.bigbank.account.AccountLog;
import com.bigbank.account.AccountReport;
import com.bigbank.account.AccountSummary;
import com.bigbank.account.CustomerProfileData;
import com.bigbank.account.StockSummary;

@Service(AccountDataService.class)
public class AccountDataServiceImpl implements AccountDataService {

    public CustomerProfileData getCustomerProfile(String logonID) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public AccountReport getAccountReport(int customerID) {
        final AccountFactory accountFactory = AccountFactory.INSTANCE;
        AccountReport accountReport = accountFactory.createAccountReport();
        List accounts = accountReport.getAccountSummaries();

        AccountSummary account = accountFactory.createAccountSummary();
        account.setAccountType("1111");
        account.setAccountNumber("22-22-22");
        account.setBalance(123.45F);
        accounts.add(account);

        account = accountFactory.createAccountSummary();
        account.setAccountType("04-11-19");
        account.setAccountNumber("11-23");
        account.setBalance(543.21F);
        accounts.add(account);

        List stocks = accountReport.getStockSummaries();
        StockSummary stock = accountFactory.createStockSummary();
        stock.setSymbol("IBM");
        stock.setPurchaseDate("1999-11-23");
        stock.setPurchaseLotNumber(101);
        stock.setPurchasePrice(33.33F);
        stock.setQuantity(10);
        stocks.add(stock);

        stock = accountFactory.createStockSummary();
        stock.setSymbol("TUSK");
        stock.setPurchaseDate("2005-01-05");
        stock.setPurchaseLotNumber(102);
        stock.setPurchasePrice(11.11F);
        stock.setQuantity(4);
        stocks.add(stock);
        return accountReport;
    }

    public CustomerProfileData createAccount(CustomerProfileData customerProfile, boolean createSavings, boolean createCheckings)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public float deposit(String param6, float param7) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    public StockSummary purchaseStock(int param0, StockSummary stock) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public StockSummary sellStock(int param13, int param14) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public float withdraw(String param16, float param17) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    public AccountLog getAccountLog(final int customerID) throws RemoteException {
        throw new RemoteException("This method should not be called");
    }
}
