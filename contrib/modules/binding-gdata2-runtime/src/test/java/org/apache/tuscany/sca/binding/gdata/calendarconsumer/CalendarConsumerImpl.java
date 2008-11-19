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
package org.apache.tuscany.sca.binding.gdata.calendarconsumer;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;

import org.apache.tuscany.sca.binding.gdata.collection.Collection;
import org.osoa.sca.annotations.Reference;

public class CalendarConsumerImpl {

    @Reference
    public Collection resourceCollection;

    public BaseFeed getFeed() {
        return resourceCollection.getFeed();
    }

    public BaseEntry post(BaseEntry entry) {
        return resourceCollection.post(entry);
    }

    public BaseEntry get(String id) throws org.apache.tuscany.sca.implementation.data.collection.NotFoundException {
        return resourceCollection.get(id);
    }

    public BaseEntry put(String id, BaseEntry entry) throws org.apache.tuscany.sca.implementation.data.collection.NotFoundException {
        return resourceCollection.put(id, entry);
    }

    public void delete(String id) throws org.apache.tuscany.sca.implementation.data.collection.NotFoundException {
        resourceCollection.delete(id);
    }

    public BaseFeed query(String queryString) {
        return resourceCollection.query(queryString);
    }
}
