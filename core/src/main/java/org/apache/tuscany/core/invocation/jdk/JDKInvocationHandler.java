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

import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Receives a request from a JDK proxy and dispatches it to a target invoker or source interceptor stack
 * 
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandler implements InvocationHandler {

    private MessageFactory messageFactory;

    /*
     * an association of an operation to configuration holder. The holder contains the master invocation configuration
     * and a locale clone of the master TargetInvoker. TargetInvokers will be cloned by the handler and placed in the
     * holder if they are cacheable. This allows optimizations such as avoiding target resolution when a source refers
     * to a target of greater scope since the target reference can be maintained by the invoker. When a target invoker
     * is not cacheable, the master associated with the invocation configuration will be used.
     */
    private Map<Method, ConfigHolder> configuration;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public JDKInvocationHandler(MessageFactory messageFactory, Map<Method, InvocationConfiguration> configuration) {
        assert (configuration != null) : "Configuration not specified";
        this.configuration = new HashMap<Method, ConfigHolder>(configuration.size());
        for (Map.Entry<Method, InvocationConfiguration> entry : configuration.entrySet()) {
            this.configuration.put(entry.getKey(), new ConfigHolder(entry.getValue()));
        }
        // this.configuration = configuration;
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
        ConfigHolder holder = configuration.get(method);
        if (holder == null) {
            TargetException e = new TargetException("Operation not configured");
            e.setIdentifier(method.getName());
            throw e;
        }
        InvocationConfiguration config = holder.config;
        if (config != null) {
            headInterceptor = config.getSourceInterceptor();
        }

        TargetInvoker invoker;

        if (holder.cachedInvoker == null) {
            assert config != null;
            if(config.getTargetInvoker() == null){
                TargetException e= new TargetException("No target invoker configured for operation");
                e.setIdentifier(config.getMethod().getName());
                throw e;
            }
            if (config.getTargetInvoker().isCacheable()) {
                // clone and store the invoker locally
                holder.cachedInvoker = (TargetInvoker) config.getTargetInvoker().clone();
                invoker = holder.cachedInvoker;
            } else {
                invoker = config.getTargetInvoker();
            }
        } else {
            assert config != null;
            invoker = config.getTargetInvoker();
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
            msg.setTargetInvoker(invoker);// config.getTargetInvoker());
            msg.setBody(args);
            // dispatch the invocation down the chain and get the response
            Message resp = headInterceptor.invoke(msg);

            Object body = resp.getBody();
            if (body instanceof Throwable) {
                throw (Throwable) body;
            }
            return body;
        }
    }

    /**
     * A holder used to associate an invocation configuration with a local copy of a target invoker that was previously
     * cloned from the configuration master
     */
    private class ConfigHolder {

        public ConfigHolder(InvocationConfiguration config) {
            this.config = config;
        }

        InvocationConfiguration config;

        TargetInvoker cachedInvoker;
    }

}