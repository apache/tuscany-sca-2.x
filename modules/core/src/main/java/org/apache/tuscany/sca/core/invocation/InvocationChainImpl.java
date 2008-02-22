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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.PassByValueAware;

/**
 * Default implementation of an invocation chain
 * 
 * @version $Rev$ $Date$
 */
public class InvocationChainImpl implements InvocationChain {
    private Operation sourceOperation;
    private Operation targetOperation;
    private List<Invoker> invokers = new ArrayList<Invoker>();
    private boolean allowsPassByReference;

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

    public void setTargetOperation(Operation operation) {
        this.targetOperation = operation;
    }

    public void addInterceptor(Interceptor interceptor) {
        invokers.add(interceptor);
        int index = invokers.size() - 1;
        if (index - 1 >= 0) {
            Invoker before = invokers.get(index - 1);
            if (before instanceof Interceptor) {
                ((Interceptor)before).setNext(interceptor);
            }
        }
    }

    public void addInvoker(Invoker invoker) {
        invokers.add(invoker);
        int index = invokers.size() - 1;
        if (index - 1 >= 0) {
            Invoker before = invokers.get(index - 1);
            if (before instanceof Interceptor) {
                ((Interceptor)before).setNext(invoker);
            }
        }
    }

    public Invoker getHeadInvoker() {
        return invokers.isEmpty() ? null : invokers.get(0);
    }

    public Invoker getTailInvoker() {
        return invokers.isEmpty() ? null : invokers.get(invokers.size() - 1);
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
        invokers.add(index, interceptor);
        if (index - 1 >= 0) {
            Invoker before = invokers.get(index - 1);
            if (before instanceof Interceptor) {
                ((Interceptor)before).setNext(interceptor);
            }
        }
        if (index + 1 < invokers.size()) {
            Invoker after = invokers.get(index + 1);
            interceptor.setNext(after);
        }
    }

    public boolean allowsPassByReference() {
        if(allowsPassByReference) {
            // No need to check the invokers
            return true;
        }
        // Check if any of the invokers allows pass-by-reference
        boolean allowsPBR = false;
        for (Invoker i : invokers) {
            if (i instanceof PassByValueAware) {
                if (((PassByValueAware)i).allowsPassByReference()) {
                    allowsPBR = true;
                    break;
                }
            }
        }
        return allowsPBR;
    }

    public void setAllowsPassByReference(boolean allowsPBR) {
        this.allowsPassByReference = allowsPBR;
    }

}
