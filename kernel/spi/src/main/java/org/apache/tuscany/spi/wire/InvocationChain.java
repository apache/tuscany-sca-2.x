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

import org.apache.tuscany.spi.model.Operation;

/**
 * An inbound or outbound invocation pipeline for a service operation. Wires consist of 1..n invocation chains
 * associated with the operations of the service contract the wire represents. Invocation chains are associated with the
 * outbound or inbound side of a wire are bridged or "connected" when an assembly is processed. Outbound chains are only
 * connected to inbound chains and vice versa.
 * <p/>
 * Invocation chains contain at least one {@link Interceptor} that process invocations in an around-style manner. In
 * some scenarios, a service proxy may only contain inbound invocation chains, for example, when a service is resolved
 * through a locate operation by a non-component client. In this case, there will be no outbound invocation chains and
 * the target invoker will be held by the target-side and passed down the pipeline.
 * <p/>
 * A {@link Message} is used to pass data associated with an invocation through the chain. <code>Message</code>s contain
 * a {@link TargetInvoker} responsible for dispatching to a target instance and may be cached on the source-side.
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
     * Adds an interceptor at the given position in the interceptor stack
     *
     * @param index       the position in the interceptor stack to add the interceptor
     * @param interceptor the interceptor to add
     */
    void addInterceptor(int index, Interceptor interceptor);

    /**
     * Returns the first interceptor in the chain
     */
    Interceptor getHeadInterceptor();

    /**
     * Returns the last interceptor in the chain
     */
    Interceptor getTailInterceptor();

}
