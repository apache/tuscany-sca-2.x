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

import org.osoa.sca.annotations.AllowsPassByReference;
import org.osoa.sca.annotations.Remotable;

@Remotable
@AllowsPassByReference
public interface AccountDataService {

    /**
     * Auto generated method signatures
     * 
     * @param param0*
     * @param param1*
     * @param param2
     */
    public com.bigbank.account.StockSummary purchaseStock(int param0, com.bigbank.account.StockSummary parm1);

    /**
     * Auto generated method signatures
     * 
     * @param param4
     */
    public com.bigbank.account.CustomerProfileData getCustomerProfile(java.lang.String param4);

    /**
     * Auto generated method signatures
     * 
     * @param param6*
     * @param param7
     */
    public float deposit(java.lang.String param6, float param7);

    /**
     * Auto generated method signatures
     * 
     * @param param9*
     * @param param10*
     * @param param11
     */
    public com.bigbank.account.CustomerProfileData createAccount(com.bigbank.account.CustomerProfileData param9, boolean param10, boolean param11);

    /**
     * Auto generated method signatures
     * 
     * @param param13*
     * @param param14
     */
    public com.bigbank.account.StockSummary sellStock(int param13, int param14);

    /**
     * Auto generated method signatures
     * 
     * @param param16*
     * @param param17
     */
    public float withdraw(java.lang.String param16, float param17);

    /**
     * Auto generated method signatures
     * 
     * @param param19
     */
    public com.bigbank.account.AccountReport getAccountReport(int param19);

    /**
     * Auto generated method signatures
     * 
     * @param param0
     */
    public com.bigbank.account.AccountLog getAccountLog(int param0);

}
