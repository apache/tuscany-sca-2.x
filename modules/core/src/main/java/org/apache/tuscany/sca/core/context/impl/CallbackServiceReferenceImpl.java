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
package org.apache.tuscany.sca.core.context.impl;


import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeWireImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

public class CallbackServiceReferenceImpl<B> extends ServiceReferenceImpl<B> {
    private RuntimeWire wire;
    private List<RuntimeWire> wires;
    private Endpoint resolvedEndpoint;

    /*
     * Public constructor for Externalizable serialization/deserialization
     */
    public CallbackServiceReferenceImpl() {
        super();
    }
        
    public CallbackServiceReferenceImpl(Class<B> interfaze, List<RuntimeWire> wires, ProxyFactory proxyFactory) {
        super(interfaze, null, proxyFactory);
        this.wires = wires;
		init();
    }

    public void init() {
        Message msgContext = ThreadMessageContext.getMessageContext();
        wire = selectCallbackWire(msgContext);
        if (wire == null) {
            //FIXME: need better exception
            throw new RuntimeException("No callback binding found for " + msgContext.getTo().toString());
        }
        resolvedEndpoint = msgContext.getFrom().getCallbackEndpoint();
    }

    @Override
    protected Object createProxy() throws Exception {
        return proxyFactory.createCallbackProxy(this);
	}

    public RuntimeWire getCallbackWire() {
        if (resolvedEndpoint == null) {
            return null;
        } else {
            return cloneAndBind(wire);
		}
    }

    public Endpoint getResolvedEndpoint() {
	    return resolvedEndpoint;
	}

    private RuntimeWire selectCallbackWire(Message msgContext) {
        // look for callback binding with same name as service binding
        Endpoint to = msgContext.getTo();
        if (to == null) {
            //FIXME: need better exception
            throw new RuntimeException("Destination for forward call is not available");
        }
        for (RuntimeWire wire : wires) {
            if (wire.getEndpointReference().getBinding().getName().equals(to.getBinding().getName())) {
			    return wire;
            }
        }

        // if no match, look for callback binding with same type as service binding
        for (RuntimeWire wire : wires) {
            if (wire.getEndpointReference().getBinding().getClass() == to.getBinding().getClass()) {
			    return wire;
            }
        }

        // no suitable callback wire was found
        return null;
    }

    private RuntimeWire cloneAndBind(RuntimeWire wire) {
        RuntimeWire boundWire = null;
        if (resolvedEndpoint != null) {
            boundWire = ((RuntimeWireImpl)wire).lookupCache(resolvedEndpoint);
            if (boundWire != null) {
                return boundWire;
            }
            try {
                // TODO - EPR - is this correct?
                                
                // Fluff up a new response wire based on the callback endpoint
                RuntimeComponentReference ref =
                    bind((RuntimeComponentReference)wire.getEndpointReference().getReference(),
                          resolvedEndpoint);
                
                boundWire = ref.getRuntimeWires().get(0);
                
                Binding binding = wire.getEndpointReference().getBinding();
                
                ((RuntimeWireImpl)wire).addToCache(resolvedEndpoint, boundWire);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
        }
        return boundWire;
    }

    private  RuntimeComponentReference bind(RuntimeComponentReference reference,
                                            Endpoint callbackEndpoint) throws CloneNotSupportedException {
        
        // clone the callback reference ready to configure it for this callback endpoint
        RuntimeComponentReference ref = (RuntimeComponentReference)reference.clone();
        ref.getTargets().clear();
        ref.getBindings().clear();
        ref.getEndpointReferences().clear();
        
        // no access to the assembly factory so clone an existing epr
        EndpointReference callbackEndpointReference = (EndpointReference)reference.getEndpointReferences().get(0).clone();

        callbackEndpointReference.setReference(ref);
        callbackEndpointReference.setTargetEndpoint(callbackEndpoint);
        callbackEndpointReference.setUnresolved(true);
       
        // The callback endpoint will be resolved with the registry 
        // when the wire chains are created
        ref.getEndpointReferences().add(callbackEndpointReference);
        
        return ref;
    }
}
