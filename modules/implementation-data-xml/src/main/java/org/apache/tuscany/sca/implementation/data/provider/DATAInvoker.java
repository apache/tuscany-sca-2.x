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

package org.apache.tuscany.sca.implementation.data.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.tuscany.sca.data.engine.config.ConnectionInfo;
import org.apache.tuscany.sca.implementation.data.jdbc.JDBCHelper;
import org.apache.tuscany.sca.implementation.data.jdbc.JDBCResultSetStreamReader;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;


/**
 * Implements a target invoker for DAS component implementations.
 *
 * The target invoker is responsible for dispatching invocations to the particular
 * component implementation logic. The current component implementation will
 * dispatch calls to the DAS apis to retrieve the requested data from the backend store
 *
 * @version $Rev$ $Date$
 */
public class DATAInvoker implements Invoker {
    protected final Operation operation;
    protected final ConnectionInfo connectionInfo;
    protected final String table;
    
    public DATAInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
        this.operation = operation;
        this.connectionInfo = connectionInfo;
        this.table = table;
    }
    
    public Message invoke(Message msg) {
        // Shouldn't get here, as the only supported operations
        // are the ones defined DATA interface and implemented
        // by specific invoker subclasses
        
        throw new UnsupportedOperationException(operation.getName());
    }
    
    
    /****************************************************************
     *
     * Internal invoker implementations for each supported operation
     *
     *****************************************************************/
    
    
    /**
     * Get operation invoker
     *
     * @version $Rev$ $Date$
     */
    public static class GetInvoker extends DATAInvoker {
        
        public GetInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }
        
        @Override
        public Message invoke(Message msg) {
            
            // Get an entry
            String sqlQuery = null;
            String id = (String)((Object[])msg.getBody())[0];
            
            if (id == null) {
                sqlQuery = "SELECT * FROM " + this.table;
            } else {
                sqlQuery = "SELECT * FROM " + this.table + " WHERE ID = " + id;
            }
            
            Connection connection = null;
            PreparedStatement queryStatement = null;
            ResultSet resultSet = null;
            try {
                connection = JDBCHelper.getConnection(connectionInfo);
                queryStatement = connection.prepareStatement(sqlQuery);
                resultSet = queryStatement.executeQuery();
                
                
            } catch(SQLException sqle) {
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
                JDBCHelper.cleanupResources(connection, queryStatement, resultSet);
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
                JDBCHelper.cleanupResources(connection, queryStatement, resultSet);
            } finally {
                //default we leave the connection open to pass to the JDBCStreamReader
            }
            
            msg.setBody(new JDBCResultSetStreamReader(resultSet));
            return msg;
        }
    }    
    
    /**
     * Delete operation invoker
     */
    public static class DeleteInvoker extends DATAInvoker {
        
        public DeleteInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }
        
        @Override
        public Message invoke(Message msg) {
            
            // Get an entry
            String sqlQuery = null;
            String id = (String)((Object[])msg.getBody())[0];
            
            if (id == null) {
                sqlQuery = "DELETE FROM " + this.table;
            } else {
                sqlQuery = "DELETE FROM " + this.table + " WHERE ID = " + id;
            }
            
            Connection connection = null;
            PreparedStatement queryStatement = null;
            int result = -1;
            
            try {
                connection = JDBCHelper.getConnection(connectionInfo);
                queryStatement = connection.prepareStatement(sqlQuery);
                result = queryStatement.executeUpdate();
                
            } catch(SQLException sqle) {
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                JDBCHelper.cleanupResources(connection, queryStatement, null);
            }            
            
            msg.setBody(result);
            return msg;            
        }
    }
    
}
