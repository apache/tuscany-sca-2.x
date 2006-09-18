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
package org.apache.servicemix.sca.bigbank.account;

import java.util.ArrayList;

import org.apache.servicemix.sca.bigbank.accountdata.AccountDataService;
import org.apache.servicemix.sca.bigbank.accountdata.CheckingAccount;
import org.apache.servicemix.sca.bigbank.accountdata.SavingsAccount;
import org.apache.servicemix.sca.bigbank.accountdata.StockAccount;
import org.apache.servicemix.sca.bigbank.stockquote.StockQuoteRequest;
import org.apache.servicemix.sca.bigbank.stockquote.StockQuoteResponse;
import org.apache.servicemix.sca.bigbank.stockquote.StockQuoteService;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(interfaces=AccountService.class)
public class AccountServiceImpl implements AccountService {

    @Property
    public String currency = "USD";

    @Reference
    public AccountDataService accountDataService;
    @Reference
    public StockQuoteService stockQuoteService;

    public AccountServiceImpl() {
    }

    public AccountReportResponse getAccountReport(AccountReportRequest request) {
    	AccountReportResponse report = new AccountReportResponse();
    	String customerID = request.getCustomerID();
    	report.setAccountSummaries(new ArrayList<AccountSummary>());
    	report.getAccountSummaries().add(getCheckAccountSummary(customerID));
    	report.getAccountSummaries().add(getSavingsAccountSummary(customerID));
    	report.getAccountSummaries().add(getStockAccountSummary(customerID));
        return report;
    }
    
    private AccountSummary getCheckAccountSummary(String customerID) {
    	CheckingAccount checking = accountDataService.getCheckingAccount(customerID);
    	AccountSummary summary = new AccountSummary();
    	summary.setAccountNumber(checking.getAccountNumber());
    	summary.setAccountType("Checking");
    	summary.setBalance(checking.getBalance());
    	return summary;
    }

    private AccountSummary getSavingsAccountSummary(String customerID) {
    	SavingsAccount savings = accountDataService.getSavingsAccount(customerID);
    	AccountSummary summary = new AccountSummary();
    	summary.setAccountNumber(savings.getAccountNumber());
    	summary.setAccountType("Savings");
    	summary.setBalance(savings.getBalance());
    	return summary;
    }

    private AccountSummary getStockAccountSummary(String customerID) {
    	StockAccount stock = accountDataService.getStockAccount(customerID);
    	AccountSummary summary = new AccountSummary();
    	summary.setAccountNumber(stock.getAccountNumber());
    	summary.setAccountType("Stock");
    	float quote = getQuote(stock.getSymbol());
    	summary.setBalance(quote * stock.getQuantity());
    	return summary;
    }
    
    private float getQuote(String symbol) {
    	StockQuoteRequest req = new StockQuoteRequest();
    	req.setSymbol(symbol);
    	StockQuoteResponse rep = stockQuoteService.getQuote(req);
    	return rep.getResult();
    }

}
