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

import bigbank.account.checking.CheckingAccountDetails;
import bigbank.account.checking.CheckingAccountService;
import bigbank.account.savings.SavingsAccountDetails;
import bigbank.account.savings.SavingsAccountService;
import bigbank.account.stock.StockAccountDetails;
import bigbank.account.stock.StockAccountService;
import bigbank.stockquote.StockQuoteService;
import bigbank.calculator.CalculatorService;

/**
 * Account service implementation
 */
public class AccountServiceImpl implements AccountService {

    private SavingsAccountService savingsAccountService;
    
    private CheckingAccountService checkingAccountService;
    
    private StockAccountService stockAccountService;
    
    private CalculatorService calculatorService;
    
    private StockQuoteService stockQuoteService;
    
    private String currency;

    public AccountReport getAccountReport(String customerID) {

        // Get the checking, savings and stock accounts from the AccountData
        // service component
        CheckingAccountDetails checking = null;
        List<String> summaries = new ArrayList<String>();
        try {
            checking = checkingAccountService.getAccountDetails(customerID);
            System.out.println("Checking account: " + checking);
            summaries.add(checking.toString());

            SavingsAccountDetails savings = savingsAccountService.getAccountDetails(customerID);
            System.out.println("Savings account: " + savings);
            summaries.add(savings.toString());

            StockAccountDetails stock = stockAccountService.getAccountDetails(customerID);
            System.out.println("Stock account: " + stock);
            summaries.add(stock.toString());
            
            // Get the stock price in USD
            double price = stockQuoteService.getQuote(stock.getSymbol());
            System.out.println("Stock price for " + stock.getSymbol() + ": " + price);
            
            // Convert to the configured currency
            if (currency.equals("EURO")) {
                
                // Use our fancy calculator service to convert to the target currency
                price = calculatorService.multiply(price, 0.70);
                
                System.out.println("Converted to " + currency + ": " + price);
            }       
              
            // Calculate the value of the stock account
            double stockValue = price * stock.getQuantity();
            summaries.add(stock.toString());
            
            AccountReport report = new AccountReport(currency, summaries);
            
            return report;
        } catch ( Throwable e ) {
            e.printStackTrace();
            return null;
        }
    }
    
    public SavingsAccountService getSavingsAccountService() {
        return savingsAccountService;
    }

    public void setSavingsAccountService(SavingsAccountService savingsAccountService) {
        this.savingsAccountService = savingsAccountService;
    }
    
    public CheckingAccountService getCheckingAccountService() {
        return checkingAccountService;
    }

    public void setCheckingAccountService(CheckingAccountService checkingAccountService) {
        this.checkingAccountService = checkingAccountService;
    }
    
    public StockAccountService getStockAccountService() {
        return stockAccountService;
    }

    public void setStockAccountService(StockAccountService stockAccountService) {
        this.stockAccountService = stockAccountService;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public CalculatorService getCalculatorService() {
        return calculatorService;
    }

    public void setCalculatorService(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    public StockQuoteService getStockQuoteService() {
        return stockQuoteService;
    }

    public void setStockQuoteService(StockQuoteService stockQuoteService) {
        this.stockQuoteService = stockQuoteService;
    }
}
