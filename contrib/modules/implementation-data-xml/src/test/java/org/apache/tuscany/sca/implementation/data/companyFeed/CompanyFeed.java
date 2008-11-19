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

package org.apache.tuscany.sca.implementation.data.companyFeed;

import org.apache.tuscany.sca.data.collection.Collection;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.implementation.data.DATA;
import org.osoa.sca.annotations.Reference;

import commonj.sdo.DataObject;

public class CompanyFeed implements Collection<String, DataObject> {
    
    @Reference
    protected DATA dataService;
    
    public Entry<String, DataObject>[] getAll() {
        return null;
    }

    public DataObject get(String id) throws NotFoundException{
        
        DataObject data = null;//dataService.get(id);        
        if(data == null) {
            throw new NotFoundException();
        } else {
            return data;
        }
    }

    public void delete(String id) throws NotFoundException {
    }

    public String post(String key, DataObject item) {
        return null;
    }
    
    public void put(String key, DataObject item) throws NotFoundException {
    }
    
    public Entry<String, DataObject>[] query(String queryString) {
        return null;
    }
}
