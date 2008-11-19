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
import java.util.ListIterator;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Phase;

/**
 * Default implementation of an invocation chain
 * 
 * @version $Rev$ $Date$
 */
public class InvocationChainImpl implements InvocationChain {
    private Operation sourceOperation;
    private Operation targetOperation;
    private List<Node> nodes = new ArrayList<Node>();

    // FIXME: Not a good practice to use static reference
    private static final PhaseManager phaseManager = new PhaseManager();
    private boolean forReference;
    private boolean allowsPassByReference;

    public InvocationChainImpl(Operation sourceOperation, Operation targetOperation, boolean forReference) {
        // TODO - binding invocation chain doesn't provide operations
        //assert sourceOperation != null;
        //assert targetOperation != null;
        this.targetOperation = targetOperation;
        this.sourceOperation = sourceOperation;
        this.forReference = forReference;
    }

    public Operation getTargetOperation() {
        return targetOperation;
    }

    public void setTargetOperation(Operation operation) {
        this.targetOperation = operation;
    }

    public void addInterceptor(Interceptor interceptor) {
        String phase = forReference ? Phase.REFERENCE : Phase.SERVICE;
        addInterceptor(phase, interceptor);
    }

    public void addInvoker(Invoker invoker) {
        String phase = forReference ? Phase.REFERENCE_BINDING : Phase.IMPLEMENTATION;
        addInvoker(phase, invoker);
    }

    public Invoker getHeadInvoker() {
        return nodes.isEmpty() ? null : nodes.get(0).getInvoker();
    }

    public Invoker getTailInvoker() {
        return nodes.isEmpty() ? null : nodes.get(nodes.size() - 1).getInvoker();
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
        addInterceptor(interceptor);
    }

    public void addInterceptor(String phase, Interceptor interceptor) {
        addInvoker(phase, interceptor);
    }

    private void addInvoker(String phase, Invoker invoker) {
        int index = phaseManager.getAllPhases().indexOf(phase);
        if (index == -1) {
            throw new IllegalArgumentException("Invalid phase name: " + phase);
        }
        Node node = new Node(index, invoker);
        ListIterator<Node> li = nodes.listIterator();
        Node before = null, after = null;
        boolean found = false;
        while (li.hasNext()) {
            before = after;
            after = li.next();
            if (after.getPhaseIndex() > index) {
                // Move back
                li.previous();
                li.add(node);
                found = true;
                break;
            }
        }
        if (!found) {
            // Add to the end
            nodes.add(node);
            before = after;
            after = null;
        }

        // Relink the interceptors
        if (before != null) {
            if (before.getInvoker() instanceof Interceptor) {
                ((Interceptor)before.getInvoker()).setNext(invoker);
            }
        }
        if (after != null) {
            if (invoker instanceof Interceptor) {
                ((Interceptor)invoker).setNext(after.getInvoker());
            }
        }

    }

    public boolean allowsPassByReference() {
        if (allowsPassByReference) {
            // No need to check the invokers
            return true;
        }
        // Check if any of the invokers allows pass-by-reference
        boolean allowsPBR = false;
        for (Node i : nodes) {
            if (i.getInvoker() instanceof DataExchangeSemantics) {
                if (((DataExchangeSemantics)i.getInvoker()).allowsPassByReference()) {
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

    private static class Node {
        private int phaseIndex;
        private Invoker invoker;

        public Node(int phaseIndex, Invoker invoker) {
            super();
            this.phaseIndex = phaseIndex;
            this.invoker = invoker;
        }

        public int getPhaseIndex() {
            return phaseIndex;
        }

        public Invoker getInvoker() {
            return invoker;
        }

        @Override
        public String toString() {
            return "(" + phaseIndex + ")" + invoker;
        }
    }

}
