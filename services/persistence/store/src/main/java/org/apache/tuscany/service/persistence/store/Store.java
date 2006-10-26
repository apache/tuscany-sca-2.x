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
package org.apache.tuscany.service.persistence.store;

/**
 * Implementations provide a persistent store for runtime data such as conversational state. A persistent store could be
 * implemented in a durable fashion using JDBC or a journaling system, or using a non-durable mechanism such as an
 * in-memory map.
 *
 * @version $Rev$ $Date$
 */
public interface Store {

    /* Used to indicate an entry should not expire */
    static final long NEVER = -1;

    /**
     * Writes the given record to the store. Implementations may choose different strategies for writing data such as
     * write-through or write-behind.
     *
     * @param id     the unique id of the record
     * @param record the data to persist
     * @throws StoreWriteException if an error occurs during the write operation
     */
    void writeRecord(Object id, Object record) throws StoreWriteException;

    /**
     * Writes the given record to the store. Implementations may choose different strategies for writing data such as
     * write-through or write-behind.
     *
     * @param id         the unique id of the record
     * @param record     the data to persist
     * @param expiration the time in milliseconds when the entry expires
     * @throws StoreWriteException if an error occurs during the write operation
     */
    void writeRecord(Object id, Object record, long expiration) throws StoreWriteException;

    /**
     * Writes the given record to the store. If force is true, the data must be written in synchronously
     *
     * @param id     the unique id of the record
     * @param record the data to persist
     * @param force  if true writes the data synchronously
     * @throws StoreWriteException if an error occurs during the write operation
     */
    void writeRecord(Object id, Object record, boolean force) throws StoreWriteException;

    /**
     * Writes the given record to the store. If force is true, the data must be written in synchronously
     *
     * @param id         the unique id of the record
     * @param record     the data to persist
     * @param force      if true writes the data synchronously
     * @param expiration the time in milliseconds when the entry expires
     * @throws StoreWriteException if an error occurs during the write operation
     */
    void writeRecord(Object id, Object record, long expiration, boolean force) throws StoreWriteException;

    /**
     * Returns a record in the store corresponding to the given id
     */
    Object readRecord(Object id);

    /**
     * Removes all records from the store
     */
    void removeRecords();

    /**
     * Initiates a recovery operation, for example during restart after a crash
     *
     * @param listener the listener to receive recovery callback events
     */
    void recover(RecoveryListener listener);

}
