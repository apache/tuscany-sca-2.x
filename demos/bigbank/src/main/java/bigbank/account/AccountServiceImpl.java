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

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import stockquote.StockQuoteService;
import bigbank.account.checking.CheckingAccountDetails;
import bigbank.account.checking.CheckingAccountService;
import bigbank.account.savings.SavingsAccountDetails;
import bigbank.account.savings.SavingsAccountService;
import bigbank.account.stock.StockAccountDetails;
import bigbank.account.stock.StockAccountService;
import calculator.CalculatorService;

/**
 * @version $$Rev$$ $$Date$$
 */

@Service(AccountService.class)
public class AccountServiceImpl implements AccountService {

    @Reference
    protected SavingsAccountService savingsAcService;
    
    @Reference 
    protected CheckingAccountService checkingAcService;
    
    @Reference
    protected StockAccountService stockAcService;
    
    @Reference
    protected StockQuoteService stockQuoteService;
    
    @Reference
    protected CalculatorService calculatorService;
    
    @Property
    protected String currency;

    public double getAccountReport(String customerID) {

        // Get the checking, savings and stock accounts from the AccountData
        // service component
        CheckingAccountDetails checking = null;
        try {
            checking = checkingAcService.getAccountDetails(customerID);
            System.out.println("Checking account: " + checking);

            SavingsAccountDetails savings = savingsAcService.getAccountDetails(customerID);
            System.out.println("Savings account: " + savings);

            StockAccountDetails stock = stockAcService.getAccountDetails(customerID);
            System.out.println("Stock account: " + stock);
        
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
            
            // Calculate the total balance of all accounts and return it
            double balance = checking.getBalance() + savings.getBalance() + stockValue;
            
            return balance;
        } catch ( Throwable e ) {
            e.printStackTrace();
            return 0;
        }
    }
}
