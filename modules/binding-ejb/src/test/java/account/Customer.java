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
package account;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface Customer {

    /**
     * This method deposits the amount. method accesses external EJB to get the 
     * current balance and add the amount to existing balance.
     *
     * @param String amount to be deposited
     * @return total amount in customer accound after deposit
     */
    Double depositAmount(java.lang.String accountNo, Double amount);

}
