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

package org.apache.tuscany.sca.core.invocation.impl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.Invocable;
import org.oasisopen.sca.ServiceReference;

public class AsyncJDKInvocationHandler extends JDKInvocationHandler {
    
    private static final long serialVersionUID = 1L;

    public AsyncJDKInvocationHandler(MessageFactory messageFactory, ServiceReference<?> callableReference) {
        super(messageFactory, callableReference);
    }

    public AsyncJDKInvocationHandler(MessageFactory messageFactory,
                                     Class<?> businessInterface,
                                     Invocable source) {
        super(messageFactory, businessInterface, source);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isAsyncCallback(method)) {
            return doInvokeAsyncCallback(proxy, method, args);            
        } else if (isAsyncPoll(method)) {
            return doInvokeAsyncPoll(proxy, method, args);            
        } else {
            return super.invoke(proxy, method, args);
        }
    }

    protected boolean isAsyncCallback(Method method) {
        if (method.getName().endsWith("Async") && (method.getReturnType().isAssignableFrom(Future.class))) {
            if (method.getParameterTypes().length > 0) {
                return method.getParameterTypes()[method.getParameterTypes().length-1].isAssignableFrom(AsyncHandler.class);
            }
        }
        return false;
    }

    protected boolean isAsyncPoll(Method method) {
        return method.getName().endsWith("Async") && (method.getReturnType().isAssignableFrom(Response.class));
    }

    protected AsyncResponse doInvokeAsyncPoll(Object proxy, Method asyncMethod, Object[] args) {
        Object response;
        boolean isException;
        try {
            response = super.invoke(proxy, getNonAsyncMethod(asyncMethod), args);
            isException = false;
        } catch (Throwable e) {
            response = e;
            isException = true;
        }
        return new AsyncResponse(response, isException);
    }

    private Object doInvokeAsyncCallback(Object proxy, Method asyncMethod, Object[] args) {
        AsyncHandler handler = (AsyncHandler)args[args.length-1];
        Response response = doInvokeAsyncPoll(proxy,asyncMethod,Arrays.copyOf(args, args.length-1));
        handler.handleResponse(response);
        
        return null;
    }

    protected Method getNonAsyncMethod(Method asyncMethod) {
        String methodName = asyncMethod.getName().substring(0, asyncMethod.getName().length()-5);
        for (Method m : businessInterface.getMethods()) {
            if (methodName.equals(m.getName())) {
                return m;
            }
        }
        throw new IllegalStateException("No non-async method matching async method " + asyncMethod.getName());
    }
}
