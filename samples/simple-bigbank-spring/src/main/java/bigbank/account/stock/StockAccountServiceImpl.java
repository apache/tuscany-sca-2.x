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
package bigbank.account.stock;

import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Service;

@Service(StockAccountService.class)
public class StockAccountServiceImpl implements StockAccountService {
    private Map<String, String> custAcctMap = new HashMap<String, String>();
    private Map<String, StockAccountDetails> stockAccts = new HashMap<String, StockAccountDetails>();
    
    public StockAccountServiceImpl() {
        custAcctMap.put("Customer_01", "STA_Customer_01");
        custAcctMap.put("Customer_02", "STA_Customer_02");
        custAcctMap.put("Customer_03", "STA_Customer_03");
        
        stockAccts.put("STA_Customer_01", new StockAccountDetails("STA_Customer_01", "IBM", 100));
        stockAccts.put("STA_Customer_02", new StockAccountDetails("STA_Customer_02", "IBM", 200));
        stockAccts.put("STA_Customer_03", new StockAccountDetails("STA_Customer_03", "SYM_3", 125));
    }
    
    
    public StockAccountDetails buy(String accountNo, String symbol, int quantity) {
        return null;
    }

    public StockAccountDetails getAccountDetails(String customerID) {
       return stockAccts.get(custAcctMap.get(customerID));
    }

    public StockAccountDetails sell(String accountNo, String symbol, int quantity) {
        return null;
    }
    
	
    
}
