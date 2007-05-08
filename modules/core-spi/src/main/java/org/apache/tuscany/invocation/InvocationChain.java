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
package org.apache.tuscany.invocation;

import org.apache.tuscany.interfacedef.Operation;

/**
 * A wire consists of 1..n invocation chains associated with the operations of its source service contract.
 * <p/>
 * Invocation chains may contain </ode>Interceptors</code> that process invocations in an around-style manner.
 * <p/>
 * A <code>Message</code> is used to pass data associated with an invocation through the chain.
 *
 * @version $Rev$ $Date$
 */
public interface InvocationChain {
    /**
     * Returns the target operation for this invocation chain.
     *
     * @return the target operation for this invocation chain
     */
    Operation getTargetOperation();
    
    /**
     * Returns the source operation for this invocation chain.
     *
     * @return the source operation for this invocation chain
     */    
    Operation getSourceOperation();

    /**
     * Adds an interceptor to the chain
     *
     * @param interceptor the interceptor to add
     */
    void addInterceptor(Interceptor interceptor);

    /**
     * Adds an invoker to the chain
     *
     * @param invoker the invoker to add
     */
    void addInvoker(Invoker invoker);

    /**
     * Returns the first invoker in the chain.
     *
     * @return the first invoker in the chain
     */
    Invoker getHeadInvoker();

    /**
     * Returns the last invoker in the chain.
     *
     * @return the last invoker in the chain
     */
    Invoker getTailInvoker();

    /**
     * Adds an interceptor at the given position in the interceptor stack
     *
     * @param index       the position in the interceptor stack to add the interceptor
     * @param interceptor the interceptor to add
     */
    void addInterceptor(int index, Interceptor interceptor);

}
