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
package org.apache.tuscany.service.persistence.store.jdbc;

import java.io.Serializable;

/**
 * Represents a persistent object and its metadata.
 * <p/>
 * Note this class has a natural ordering that is inconsistent with equals.
 *
 * @version $Rev$ $Date$
 */
public class Record implements Comparable {
    public static final int INSERT = 0;
    public static final int UPDATE = 1;

    private String ownerId;
    private String id;
    private Serializable object;
    private long expiration = JDBCStore.NEVER;
    private int operation;

    /**
     * Creates a new record
     *
     * @param ownerId
     * @param id         the unique id of the record
     * @param object     the object to serialize
     * @param expiration the expirary time, {@link org.apache.tuscany.spi.services.store.Store.NEVER} if there is no
     *                   expiration
     * @param operation  an <code>INSERT</code> or <code>UPDATE</code> operation
     */
    public Record(String ownerId, String id, Serializable object, long expiration, int operation) {
        this.id = id;
        this.object = object;
        this.expiration = expiration;
        this.operation = operation;
        this.ownerId = ownerId;
    }

    /**
     * Returns the unique object id
     *
     * @return the unique object id
     */
    public String getId() {
        return id;
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
     * Sets the object to serialize
     *
     * @param object the object
     */
    public void setObject(Serializable object) {
        this.object = object;
    }

    /**
     * Returns the expiration time
     *
     * @return the expiration time
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Returns the type of operation
     *
     * @return the type of operation
     */
    public int getOperation() {
        return operation;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int compareTo(Object o) {
        assert o instanceof Record;
        Record record = (Record) o;
        if (record.getOperation() == operation) {
            return 0;
        } else if (record.getOperation() == INSERT) {
            return 1;
        } else if (record.getOperation() == UPDATE) {
            return -1;
        } else {
            throw new AssertionError();
        }
    }
}
