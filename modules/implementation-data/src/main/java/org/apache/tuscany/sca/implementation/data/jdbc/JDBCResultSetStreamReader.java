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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * JDBCResultSetStreamReader perform streaming of database tables as XML
 *    
 * @version $Rev$ $Date$
 */
public class JDBCResultSetStreamReader extends JDBCAbstractResultSetStreamReader implements XMLStreamReader {
    private static final int START = -1;
    private static final int START_RECORDSET_STATE = 0;
    private static final int START_RECORD_STATE = 1;
    private static final int START_ROW_STATE = 2;
    private static final int ROW_CARACHTERS = 3;
    private static final int END_ROW_STATE = 4;
    private static final int END_RECORD_STATE = 5;
    private static final int END_RECORDSET_STATE = 6;
    private static final int END = 7;
    
    private Connection connection;
    private ResultSet resultSet;
    private ResultSetMetaData tableMetaData;
    private String tableName;
    
    private int resultSetCursor=-1;
    private int columnCursor=-1;
    
    private int resultSetSize=-1;
    private int recordColumnSize=-1;

    
    private int state = START;
    
    public JDBCResultSetStreamReader(Connection connection, ResultSet resultSet) {
        this.connection = connection;
        this.resultSet = resultSet;
        
        init();
    }
    
    private void init() {
        try {
            // retrieve table information
            tableMetaData = resultSet.getMetaData();
            tableName = tableMetaData.getTableName(1).toLowerCase();

            // position the resultSet on the first row
            resultSetSize = getResultSetSize();
            recordColumnSize = tableMetaData.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing JDBCStreamReader", e);
        }

    }
    
    private int getResultSetSize() {
        if (resultSetSize == -1) {
            String sqlCount = null;
            PreparedStatement queryStatement = null;
            ResultSet countResultSet = null;
            
            try {
                sqlCount = "SELECT COUNT(*) FROM " + this.resultSet.getMetaData().getTableName(1);
                queryStatement = connection.prepareStatement(sqlCount);
                countResultSet = queryStatement.executeQuery();
                
                if(countResultSet.next()) {
                    resultSetSize = countResultSet.getInt(1);
                }
            }catch(SQLException e) {
                //ignore
            }finally {
                JDBCHelper.cleanupResources(null, queryStatement, countResultSet);
            }
        }
        return resultSetSize;
    }
    
    /**
     * Close JDBC connection and any other resources associated
     */
    @Override
    public void close() throws XMLStreamException {
        JDBCHelper.cleanupResources(connection, null, resultSet);
    }


    public String getElementText() throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }


    public int getEventType() {
        return getCurrentParsingState();
    }

    public QName getName() {
        QName elementName = null;
        switch (state) {
            case START_RECORDSET_STATE:
            case END_RECORDSET_STATE: 
                elementName = new QName(null, tableName + "_table");
                break;
            case START_RECORD_STATE:
            case END_RECORD_STATE: 
                elementName = new QName(null, tableName);
                break;
            case START_ROW_STATE:
            case END_ROW_STATE: 
                try {
                    elementName = new QName(null, tableMetaData.getColumnName(columnCursor));
                } catch (SQLException e) {
                    // ignore
                }
        }
        return elementName;
    }

    public String getText() {
        String text = null;
        if(getCurrentParsingState() == CHARACTERS) {
            try {
                text = resultSet.getString(columnCursor);
            } catch(SQLException e) {
                //ignore, return null
            }
        }
        return text;
    }

    public char[] getTextCharacters() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getTextLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getTextStart() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean hasName() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasNext() throws XMLStreamException {
        if( getCurrentParsingState() == END_DOCUMENT) {
            return false;
        }
        return true;
    }

    public boolean hasText() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCharacters() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEndElement() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isStartElement() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isWhiteSpace() {
        // TODO Auto-generated method stub
        return false;
    }

    public int next() throws XMLStreamException {
        boolean EOF;
        boolean EOR;
        
        switch(state) {
            case START : 
                //check if there are any records on the resultSet
                //otherwise move directly to END state
                if(getResultSetSize()>0) {
                    state++;
                }else {
                    state = END;
                }
                break;
            case START_RECORDSET_STATE :
                EOF = true;
                //position on the first record
                try {
                    if(resultSet.next()); {
                        //reset cursor
                        this.resultSetCursor = 1;
                        this.columnCursor = 1;
                        EOF = false;
                    }
                } catch (SQLException e) {
                    // ignore, we will move to next state
                }
                
                if(!EOF) {
                    state++;
                } else {
                    state = END_RECORDSET_STATE;
                }
                break;
            case START_RECORD_STATE :
                state++;
                break;
            case START_ROW_STATE :
                state++;
                break;
            case ROW_CARACHTERS :
                state++;
                break;
            case END_ROW_STATE :
                EOR = true;
                //check if there are more rows to be processed
                if(columnCursor < recordColumnSize) {
                    //move column cursor to next row
                    columnCursor++;
                    EOR = false;
                }
                
                if(!EOR) {
                    state = START_ROW_STATE;
                } else {
                    state = END_RECORD_STATE;
                }
                break;
            case END_RECORD_STATE :
                EOF = true;
                //check if there are more records to be processed
                try {
                    if(resultSet.next()); {
                        //reset cursor
                        this.resultSetCursor++;
                        this.columnCursor = 1;
                        EOF = resultSetCursor > resultSetSize ? true : false;
                    }
                } catch (SQLException e) {
                    // ignore, we will move to next state
                }
                
                if(!EOF) {
                    state = START_RECORD_STATE;
                }else {
                    state = END_RECORDSET_STATE;
                }
                break;
            case END_RECORDSET_STATE : 
                state++;
                break;
            case END:
                break;
                
        }
        //set the current event
        return getCurrentParsingState();
    }

    public int nextTag() throws XMLStreamException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void require(int arg0, String arg1, String arg2) throws XMLStreamException {
        // TODO Auto-generated method stub

    }

    
    /**
     * Helper methods
     */
   
    /**
     * Compute XMLStreamReader event based on internal event state
     */
    private int getCurrentParsingState() {
        int returnEvent = -1;
        
        switch(state) {
            case START : 
                if(getResultSetSize()>0) {
                    returnEvent = START_DOCUMENT;
                }else {
                    returnEvent = END_DOCUMENT;
                }
                break;
            case START_RECORDSET_STATE :
            case START_RECORD_STATE :
            case START_ROW_STATE :
                returnEvent = START_ELEMENT;
                break;
            case ROW_CARACHTERS:
                returnEvent = CHARACTERS;
                break;
            case END_ROW_STATE : 
            case END_RECORD_STATE :
            case END_RECORDSET_STATE : 
                returnEvent = END_ELEMENT;
                break;
            case END :
                returnEvent = END_DOCUMENT;
        }
        //set the current event
        return returnEvent;
    }
    

    
}

class JDBCAbstractResultSetStreamReader implements XMLStreamReader {
    public void close() throws XMLStreamException {
    }

    public int getAttributeCount() {
        return 0;
    }

    public String getAttributeLocalName(int arg0) {
        return null;
    }

    public QName getAttributeName(int arg0) {
        return null;
    }

    public String getAttributeNamespace(int arg0) {
        return null;
    }

    public String getAttributePrefix(int arg0) {
        return null;
    }

    public String getAttributeType(int arg0) {
        return null;
    }

    public String getAttributeValue(int arg0) {
        return null;
    }

    public String getAttributeValue(String arg0, String arg1) {
        return null;
    }

    public String getCharacterEncodingScheme() {
        return null;
    }

    public String getElementText() throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getEncoding() {
        return null;
    }

    public int getEventType() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getLocalName() {
        // TODO Auto-generated method stub
        return null;
    }

    public Location getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    public QName getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public NamespaceContext getNamespaceContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getNamespaceCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getNamespacePrefix(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getNamespaceURI() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getNamespaceURI(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getNamespaceURI(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPIData() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPITarget() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getProperty(String arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getText() {
        // TODO Auto-generated method stub
        return null;
    }

    public char[] getTextCharacters() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getTextLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getTextStart() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasName() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasNext() throws XMLStreamException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasText() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isAttributeSpecified(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCharacters() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEndElement() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isStandalone() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isStartElement() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isWhiteSpace() {
        // TODO Auto-generated method stub
        return false;
    }

    public int next() throws XMLStreamException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int nextTag() throws XMLStreamException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void require(int arg0, String arg1, String arg2) throws XMLStreamException {
        // TODO Auto-generated method stub

    }

    public boolean standaloneSet() {
        // TODO Auto-generated method stub
        return false;
    }
    
}

