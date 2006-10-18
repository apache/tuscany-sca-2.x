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
package org.apache.tuscany.core.implementation.composite;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;

public abstract class AbstractOperationOutboundInvocationHandler {

    public abstract Message invoke(Operation operation, Message msg) throws Throwable;

    protected Message invoke(OutboundInvocationChain chain, TargetInvoker invoker, Message msg) throws Throwable {
        Interceptor headInterceptor = chain.getHeadInterceptor();
        if (headInterceptor == null) {
            try {
                // short-circuit the dispatch and invoke the target directly
                TargetInvoker targetInvoker = chain.getTargetInvoker();
                if (targetInvoker == null) {
                    String name = chain.getOperation().getName();
                    throw new AssertionError("No target invoker [" + name + "]");
                }
                return targetInvoker.invoke(msg);
            } catch (InvocationRuntimeException e) {
                // the cause was thrown by the target so throw it
                throw e.getCause();
            }
        } else {
            msg.setTargetInvoker(invoker);
            msg.setFromAddress(getFromAddress());

            Message resp = headInterceptor.invoke(msg);

            return resp;
        }
    }

    protected abstract Object getFromAddress();
}
