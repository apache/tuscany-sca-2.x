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

package org.apache.tuscany.sca.binding.corba.impl.reference;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.omg.CORBA.Object;

/**
 * @version $Rev$ $Date$ 
 * Interceptor for CORBA reference methods
 */
public class InterfaceMethodInterceptor implements MethodInterceptor {

    private Object reference;
    private Class<?> javaClass;

    public InterfaceMethodInterceptor(Object reference, Class<?> javaClass) {
        this.reference = reference;
        this.javaClass = javaClass;
    }

    /**
     * Create and execute DynaCorbaRequest instance, basing on intercepted
     * method arguments, return types, exceptions
     */
    public java.lang.Object intercept(java.lang.Object object,
                                      Method method,
                                      java.lang.Object[] arguments,
                                      MethodProxy arg3) throws Throwable {
        DynaCorbaRequest request = new DynaCorbaRequest(reference, method.getName());
        request.setReferenceClass(javaClass);
        for (int i = 0; i < arguments.length; i++) {
            request.addArgument(arguments[i]);
        }
        request.setOutputType(method.getReturnType());
        Class<?>[] exceptions = method.getExceptionTypes();
        for (int i = 0; i < exceptions.length; i++) {
            request.addExceptionType(exceptions[i]);
        }
        DynaCorbaResponse response = request.invoke();
        return response.getContent();
    }

}
