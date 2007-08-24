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

package bigbank.webclient.client;

import java.util.List;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;


import com.bigbank.account.AccountReport;
import com.bigbank.account.AccountService;

public class TestAccountService {


    public static void main(String[] args) throws Exception {
        CompositeContext moduleContext = CurrentCompositeContext.getContext();

        AccountService accountService = (AccountService) moduleContext.locateService(AccountService.class, "AccountServiceComponent");

        AccountReport report = accountService.getAccountReport(12345);
        List summaries = report.getAccountSummaries();

        System.out.println("retrieved " + summaries.size() + " summaries");

    }
}
