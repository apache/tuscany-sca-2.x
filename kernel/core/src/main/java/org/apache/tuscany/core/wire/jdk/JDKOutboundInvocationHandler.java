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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.osoa.sca.NoRegisteredCallbackException;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ReactivationException;
import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.TargetInvocationException;
import org.apache.tuscany.spi.component.WorkContext;
import static org.apache.tuscany.spi.model.InteractionScope.CONVERSATIONAL;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.util.UriHelper;
import org.apache.tuscany.spi.wire.AbstractOutboundInvocationHandler;
import org.apache.tuscany.spi.wire.OutboundChainHolder;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

import org.apache.tuscany.core.wire.NoMethodForOperationException;
import org.apache.tuscany.core.wire.WireUtils;


/**
 * Receives a request from a proxy and performs an invocation on an {@link org.apache.tuscany.spi.wire.OutboundWire} via
 * an {@link org.apache.tuscany.spi.wire.OutboundInvocationChain}
 *
 * @version $Rev$ $Date$
 */
public final class JDKOutboundInvocationHandler extends AbstractOutboundInvocationHandler
    implements WireInvocationHandler, InvocationHandler, Externalizable, SCAExternalizable {
    private static final long serialVersionUID = -6155278451964527325L;

    /*
     * an association of an operation to chain holder. The holder contains an invocation chain
     * and a local clone of the master TargetInvoker. TargetInvokers will be cloned by the handler and placed in the
     * holder if they are cacheable. This allows optimizations such as avoiding target resolution when a source refers
     * to a target of greater scope since the target reference can be maintained by the invoker. When a target invoker
     * is not cacheable, the master associated with the wire chains will be used.
     */
    private transient Map<Method, OutboundChainHolder> chains;
    private transient WorkContext workContext;
    private transient URI fromAddress;
    private transient boolean wireContainerIsAtomicComponent;
    private transient boolean contractHasCallback;
    private transient boolean callbackIsImplemented;
    private transient String callbackClassName;
    private transient boolean contractIsRemotable;
    private transient boolean contractIsConversational;
    private transient String convIdForRemotableTarget;
    private transient String convIdFromThread;
    private String referenceName;
    private Class<?> interfaze;

    /**
     * Constructor used for deserialization only
     */
    public JDKOutboundInvocationHandler() {
    }

    public JDKOutboundInvocationHandler(Class<?> interfaze, OutboundWire wire, WorkContext workContext)
        throws NoMethodForOperationException {
        this.workContext = workContext;
        this.interfaze = interfaze;
        init(interfaze, wire, null);
    }

    public JDKOutboundInvocationHandler(Class<?> interfaze,
                                        OutboundWire wire,
                                        Map<Method, OutboundChainHolder> mapping,
                                        WorkContext workContext)
        throws NoMethodForOperationException {
        this.workContext = workContext;
        this.interfaze = interfaze;
        init(interfaze, wire, mapping);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        OutboundChainHolder holder = chains.get(method);
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
            throw new TargetInvocationException("Operation not configured", method.getName());
        }
        OutboundInvocationChain chain = holder.getChain();
        TargetInvoker invoker;

        if (holder.getCachedInvoker() == null) {
            assert chain != null;
            if (chain.getTargetInvoker() == null) {
                String name = chain.getOperation().getName();
                throw new TargetInvocationException("No target invoker configured for operation", name);
            }
            if (chain.getTargetInvoker().isCacheable()) {
                // clone and store the invoker locally
                holder.setCachedInvoker((TargetInvoker) chain.getTargetInvoker().clone());
                invoker = holder.getCachedInvoker();
            } else {
                invoker = chain.getTargetInvoker();
            }
        } else {
            assert chain != null;
            invoker = chain.getTargetInvoker();
        }

        if (wireContainerIsAtomicComponent && contractHasCallback && !callbackIsImplemented) {
            throw new NoRegisteredCallbackException("Instance is does not implement callback: "
                + callbackClassName);
        }

        if (contractIsConversational) {
            assert workContext != null : "Work context cannot be null for conversational invocation";
            // Check for a conv id on thread and remember it
            convIdFromThread = (String) workContext.getIdentifier(Scope.CONVERSATION);
            if (contractIsRemotable) {
                if (convIdForRemotableTarget == null) {
                    convIdForRemotableTarget = createConversationID();
                }
                // Always use the conv id for this target
                workContext.setIdentifier(Scope.CONVERSATION, convIdForRemotableTarget);
            } else if (convIdFromThread == null) {
                String newConvId = createConversationID();
                workContext.setIdentifier(Scope.CONVERSATION, newConvId);
            }
        }

        Object result = invoke(chain, invoker, args, null, null);
        if (contractIsConversational && contractIsRemotable) {
            // Make sure we restore the remembered conv id to continue propagating
            workContext.setIdentifier(Scope.CONVERSATION, convIdFromThread);
        }
        return result;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }

    protected URI getFromAddress() {
        return contractHasCallback ? fromAddress : null;
    }

    public void setWorkContext(WorkContext context) {
        workContext = context;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(referenceName);
        out.writeObject(interfaze);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        referenceName = (String) in.readObject();
        interfaze = (Class<?>) in.readObject();
    }

    public void reactivate() throws ReactivationException {
        AtomicComponent owner = workContext.getCurrentAtomicComponent();
        if (owner == null) {
            throw new ReactivationException("Current atomic component not set on work context");
        }
        List<OutboundWire> wires = owner.getOutboundWires().get(referenceName);
        if (wires == null) {
            throw new ReactivationException("Reference wire not found", referenceName, owner.getUri().toString());
        }
        // TODO handle multiplicity
        OutboundWire wire = wires.get(0);
        try {
            init(interfaze, wire, null);
        } catch (NoMethodForOperationException e) {
            throw new ReactivationException(e);
        }
    }

    private void init(Class<?> interfaze, OutboundWire wire, Map<Method, OutboundChainHolder> mapping)
        throws NoMethodForOperationException {
        ServiceContract contract = wire.getServiceContract();
        this.referenceName = wire.getUri().getFragment();
        // TODO JFM remove getContainer
        this.fromAddress = UriHelper.getDefragmentedName(wire.getUri());
        this.contractIsConversational = contract.getInteractionScope().equals(CONVERSATIONAL);
        this.contractIsRemotable = contract.isRemotable();
        this.contractHasCallback = contract.getCallbackClass() != null;
        if (contractHasCallback) {
            this.callbackClassName = contract.getCallbackClass().getName();
        } else {
            this.callbackClassName = null;
        }
        // FIXME JFM this should not be dependent on PojoAtomicComponent
        // JFM commenting out as this should not be specific to pojo types
//        this.wireContainerIsAtomicComponent = scaObject instanceof PojoAtomicComponent;
//        if (wireContainerIsAtomicComponent && contractHasCallback) {
//            this.callbackIsImplemented =
//                ((PojoAtomicComponent) scaObject).implementsCallback(contract.getCallbackClass());
//        } else {
//            this.callbackIsImplemented = false;
//        }
        if (mapping == null) {
            chains = WireUtils.createInterfaceToWireMapping(interfaze, wire);
        } else {
            chains = mapping;
        }
    }

    // TODO Temporary fix to return a string with a UUID
    private String createConversationID() {
        return UUID.randomUUID().toString();
    }


}
