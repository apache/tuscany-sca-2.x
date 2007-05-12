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

package org.apache.tuscany.sca.assembly.dsl;

import org.apache.tuscany.sca.assembly.dsl.CompositeBuilder;
import org.apache.tuscany.sca.assembly.dsl.impl.AssemblyBuilderImpl;

public class BigBankBuilder extends AssemblyBuilderImpl {
	
	public CompositeBuilder build() {
		
		CompositeBuilder bigbankAccount = composite("bigbank.account").contains(

			component("AccountServiceComponent").
			implementedBy(AccountServiceImpl.class).
			uses(
				reference("accountDataService").typedBy(AccountDataService.class).wiredTo("AccountDataServiceComponent/AccountDataService"),
				reference("stockQuoteService").promotedAs("StockQuoteService")
			).
			provides(
				service("AccountDataService").typedBy(AccountService.class).promoted()
			).
			declares(
				property("currency").ofType("string").configuredTo("USD")
			),
		
			component("AccountDataServiceComponent").
			implementedBy(AccountDataServiceImpl.class).
			provides(
				service("AccountDataService").typedBy(AccountDataService.class)
			)
		);
		
		CompositeBuilder bigbankApp = composite("bigbank.app").
			contains(
				component("BigBankAccount").implementedBy(bigbankAccount)
			);
		
		CompositeBuilder domain = domain("http://bigbank.org").includes(bigbankApp);
		
		return domain;
	}
}
