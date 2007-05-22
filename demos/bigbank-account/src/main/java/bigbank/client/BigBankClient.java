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

package bigbank.client;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import bigbank.account.AccountService;

/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 */
public class BigBankClient {
    
    public static void main(String[] args) throws Exception {

        SCADomain domain = SCADomain.newInstance("http://localhost:8080", "/", "BigBank.composite");
        
        AccountService accountService = domain.getService(AccountService.class, "AccountServiceComponent");

        String customerID = "1234";
        
        System.out.println("Calling account service for customer: " + customerID);
        System.out.println();
        double balance = accountService.getAccountReport(customerID);

        System.out.println();
        System.out.println("Balance: " + balance);

        domain.close();
    }  

}
