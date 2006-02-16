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
package org.apache.tuscany.core.invocation;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.invocation.impl.RequestResponseInterceptor;
import org.apache.tuscany.core.message.channel.MessageChannel;
import org.apache.tuscany.core.message.channel.impl.MessageChannelImpl;
import org.apache.tuscany.core.message.channel.impl.MessageDispatcher;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.types.OperationType;

/**
 * Represents the proxy configuration information for an operation on a service reference
 * 
 * @version $Rev$ $Date$
 */
public class InvocationConfiguration {

    private OperationType operation;

    private TargetInvoker targetInvoker;

    private Interceptor sourceInterceptorChainHead;

    private Interceptor sourceInterceptorChainTail;

    private Interceptor targetInterceptorChainHead;

    private Interceptor targetInterceptorChainTail;

    private List<MessageHandler> requestHandlers;

    private List<MessageHandler> responseHandlers;

    private MessageChannel targetRequestChannel;

    private MessageChannel targetResponseChannel;

    public InvocationConfiguration(OperationType operation) {
        assert (operation != null) : "No operation type specified";
        this.operation = operation;
    }

    public OperationType getOperationType() {
        return operation;
    }

    public void addTargetRequestChannel(MessageChannel channel) {
        targetRequestChannel = channel;
    }

    public void addTargetResponseChannel(MessageChannel channel) {
        targetResponseChannel = channel;
    }

    public void addSourceInterceptor(Interceptor interceptor) {
        if (sourceInterceptorChainHead == null) {
            sourceInterceptorChainHead = interceptor;
        } else {
            sourceInterceptorChainTail.setNext(interceptor);
        }
        sourceInterceptorChainTail = interceptor;
    }

    public void addTargetInterceptor(Interceptor interceptor) {
        if (targetInterceptorChainHead == null) {
            targetInterceptorChainHead = interceptor;
        } else {
            targetInterceptorChainTail.setNext(interceptor);
        }
        targetInterceptorChainTail = interceptor;
    }

    public void addRequestHandler(MessageHandler handler) {
        if (requestHandlers == null) {
            requestHandlers = new ArrayList<MessageHandler>();
        }
        requestHandlers.add(handler);
    }

    public void addResponseHandler(MessageHandler handler) {
        if (responseHandlers == null) {
            responseHandlers = new ArrayList<MessageHandler>();
        }
        responseHandlers.add(handler);
    }

    public void setTargetInvoker(TargetInvoker invoker) {
        this.targetInvoker = invoker;
    }

    public TargetInvoker getTargetInvoker() {
        return targetInvoker;
    }

    public Interceptor getSourceInterceptor() {
        return sourceInterceptorChainHead;
    }

    public Interceptor getTargetInterceptor() {
        return targetInterceptorChainHead;
    }

    public List<MessageHandler> getRequestHandlers() {
        return requestHandlers;
    }

    public List<MessageHandler> getResponseHandlers() {
        return responseHandlers;
    }

    /**
     * Build the configuration, link the interceptors and handlers together
     */
    public void build() {

        if (requestHandlers != null && targetInterceptorChainHead != null) {
            // on target side, connect existing handlers and interceptors
            MessageHandler messageDispatcher = new MessageDispatcher(targetInterceptorChainHead);
            requestHandlers.add(messageDispatcher);
        }

        if (requestHandlers != null) {
            MessageChannel requestChannel = new MessageChannelImpl(requestHandlers);
            MessageChannel responseChannel = new MessageChannelImpl(responseHandlers);
            Interceptor channelInterceptor = new RequestResponseInterceptor(requestChannel, targetRequestChannel,
                    responseChannel, targetResponseChannel);

            if (sourceInterceptorChainHead != null) {
                sourceInterceptorChainTail.setNext(channelInterceptor);
            } else {
                sourceInterceptorChainHead = channelInterceptor;
            }

        } else {
            // no request handlers
            if (sourceInterceptorChainHead != null) {
                if (targetInterceptorChainHead != null) {
                    // Connect source interceptor chain directly to target interceptor chain
                    sourceInterceptorChainTail.setNext(targetInterceptorChainHead);
                    //sourceInterceptorChainTail = targetInterceptorChainHead;
                } else {
                    // Connect source interceptor chain to the target request channel
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                            targetResponseChannel);
                    sourceInterceptorChainTail.setNext(channelInterceptor);
                }
            } else {
                // no source interceptor chain or source handlers, conntect to target interceptor chain or channel
                if (targetInterceptorChainHead != null) {
                    sourceInterceptorChainHead = targetInterceptorChainHead;
                    sourceInterceptorChainTail = targetInterceptorChainHead;
                } else {
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                            targetResponseChannel);
                    sourceInterceptorChainHead = channelInterceptor;
                    sourceInterceptorChainTail = channelInterceptor;
                }
            }
        }
    }

}
