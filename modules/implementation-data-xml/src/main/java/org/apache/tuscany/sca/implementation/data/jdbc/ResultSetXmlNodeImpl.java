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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.xml.SimpleXmlNodeImpl;
import org.apache.tuscany.sca.databinding.xml.XmlNode;

/**
 * @version $Rev$ $Date$
 */
public class ResultSetXmlNodeImpl implements XmlNode {
    private static final String NS = "";
    private static final QName RESULT_SET = new QName(NS, "resultSet");
    private static final QName RECORD = new QName(NS, "record");
    private static final QName COLUMN = new QName(NS, "column");
    private static final QName NAME = new QName(NS, "name");

    private ResultSet resultSet;
    private String[] columnNames;

    /**
     * @param resultSet
     */
    public ResultSetXmlNodeImpl(ResultSet resultSet) {
        super();
        this.resultSet = resultSet;
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            columnNames = new String[metaData.getColumnCount()];
            for (int i = 0; i < columnNames.length; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#attributes()
     */
    public List<XmlNode> attributes() {
        return Collections.emptyList();
    }
    
    public Type getType() {
        return Type.ELEMENT;
    }


    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#children()
     */
    public Iterator<XmlNode> children() {
        return new ResultSetIteraror();
    }

    private class ResultSetIteraror implements Iterator<XmlNode> {
        private Boolean hasNext;

        public ResultSetIteraror() {
        }

        public boolean hasNext() {
            try {
                if (hasNext == null) {
                    hasNext = resultSet.next();
                }
                return hasNext;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        public XmlNode next() {
            hasNext();
            hasNext = null;
            return new RecordXmlNodeImpl();
        }

        public void remove() {
        }
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getName()
     */
    public QName getName() {
        return RESULT_SET;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getValue()
     */
    public String getValue() {
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#namespaces()
     */
    public Map<String, String> namespaces() {
        return Collections.emptyMap();
    }

    private class RecordXmlNodeImpl extends XmlNodeImpl {
        int index = 0;

        @Override
        public Iterator<XmlNode> children() {
            return new Iterator<XmlNode>() {

                public boolean hasNext() {
                    return index < columnNames.length;
                }

                public XmlNode next() {
                    return new ColumnXmlNodeImpl(index++);
                }

                public void remove() {
                }

            };
        }

        @Override
        public QName getName() {
            return RECORD;
        }

    }

    private class ColumnXmlNodeImpl extends XmlNodeImpl {
        private int index;

        /**
         * @param index
         */
        public ColumnXmlNodeImpl(int index) {
            super();
            this.index = index;
        }

        @Override
        public List<XmlNode> attributes() {
            XmlNode attr = new SimpleXmlNodeImpl(NAME, columnNames[index], XmlNode.Type.ATTRIBUTE);
            return Arrays.asList(attr);
        }

        @Override
        public Iterator<XmlNode> children() {
            XmlNode[] nodes = {new ValueXmlNodeImpl(index)};
            return Arrays.asList(nodes).iterator();
        }

        @Override
        public QName getName() {
            return COLUMN;
        }

    }

    private class ValueXmlNodeImpl extends XmlNodeImpl {
        private int index;

        /**
         * @param index
         */
        public ValueXmlNodeImpl(int index) {
            super();
            this.index = index;
        }

        @Override
        public String getValue() {
            try {
                return String.valueOf(resultSet.getObject(index + 1));
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Type getType() {
            return Type.CHARACTERS;
        }

    }

    private static abstract class XmlNodeImpl implements XmlNode {

        public List<XmlNode> attributes() {
            return Collections.emptyList();
        }

        public Iterator<XmlNode> children() {
            return null;
        }

        public QName getName() {
            return null;
        }

        public String getValue() {
            return null;
        }

        public boolean isLeaf() {
            return false;
        }

        public Map<String, String> namespaces() {
            return Collections.emptyMap();
        }

        public Type getType() {
            return Type.ELEMENT;
        }

    }


}
