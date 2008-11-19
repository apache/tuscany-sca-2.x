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

import org.apache.tuscany.sca.implementation.data.DATAImplementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * DATA Implementation provider
 * 
 * @version $Rev$ $Date$
 */
public class DATAImplementationProvider implements ImplementationProvider {
    //private RuntimeComponent component;
    private DATAImplementation implementation;

    /**
     * Constructs a new DATA implementation.
     */
    public DATAImplementationProvider(RuntimeComponent component, DATAImplementation implementation) {
        //this.component = component;
        this.implementation = implementation;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        String operationName = operation.getName();
        String tableName = service.getName();

        String interfaceFullName = operation.getInterface().toString();
        int index = interfaceFullName.lastIndexOf(".") + 1;
        String interfaceName = interfaceFullName.substring(index, interfaceFullName.length());

        if (interfaceName.equals("DATACollection")) {

            if (operationName.equals("getAll")) {
                return new DATAInvoker.GetAllInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("query")) {
                return new DATAInvoker.QueryInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("post")) {
                return new DATAInvoker.PostInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("get")) {
                return new DATAInvoker.GetInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("put")) {
                return new DATAInvoker.PutInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("delete")) {
                return new DATAInvoker.DeleteInvoker(operation, implementation.getConnectionInfo(), tableName);
            }

        } else if (interfaceName.equals("DATA")) {
            
            tableName = tableName.split("_")[0];
            
            if (operationName.equals("get")) {
                return new DATAInvoker.GetDATAInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("insert")) {
                return new DATAInvoker.InsertDATAInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("update")) {
                return new DATAInvoker.UpdateDATAInvoker(operation, implementation.getConnectionInfo(), tableName);
            } else if (operationName.equals("delete")) {
                return new DATAInvoker.DeleteDATAInvoker(operation, implementation.getConnectionInfo(), tableName);
            }
        }
        
        return new DATAInvoker(operation, implementation.getConnectionInfo(), tableName);
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
    // System.out.println("Starting " + component.getName());
    }

    public void stop() {
    // System.out.println("Stopping " + component.getName());
    }
}
