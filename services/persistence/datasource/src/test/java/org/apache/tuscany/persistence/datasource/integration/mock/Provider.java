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
package org.apache.tuscany.persistence.datasource.integration.mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.PrintWriter;
import javax.sql.DataSource;

import org.apache.tuscany.persistence.datasource.DataSourceProvider;
import org.apache.tuscany.persistence.datasource.ProviderException;

/**
 * @version $Rev$ $Date$
 */
public class Provider implements DataSourceProvider, DataSource {

    private String test;

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void init() throws ProviderException {

    }

    public void close() throws ProviderException {

    }

    public DataSource getDataSource() throws ProviderException {
        return this;
    }

    public Connection getConnection() throws SQLException {
        return null;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }
}
