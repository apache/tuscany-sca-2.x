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

import java.util.ArrayList;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

/**
 * Contains a outgoing invocation pipeline for a service operation.
 *
 * @version $Rev$ $Date$
 */
public class OutboundInvocationChainImpl extends InvocationChainImpl implements OutboundInvocationChain {

    /**
     * Creates an new outbound chain
     */
    public OutboundInvocationChainImpl(Operation operation) {
        super(operation);
    }

    public void prepare() {
        if ((requestHandlers != null || responseHandlers != null) && targetInterceptorChainHead != null) {
            MessageHandler messageDispatcher = new MessageDispatcher(targetInterceptorChainHead);
            if (requestHandlers == null) {
                // case where there is only a response handler
                requestHandlers = new ArrayList<MessageHandler>();
                requestChannel = new MessageChannelImpl(requestHandlers);
            }

            requestHandlers.add(messageDispatcher);
        }

        if (requestHandlers != null || responseHandlers != null) {
            Interceptor channelInterceptor = new RequestResponseInterceptor(requestChannel, targetRequestChannel,
                responseChannel, targetResponseChannel);
            if (interceptorChainHead != null) {
                interceptorChainTail.setNext(channelInterceptor);
            } else {
                interceptorChainHead = channelInterceptor;
            }

        } else {
            // no handlers
            if (interceptorChainHead != null) {
                if (targetInterceptorChainHead != null) {
                    // Connect source interceptor chain directly to target interceptor chain
                    interceptorChainTail.setNext(targetInterceptorChainHead);
                } else if (!(interceptorChainTail instanceof InvokerInterceptor)) {
                    // Connect source interceptor chain to the target request channel
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                        targetResponseChannel);
                    interceptorChainTail.setNext(channelInterceptor);
                }
            } else {
                // no source interceptor chain or source handlers, connect to target interceptor chain or channel
                if (targetInterceptorChainHead != null) {
                    interceptorChainHead = targetInterceptorChainHead;
                    interceptorChainTail = targetInterceptorChainHead;
                } else {
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                        targetResponseChannel);
                    interceptorChainHead = channelInterceptor;
                    interceptorChainTail = channelInterceptor;
                }
            }
        }
    }

}
