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

/**
 * A header entry for a record written to the log. A header contains the following information:
 * <pre>
 * <ul>
 *  <li><strong>Owner id</strong> - the unique id of the owner, typically the SCAObject canonical name
 *  <li><strong>Operation</strong> - if the record is an INSERT, UPDATE, or DELETE
 *  <li><strong>Most Significant bits</strong> - the most signficant bits of the <code>java.util.UUID</code>
 * representing the id of the persisted instance
 *  <li><strong>Least Significant bits</strong> - the least signficant bits of the <code>java.util.UUID</code>
 * representing the id of the persisted instance
 *  <li><strong>Expiration</strong> - the expiration time in milliseconds when the record is set to expire
 *  <li><strong>Number of blocks</strong> - the number of blocks the record is written across
 *  <li><strong>Fields</strong> - any other data associated with the header
 * </ul>
 * </pre>
 *
 * @version $Rev$ $Date$
 */
public class Header {
    public static final short INSERT = 0;
    public static final short UPDATE = 1;
    public static final short DELETE = 2;

    protected byte[][] fields;
    private short operation;
    private String ownerId;
    private String id;
    private long expiration;
    private int numBlocks;

    public Header() {
    }

    /**
     * Returns the record operation type
     *
     * @return the record operation type
     */
    public short getOperation() {
        return operation;
    }

    /**
     * Sets the record operation type
     */
    public void setOperation(short operation) {
        this.operation = operation;
    }

    /**
     * Returns the number of blocks the record is written across
     *
     * @return the number of blocks the record is written across
     */
    public int getNumBlocks() {
        return numBlocks;
    }

    /**
     * Sets the number of blocks the record is written across
     */
    public void setNumBlocks(int numBlocks) {
        this.numBlocks = numBlocks;
    }

    /**
     * Returns the unique owner id
     *
     * @return the unique owner id
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the unique owner id
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Returns the record id
     *
     * @return the record id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the most significant bits of the record UUID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the time in milliseconds the record is set to expire
     *
     * @return the time in milliseconds the record is set to expire
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Sets the time the record is set to expire in milliseconds
     */
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    /**
     * Returns additional header data
     *
     * @return additional header data
     */
    public byte[][] getFields() {
        return fields;
    }

    /**
     * Sets additional header data
     */
    public void setFields(byte[][] fields) {
        this.fields = fields;
    }

}
