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
package org.apache.tuscany.core.invocation;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.invocation.Interceptor;
import org.apache.tuscany.invocation.InvocationChain;
import org.apache.tuscany.invocation.TargetInvoker;

/**
 * Default implementation of an invocation chain
 * 
 * @version $Rev$ $Date$
 */
public class InvocationChainImpl implements InvocationChain {
    protected Operation sourceOperation;
    protected Operation targetOperation;
    protected TargetInvoker targetInvoker;
    protected Interceptor interceptorChainHead;
    protected Interceptor interceptorChainTail;

    public InvocationChainImpl(Operation operation) {
        assert operation != null;
        this.targetOperation = operation;
        this.sourceOperation = operation;
    }

    public InvocationChainImpl(Operation sourceOperation, Operation targetOperation) {
        assert sourceOperation != null;
        assert targetOperation != null;
        this.targetOperation = targetOperation;
        this.sourceOperation = sourceOperation;
    }

    public Operation getTargetOperation() {
        return targetOperation;
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

    public void addInterceptor(int index, Interceptor interceptor) {
        int i = 0;
        Interceptor next = interceptorChainHead;
        Interceptor prev = null;
        while (next != null && i < index) {
            prev = next;
            next = next.getNext();
            i++;
        }
        if (i == index) {
            if (prev != null) {
                prev.setNext(interceptor);
            } else {
                interceptorChainHead = interceptor;
            }
            interceptor.setNext(next);
            if (next == null) {
                interceptorChainTail = interceptor;
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public Interceptor getHeadInterceptor() {
        return interceptorChainHead;
    }

    public Interceptor getTailInterceptor() {
        return interceptorChainTail;
    }

    /**
     * @return the sourceOperation
     */
    public Operation getSourceOperation() {
        return sourceOperation;
    }

    /**
     * @param sourceOperation the sourceOperation to set
     */
    public void setSourceOperation(Operation sourceOperation) {
        this.sourceOperation = sourceOperation;
    }

}
