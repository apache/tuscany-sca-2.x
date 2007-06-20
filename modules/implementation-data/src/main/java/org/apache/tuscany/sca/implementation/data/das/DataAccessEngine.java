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

package org.apache.tuscany.sca.implementation.data.das;

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.DAS;

import commonj.sdo.DataObject;

/**
 * Facade to hide DAS implementation details of handling commands
 * 
 * @version $Rev$ $Date$
 */
public class DataAccessEngine {
    private final DAS das;

    public DataAccessEngine(DAS das) {
        this.das = das;
    }

    public DataObject executeGet(String table, String id) {
        try {
            String sqlQuery = "select * from " + table.toUpperCase() + " where ID = " + id;
            Command command = this.das.createCommand(sqlQuery);
            return command.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
