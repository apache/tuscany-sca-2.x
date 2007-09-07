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

package org.apache.tuscany.sca.implementation.das.provider;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.sca.data.engine.DataAccessEngine;
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
public class DASInvoker implements Invoker {
    private final Operation operation;
    private final DataAccessEngine dataAccessEngine;
    
    public DASInvoker(Operation operation, DataAccessEngine dataAccessEngine) {
        this.operation = operation;
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
        //check if static way
        if (operation.getName().equals("executeCommand")) {
            String commandName, xPath;
            
            //simple execute command by name
            if( args.length == 1){
                commandName = (String) args[0];
                return this.dataAccessEngine.executeCommand(commandName);
            } else {
                commandName = (String) args[0];
                xPath = (String) args[1];
                
                return this.dataAccessEngine.executeCommand(commandName, xPath);
            }
        } else { // dynamic mapping to command
            return this.dataAccessEngine.executeCommand(operation.getName());
        }
    }
}
