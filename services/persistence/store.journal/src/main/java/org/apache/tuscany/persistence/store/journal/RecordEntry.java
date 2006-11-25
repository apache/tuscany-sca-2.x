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
package org.apache.tuscany.persistence.store.journal;

import java.io.Serializable;

/**
 * A journal store cache entry for a record
 *
 * @version $Rev$ $Date$
 */
public class RecordEntry {
    private Serializable object;
    private short operation;
    private long expiration;
    private long[] journalKeys;


    /**
     * Creates a new cached record
     *
     * @param object     the object to serialize
     * @param operation  an <code>INSERT</code> or <code>UPDATE</code> operation
     * @param journalKey the journal key for the block where the record is written
     */
    public RecordEntry(Serializable object, short operation, long[] journalKey, long expiration) {
        assert object != null;
        this.object = object;
        this.operation = operation;
        this.journalKeys = journalKey;
        this.expiration = expiration;
    }

    /**
     * Returns the object
     *
     * @return the object
     */
    public Serializable getObject() {
        return object;
    }

    /**
     * Returns the type of operation
     *
     * @return the type of operation
     */
    public short getOperation() {
        return operation;
    }

    public long getExpiration() {
        return expiration;
    }

    public long[] getJournalKeys() {
        return journalKeys;
    }

    public void setJournalKeys(long[] journalKeys) {
        this.journalKeys = journalKeys;
    }

}
