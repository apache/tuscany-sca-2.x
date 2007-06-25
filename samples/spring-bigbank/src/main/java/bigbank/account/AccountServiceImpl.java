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
package bigbank.account;

import java.util.ArrayList;
import java.util.List;

import bigbank.accountdata.AccountDataService;
import bigbank.accountdata.CheckingAccount;
import bigbank.accountdata.SavingsAccount;
import bigbank.accountdata.StockAccount;
import bigbank.stockquote.StockQuoteService;

/**
 * Account service implementation
 */
public class AccountServiceImpl implements AccountService {

    private AccountDataService accountDataService;
    
    private StockQuoteService stockQuoteService;
    
    private String currency;

    public AccountReport getAccountReport(String s) {
        List<String> summaries = new ArrayList<String>();

        CheckingAccount ca = accountDataService.getCheckingAccount(s);
        summaries.add(ca.getSummary());

        SavingsAccount sa = accountDataService.getSavingsAccount(s);
        summaries.add(sa.getSummary());

        StockAccount sk = accountDataService.getStockAccount(s);
        
        double price = stockQuoteService.getQuote(sk.getSymbol());
        sk.setBalance(sk.getQuantity() * price);
        
        summaries.add(sk.getSummary());

        AccountReport report = new AccountReport(currency, summaries);
        
        return report;
    }

    public AccountDataService getAccountDataService() {
        return accountDataService;
    }

    public void setAccountDataService(AccountDataService accountDataService) {
        this.accountDataService = accountDataService;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public StockQuoteService getStockQuoteService() {
        return stockQuoteService;
    }

    public void setStockQuoteService(StockQuoteService stockQuoteService) {
        this.stockQuoteService = stockQuoteService;
    }
}
