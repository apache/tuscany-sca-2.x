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
package org.apache.tuscany.hessian;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;

/**
 * Dispatches a service invocation to a Channel
 *
 * @version $Rev$ $Date$
 */
public class InvokerInterceptor implements Interceptor {
    private String operation;
    private Channel channel;

    /**
     * Creates the interceptor.
     *
     * @param operation the service operation the interceptor dispatches for
     * @param channel   the channel to dispatch to
     */
    public InvokerInterceptor(String operation, Channel channel) {
        this.operation = operation;
        this.channel = channel;
    }

    public boolean isOptimizable() {
        return false;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            return channel.send(operation, null, msg);
        } catch (InvocationException e) {
            msg.setBodyWithFault(e);
            return msg;
        }
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an target interceptor chain");
    }

    public Interceptor getNext() {
        return null;
    }


}
