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

package org.apache.tuscany.sca.host.embedded.test.extension.provider;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;


/**
 * Implements an invoker for test component implementations.
 * 
 * The target invoker is responsible for handling operation invocations.
 * 
 * @version $Rev$ $Date$
 */
public class TestInvoker implements Invoker {
    private Operation operation;
    private String greeting;
    
    public TestInvoker(Operation operation, String greeting) {
        this.operation = operation;
        this.greeting = greeting;
    }
    
    public Message invoke(Message msg) {
        Object[] args = msg.getBody();
        if (operation.getName().equals("ping")) {
            msg.setBody(greeting + " " + args[0]);
        } else {
            msg.setFaultBody(new Exception("Operation " + operation.getName() + " is not supported"));
        }
        return msg;
    }
}
