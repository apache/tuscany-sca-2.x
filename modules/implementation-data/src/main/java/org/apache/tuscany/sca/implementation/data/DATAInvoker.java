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

package org.apache.tuscany.sca.implementation.data;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.sca.implementation.data.das.DataAccessEngine;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;


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
    private final Operation operation;
    private final String table;
    private final DataAccessEngine dataAccessEngine;
    
    public DATAInvoker(Operation operation, String table, DataAccessEngine dataAccessEngine) {
        this.operation = operation;
        this.table = table;
        this.dataAccessEngine = dataAccessEngine;
    }
    
    public Message invoke(Message msg) {
        try {
            Object[] args = msg.getBody();
            Object resp = doTheWork(args);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }

    public Object doTheWork(Object[] args) throws InvocationTargetException {
        if (operation.getName().equals("get")) {
            String id = (String) args[0];
            
            //simple execute command by name
            return this.dataAccessEngine.executeGet(table, id);
        } else {
            return null;
        }
    }
}
