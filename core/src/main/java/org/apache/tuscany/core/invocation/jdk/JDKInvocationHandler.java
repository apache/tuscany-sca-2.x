/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.invocation.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;

/**
 * Receives a request from a JDK proxy and dispatches it to a target invoker or source interceptor stack
 * 
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandler implements InvocationHandler {

    private MessageFactory messageFactory;

    private Map<Method, InvocationConfiguration> configuration;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public JDKInvocationHandler(MessageFactory messageFactory, Map<Method, InvocationConfiguration> configuration) {
        assert (configuration != null) : "Configuration not specified";
        this.configuration = configuration;
        this.messageFactory = messageFactory;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    /**
     * Dispatches a client request made on a proxy
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Interceptor headInterceptor = null;
        InvocationConfiguration config = configuration.get(method);
        if (config != null) {
            headInterceptor = config.getSourceInterceptor();
        }
        if (headInterceptor == null) {
            try {
                // short-circuit the dispatch and invoke the target directly
                if (config.getTargetInvoker() == null) {
                    throw new AssertionError("No target invoker [" + method.getName() + "]");
                }
                return config.getTargetInvoker().invokeTarget(args);
            } catch (InvocationTargetException e) {
                // the cause was thrown by the target so throw it
                throw e.getCause();
            }
        } else {
            Message msg = messageFactory.createMessage();
            msg.setTargetInvoker(config.getTargetInvoker());
            msg.setPayload(args);
            // dispatch the invocation down the chain and get the response
            Message resp = headInterceptor.invoke(msg);

            Object payload = resp.getPayload();
            if (payload instanceof Throwable)
                throw (Throwable) payload;
            return payload;
        }
    }
}