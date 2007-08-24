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
package bigbank.account.client;

import java.util.Iterator;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import com.bigbank.account.AccountReport;
import com.bigbank.account.AccountService;
import com.bigbank.account.AccountSummary;

public class AccountClient {

    public void start() {
    }

    public void stop() {
    }

    public static void main(String[] args) throws Exception {

        CompositeContext context = CurrentCompositeContext.getContext();

        AccountService accountService = context.locateService(AccountService.class, "AccountServiceComponent");

        AccountReport accountReport = accountService.getAccountReport(12345);

        for (Iterator i = accountReport.getAccountSummaries().iterator(); i.hasNext();) {
            AccountSummary accountSummary = (AccountSummary) i.next();

            System.out.println(accountSummary.getAccountNumber());
            System.out.println(accountSummary.getAccountType());
            System.out.println(accountSummary.getBalance());
        }

    }

}
