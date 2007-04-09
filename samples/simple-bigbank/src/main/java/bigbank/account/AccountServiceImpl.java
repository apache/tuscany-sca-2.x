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

import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Reference;

import java.util.List;

import bigbank.accountdata.AccountDataService;
import bigbank.accountdata.CheckingAccount;

/**
 * @version $$Rev$$ $$Date$$
 */

@Service(AccountService.class)
public class AccountServiceImpl implements AccountService {

    @Reference
    public AccountDataService accountDataService;

    public AccountReport getAccountReport(String s) {
        AccountReport report = new AccountReport();

        CheckingAccount ca = accountDataService.getCheckingAccount(s);
        report.addAccount(ca);

        return report;
    }
}
