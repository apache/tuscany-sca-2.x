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
package org.apache.tuscany.spi.wire;

import java.util.List;

import org.apache.tuscany.spi.model.Operation;

/**
 * A source- or target-side invocation pipeline for a service operation. Invocation chains are associated with the
 * source or target side of a wire and are bridged when an assembly is processed.
 * <p/>
 * Invocation configurations contain at least one {@link Interceptor} and may have 0 to N {@link MessageHandler}s.
 * <code>Interceptors>/code> process invocations in a synchronous, around style manner while
 * <code>MessageHandler</code>s do so in a one-way manner.
 * <p/>
 * Source-side chains may only connect to target-side chains. Target-side chains may connect to other target-side
 * chains, for example, when invoking from a {@link org.apache.tuscany.spi.component.Service} to an {@link
 * org.apache.tuscany.spi.component.AtomicComponent}.
 * <p/>
 * In some scenarios, a service proxy may only contain target-side invocaton chains, for example, when a service is
 * resolved through a locate operation by a non-component client. In this case, there will be no source-side wire chains
 * and the target invoker will be held by the target-side and passed down the pipeline.
 * <p/>
 * A {@link Message} is used to pass data associated with an invocation through the chain. <code>Message</code>s contain
 * a {@link TargetInvoker} responsible for dispatching to a target instance and may be cached on the client-side.
 * Caching allows various optimizations such as avoiding target instance resolution when the client-side lifecycle scope
 * is a shorter duration than the target.
 *
 * @version $Rev$ $Date$
 */
public interface InvocationChain {
    /**
     * Returns the target operation for this invocation chain
     */
    Operation getOperation();

    /**
     * Adds a request handler to the invocation chain
     */
    void addRequestHandler(MessageHandler handler);

    /**
     * Adds a response handler to the invocation chain
     */
    void addResponseHandler(MessageHandler handler);

    /**
     * Returns the request handler chain
     */
    List<MessageHandler> getRequestHandlers();

    /**
     * Returns the response handler chain
     */
    List<MessageHandler> getResponseHandlers();

    /**
     * Returns the request channel for the chain
     */
    MessageChannel getRequestChannel();

    /**
     * Returns the response channel for the chain
     */
    MessageChannel getResponseChannel();

    /**
     * Sets the target invoker to pass down the chain
     */
    void setTargetInvoker(TargetInvoker invoker);

    /**
     * Returns the target invoker that is passed down the chain
     */
    TargetInvoker getTargetInvoker();

    /**
     * Adds an interceptor to the chain
     */
    void addInterceptor(Interceptor interceptor);

    /**
     * Returns the first interceptor in the chain
     */
    Interceptor getHeadInterceptor();

    /**
     * Returns the last interceptor in the chain
     */
    Interceptor getTailInterceptor();

    /**
     * Sets the head interceptor of the bridged target-side chain
     */
    void setTargetInterceptor(Interceptor interceptor);

    /**
     * Returns the head interceptor of the birdged target-side chain
     */
    Interceptor getTargetInterceptor();

    /**
     * Sets the target-side request channel when two chains are bidged
     */
    void setTargetRequestChannel(MessageChannel channel);

    /**
     * Sets the target-side response channel when two chains are bridged
     */
    void setTargetResponseChannel(MessageChannel channel);

    /**
     * Returns the target-side request channel when two chains are bridged
     */
    MessageChannel getTargetRequestChannel();

    /**
     * Returns the target-side response channel when two chains are bridged
     */
    MessageChannel getTargetResponseChannel();

    /**
     * Signals to the chain that its configuration is complete. Implementations may use this callback to prepare their
     * invocation chains.
     */
    void prepare();
}
