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
package bigbank.account.savings;

import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Service;

/**
 * 
 *  */

@Service(SavingsAccountService.class)
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private Map<String, String> custAcctMap = new HashMap<String, String>();
    private Map<String, Double> savingsAccts = new HashMap<String, Double>();
    
    public SavingsAccountServiceImpl() {
        custAcctMap.put("Customer_01", "SVA_Customer_01");
        custAcctMap.put("Customer_02", "SVA_Customer_02");
        custAcctMap.put("Customer_03", "SVA_Customer_03");
        
        savingsAccts.put("SVA_Customer_01", new Double(1000));
        savingsAccts.put("SVA_Customer_02", new Double(1500));
        savingsAccts.put("SVA_Customer_03", new Double(2000));
    }
    
	public double deposit(String accountNo, double depositAmt) {
        savingsAccts.put(accountNo, new Double(savingsAccts.get(accountNo).doubleValue() + depositAmt));
        return savingsAccts.get(accountNo).doubleValue();
    }

    public SavingsAccountDetails getAccountDetails(String customerID) {
        SavingsAccountDetails savingsAccount = new SavingsAccountDetails();
        savingsAccount.setAccountNumber(custAcctMap.get(customerID));
        savingsAccount.setBalance(savingsAccts.get(savingsAccount.getAccountNumber()).doubleValue());

        return savingsAccount;
    }

    public double withdraw(String accountNo, double withdrawalAmount) {
        double balance = savingsAccts.get(accountNo).doubleValue();
        if ( balance - withdrawalAmount > 0 ) {
            balance = balance - withdrawalAmount;
            savingsAccts.put(accountNo, balance);
        }
        return balance;
    }
}
