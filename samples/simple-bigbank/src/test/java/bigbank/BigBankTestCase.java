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
package bigbank;

import junit.framework.TestCase;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import bigbank.account.AccountService;

public class BigBankTestCase extends TestCase {

    AccountService accountService;

    protected void setUp() throws Exception {
    	SCARuntime.start("BigBank.composite");

        CompositeContext context = CurrentCompositeContext.getContext();
        accountService = context.locateService(AccountService.class, "AccountServiceComponent");
    }
    
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }

    public void test() throws Exception {
        System.out.println("Account summary: " + accountService.getAccountReport("Foo") );
    }
}
