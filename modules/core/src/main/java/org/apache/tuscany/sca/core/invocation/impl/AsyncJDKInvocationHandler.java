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
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.Invocable;
import org.oasisopen.sca.ServiceReference;

/**
 * An InvocationHandler which deals with JAXWS-defined asynchronous client Java API method calls
 * 
 * 2 asynchronous mappings exist for any given synchronous service operation, as shown in this example:
 *  public interface StockQuote {
 *      float getPrice(String ticker);
 *      Response<Float> getPriceAsync(String ticker);
 *      Future<?> getPriceAsync(String ticker, AsyncHandler<Float> handler);
 *  }
 *
 * - the second method is called the "polling method", since the returned Response<?> object permits
 *   the client to poll to see if the async call has completed
 * - the third method is called the "async callback method", since in this case the client application can specify
 *   a callback operation that is automatically called when the async call completes
 */
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

    /**
     * Perform the invocation of the operation
     * - provides support for all 3 forms of client method: synchronous, polling and async callback
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isAsyncCallback(method)) {
            return doInvokeAsyncCallback(proxy, method, args);            
        } else if (isAsyncPoll(method)) {
            return doInvokeAsyncPoll(proxy, method, args);            
        } else {
        	// Regular synchronous method call
            return super.invoke(proxy, method, args);
        }
    }

    /**
     * Indicates if a supplied method has the form of an async callback method
     * @param method - the method
     * @return - true if the method has the form of an async callback
     */
    protected boolean isAsyncCallback(Method method) {
        if (method.getName().endsWith("Async") && (method.getReturnType().isAssignableFrom(Future.class))) {
            if (method.getParameterTypes().length > 0) {
                return method.getParameterTypes()[method.getParameterTypes().length-1].isAssignableFrom(AsyncHandler.class);
            }
        }
        return false;
    }

    /**
     * Indicates is a supplied method has the form of an async polling method
     * @param method - the method
     * @return - true if the method has the form of an async polling method
     */
    protected boolean isAsyncPoll(Method method) {
        return method.getName().endsWith("Async") && (method.getReturnType().isAssignableFrom(Response.class));
    }

    /**
     * Invoke an async polling method
     * @param proxy - the reference proxy
     * @param asyncMethod - the async method to invoke
     * @param args - array of input arguments to the method
     * @return - the Response<?> object that is returned to the client application, typed by the 
     *           type of the response
     */
    protected Response doInvokeAsyncPoll(Object proxy, Method asyncMethod, Object[] args) {
        Object response;
        boolean isException;
        Class<?> returnType = getNonAsyncMethod(asyncMethod).getReturnType();
        // Allocate the Future<?> / Response<?> object - note: Response<?> is a subclass of Future<?>
        AsyncInvocationFutureImpl future = AsyncInvocationFutureImpl.newInstance( returnType );
        try {
            response = super.invoke(proxy, getNonAsyncMethod(asyncMethod), args);
            isException = false;
            future.setResponse(response);
        } catch (Throwable e) {
            response = e;
            isException = true;
            future.setFault(e);
        }
        return future;
        //return new AsyncResponse(response, isException);
    }

    /**
     * Invoke an async callback method
     * @param proxy - the reference proxy
     * @param asyncMethod - the async method to invoke
     * @param args - array of input arguments to the method
     * @return - the Future<?> object that is returned to the client application, typed by the type of
     *           the response
     */
    private Object doInvokeAsyncCallback(Object proxy, Method asyncMethod, Object[] args) {
        AsyncHandler handler = (AsyncHandler)args[args.length-1];
        Response response = doInvokeAsyncPoll(proxy,asyncMethod,Arrays.copyOf(args, args.length-1));
        handler.handleResponse(response);
        
        return response;
    }

    /**
     * Return the synchronous method that is the equivalent of an async method
     * @param asyncMethod - the async method
     * @return - the equivalent synchronous method
     */
    protected Method getNonAsyncMethod(Method asyncMethod) {
        String methodName = asyncMethod.getName().substring(0, asyncMethod.getName().length()-5);
        for (Method m : businessInterface.getMethods()) {
            if (methodName.equals(m.getName())) {
                return m;
            }
        }
        throw new IllegalStateException("No synchronous method matching async method " + asyncMethod.getName());
    }
}
