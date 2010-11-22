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

package sampleasync.impl;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Invoker for Sample components that implement a Java interface.
 * 
 * @version $Rev$ $Date$
 */
class SampleJavaInvoker implements Invoker {
    final Object instance;
    final Method method;

    SampleJavaInvoker(final JavaOperation op, final Class<?> clazz, final Object instance) throws SecurityException, NoSuchMethodException {
        this.instance = instance;
        this.method = clazz.getMethod(op.getJavaMethod().getName(), op.getJavaMethod().getParameterTypes());
    }

    public Message invoke(final Message msg) {
        try {
            // Call the method that implements the operation
            msg.setBody(method.invoke(instance, (Object[])msg.getBody()));
        } catch(Exception e) {
            e.printStackTrace();
            msg.setFaultBody(e.getCause());
        }
        return msg;
    }
}
