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

package sample;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;

public class JavaInvoker implements Invoker {

    protected Class clazz;
    protected Object instance;
    protected Operation operation;
    
    public JavaInvoker(Class clazz, Object instance, Operation operation) {
        this.clazz = clazz;
        this.instance = instance;
        this.operation = operation;
    }
    
    public Message invoke(Message msg) {
        try {
            msg.setBody(getMethod().invoke(instance, (Object[])msg.getBody()));
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return msg;
    }
    
    protected Method getMethod() {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(operation.getName())) {
                return method;
            }
        }
        throw new ServiceRuntimeException("no method found for operation: " + operation.getName());
    }

}
