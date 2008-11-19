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
package org.apache.tuscany.sca.data.collection;

import org.osoa.sca.annotations.Remotable;

/**
 * Provides access to a collection of data items.
 * 
 * @version $Rev$ $Date$
 */
@Remotable
public interface Collection <K, D> {

    /**
     * Get the whole collection.
     * 
     * @return the whole collection.
     */
    Entry<K, D>[] getAll();

    /**
     * Returns a collection resulting from a query.
     * 
     * @return the collection.
     */
    Entry<K, D>[] query(String queryString);

    /**
     * Creates a new item.
     * 
     * @param key
     * @param item
     * @return
     */
    K post(K key, D item);

    /**
     * Retrieves an item.
     * 
     * @param key
     * @return
     */
    D get(K key) throws NotFoundException;

    /**
     * Updates an item.
     * 
     * @param key
     * @param item
     * @return
     */
    void put(K key, D item) throws NotFoundException;

    /**
     * Delete an item.
     * 
     * @param key
     */
    void delete(K key) throws NotFoundException;

}
