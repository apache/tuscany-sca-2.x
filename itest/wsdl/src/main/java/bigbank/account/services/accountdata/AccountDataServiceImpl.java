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
package bigbank.account.services.accountdata;

import java.rmi.RemoteException;

import org.osoa.sca.annotations.Service;

import com.bigbank.account.AccountFactory;
import com.bigbank.account.AccountLog;
import com.bigbank.account.AccountReport;
import com.bigbank.account.CustomerProfileData;
import com.bigbank.account.StockSummary;

@Service(AccountDataService.class)
public class AccountDataServiceImpl implements AccountDataService {

    public CustomerProfileData getCustomerProfile(String logonID) throws RemoteException {
        
        return null;
    }

    public AccountReport getAccountReport(int customerID) {
        
        return null;
    }

    public CustomerProfileData createAccount(CustomerProfileData customerProfile, boolean createSavings, boolean createCheckings)
            throws RemoteException {
        CustomerProfileData data = AccountFactory.INSTANCE.createCustomerProfileData();
        data.setLoginID(customerProfile.getLoginID());
        data.setAddress(customerProfile.getAddress());
        data.setEmail(customerProfile.getEmail());
        data.setFirstName(customerProfile.getFirstName());
        data.setId(customerProfile.getId());
        data.setLastName(customerProfile.getLastName());
        data.setPassword(customerProfile.getPassword());
        return data;
    }

    public float deposit(String param6, float param7) throws RemoteException {
        
        return 0;
    }

    public StockSummary purchaseStock(int param0, StockSummary stock) throws RemoteException {
        
        return null;
    }

    public StockSummary sellStock(int param13, int param14) throws RemoteException {
        
        return null;
    }

    public float withdraw(String param16, float param17) throws RemoteException {
        
        return 0;
    }

    public AccountLog getAccountLog(final int customerID) throws RemoteException {
        throw new RemoteException("This method should not be called");
    }
}
