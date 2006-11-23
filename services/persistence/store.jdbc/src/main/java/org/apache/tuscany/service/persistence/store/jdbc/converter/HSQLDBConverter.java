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
package org.apache.tuscany.service.persistence.store.jdbc.converter;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.tuscany.spi.services.store.StoreReadException;

import org.apache.tuscany.spi.services.store.StoreWriteException;

/**
 * Performs writing and reading operations to HSQLDB
 *
 * @version $Rev$ $Date$
 */
public class HSQLDBConverter extends AbstractConverter {

    // HSQLDB does not support SELECT FOR UPDATE
    protected String selectUpdateSql = "SELECT ID_1 FROM CONVERSATION_STATE WHERE  OWNER = ? AND ID_1 = ? AND ID_2 = ?";

    public String getSelectUpdateSql() {
        return selectUpdateSql;
    }

    public void insert(PreparedStatement stmt, String ownerId, UUID id, long expiration, Serializable object)
        throws StoreWriteException {
        try {
            stmt.setString(OWNER, ownerId);
            stmt.setLong(MOST_SIGNIFICANT_BITS, id.getMostSignificantBits());
            stmt.setLong(LEAST_SIGNIFICANT_BITS, id.getLeastSignificantBits());
            stmt.setLong(EXPIRATION, expiration);
            stmt.setBytes(DATA, serialize(object));
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } catch (IOException e) {
            throw new StoreWriteException(e);
        }
    }

    public void update(PreparedStatement stmt, String ownerId, UUID id, Serializable object)
        throws StoreWriteException {
        try {
            stmt.setBytes(OBJECT_UPDATE, serialize(object));
            stmt.setString(OWNER_UPDATE, ownerId);
            stmt.setLong(MOST_SIGNIFICANT_BITS_UPDATE, id.getMostSignificantBits());
            stmt.setLong(LEAST_SIGNIFICANT_BITS_UPDATE, id.getLeastSignificantBits());
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } catch (IOException e) {
            throw new StoreWriteException(e);
        }
    }

    public Object read(Connection conn, String ownerId, UUID id) throws StoreReadException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findSql);
            stmt.setString(OWNER, ownerId);
            stmt.setLong(MOST_SIGNIFICANT_BITS, id.getMostSignificantBits());
            stmt.setLong(LEAST_SIGNIFICANT_BITS, id.getLeastSignificantBits());
            ResultSet rs = stmt.executeQuery();
            boolean more = rs.next();
            if (!more) {
                return null;
            }
            return deserialize(rs.getBytes(DATA));
        } catch (SQLException e) {
            throw new StoreReadException(e);
        } catch (IOException e) {
            throw new StoreReadException(e);
        } catch (ClassNotFoundException e) {
            throw new StoreReadException(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}
