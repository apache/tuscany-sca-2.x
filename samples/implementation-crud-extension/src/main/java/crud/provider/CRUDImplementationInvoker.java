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

package crud.provider;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

import crud.backend.ResourceManager;


/**
 * Implements a target invoker for CRUD component implementations.
 * 
 * The target invoker is responsible for dispatching invocations to the particular
 * component implementation logic. In this example we are simply delegating the
 * CRUD operation invocations to the corresponding methods on our fake
 * resource manager.
 */
class CRUDImplementationInvoker implements Invoker {
    private Operation operation;
    private ResourceManager resourceManager;
    
    CRUDImplementationInvoker(Operation operation, ResourceManager resourceManager) {
        this.operation = operation;
        this.resourceManager = resourceManager;
    }
    
    public Message invoke(Message msg) {
        try {
            Object[] args = msg.getBody();
            Object resp = null;
            
            if (operation.getName().equals("create")) {
                resp = resourceManager.createResource(args[0]);
                
            } else if (operation.getName().equals("retrieve")) {
                resp = resourceManager.retrieveResource((String)args[0]);
                
            } else if (operation.getName().equals("update")) {
                resp = resourceManager.updateResource((String)args[0], args[1]);
                
            } else if (operation.getName().equals("delete")) {
                resourceManager.deleteResource((String)args[0]);
            }
            
            msg.setBody(resp);
        } catch (Exception e) {
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }

}
