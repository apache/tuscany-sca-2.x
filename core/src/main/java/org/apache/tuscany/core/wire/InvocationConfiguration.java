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
import java.util.ArrayList;
import java.util.List;

/**
 * Contains a source- or target-side invocation pipeline for a service operation. The runtime framework creates invocation chains
 * on a per-operation, per-service basis. Further, invocation chains are further distinguished by being part of the source or
 * target sides of a wire. Chains are "bridged" together by the runtime by a set of {@link
 * org.apache.tuscany.core.builder.WireBuilder}s with the source-side holding references to the target.
 * <p/>
 * <code>InvocationChain</code>s are managed by {@link WireConfiguration}s, which are used by {@link
 * org.apache.tuscany.core.wire.WireFactory}s to buildSource wires and proxies.
 * <p/>
 * Invocation configurations must contain at least one interceptor and may have 0 to N handlers. Handlers process a wire request
 * or response in a one-way fashion. A typical wire sequence where interceptors and handlers are configured for both the source
 * and target-side will proceed as follows:
 * <pre>
 * <ol>
 * <li>The first source interceptor will be called with a message, which will in
 *     turn invoke the next interceptor in the chain <li>The last source interceptor, which must be of type {@link
 *     org.apache.tuscany.core.wire.impl.RequestResponseInterceptor} if there are handlers present, will be invoked. The RR
 *     interceptor will in turn pass the message to a {@link MessageChannel} which will invoke all source-side request handlers.
 * <li>The RR interceptor will then invoke the target-side request <code>MessageChannel</code>.
 * <li>The last source-side handler, an instance of {@link org.apache.tuscany.core.wire.impl.MessageDispatcher}, will invoke the
 *     first source-side interceptor, which in turn will pass the message down the target-side interceptor chain.
 * <li>If the target is a component instance the last target-side interceptor, an instance of
 *     {@link org.apache.tuscany.core.wire.impl.InvokerInterceptor} will retrieve the {@link TargetInvoker} from the message and
 *     call it to invoke the operation on a target instance. <tt>TargetInvoker</tt>s are help by
 *     the source proxy to enable optimizations such as caching of target instances. <li> The response is returned up the wire
 * stack
 *     until it reaches the source-side <tt>RequestResponseInterceptor</tt>, which invokes the target and source-side response
 *     channels respectively.
 * <li>The response is then passed back up the rest of the wire stack. </ol>
 * </pre>
 * <p/>
 * The source-to-target bridge may be constructed in any of the following ways:
 * <pre>
 * <ul>
 * <li>Source handler-to-target handler
 * <li>Source handler-to-target interceptor
 * <li>Source interceptor-to-target handler
 * <li>Source interceptor-to-target interceptor
 * </ul>
 * </pre>
 * <p/>
 * In some scenarios, a service proxy may only contain target-side invocaton chains, for example, when a service is resolved
 * through a locate operation by a non-component client. In this case, there will be no source-side wire chains and the target
 * invoker will be held by the target-side and passed down the pipeline.
 *
 * @version $Rev$ $Date$
 * @see org.apache.tuscany.core.builder.WireBuilder
 * @see WireFactory
 * @see TargetInvoker
 * @see org.apache.tuscany.core.wire.impl.MessageDispatcher
 */
public abstract class InvocationConfiguration {

    // the operation on the target that will utlimately be invoked
    protected Method operation;

    // responsible for invoking a target instance
    protected TargetInvoker targetInvoker;

    protected Interceptor interceptorChainHead;

    protected Interceptor interceptorChainTail;

    protected List<MessageHandler> requestHandlers;

    protected List<MessageHandler> responseHandlers;

    public InvocationConfiguration(Method operation) {
        assert (operation != null) : "No operation type specified";
        this.operation = operation;
    }

    /**
     * Returns the target operation for this invocation chain
     */
    public Method getMethod() {
        return operation;
    }

    /**
     * Adds an request handler to the invocation chain
     */
    public void addRequestHandler(MessageHandler handler) {
        if (requestHandlers == null) {
            requestHandlers = new ArrayList<MessageHandler>();
        }
        requestHandlers.add(handler);
    }

    /**
     * Adds an response handler to the invocation chain
     */
    public void addResponseHandler(MessageHandler handler) {
        if (responseHandlers == null) {
            responseHandlers = new ArrayList<MessageHandler>();
        }
        responseHandlers.add(handler);
    }

    /**
     * Returns the request handler chain
     */
    public List<MessageHandler> getRequestHandlers() {
        return requestHandlers;
    }

    /**
     * Returns the response handler chain
     */
    public List<MessageHandler> getResponseHandlers() {
        return responseHandlers;
    }

    /**
     * Sets the target invoker to pass down the chain
     */
    public void setTargetInvoker(TargetInvoker invoker) {
        this.targetInvoker = invoker;
    }

    /**
     * Returns the target invoker that is passed down the chain
     */
    public TargetInvoker getTargetInvoker() {
        return targetInvoker;
    }

    /**
     * Adds an interceptor to the chain
     */
    public void addInterceptor(Interceptor interceptor) {
        if (interceptorChainHead == null) {
            interceptorChainHead = interceptor;
        } else {
            interceptorChainTail.setNext(interceptor);
        }
        interceptorChainTail = interceptor;
    }

    /**
     * Returns the last interceptor in the chain
     */
    public Interceptor getTailInterceptor() {
        return interceptorChainTail;
    }

    /**
     * Returns the first interceptor in the chain
     */
    public Interceptor getHeadInterceptor() {
        return interceptorChainHead;
    }

    /**
     * Signals to the chain that its configuration is complete. Implementations may use this callback to prepare there invocation
     * chains.
     */
    public abstract void build();
}
