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

import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Returns proxy instance for a wire callback
 *
 * @version $Rev$ $Date$
 */
public class CallbackWireObjectFactory<B> extends CallableReferenceImpl<B> {
    private RuntimeWire wire;
    private List<RuntimeWire> wires;
    private EndpointReference resolvedEndpoint;

    public CallbackWireObjectFactory(Class<B> interfaze, ProxyFactory proxyFactory, List<RuntimeWire> wires) {
        super(interfaze, null, proxyFactory);
        this.wires = wires;
    }

    public void resolveTarget() {
        Message msgContext = ThreadMessageContext.getMessageContext();
        wire = selectCallbackWire(msgContext);
        if (wire == null) {
            //FIXME: need better exception
            throw new RuntimeException("No callback wire found for " + msgContext.getFrom().getURI());
        }
        this.resolvedEndpoint = getCallbackEndpoint(msgContext);
        bind(wire);
    }

    @Override
    public B getInstance() throws ObjectCreationException {
        if (wire != null) {
            // wire and endpoint already resolved, so return a pre-wired proxy
            wire.setTarget(resolvedEndpoint);
            wire.rebuild();
            return super.getInstance();
        } else {
            // wire not yet selected, so return a proxy that resolves the target dynamically
            return proxyFactory.createCallbackProxy(businessInterface, wires);
        }
    }

    public RuntimeWire selectCallbackWire(Message msgContext) {
        EndpointReference from = msgContext.getFrom();
        if (from == null) {
            return null;
        }

        //FIXME: need a cache for better performance.  This requires making this
        // method non-static, which means changing the signature of createCallbackProxy().

        // first choice is wire with matching destination endpoint
        for (RuntimeWire wire : wires) {
            if (from.getURI().equals(wire.getTarget().getURI())) {
                try {
                    return (RuntimeWire)wire.clone();
                } catch (CloneNotSupportedException e) {
                    throw new ServiceRuntimeException(e);
                }
            }
        }

        // no exact match, so find callback binding with same name as service binding
        EndpointReference to = msgContext.getTo();
        if (to == null) {
            //FIXME: need better exception
            throw new RuntimeException("Destination for forward call is not available");
        }
        for (RuntimeWire wire : wires) {
            if (wire.getSource().getBinding().getName().equals(to.getBinding().getName())) {
                //FIXME: need better way to represent dynamic wire
                if (wire.getTarget().getURI().equals("/")) { // dynamic wire
                    //FIXME: avoid doing this for genuine dynamic wires
                    return cloneAndBind(msgContext, wire);
                }
                //FIXME: no dynamic wire, so should attempt to create a static wire 
            }
        }

        // no match so far, so find callback binding with same type as service binding
        for (RuntimeWire wire : wires) {
            if (wire.getSource().getBinding().getClass() == to.getBinding().getClass()) {
                //FIXME: need better way to represent dynamic wire
                if (wire.getTarget().getURI().equals("/")) { // dynamic wire
                    //FIXME: avoid doing this for genuine dynamic wires
                    return cloneAndBind(msgContext, wire);
                }
                //FIXME: no dynamic wire, so should attempt to create a static wire 
            }
        }

        // no suitable callback wire was found
        return null;
    }

    /**
     * @param msgContext
     */
    private static EndpointReference getCallbackEndpoint(Message msgContext) {
        EndpointReference to = msgContext.getTo();
        if (to == null) {
            return null;
        }
        return to.getReferenceParameters().getCallbackReference();
    }

    private static RuntimeWire cloneAndBind(Message msgContext, RuntimeWire wire) {
        EndpointReference callback = getCallbackEndpoint(msgContext);
        if (callback != null && callback.getContract() != null) {
            try {
                RuntimeComponentReference ref = null;
                if (callback.getContract() instanceof RuntimeComponentReference) {
                    ref = (RuntimeComponentReference)callback.getContract();
                    return ref.getRuntimeWire(callback.getBinding());
                } else {
                    ref =
                        bind((RuntimeComponentReference)wire.getSource().getContract(),
                             callback.getComponent(),
                             (RuntimeComponentService)callback.getContract());

                    return ref.getRuntimeWires().get(0);
                }
            } catch (CloneNotSupportedException e) {
                // will not happen
                return null;
            }
        } else {
            return wire;
        }
    }

    private static RuntimeComponentReference bind(RuntimeComponentReference reference,
                                                  RuntimeComponent component,
                                                  RuntimeComponentService service) throws CloneNotSupportedException {
        RuntimeComponentReference ref = (RuntimeComponentReference)reference.clone();
        ref.getTargets().add(service);
        ref.getBindings().clear();
        for (Binding binding : service.getBindings()) {
            if (binding instanceof OptimizableBinding) {
                OptimizableBinding optimizableBinding = (OptimizableBinding)((OptimizableBinding)binding).clone();
                optimizableBinding.setTargetBinding(binding);
                optimizableBinding.setTargetComponent(component);
                optimizableBinding.setTargetComponentService(service);
                ref.getBindings().add(optimizableBinding);
            } else {
                ref.getBindings().add(binding);
            }
        }
        return ref;
    }

}
