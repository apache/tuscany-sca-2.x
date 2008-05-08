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
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

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
 * dispatch calls to the DAS APIs to retrieve the requested data from the back-end store
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
     * GetAll operation invoker
     */
    public static class GetAllInvoker extends DATAInvoker {

        public GetAllInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) {
            return msg;
        }
    }

    /**
     * Query operation invoker
     */
    public static class QueryInvoker extends DATAInvoker {

        public QueryInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) {
            return msg;
        }
    }

    /**
     * Post operation invoker
     */
    public static class PostInvoker extends DATAInvoker {

        public PostInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) {
            
            StringBuilder sqlInsert = new StringBuilder();
            XMLStreamReader insertStream = (XMLStreamReader) ((Object[]) msg.getBody())[1];

            if (insertStream == null) {
                throw new IllegalArgumentException("The XMLStreamReader \"insertStream\" must not be null");
            }


            Connection connection = null;
            PreparedStatement inStmt = null;

            List<String> colNames = new ArrayList<String>();
            List<String> values = new ArrayList<String>();

            int result = 0;
            try {

                connection = JDBCHelper.getConnection(connectionInfo);

                while (insertStream.hasNext()) {

                    insertStream.next();
                    if (insertStream.isStartElement()) {
                        if (insertStream.getLocalName().equals("record")) {
                            sqlInsert.append("INSERT INTO " + this.table + " (");
                        } else if (insertStream.getLocalName().equals("column")) {
                            colNames.add(insertStream.getAttributeValue(0));
                            insertStream.next();
                            if (insertStream.isCharacters()) {
                                values.add(insertStream.getText());
                            }
                        }
                    } else if (insertStream.isEndElement() && insertStream.getLocalName().equals("record")) {
                        for (String c : colNames) {
                            sqlInsert.append(" " + c + ",");
                        }

                        sqlInsert.deleteCharAt(sqlInsert.length() - 1);
                        sqlInsert.append(" ) VALUES (");

                        for (String v : values) {
                            sqlInsert.append(" '" + v + "',");
                        }

                        sqlInsert.deleteCharAt(sqlInsert.length() - 1);
                        sqlInsert.append(" )");

                        inStmt = connection.prepareStatement(sqlInsert.toString());

                        result += inStmt.executeUpdate();

                        // Clean up resources
                        inStmt.close();
                        sqlInsert.delete(0, sqlInsert.length());
                        values.clear();
                        colNames.clear();
                    }
                }
            } catch (XMLStreamException e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                JDBCHelper.cleanupResources(connection, inStmt, null);
            }

            msg.setBody(Integer.toString(result));
            return msg;
        }
    }

    /**
     * Get operation invoker
     */
    public static class GetInvoker extends DATAInvoker {

        public GetInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) {
            
            // Get an entry
            String sqlQuery = null;
            String id = (String) ((Object[]) msg.getBody())[0];

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


            } catch (SQLException sqle) {
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
     * Put operation invoker
     */
    public static class PutInvoker extends DATAInvoker {

        public PutInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) {
            
            XMLStreamReader updateStream = (XMLStreamReader) ((Object[]) msg.getBody())[1];

            if (updateStream == null) {
                throw new IllegalArgumentException("The XMLStreamReader \"updateStream\" must not be null");
            }

            Connection connection = null;
            PreparedStatement upStmt = null;

            String id = null;
            String columnName = null;
            String newValue = null;
            int result = 0;

            try {
                connection = JDBCHelper.getConnection(connectionInfo);
                while (updateStream.hasNext()) {
                    updateStream.next();

                    if (updateStream.isStartElement() && updateStream.getLocalName().equals("column")) {
                        columnName = updateStream.getAttributeValue(0);
                        updateStream.next();
                        if (updateStream.isCharacters()) {
                            if (columnName.equals("ID")) {
                                id = updateStream.getText();
                            } else {
                                newValue = updateStream.getText();

                                upStmt = connection.prepareStatement("UPDATE " + this.table + " SET " + columnName + " = '" + newValue + "' WHERE ID = " + id);

                                result += upStmt.executeUpdate();
                                upStmt.close();
                            }
                        }
                    }
                }
            } catch (XMLStreamException e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                JDBCHelper.cleanupResources(connection, upStmt, null);
            }

            msg.setBody(result);
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
            String sqlDelete = null;
            String id = (String) ((Object[]) msg.getBody())[0];

            if (id == null) {
                sqlDelete = "DELETE FROM " + this.table;
            } else {
                sqlDelete = "DELETE FROM " + this.table + " WHERE ID = " + id;
            }

            Connection connection = null;
            PreparedStatement deleteStatement = null;
            int result = -1;

            try {
                connection = JDBCHelper.getConnection(connectionInfo);
                deleteStatement = connection.prepareStatement(sqlDelete);
                result = deleteStatement.executeUpdate();

            } catch (SQLException sqle) {
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                JDBCHelper.cleanupResources(connection, deleteStatement, null);
            }

            msg.setBody(result);
            return msg;
        }
    }
    /**
     * Get operation invoker
     *
     * @version $Rev$ $Date$
     */
    public static class GetDATAInvoker extends DATAInvoker {

        public GetDATAInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) {

            // Get an entry
            String sqlQuery = null;
            String id = (String) ((Object[]) msg.getBody())[0];

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


            } catch (SQLException sqle) {
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
     * Insert operation invoker
     */
    public static class InsertDATAInvoker extends DATAInvoker {

        public InsertDATAInvoker(Operation operation,
                ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) throws IllegalArgumentException {
            StringBuilder sqlInsert = new StringBuilder();
            XMLStreamReader insertStream = (XMLStreamReader) ((Object[]) msg.getBody())[0];

            if (insertStream == null) {
                throw new IllegalArgumentException("The XMLStreamReader \"insertStream\" must not be null");
            }


            Connection connection = null;
            PreparedStatement inStmt = null;

            List<String> colNames = new ArrayList<String>();
            List<String> values = new ArrayList<String>();

            int result = 0;
            try {

                connection = JDBCHelper.getConnection(connectionInfo);

                while (insertStream.hasNext()) {

                    insertStream.next();
                    if (insertStream.isStartElement()) {
                        if (insertStream.getLocalName().equals("record")) {
                            sqlInsert.append("INSERT INTO " + this.table + " (");
                        } else if (insertStream.getLocalName().equals("column")) {
                            colNames.add(insertStream.getAttributeValue(0));
                            insertStream.next();
                            if (insertStream.isCharacters()) {
                                values.add(insertStream.getText());
                            }
                        }
                    } else if (insertStream.isEndElement() && insertStream.getLocalName().equals("record")) {
                        for (String c : colNames) {
                            sqlInsert.append(" " + c + ",");
                        }

                        sqlInsert.deleteCharAt(sqlInsert.length() - 1);
                        sqlInsert.append(" ) VALUES (");

                        for (String v : values) {
                            sqlInsert.append(" '" + v + "',");
                        }

                        sqlInsert.deleteCharAt(sqlInsert.length() - 1);
                        sqlInsert.append(" )");

                        inStmt = connection.prepareStatement(sqlInsert.toString());
                        result += inStmt.executeUpdate();

                        // Clean up resources
                        inStmt.close();
                        sqlInsert.delete(0, sqlInsert.length());
                        values.clear();
                        colNames.clear();
                    }
                }
            } catch (XMLStreamException e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                JDBCHelper.cleanupResources(connection, inStmt, null);
            }

            msg.setBody(result);
            return msg;
        }
    }

    /**
     * Update operation invoker
     */
    public static class UpdateDATAInvoker extends DATAInvoker {

        public UpdateDATAInvoker(Operation operation,
                ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) throws IllegalArgumentException {

            XMLStreamReader updateStream = (XMLStreamReader) ((Object[]) msg.getBody())[0];

            if (updateStream == null) {
                throw new IllegalArgumentException("The XMLStreamReader \"updateStream\" must not be null");
            }

            Connection connection = null;
            PreparedStatement upStmt = null;

            String id = null;
            String columnName = null;
            String newValue = null;
            int result = 0;

            try {
                connection = JDBCHelper.getConnection(connectionInfo);
                while (updateStream.hasNext()) {
                    updateStream.next();

                    if (updateStream.isStartElement() && updateStream.getLocalName().equals("column")) {
                        columnName = updateStream.getAttributeValue(0);
                        updateStream.next();
                        if (updateStream.isCharacters()) {
                            if (columnName.equals("ID")) {
                                id = updateStream.getText();
                            } else {
                                newValue = updateStream.getText();

                                upStmt = connection.prepareStatement("UPDATE " + this.table + " SET " + columnName + " = '" + newValue + "' WHERE ID = " + id);

                                result += upStmt.executeUpdate();
                                upStmt.close();
                            }
                        }
                    }
                }
            } catch (XMLStreamException e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                JDBCHelper.cleanupResources(connection, upStmt, null);
            }

            msg.setBody(result);
            return msg;
        }
    }

    /**
     * Delete operation invoker
     */
    public static class DeleteDATAInvoker extends DATAInvoker {

        public DeleteDATAInvoker(Operation operation, ConnectionInfo connectionInfo, String table) {
            super(operation, connectionInfo, table);
        }

        @Override
        public Message invoke(Message msg) {

            // Get an entry
            String sqlDelete = null;
            String id = (String) ((Object[]) msg.getBody())[0];

            if (id == null) {
                sqlDelete = "DELETE FROM " + this.table;
            } else {
                sqlDelete = "DELETE FROM " + this.table + " WHERE ID = " + id;
            }

            Connection connection = null;
            PreparedStatement deleteStatement = null;
            int result = -1;

            try {
                connection = JDBCHelper.getConnection(connectionInfo);
                deleteStatement = connection.prepareStatement(sqlDelete);
                result = deleteStatement.executeUpdate();

            } catch (SQLException sqle) {
                msg.setFaultBody(new ServiceRuntimeException(sqle.getCause()));
            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                JDBCHelper.cleanupResources(connection, deleteStatement, null);
            }

            msg.setBody(result);
            return msg;
        }
    }
}
