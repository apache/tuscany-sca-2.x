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
package org.apache.tuscany.sca.core.invocation;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;

/**
 * Default implementation of an invocation chain
 * 
 * @version $Rev$ $Date$
 */
public class InvocationChainImpl implements InvocationChain {
    private Operation sourceOperation;
    private Operation targetOperation;
    private Invoker invokerChainHead;
    private Invoker invokerChainTail;

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

    public void addInterceptor(Interceptor interceptor) {
        if (invokerChainHead == null) {
            invokerChainHead = interceptor;
        } else {
            if (invokerChainHead instanceof Interceptor) {
                ((Interceptor)invokerChainTail).setNext(interceptor);
            }
        }
        invokerChainTail = interceptor;
    }

    public void addInvoker(Invoker invoker) {
        if (invokerChainHead == null) {
            invokerChainHead = invoker;
        } else {
            if (invokerChainTail instanceof Interceptor) {
                ((Interceptor)invokerChainTail).setNext(invoker);
            }
        }
        invokerChainTail = invoker;
    }

    public Invoker getHeadInvoker() {
        return invokerChainHead;
    }

    public Invoker getTailInvoker() {
        return invokerChainTail;
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

    public void addInterceptor(int index, Interceptor interceptor) {
        int i = 0;
        Invoker next = invokerChainHead;
        Invoker prev = null;
        while (next != null && i < index) {
            prev = next;
            if (next instanceof Interceptor) {
                next = ((Interceptor)next).getNext();
                i++;
            } else {
                throw new ArrayIndexOutOfBoundsException(index);
            }
        }
        if (i == index) {
            if (prev != null) {
                ((Interceptor)prev).setNext(interceptor);
            } else {
                invokerChainHead = interceptor;
            }
            interceptor.setNext(next);
            if (next == null) {
                invokerChainTail = interceptor;
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

}
