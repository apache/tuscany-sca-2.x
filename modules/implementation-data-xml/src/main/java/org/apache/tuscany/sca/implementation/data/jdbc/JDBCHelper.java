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

package org.apache.tuscany.sca.implementation.data.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tuscany.das.rdb.exception.DataSourceInitializationException;
import org.apache.tuscany.sca.data.engine.config.ConnectionInfo;

/**
 * JDBC Helper
 *    - JDBC Connection utility methods
 *    - JDBC Resource cleanup methods
 *    
 * @version $Rev$ $Date$
 */
public class JDBCHelper {
    
    /**
     * protected constructor
     */
    protected JDBCHelper() {
        
    }

    /**
     * 
     * @param connectionInfo
     * @return
     */
    public static Connection getConnection(ConnectionInfo connectionInfo) {
        if (connectionInfo.getDataSource() == null && connectionInfo.getConnectionProperties() == null) {
            throw new IllegalArgumentException("Not enough information to create Database Connection.");
        }
        
        if(connectionInfo.getDataSource() != null && connectionInfo.getConnectionProperties() != null) {
            throw new IllegalArgumentException("Use either dataSource or ConnectionProperties. Can't use both !");            
        }
        
        if(connectionInfo.getDataSource() != null) {
            return getDataSourceConnection(connectionInfo);
        } else {
            return getDriverManagerConnection(connectionInfo);
        }
    }

    /**
     * Initializes a DB connection on a managed environmet (e.g inside Tomcat)
     * 
     * @param connectionInfo
     * @return
     */
    private static Connection getDataSourceConnection(ConnectionInfo connectionInfo) {
        Connection connection = null;

        InitialContext ctx;
        try {
            ctx = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        try {
            DataSource ds = (DataSource) ctx.lookup(connectionInfo.getDataSource());
            try {
                connection = ds.getConnection();
                if (connection == null) {
                    throw new RuntimeException("Could not obtain a Connection from DataSource");
                }
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        
        return connection;
    }

    /**
     * Initialize a DB connection on a J2SE environment
     * For more info, see http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/drivermanager.html
     * 
     * @param connectionInfo
     * @return
     */
    private static Connection getDriverManagerConnection(ConnectionInfo connectionInfo) {
        Connection connection = null;

        if (connectionInfo.getConnectionProperties() == null) {
            throw new RuntimeException("No existing context and no connection properties");
        }

        if (connectionInfo.getConnectionProperties().getDriverClass() == null) {
            throw new RuntimeException("No jdbc driver class specified!");
        }

        try {
            Properties p = System.getProperties();
            p.put("derby.system.home", "target");
            
            //initialize driver and register it with DriverManager
            Class.forName(connectionInfo.getConnectionProperties().getDriverClass());

            //prepare to initialize connection
            String databaseUrl = connectionInfo.getConnectionProperties().getDatabaseURL();
            String userName = connectionInfo.getConnectionProperties().getUsername();
            String userPassword = connectionInfo.getConnectionProperties().getPassword();
            int loginTimeout = connectionInfo.getConnectionProperties().getLoginTimeout();

            DriverManager.setLoginTimeout(loginTimeout);
            if( (userName == null || userName.length() ==0) && (userPassword == null || userPassword.length()==0) ){
                //no username or password suplied
                connection = DriverManager.getConnection(databaseUrl);
            }else{
                connection = DriverManager.getConnection(databaseUrl, userName, userPassword);
            }

            if(connection == null){
                throw new DataSourceInitializationException("Error initializing connection : null");
            }
            
            //FIXME we should make this flexible, we can't autocommit when participating in transactions
            connection.setAutoCommit(true);
            
        }catch(ClassNotFoundException cnf){
            throw new DataSourceInitializationException("JDBC Driver '" + connectionInfo.getConnectionProperties().getDriverClass() + "' not found", cnf);
        }catch(SQLException sqle){
            throw new DataSourceInitializationException(sqle.getMessage(), sqle);
        }
        
        return connection;
    }
    
    /**
     * Cleanup and close all jdbc resources in the proper order and ignoring erros
     * @param connection The connection to be closed
     * @param queryStatement The statement to be closed
     * @param resultSet The Resultset to be closed
     */
    public static void cleanupResources(Connection connection, PreparedStatement queryStatement, ResultSet resultSet) {
        cleanupResultSet(resultSet);
        cleanupPreparedStatement(queryStatement);
        cleanupConnection(connection);
    }

    /**
     * Proper cleanup the resultset
     * @param resultSet
     */
    private static void cleanupResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // We should log the error. Since we're trying to close, we don't re-throw.
            }
        }
    }

    /**
     * Proper cleanup the prepared statement
     * @param queryStatement
     */
    private static void cleanupPreparedStatement(PreparedStatement queryStatement) {
        if (queryStatement != null) {
            try {
                queryStatement.close();
            } catch (SQLException e) {
                // We should log the error. Since we're trying to close, we don't re-throw.
            }
        }
    }

    /**
     * proper cleanup the connection
     * @param connection
     */
    private static void cleanupConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // We should log the error. Since we're trying to close, we don't re-throw.
            }
        }
    }    

}
