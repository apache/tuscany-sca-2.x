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
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.core.assembly.RuntimeWireImpl;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Returns proxy instance for a wire callback
 *
 * @version $Rev: 576055 $ $Date: 2007-09-16 08:11:45 +0100 (Sun, 16 Sep 2007) $
 */
public class CallbackReferenceImpl<B> extends CallableReferenceImpl<B> {
    private RuntimeWire wire;
    private List<RuntimeWire> wires;
    private EndpointReference resolvedEndpoint;
	private Object convID;

    public static CallbackReferenceImpl newInstance(Class interfaze,
                                                    ProxyFactory proxyFactory,
                                                    List<RuntimeWire> wires) {
        if (getCallbackEndpoint(ThreadMessageContext.getMessageContext()) != null) {
            return new CallbackReferenceImpl(interfaze, proxyFactory, wires);
        } else {
            return null;
        }
    }

    private CallbackReferenceImpl(Class<B> interfaze, ProxyFactory proxyFactory, List<RuntimeWire> wires) {
        super(interfaze, null, proxyFactory);
        this.wires = wires;
		init();
    }

    public void init() {
        Message msgContext = ThreadMessageContext.getMessageContext();
        wire = selectCallbackWire(msgContext);
        if (wire == null) {
            //FIXME: need better exception
            throw new RuntimeException("No callback binding found for " + msgContext.getTo().getURI());
        }
        resolvedEndpoint = getCallbackEndpoint(msgContext);
        convID = msgContext.getFrom().getReferenceParameters().getConversationID();
        callbackID = msgContext.getFrom().getReferenceParameters().getCallbackID();
    }

    @Override
    protected Object createProxy() throws Exception {
        return proxyFactory.createCallbackProxy(this);
	}

    protected RuntimeWire getCallbackWire() {
        if (resolvedEndpoint == null) {
            return null;
        } else {
            return cloneAndBind(wire);
		}
    }

    protected Object getConvID() {
	    return convID;
	}

    protected EndpointReference getResolvedEndpoint() {
	    return resolvedEndpoint;
	}

    private RuntimeWire selectCallbackWire(Message msgContext) {
        // look for callback binding with same name as service binding
        EndpointReference to = msgContext.getTo();
        if (to == null) {
            //FIXME: need better exception
            throw new RuntimeException("Destination for forward call is not available");
        }
        for (RuntimeWire wire : wires) {
            if (wire.getSource().getBinding().getName().equals(to.getBinding().getName())) {
			    return wire;
            }
        }

        // if no match, look for callback binding with same type as service binding
        for (RuntimeWire wire : wires) {
            if (wire.getSource().getBinding().getClass() == to.getBinding().getClass()) {
			    return wire;
            }
        }

        // no suitable callback wire was found
        return null;
    }

    /**
     * @param msgContext
     */
    private static EndpointReference getCallbackEndpoint(Message msgContext) {
        EndpointReference from = msgContext.getFrom();
        if (from == null) {
            return null;
        }
        return from.getReferenceParameters().getCallbackReference();
    }

    private RuntimeWire cloneAndBind(RuntimeWire wire) {
        RuntimeWire boundWire = null;
        if (resolvedEndpoint != null) {
            boundWire = ((RuntimeWireImpl)wire).lookupCache(resolvedEndpoint);
            if (boundWire != null) {
                return boundWire;
            }
            try {
                Contract contract = resolvedEndpoint.getContract();
                RuntimeComponentReference ref = null;
                if (contract == null) {
                    boundWire = (RuntimeWire)wire.clone();

                } else if (contract instanceof RuntimeComponentReference) {
                    ref = (RuntimeComponentReference)contract;
                    boundWire = ref.getRuntimeWire(resolvedEndpoint.getBinding());

                } else {  // contract instanceof RuntimeComponentService
                    ref = bind((RuntimeComponentReference)wire.getSource().getContract(),
                               resolvedEndpoint.getComponent(),
                               (RuntimeComponentService)contract);
                    boundWire = ref.getRuntimeWires().get(0);
                }
                configureWire(boundWire);
                ((RuntimeWireImpl)wire).addToCache(resolvedEndpoint, boundWire);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
        }
        return boundWire;
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

    private void configureWire(RuntimeWire wire) {
        // need to set the endpoint on the binding also so that when the chains are created next
        // the sca binding can decide whether to provide local or remote invokers. 
        // TODO - there is a problem here though in that I'm setting a target on a 
        //        binding that may possibly be trying to point at two things in the multi threaded 
        //        case. Need to confirm the general model here and how the clone and bind part
        //        is intended to work
        Binding binding = wire.getSource().getBinding();
        binding.setURI(resolvedEndpoint.getURI());

        // also need to set the target contract as it varies for the sca binding depending on 
        // whether it is local or remote
        RuntimeComponentReference ref = (RuntimeComponentReference)wire.getSource().getContract();
        wire.getTarget().setInterfaceContract(ref.getBindingProvider(binding).getBindingInterfaceContract());
    }
}
