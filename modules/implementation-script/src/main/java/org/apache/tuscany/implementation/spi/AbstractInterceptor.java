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

package org.apache.tuscany.implementation.spi;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.invocation.Interceptor;
import org.apache.tuscany.invocation.InvocationRuntimeException;
import org.apache.tuscany.invocation.Message;

/**
 * TODO: couldn't something like this class be provided by the runtime?
 *   or even better, how about a new "Invoker" interface that just 
 *   has an invoke method and not the next an optimizable stuff
 */
public abstract class AbstractInterceptor implements Interceptor {

    private Interceptor next;

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = doInvoke((Object[])msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        }
        return msg;
    }

    abstract public Object doInvoke(Object[] objects) throws InvocationTargetException;

    public boolean isOptimizable() {
        return false;
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

}
