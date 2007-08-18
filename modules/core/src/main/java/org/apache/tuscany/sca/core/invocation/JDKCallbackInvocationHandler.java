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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.NoRegisteredCallbackException;

/**
 * Responsible for dispatching to a callback through a wire. <p/> TODO cache
 * target invoker
 * 
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends JDKInvocationHandler {
    private static final long serialVersionUID = -3350283555825935609L;
    private transient List<RuntimeWire> wires;

    /**
     * Constructor used for deserialization only
     */
    public JDKCallbackInvocationHandler(MessageFactory messageFactory) {
        super(messageFactory, null, null);
    }

    public JDKCallbackInvocationHandler(MessageFactory messageFactory, List<RuntimeWire> wires) {
        super(messageFactory, null, null);
        this.wires = wires;
    }

    private RuntimeComponentReference bind(RuntimeComponentReference reference,
                                           RuntimeComponent component,
                                           RuntimeComponentService service) throws CloneNotSupportedException {
        RuntimeComponentReference ref = (RuntimeComponentReference)reference.clone();
        ref.getTargets().add(service);
        ref.getBindings().clear();
        for (Binding binding : service.getBindings()) {
            if (binding instanceof WireableBinding) {
                WireableBinding wireableBinding = (WireableBinding)((WireableBinding)binding).clone();
                wireableBinding.setTargetBinding(binding);
                wireableBinding.setTargetComponent(component);
                wireableBinding.setTargetComponentService(service);
                wireableBinding.setRemote(false);
                ref.getBindings().add(wireableBinding);
            } else {
                ref.getBindings().add(binding);
            }
        }
        return ref;
    }

    @SuppressWarnings( {"unchecked"})
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class) && "equals".equals(method.getName())) {
            // TODO implement
            throw new UnsupportedOperationException();
        } else if (Object.class.equals(method.getDeclaringClass()) && "hashCode".equals(method.getName())) {
            return hashCode();
            // TODO beter hash algorithm
        }

        // wire not pre-selected, so select a wire now to be used for the callback
        Message msgContext = ThreadMessageContext.getMessageContext();
        RuntimeWire wire = CallbackWireObjectFactory.selectCallbackWire(msgContext, wires);
        if (wire == null) {
            //FIXME: need better exception
            throw new RuntimeException("No callback wire found for " + msgContext.getFrom().getURI());
        }
        RuntimeWire resolvedWire = (RuntimeWire)wire.clone();
        EndpointReference callback = msgContext.getFrom().getCallbackEndpoint();
        if (callback != null) {
            RuntimeComponentReference ref =
                bind((RuntimeComponentReference)wire.getSource().getContract(),
                     callback.getComponent(),
                     (RuntimeComponentService)callback.getContract());
            resolvedWire = ref.getRuntimeWires().get(0);
        }
        setConversational(resolvedWire);
        setEndpoint(msgContext.getFrom());

        //FIXME: can we use the same code as JDKInvocationHandler to select the chain? 
        InvocationChain chain = getInvocationChain(method, resolvedWire);
        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }

        try {
            return invoke(chain, args, resolvedWire);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof NoRegisteredCallbackException) {
                throw t;
            }
            throw e;
        }
    }

}
