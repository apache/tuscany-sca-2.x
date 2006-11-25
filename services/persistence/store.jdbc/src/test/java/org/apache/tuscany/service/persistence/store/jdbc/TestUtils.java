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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * @version $Rev$ $Date$
 */
public final class TestUtils {

    public static final String CREATE_TABLE =
        "CREATE TABLE CONVERSATION_STATE(OWNER VARCHAR, ID VARCHAR, EXPIRATION BIGINT, "
            + "OBJECT LONGVARBINARY);";

    public static final String DROP_TABLE = "DROP TABLE CONVERSATION_STATE IF EXISTS";
    public static final String SELECT_SQL = "SELECT * FROM CONVERSATION_STATE";

    private TestUtils() {
    }

    public static DataSource createTables() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:mem:test");
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.createStatement().execute(CREATE_TABLE);
            conn.commit();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return ds;
    }

    public static void cleanup(DataSource ds) throws SQLException {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.createStatement().execute(DROP_TABLE);
            conn.createStatement().execute("SHUTDOWN");
            conn.commit();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
