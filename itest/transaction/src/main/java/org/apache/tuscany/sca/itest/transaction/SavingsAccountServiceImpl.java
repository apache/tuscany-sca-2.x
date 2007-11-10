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

package org.apache.tuscany.sca.itest.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.XAConnection;

import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(AccountService.class)
@Scope("COMPOSITE")
public class SavingsAccountServiceImpl extends AccountServiceImpl {
    private final static Logger log = Logger.getLogger(SavingsAccountServiceImpl.class.getName());
    private EmbeddedXADataSource xads;

    @Init
    public void init() throws SQLException {
        // Create the database and a table
        xads = new EmbeddedXADataSource();
        xads.setDatabaseName("target/test");
        xads.setCreateDatabase("create");

        XAConnection xaconn = xads.getXAConnection();
        Connection conn = xaconn.getConnection();
        PreparedStatement ps =
            conn.prepareStatement("create table SavingsAccounts(accountNumber char(100), balance float)");
        try {
            ps.execute();
        } catch (SQLException ex) {
            log.info(ex.getMessage());
        }
        ps = conn.prepareStatement("delete from SavingsAccounts");
        ps.execute();
        
        ps = conn.prepareStatement("insert into SavingsAccounts(accountNumber, balance) values(?, ?)");
        for (int i = 0; i < 2; i++) {
            ps.setString(1, "S00" + (i+1));
            ps.setFloat(2, (float)(1000.0f + Math.random() * 500.0));
            ps.executeUpdate();
        }
        conn.commit();
        conn.close();
    }

    @Override
    protected float load(String accountNumber) throws AccountNotFoundException {
        try {
            XAConnection xaconn = xads.getXAConnection();

            Connection conn = xaconn.getConnection();
            PreparedStatement ps = conn.prepareStatement("select balance from SavingsAccounts where accountNumber=?");
            ps.setString(1, accountNumber);
            ResultSet rs1 = ps.executeQuery();
            boolean found = rs1.next();
            if (found) {
                float balance = rs1.getFloat(1);
                conn.commit();
                conn.close();
                return balance;
            } else {
                conn.commit();
                conn.close();
                throw new AccountNotFoundException(accountNumber);
            }
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    @Override
    protected void save(String accountNumber, float balance) throws AccountNotFoundException {
        try {
            XAConnection xaconn = xads.getXAConnection();

            Connection conn = xaconn.getConnection();
            PreparedStatement ps = conn.prepareStatement("update SavingsAccounts set balance=? where accountNumber=?");
            ps.setFloat(1, balance);
            ps.setString(2, accountNumber);
            int rows = ps.executeUpdate();
            conn.commit();
            boolean found = (rows >= 1);
            if (found) {
                conn.close();
            } else {
                conn.close();
                throw new AccountNotFoundException(accountNumber);
            }
        } catch (SQLException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    @Destroy
    public void destroy() throws SQLException {
        XAConnection xaconn = xads.getXAConnection();
        Connection conn = xaconn.getConnection();
        PreparedStatement ps = conn.prepareStatement("drop table SavingsAccounts");
        ps.execute();
        conn.commit();
        conn.close();
    }

}
