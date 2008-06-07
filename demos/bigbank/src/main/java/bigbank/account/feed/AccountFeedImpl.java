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
package bigbank.account.feed;

import org.apache.tuscany.sca.data.collection.Collection;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import bigbank.account.AccountService;

/**
 * @version $$Rev$$ $$Date$$
 */

@Service(Collection.class)
public class AccountFeedImpl implements Collection<String, String> {

    @Reference
    protected AccountService accountService;
    
    public Entry<String, String>[] getAll() {

        // Add the Account report entry 
        String report = get("1234");
        Entry<String, String> entry = new Entry<String, String>("1234", report);

        return new Entry[] { entry } ;
    }

    public String get(String id) {

        // Get the account report for the specified customer ID
        double balance = accountService.getAccountReport(id); 
        String report = Double.toString(balance);
        
        return report;
    }

    public void delete(String key) throws NotFoundException {
    }

    public String post(String key, String item) {
        return null;
    }

    public void put(String key, String item) throws NotFoundException {
    }

    public Entry<String, String>[] query(String queryString) {
        return null;
    }
}
