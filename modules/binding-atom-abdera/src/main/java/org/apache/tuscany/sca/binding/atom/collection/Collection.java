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
package org.apache.tuscany.sca.binding.atom.collection;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.osoa.sca.annotations.Remotable;

/**
 * Provides access to a collection of resources using Atom.
 * 
 * @version $Rev$ $Date$
 */
@Remotable
public interface Collection {

    /**
     * Get an Atom feed for a collection of resources.
     * 
     * @return the Atom feed
     */
    Feed getFeed();

    /**
     * Get an Atom feed for a collection of resources resulting
     * from a query.
     * 
     * @param queryString a query string
     * @return the Atom feed
     */
    Feed query(String queryString);

    /**
     * Creates a new entry.
     * 
     * @param entry
     * @return
     */
    Entry post(Entry entry);

    /**
     * Retrieves an entry.
     * 
     * @param id
     * @return
     */
    Entry get(String id) throws NotFoundException;

    /**
     * Update an entry.
     * 
     * @param id
     * @param entry
     * @return
     */
    void put(String id, Entry entry) throws NotFoundException;

    /**
     * Delete an entry.
     * 
     * @param id
     */
    void delete(String id) throws NotFoundException;

}
