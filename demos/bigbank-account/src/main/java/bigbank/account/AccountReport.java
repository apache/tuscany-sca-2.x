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


/**
 * @version $$Rev$$ $$Date: 2007-04-14 11:34:18 -0700 (Sat, 14 Apr
 *          2007) $$
 */

public class AccountReport {
    private String currency;
    private String checkingAccountNumber;
    private double checkingBalance;
    private String savingsAccountNumber;
    private double savingsBalance;
    private String stockAccountNumber;
    private double stockBalance;
    
    public String getCheckingAccountNumber() {
        return checkingAccountNumber;
    }
    public void setCheckingAccountNumber(String checkingAccountID) {
        this.checkingAccountNumber = checkingAccountID;
    }
    public double getCheckingBalance() {
        return checkingBalance;
    }
    public void setCheckingBalance(double checkingBalance) {
        this.checkingBalance = checkingBalance;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getSavingsAccountNumber() {
        return savingsAccountNumber;
    }
    public void setSavingsAccountNumber(String savingsAccountID) {
        this.savingsAccountNumber = savingsAccountID;
    }
    public double getSavingsBalance() {
        return savingsBalance;
    }
    public void setSavingsBalance(double savingsBalance) {
        this.savingsBalance = savingsBalance;
    }
    public String getStockAccountNumber() {
        return stockAccountNumber;
    }
    public void setStockAccountNumber(String stockAccountID) {
        this.stockAccountNumber = stockAccountID;
    }
    public double getStockBalance() {
        return stockBalance;
    }
    public void setStockBalance(double stockBalance) {
        this.stockBalance = stockBalance;
    }

    public String toString() {
        return "Account Report: \n" +
        "Checking: " + checkingAccountNumber + ", balance:" + checkingBalance + "\n" +
        "Savings: " + savingsAccountNumber + ", balance:" + savingsBalance + "\n" +
        "Stocks: " + stockAccountNumber + ", balance:" + stockBalance;
    }
}
