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

import org.apache.tuscany.core.wire.impl.MessageChannelImpl;
import org.apache.tuscany.core.wire.impl.MessageDispatcher;
import org.apache.tuscany.core.wire.impl.RequestResponseInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains a source- or target-side wire pipeline for a service operation. Source and target wire pipelines
 * are "bridged" together by a set of wire builders with the source-side holding references to the target.
 * <p>
 * A set of wire configurations are used by a {@link org.apache.tuscany.core.wire.ProxyFactory} to
 * create service proxies.
 * <p>
 * Invocation configurations must contain at least one interceptor and may have 0 to N handlers. Handlers process an
 * wire request or response in a one-way fashion. A typical wire sequence where interceptors and handlers
 * are configured for both the source and target-side will proceed as follows:
 * <ol>
 * <li>The first source interceptor will be called with a message, which will in turn invoke the next interceptor in
 * the chain
 * <li>The last source interceptor, which must be of type
 * {@link org.apache.tuscany.core.wire.impl.RequestResponseInterceptor} if there are handlers present, will be
 * invoked. The RR interceptor will in turn pass the message to a
 * {@link org.apache.tuscany.core.wire.MessageChannel} which will invoke all source-side request handlers.
 * <li> The RR interceptor will then invoke the target-side request <tt>MessageChannel</tt>.
 * <li> The last source-side handler, an instance of
 * {@link org.apache.tuscany.core.wire.impl.MessageDispatcher}, will invoke the first source-side
 * interceptor, which in turn will pass the message down the target-side interceptor chain.
 * <li> If the target is a component instance the last target-side interceptor, an instance of
 * {@link org.apache.tuscany.core.wire.impl.InvokerInterceptor} will retrieve the
 * {@link org.apache.tuscany.core.wire.TargetInvoker} from the message and call it to invoke the operation on a
 * target instance. <tt>TargetInvoker</tt>s are help by the source proxy to enable optimizations such as caching of
 * target instances.
 * <li> The response is returned up the wire stack until it reaches the source-side
 * <tt>RequestResponseInterceptor</tt>, which invokes the target and source-side response channels respectively.
 * <li> The response is then passed back up the rest of the wire stack.
 * </ol>
 * <p>
 * The source-to-target bridge may be constructed in any of the following ways:
 * <ul>
 * <li>Source handler-to-target handler
 * <li>Source handler-to-target interceptor
 * <li>Source interceptor-to-target handler
 * <li>Source interceptor-to-target interceptor
 * </ul>
 * <p>
 * In some scenarios, a service proxy may only contain target-side invocaton chains, for example, when a service is
 * resolved through a locate operation by a non-component client. In this case, there will be no source-side wire
 * chains and the target invoker will be held by the target-side and passed down the pipeline.
 * 
 * @see org.apache.tuscany.core.builder.WireBuilder
 * @see org.apache.tuscany.core.wire.ProxyFactory
 * @see org.apache.tuscany.core.wire.TargetInvoker
 * @see org.apache.tuscany.core.wire.impl.MessageDispatcher
 * 
 * @version $Rev$ $Date$
 */
public class InvocationConfiguration {

    // the operation on the target that will utlimately be invoked
    private Method operation;

    // responsible for invoking a target instance
    private TargetInvoker targetInvoker;

    private Interceptor sourceInterceptorChainHead;

    private Interceptor sourceInterceptorChainTail;

    private Interceptor targetInterceptorChainHead;

    private Interceptor targetInterceptorChainTail;

    private List<MessageHandler> requestHandlers;

    private List<MessageHandler> responseHandlers;

    // a source-side pointer to target request handlers, if the exist
    private MessageChannel targetRequestChannel;

    // a source-side pointer to target response handlers, if the exist
    private MessageChannel targetResponseChannel;

    /**
     * Creates an new wire configuration for the given target operation
     */
    public InvocationConfiguration(Method operation) {
        assert (operation != null) : "No operation type specified";
        this.operation = operation;
    }

    /**
     * Returns the target operation for the wire configuration
     */
    public Method getMethod() {
        return operation;
    }

    /**
     * Used by source-side configurations, sets a pointer to the target-side request channel. This may be null when no
     * target request handlers exist.
     */
    public void setTargetRequestChannel(MessageChannel channel) {
        targetRequestChannel = channel;
    }

    /**
     * Used by source-side configurations, sets a pointer to the target-side response channel. This may be null when no
     * target response handlers exist.
     */
    public void setTargetResponseChannel(MessageChannel channel) {
        targetResponseChannel = channel;
    }

    /**
     * Adds an interceptor to the wire chain for source-side configurations
     */
    public void addSourceInterceptor(Interceptor interceptor) {
        if (sourceInterceptorChainHead == null) {
            sourceInterceptorChainHead = interceptor;
        } else {
            sourceInterceptorChainTail.setNext(interceptor);
        }
        sourceInterceptorChainTail = interceptor;
    }

    /**
     * Adds an interceptor to the wire chain for target-side configurations
     */
    public void addTargetInterceptor(Interceptor interceptor) {
        if (targetInterceptorChainHead == null) {
            targetInterceptorChainHead = interceptor;
        } else {
            targetInterceptorChainTail.setNext(interceptor);
        }
        targetInterceptorChainTail = interceptor;
    }

    /**
     * Adds an request handler to the wire chain for either a source- or target-side configuration
     */
    public void addRequestHandler(MessageHandler handler) {
        if (requestHandlers == null) {
            requestHandlers = new ArrayList<MessageHandler>();
        }
        requestHandlers.add(handler);
    }

    /**
     * Adds an response handler to the wire chain for either a source- or target-side configuration
     */
    public void addResponseHandler(MessageHandler handler) {
        if (responseHandlers == null) {
            responseHandlers = new ArrayList<MessageHandler>();
        }
        responseHandlers.add(handler);
    }

    /**
     * Returns the request handler chain for either a source- or target-side configuration
     */
    public List<MessageHandler> getRequestHandlers() {
        return requestHandlers;
    }

    /**
     * Returns the response handler chain for either a source- or target-side configuration
     */
    public List<MessageHandler> getResponseHandlers() {
        return responseHandlers;
    }

    /**
     * Returns the head source-side interceptor. This will be null for target-side configurations
     */
    public Interceptor getSourceInterceptor() {
        return sourceInterceptorChainHead;
    }

    /**
     * Returns the head target-side interceptor. On source-side configurations, this will be the head interceptor of the
     * "bridged" target configuration.
     */
    public Interceptor getTargetInterceptor() {
        return targetInterceptorChainHead;
    }
    
   
    public Interceptor getLastTargetInterceptor() {
        return targetInterceptorChainTail;
    }

    /**
     * Sets the target invoker to pass down the wire pipeline. When a service proxy represents a wire,
     * the target invoker is set on the source-side.
     */
    public void setTargetInvoker(TargetInvoker invoker) {
        this.targetInvoker = invoker;
    }

    /**
     * Returns the target invoker that is passed down the wire pipeline. When a service proxy represents a wire,
     * the target invoker is cached on the source-side.
     */
    public TargetInvoker getTargetInvoker() {
        return targetInvoker;
    }

    /**
     * Prepares the configuration by linking interceptors and handlers
     */
    public void build() {

        if (requestHandlers != null && targetInterceptorChainHead != null) {
            // on target-side, connect existing handlers and interceptors
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
                    // sourceInterceptorChainTail = targetInterceptorChainHead;
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
