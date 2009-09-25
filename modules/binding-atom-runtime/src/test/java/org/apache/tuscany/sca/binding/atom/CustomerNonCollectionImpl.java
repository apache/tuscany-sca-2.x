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

package org.apache.tuscany.sca.binding.atom;

import org.apache.tuscany.sca.data.collection.Entry;
import org.osoa.sca.annotations.Scope;

/**
 * Implementation of an Atom feed that does not implement the Collections
 * interface but does have a getAll() method that will be used by the Atom
 * binding to get the feed entries.
 */
@Scope("COMPOSITE")
public class CustomerNonCollectionImpl {
    /**
     * All feed entries.
     * This is set directly by the unit tests.
     * @see AtomFeedNonCollectionTest
     */
    public static Entry<Object, Object>[] entries;

    /**
     * Default constructor 
     */
    public CustomerNonCollectionImpl() {
    }

    /**
     * Get all entries for this feed.
     * 
     * @return All entries for this feed
     */
    public Entry<Object, Object>[] getAll() {
        return entries;
    }
}
