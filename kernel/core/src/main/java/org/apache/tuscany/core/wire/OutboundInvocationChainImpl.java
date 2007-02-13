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

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

/**
 * Contains a outgoing invocation pipeline for a service operation.
 *
 * @version $Rev$ $Date$
 */
public class OutboundInvocationChainImpl extends AbstractInvocationChain implements OutboundInvocationChain {

    /**
     * Creates an new outbound chain
     */
    public OutboundInvocationChainImpl(Operation operation) {
        super(operation);
    }

    public void prepare() {
        if (interceptorChainHead != null) {
            if (targetInterceptorChainHead != null) {
                // Connect source interceptor chain directly to target interceptor chain
                interceptorChainTail.setNext(targetInterceptorChainHead);
            }
        } else {
            // no source interceptor chain or source handlers, connect to target interceptor chain or channel
            if (targetInterceptorChainHead != null) {
                interceptorChainHead = targetInterceptorChainHead;
                interceptorChainTail = targetInterceptorChainHead;
            }
        }
    }

}
