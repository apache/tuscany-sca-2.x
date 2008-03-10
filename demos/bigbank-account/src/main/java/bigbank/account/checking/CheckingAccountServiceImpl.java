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
package bigbank.account.checking;

import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Service;

/**
 * 
 *  */

@Service(CheckingAccountService.class)
public class CheckingAccountServiceImpl implements CheckingAccountService {
    private Map<String, String> custAcctMap = new HashMap<String, String>();
    private Map<String, Double> checkingAccts = new HashMap<String, Double>();
    
    public CheckingAccountServiceImpl() {
        custAcctMap.put("Customer_01", "CHA_Customer_01");
        custAcctMap.put("Customer_02", "CHA_Customer_02");
        custAcctMap.put("Customer_03", "CHA_Customer_03");
        
        checkingAccts.put("CHA_Customer_01", new Double(1000));
        checkingAccts.put("CHA_Customer_02", new Double(1500));
        checkingAccts.put("CHA_Customer_03", new Double(2000));
    }
    
	public double deposit(String accountNo, double depositAmt) {
        checkingAccts.put(accountNo, new Double(checkingAccts.get(accountNo).doubleValue() + depositAmt));
        return checkingAccts.get(accountNo).doubleValue();
    }

    public CheckingAccountDetails getAccountDetails(String customerID) {
        CheckingAccountDetails checkingAccount = new CheckingAccountDetails();
        checkingAccount.setAccountNumber(custAcctMap.get(customerID));
        checkingAccount.setBalance(checkingAccts.get(checkingAccount.getAccountNumber()).doubleValue());

        return checkingAccount;
    }

    public double withdraw(String accountNo, double withdrawalAmount) {
        double balance = checkingAccts.get(accountNo).doubleValue();
        if ( balance - withdrawalAmount > 0 ) {
            balance = balance - withdrawalAmount;
            checkingAccts.put(accountNo, balance);
        }
        return balance;
    }
}
