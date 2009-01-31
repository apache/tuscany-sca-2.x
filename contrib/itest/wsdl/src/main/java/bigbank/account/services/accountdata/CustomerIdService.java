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

/**
 * This interface allows access to a customer's id from the data service.
 */
public interface CustomerIdService {

    /*
     * Return -1 if purchaseLotNumber not found
     */
    public int getCustomerIdByPurchaseLotNumber(int purchaseLotNumber) throws RemoteException;

    /*
     * Return -1 if account not found
     */
    public int getCustomerIdByAccount(String account) throws RemoteException;

    // TODO (isilval) Fix this !!!
    // Apparently, configuration can't deal with extends so I am adding the contents of AccountDataService here

    /**
     * Auto generated method signatures
     * 
     * @param param0*
     * @param param1*
     * @param param2
     */
    public com.bigbank.account.StockSummary purchaseStock(int param0, com.bigbank.account.StockSummary parm1) throws java.rmi.RemoteException;

    /**
     * Auto generated method signatures
     * 
     * @param param4
     */
    public com.bigbank.account.CustomerProfileData getCustomerProfile(java.lang.String param4) throws java.rmi.RemoteException;

    /**
     * Auto generated method signatures
     * 
     * @param param6*
     * @param param7
     */
    public float deposit(java.lang.String param6, float param7) throws java.rmi.RemoteException;

    /**
     * Auto generated method signatures
     * 
     * @param param9*
     * @param param10*
     * @param param11
     */
    public com.bigbank.account.CustomerProfileData createAccount(com.bigbank.account.CustomerProfileData param9, boolean param10, boolean param11)
            throws java.rmi.RemoteException;

    /**
     * Auto generated method signatures
     * 
     * @param param13*
     * @param param14
     */
    public com.bigbank.account.StockSummary sellStock(int param13, int param14) throws java.rmi.RemoteException;

    /**
     * Auto generated method signatures
     * 
     * @param param16*
     * @param param17
     */
    public float withdraw(java.lang.String param16, float param17) throws java.rmi.RemoteException;

    /**
     * Auto generated method signatures
     * 
     * @param param19
     */
    public com.bigbank.account.AccountReport getAccountReport(int param19) throws java.rmi.RemoteException;

}
