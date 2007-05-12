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
package org.apache.tuscany.sca.implementation.java.injection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Performs an wire on a method of a given instance
 *
 * @version $Rev$ $Date$
 */
public class MethodEventInvoker<T> implements EventInvoker<T> {
    private final Method method;

    /**
     * Intantiates an  invoker for the given method
     */
    public MethodEventInvoker(Method method) {
        assert method != null;
        this.method = method;
    }

    public void invokeEvent(T instance) throws ObjectCallbackException {
        try {
            method.invoke(instance, (Object[]) null);
        } catch (IllegalArgumentException e) {
            String name = method.getName();
            throw new ObjectCallbackException("Exception thrown by callback method [" + name + "]", e.getCause());
        } catch (IllegalAccessException e) {
            String name = method.getName();
            throw new AssertionError("Method is not accessible [" + name + "]");
        } catch (InvocationTargetException e) {
            String name = method.getName();
            throw new ObjectCallbackException("Exception thrown by callback method [" + name + "]", e.getCause());
        }
    }

}
