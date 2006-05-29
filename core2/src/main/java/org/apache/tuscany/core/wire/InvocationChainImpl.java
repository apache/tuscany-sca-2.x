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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.TargetInvoker;


/**
 * Contains functionality common to source- and target- side invocation chains
 *
 * @version $Rev$ $Date$
 */
public abstract class InvocationChainImpl implements InvocationChain {
    protected Method operation;
    protected TargetInvoker targetInvoker;
    protected Interceptor interceptorChainHead;
    protected Interceptor interceptorChainTail;
    protected List<MessageHandler> requestHandlers;
    protected List<MessageHandler> responseHandlers;
    protected MessageChannel requestChannel;
    protected MessageChannel responseChannel;

    // the pointer to a bridged target request channel, or null if the target has an interceptor
    protected MessageChannel targetRequestChannel;
    // the pointer to a bridged target response channel, or null if the target has an interceptor
    protected MessageChannel targetResponseChannel;
    // the pointer to a bridged target head interceptor or null if the target has no interceptors
    protected Interceptor targetInterceptorChainHead;

    public InvocationChainImpl(Method operation) {
        assert (operation != null) : "No operation type specified";
        this.operation = operation;
    }

    public Method getMethod() {
        return operation;
    }

    public List<MessageHandler> getRequestHandlers() {
        return requestHandlers;
    }

    public List<MessageHandler> getResponseHandlers() {
        return responseHandlers;
    }

    public void setTargetInvoker(TargetInvoker invoker) {
        this.targetInvoker = invoker;
    }

    public TargetInvoker getTargetInvoker() {
        return targetInvoker;
    }

    public void addInterceptor(Interceptor interceptor) {
        if (interceptorChainHead == null) {
            interceptorChainHead = interceptor;
        } else {
            interceptorChainTail.setNext(interceptor);
        }
        interceptorChainTail = interceptor;
    }

    public Interceptor getHeadInterceptor() {
        return interceptorChainHead;
    }

    public Interceptor getTailInterceptor() {
        return interceptorChainTail;
    }

    public MessageChannel getRequestChannel() {
        return requestChannel;
    }

    public MessageChannel getResponseChannel() {
        return responseChannel;
    }

    public void addRequestHandler(MessageHandler handler) {
        if (requestHandlers == null) {
            requestHandlers = new ArrayList<MessageHandler>();
            requestChannel = new MessageChannelImpl(requestHandlers);
        }
        requestHandlers.add(handler);
    }

    public void addResponseHandler(MessageHandler handler) {
        if (responseHandlers == null) {
            responseHandlers = new ArrayList<MessageHandler>();
            responseChannel = new MessageChannelImpl(responseHandlers);
        }
        responseHandlers.add(handler);
    }

    public MessageChannel getTargetRequestChannel() {
        return targetRequestChannel;
    }

    public void setTargetRequestChannel(MessageChannel channel) {
        this.targetRequestChannel = channel;
    }

    public void setTargetResponseChannel(MessageChannel channel) {
        this.targetResponseChannel = channel;
    }

    public MessageChannel getTargetResponseChannel() {
        return targetResponseChannel;
    }

    public void setTargetInterceptor(Interceptor interceptor) {
        targetInterceptorChainHead = interceptor;
    }

    public Interceptor getTargetInterceptor() {
        return targetInterceptorChainHead;
    }


}
