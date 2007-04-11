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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.core.util.PojoWorkContextTunnel;
import org.apache.tuscany.core.wire.NoMethodForOperationException;
import org.apache.tuscany.core.wire.WireUtils;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ReactivationException;
import org.apache.tuscany.spi.component.SCAExternalizable;
import org.apache.tuscany.spi.component.TargetInvocationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.AbstractInvocationHandler;
import org.apache.tuscany.spi.wire.ChainHolder;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;


/**
 * Dispatches to a target through a wire.
 *
 * @version $Rev$ $Date$
 * @Deprecated
 */
public final class JDKInvocationHandler extends AbstractInvocationHandler
    implements InvocationHandler, Externalizable, SCAExternalizable {
    private static final long serialVersionUID = -6155278451964527325L;

    // the wire this handler fronts
    private transient Wire wire;
    // the name of the source reference the wire is attached to, used during deserialization
    private String referenceName;
    // the interface the reference proxy implements
    private Class<?> proxyInterface;
    private transient WorkContext workContext;
    //  if the associated wire has a callback
    private transient boolean callback;
    // if the associated wire is conversational
    private transient boolean conversational;
    /*
     * an association of an operation to chain holder. The holder contains an invocation chain
     * and a local clone of the master TargetInvoker. TargetInvokers will be cloned by the handler and placed in the
     * holder if they are cacheable. This allows optimizations such as avoiding target resolution when a source refers
     * to a target of greater scope since the target reference can be maintained by the invoker. When a target invoker
     * is not cacheable, the master associated with the wire chains will be used.
     */
    private transient Map<Method, ChainHolder> chains;

    /**
     * Constructor used for deserialization only
     */
    public JDKInvocationHandler() {
    }

    public JDKInvocationHandler(Class<?> interfaze, Wire wire, WorkContext workContext)
        throws NoMethodForOperationException {
        this.workContext = workContext;
        this.proxyInterface = interfaze;
        this.wire = wire;
        init(interfaze, wire, null);
    }

    public JDKInvocationHandler(Class<?> interfaze,
                                Wire wire,
                                Map<Method, ChainHolder> mapping,
                                WorkContext workContext)
        throws NoMethodForOperationException {
        super(false);
        this.workContext = workContext;
        this.proxyInterface = interfaze;
        init(interfaze, wire, mapping);
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
            throw new TargetInvocationException("Operation not configured", method.getName());
        }
        TargetInvoker invoker = getInvoker(holder);
        InvocationChain chain = holder.getChain();
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();

// JFM commonting out temporarily
//        if (wireContainerIsAtomicComponent && contractHasCallback && !callbackIsImplemented) {
//            throw new NoRegisteredCallbackException("Instance is does not implement callback: "
//                + callbackClassName);
//        }

        if (conversational) {
            Object id = workContext.getIdentifier(Scope.CONVERSATION);
            if (id == null) {
                String convIdFromThread = createConversationID();
                workContext.setIdentifier(Scope.CONVERSATION, convIdFromThread);
            }
        }
        LinkedList<URI> list = null;
        if (callback) {
            // set up callback address
            list = workContext.getCallbackUris();
            if (list == null) {
                list = new LinkedList<URI>();
                workContext.setCallbackUris(list);
            }
            list.add(wire.getSourceUri());
        }
        // send the invocation down the wire
        Object result = invoke(chain, invoker, args, null, list, workContext);

        if (callback) {
            list = workContext.getCallbackUris();
            if (list != null) {
                // pop last address
                list.removeLast();
            }
        }
        return result;
    }

    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }

    public void setWorkContext(WorkContext context) {
        workContext = context;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(referenceName);
        out.writeObject(proxyInterface);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        referenceName = (String) in.readObject();
        proxyInterface = (Class<?>) in.readObject();
    }

    public void reactivate() throws ReactivationException {
        AtomicComponent owner = workContext.getCurrentAtomicComponent();
        if (owner == null) {
            throw new ReactivationException("Current atomic component not set on work context");
        }
        List<Wire> wires = owner.getWires(referenceName);
        if (wires == null) {
            throw new ReactivationException("Reference wire not found", referenceName, owner.getUri().toString());
        }
        // TODO handle multiplicity
        Wire wire = wires.get(0);
        try {
            init(proxyInterface, wire, null);
        } catch (NoMethodForOperationException e) {
            throw new ReactivationException(e);
        }
    }

    /**
     * Reinitializes the proxy handler
     *
     * @param interfaze the interface the proxy implements
     * @param wire      the wire fronted by the proxy
     * @param mapping   a mapping from proxy interface methods to invocation chain holders
     * @throws NoMethodForOperationException
     */
    private void init(Class<?> interfaze, Wire wire, Map<Method, ChainHolder> mapping)
        throws NoMethodForOperationException {
        InterfaceContract contract = wire.getSourceContract();
        this.referenceName = wire.getSourceUri().getFragment();
        this.conversational = contract.getInterface().isConversational();
        this.callback = contract.getCallbackInterface() != null;
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

    /**
     * Returns the cached or a new target invoker to associate with the invocation
     *
     * @param holder the chain holder
     * @return the target invoker
     * @throws TargetInvocationException
     * @throws CloneNotSupportedException
     */
    private TargetInvoker getInvoker(ChainHolder holder) throws TargetInvocationException, CloneNotSupportedException {
        InvocationChain chain = holder.getChain();
        if (holder.getCachedInvoker() == null) {
            assert chain != null;
            if (chain.getTargetInvoker() == null) {
                String name = chain.getOperation().getName();
                throw new TargetInvocationException("No target invoker configured for operation", name);
            }
            if (chain.getTargetInvoker().isCacheable()) {
                // clone and store the invoker locally
                holder.setCachedInvoker((TargetInvoker) chain.getTargetInvoker().clone());
                return holder.getCachedInvoker();
            } else {
                return chain.getTargetInvoker();
            }
        } else {
            assert chain != null;
            return chain.getTargetInvoker();
        }
    }

    /**
     * Creates a new conversational id
     *
     * @return the conversational id
     */
    private String createConversationID() {
        return UUID.randomUUID().toString();
    }

}
