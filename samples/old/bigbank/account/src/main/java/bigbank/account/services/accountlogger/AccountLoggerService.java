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
package bigbank.account.services.accountlogger;

import java.rmi.RemoteException;

import org.osoa.sca.annotations.OneWay;

import com.bigbank.account.AccountLog;
import com.bigbank.account.StockSummary;

/**
 * This is the business interface of the Account Logger service component.
 */
public interface AccountLoggerService {

    @OneWay
    public void logDeposit(int id, String account, float amount) throws RemoteException;

    @OneWay
    public void logWithdrawal(int id, String account, float amount) throws RemoteException;

    @OneWay
    public void logPurchaseStock(int id, StockSummary stock) throws RemoteException;

    @OneWay
    public void logSellStock(int id, StockSummary stock, int quantity) throws RemoteException;

    public AccountLog getAccountLog(int id) throws RemoteException;

}
