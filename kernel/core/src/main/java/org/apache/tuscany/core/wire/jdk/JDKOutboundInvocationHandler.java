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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.AbstractOutboundInvocationHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;


/**
 * Receives a request from a proxy and performs an invocation on an {@link org.apache.tuscany.spi.wire.OutboundWire} via
 * an {@link org.apache.tuscany.spi.wire.OutboundInvocationChain}
 *
 * @version $Rev$ $Date$
 */
public class JDKOutboundInvocationHandler extends AbstractOutboundInvocationHandler
    implements WireInvocationHandler, InvocationHandler {

    /*
     * an association of an operation to chain holder. The holder contains an invocation chain
     * and a local clone of the master TargetInvoker. TargetInvokers will be cloned by the handler and placed in the
     * holder if they are cacheable. This allows optimizations such as avoiding target resolution when a source refers
     * to a target of greater scope since the target reference can be maintained by the invoker. When a target invoker
     * is not cacheable, the master associated with the wire chains will be used.
     */
    private Map<Method, ChainHolder> chains;
    private Object fromAddress;
    private boolean contractHasCallback;
    private boolean contractIsRemotable;
    private boolean contractIsConversational;
    private Object convIdForRemotableTarget;
    private Object convIdFromThread;

    private WorkContext workContext;

    public JDKOutboundInvocationHandler(OutboundWire wire) {
        this(wire, null);
    }

    public JDKOutboundInvocationHandler(OutboundWire wire, WorkContext workContext)
        throws NoMethodForOperationException {
        Map<Operation<?>, OutboundInvocationChain> invocationChains = wire.getInvocationChains();
        this.chains = new HashMap<Method, ChainHolder>(invocationChains.size());
        this.fromAddress = (wire.getContainer() == null) ? null : wire.getContainer().getName();
        Method[] methods = wire.getServiceContract().getInterfaceClass().getMethods();
        this.contractHasCallback = wire.getServiceContract().getCallbackClass() != null;
        // TODO optimize this
        for (Map.Entry<Operation<?>, OutboundInvocationChain> entry : invocationChains.entrySet()) {
            Operation operation = entry.getKey();
            Method method = findMethod(operation, methods);
            if (method == null) {
                throw new NoMethodForOperationException(operation.getName());
            }
            this.chains.put(method, new ChainHolder(entry.getValue()));
        }

        this.workContext = workContext;
        this.contractIsConversational =
            wire.getServiceContract().getInteractionScope().equals(InteractionScope.CONVERSATIONAL);
        this.contractIsRemotable = wire.getServiceContract().isRemotable();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ChainHolder holder = chains.get(method);
        if (holder == null) {
            if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
                return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
            } else if (method.getDeclaringClass().equals(Object.class)
                && "equals".equals(method.getName())) {
                // TODO implement
                throw new UnsupportedOperationException();
            } else if (Object.class.equals(method.getDeclaringClass())
                && "hashCode".equals(method.getName())) {
                return hashCode();
                // TODO beter hash algorithm
            }
            TargetException e = new TargetException("Operation not configured");
            e.setIdentifier(method.getName());
            throw e;
        }
        OutboundInvocationChain chain = holder.chain;
        TargetInvoker invoker;

        if (holder.cachedInvoker == null) {
            assert chain != null;
            if (chain.getTargetInvoker() == null) {
                TargetException e = new TargetException("No target invoker configured for operation");
                e.setIdentifier(chain.getOperation().getName());
                throw e;
            }
            if (chain.getTargetInvoker().isCacheable()) {
                // clone and store the invoker locally
                holder.cachedInvoker = (TargetInvoker) chain.getTargetInvoker().clone();
                invoker = holder.cachedInvoker;
            } else {
                invoker = chain.getTargetInvoker();
            }
        } else {
            assert chain != null;
            invoker = chain.getTargetInvoker();
        }

        if (contractIsConversational) {
            assert workContext != null : "Work context cannot be null for conversational invocation";
            // Check for a conv id on thread and remember it
            convIdFromThread = workContext.getIdentifier(Scope.CONVERSATIONAL);
            if (contractIsRemotable) {
                if (convIdForRemotableTarget == null) {
                    convIdForRemotableTarget = new org.apache.tuscany.spi.wire.MessageId();
                }
                // Always use the conv id for this target
                workContext.setIdentifier(Scope.CONVERSATIONAL, convIdForRemotableTarget);
            } else if (convIdFromThread == null) {
                Object newConvId = new org.apache.tuscany.spi.wire.MessageId();
                workContext.setIdentifier(Scope.CONVERSATIONAL, newConvId);
            }
        }

        Object result = invoke(chain, invoker, args, null, null);

        if (contractIsConversational && contractIsRemotable) {
            // Make sure we restore the remembered conv id to continue propagating
            workContext.setIdentifier(Scope.CONVERSATIONAL, convIdFromThread);
        }

        return result;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }

    protected Object getFromAddress() {
        return contractHasCallback ? fromAddress : null;
    }

    /**
     * A holder used to associate an wire chain with a local copy of a target invoker that was previously cloned from
     * the chain master
     */
    private class ChainHolder {

        OutboundInvocationChain chain;
        TargetInvoker cachedInvoker;

        public ChainHolder(OutboundInvocationChain config) {
            this.chain = config;
        }

    }

}
