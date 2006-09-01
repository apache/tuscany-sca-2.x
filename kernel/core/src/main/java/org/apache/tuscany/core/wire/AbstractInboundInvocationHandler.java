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
package org.apache.tuscany.core.wire;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Base class for dispatching an invocation through an {@link InboundInvocationChain}
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractInboundInvocationHandler {


    /**
     * Dispatches a client request made on a proxy
     */
    public Object invoke(InboundInvocationChain chain, TargetInvoker invoker, Object[] args) throws Throwable {
        MessageChannel requestChannel = chain.getRequestChannel();
        MessageChannel responseChannel = chain.getResponseChannel();
        Interceptor headInterceptor = chain.getHeadInterceptor();
        if (requestChannel == null && headInterceptor == null && responseChannel == null) {
            try {
                // short-circuit the dispatch and invoke the target directly
                if (chain.getTargetInvoker() == null) {
                    throw new AssertionError("No target invoker [" + chain.getOperation().getName() + "]");
                }
                return chain.getTargetInvoker().invokeTarget(args);
            } catch (InvocationTargetException e) {
                // the cause was thrown by the target so throw it
                throw e.getCause();
            }
        } else {
            Message msg = new MessageImpl();
            msg.setTargetInvoker(invoker);
            msg.setBody(args);
            Message resp;
            if (requestChannel != null) {
                requestChannel.send(msg);
                resp = msg.getRelatedCallbackMessage();
                if (responseChannel != null) {
                    responseChannel.send(resp);
                }
            } else {
                if (headInterceptor == null) {
                    throw new TargetException("Expected interceptor on target chain");
                }
                // dispatch the wire down the chain and get the response
                resp = headInterceptor.invoke(msg);
                if (responseChannel != null) {
                    responseChannel.send(resp);
                }
            }
            Object body = resp.getBody();
            if (body instanceof Throwable) {
                throw (Throwable) body;
            }
            return body;
        }
    }


}
