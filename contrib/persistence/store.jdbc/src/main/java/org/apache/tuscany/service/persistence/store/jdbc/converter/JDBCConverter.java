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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.tuscany.spi.services.store.StoreReadException;
import org.apache.tuscany.spi.services.store.StoreWriteException;

/**
 * Performs writing and reading operations to a JDBC driver/database combination that supports Blobs
 *
 * @version $Rev$ $Date$
 */
public class JDBCConverter extends AbstractConverter {

    public void insert(PreparedStatement stmt, String ownerId, String id, long expiration, Serializable object)
        throws StoreWriteException {
        try {
            stmt.setString(OWNER, ownerId);
            stmt.setString(ID, id);
            stmt.setLong(EXPIRATION, expiration);
            byte[] data = serialize(object);
            InputStream in = new ByteArrayInputStream(data);
            stmt.setBinaryStream(DATA, in, data.length);
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        } catch (IOException e) {
            throw new StoreWriteException(e);
        }
    }

    public void update(PreparedStatement stmt, String ownerId, String id, Serializable object)
        throws StoreWriteException {
        throw new UnsupportedOperationException();
    }

    public Object read(Connection conn, String ownerId, String id) throws StoreReadException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findSql);
            stmt.setString(OWNER, ownerId);
            stmt.setString(ID, id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            Blob blob = rs.getBlob(DATA);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = new BufferedInputStream(blob.getBinaryStream());
            int b;
            byte[] buffer = new byte[1024];
            while ((b = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, b);
            }
            return deserialize(out.toByteArray());
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