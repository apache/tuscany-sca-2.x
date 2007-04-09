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
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.tuscany.spi.services.store.StoreReadException;
import org.apache.tuscany.spi.services.store.StoreWriteException;

/**
 * Converters mediate the particularities of specific databases and JDBC drivers such as data types during read and
 * write operations
 *
 * @version $Rev$ $Date$
 */
public interface Converter {

    /**
     * Returns the SQL statement to select a record for update
     *
     * @return the SQL statement to select a record for update
     */
    String getSelectUpdateSql();

    /**
     * Returns the SQL statement to add a record
     *
     * @return the SQL statement to add a record
     */
    String getInsertSql();

    /**
     * Returns the SQL statement to update a record
     *
     * @return the SQL statement to update a record
     */
    String getUpdateSql();

    /**
     * Returns the SQL statement to retrieve a record
     *
     * @return the SQL statement to retrieve a record
     */
    String getFindSql();

    /**
     * Returns the SQL statement to remove a record
     *
     * @return the SQL statement to remove a record
     */
    String getDeleteSql();

    /**
     * Returns the SQL statement to remove expired records
     *
     * @return the SQL statement to remove expired records
     */
    String getDeleteExpiredSql();

    String getDeleteRecordSql();

    /**
     * Writes a new record to the underlying store using batch semantics. That is, the insert will be added as a batch
     * operation to the prepared statment. It is the responsibility of the client (i.e. the prepared statement "owner")
     * to exectute the statement when the batch threshold is reached. Note implementations must assume auto commit is
     * false.
     *
     * @param stmt
     * @param ownerId
     * @param id
     * @param expiration
     * @param object
     * @throws org.apache.tuscany.spi.services.store.StoreWriteException
     *
     */
    void insert(PreparedStatement stmt, String ownerId, String id, long expiration, Serializable object)
        throws StoreWriteException;

    /**
     * @param stmt
     * @param ownerId
     * @param id
     * @param object
     * @throws StoreWriteException
     */
    void update(PreparedStatement stmt, String ownerId, String id, Serializable object) throws StoreWriteException;

    /**
     * @param stmt
     * @param ownerId
     * @param id
     * @return
     * @throws StoreWriteException
     */
    boolean findAndLock(PreparedStatement stmt, String ownerId, String id) throws StoreWriteException;

    /**
     * Reads a record from the underlying store. Note implementations must assume auto commit is false.
     *
     * @param conn
     * @param ownerId
     * @param id
     * @return
     * @throws StoreReadException
     */
    Object read(Connection conn, String ownerId, String id) throws StoreReadException;

    /**
     * @param stmt
     * @param ownerId
     * @param id
     * @throws StoreWriteException
     */
    void delete(PreparedStatement stmt, String ownerId, String id) throws StoreWriteException;

}
