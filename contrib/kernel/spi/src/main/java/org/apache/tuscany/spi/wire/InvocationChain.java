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
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;

/**
 * A wire consists of 1..n invocation chains associated with the operations of its source service contract.
 * <p/>
 * Invocation chains may contain </ode>Interceptors</code> that process invocations in an around-style manner.
 * Invocation chains are also associated with a <code>TargetInvoker</code> which is responsible for dispatching on the
 * target service provider.
 * <p/>
 * A <code>Message</code> is used to pass data associated with an invocation through the chain. The TargetInvoker is
 * passed with the Message through the interceptor stack, if one is present. At last interceptor in the stack, must call
 * the TargetInvoker.
 * <p/>
 * In certain circumstances, the TargetInvoker may be cached in the source-side proxy. Caching allows various
 * optimizations such as avoiding target instance resolution when the client-side lifecycle scope is a shorter duration
 * than the target.
 *
 * @version $Rev$ $Date$
 */
public interface InvocationChain {
    /**
     * Returns the target operation for this invocation chain.
     *
     * @return the target operation for this invocation chain
     */
    Operation getOperation();

    /**
     * Returns the target physical operation for this invocation chain.
     *
     * @return the target physical operation for this invocation chain
     */
    PhysicalOperationDefinition getPhysicalOperation();

    /**
     * Sets the target invoker to pass down the chain
     *
     * @param invoker the invoker
     */
    void setTargetInvoker(TargetInvoker invoker);

    /**
     * Returns the target invoker that is passed down the chain
     *
     * @return the target invoker
     */
    TargetInvoker getTargetInvoker();

    /**
     * Adds an interceptor to the chain
     *
     * @param interceptor the interceptor to add
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
     * Returns the first interceptor in the chain.
     *
     * @return the first interceptor in the chain
     */
    Interceptor getHeadInterceptor();

    /**
     * Returns the last interceptor in the chain.
     *
     * @return the last interceptor in the chain
     */
    Interceptor getTailInterceptor();

}
