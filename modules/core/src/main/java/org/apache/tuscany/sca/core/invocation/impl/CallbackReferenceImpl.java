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
package org.apache.tuscany.sca.core.invocation.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.core.assembly.impl.EndpointReferenceImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeComponentReferenceImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeWireImpl2;
import org.apache.tuscany.sca.core.context.CompositeContext;
import org.apache.tuscany.sca.core.context.impl.CallableReferenceImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
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

    /**
     * Public constructor for Externalizable serialization/deserialization.
     */
    public CallbackReferenceImpl() {
        super();
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
     * Gets the endpoint reference from the incoming message that points
     * back to the callback service
     * 
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
            boundWire = ((RuntimeWireImpl2)wire).lookupCache(resolvedEndpoint);
            if (boundWire != null) {
                return boundWire;
            }
            try {
                Contract contract = resolvedEndpoint.getContract();
                RuntimeComponentReference ref = null;
                if (contract == null) {
                    //TODO - EPR - does it ever go through here?
                    //       in what situation is there no reference in the endpoint reference
                    //       pointing back to the callbac service
                    boundWire = (RuntimeWire)wire.clone();

                } else if (contract instanceof RuntimeComponentReference) {
                    // TODO - EPP - the endpoint reference pointing back to the 
                    //        callback service holds a reference representing
                    //        a reference to the callback service. This is true if a 
                    //        callback object has been set into the reference on the 
                    //        client side 
                    ref = (RuntimeComponentReference)contract;
                    
                    // TODO - EPR - get the wire from the reference that matches the 
                    //        injected callback reference wire. We don't have bindings yet as the 
                    //        callback object wire hasn't been initialized 
/*                    
                    for (RuntimeWire runtimeWire : ref.getRuntimeWires()){
                        if (runtimeWire.getEndpointReference().getBinding().getName().equals(wire.getEndpointReference().getBinding().getName())){
                            boundWire = runtimeWire;
                            break;
                        }
                    }
*/
                    // just get the first one for now
                    boundWire = ref.getRuntimeWires().get(0);

                } else {  // contract instanceof RuntimeComponentService
                    //TODO - EPR - I think it does this if no callback object has been set explicitly
                    ref = bind((RuntimeComponentReference)wire.getSource().getContract(),
                                resolvedEndpoint);
                    boundWire = ref.getRuntimeWires().get(0);
                }
                configureWire(boundWire);
                ((RuntimeWireImpl2)wire).addToCache(resolvedEndpoint, boundWire);
            } catch (CloneNotSupportedException e) {
                // will not happen
            }
        }
        return boundWire;
    }

    // TODO - EPR - why static & convert to ne endpoint reference
    private static RuntimeComponentReference bind(RuntimeComponentReference reference,
                                                  EndpointReference resolvedEndpoint) throws CloneNotSupportedException {
        RuntimeComponent component = resolvedEndpoint.getComponent();
        RuntimeComponentService service = (RuntimeComponentService)resolvedEndpoint.getContract();
        
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
        
        ref.getEndpointReferences().clear();
        
        for(EndpointReference2 endpointReference : reference.getEndpointReferences()){
            EndpointReference2 clone = (EndpointReference2)endpointReference.clone();
            
            clone.setReference(ref);
            clone.getBinding().setURI(resolvedEndpoint.getURI());
            
            clone.getTargetEndpoint().setComponent(resolvedEndpoint.getComponent());
            clone.getTargetEndpoint().setService((ComponentService)resolvedEndpoint.getContract());  
            clone.getTargetEndpoint().setBinding(resolvedEndpoint.getBinding());
            
            ref.getEndpointReferences().add(clone);
        }
       
        return ref;
    }

    private void configureWire(RuntimeWire wire ) {
        
        // TODO - EPR - do we actiually need this code? Combine with bind?
        // need to set the endpoint on the binding also so that when the chains are created next
        // the sca binding can decide whether to provide local or remote invokers. 
        // TODO - there is a problem here though in that I'm setting a target on a 
        //        binding that may possibly be trying to point at two things in the multi threaded 
        //        case. Need to confirm the general model here and how the clone and bind part
        //        is intended to work
        Binding binding = wire.getSource().getBinding();
        binding.setURI(resolvedEndpoint.getURI());

        // set the target contract as it varies for the sca binding depending on 
        // whether it is local or remote
        RuntimeComponentReference ref = (RuntimeComponentReference)wire.getSource().getContract();
        
        // TODO - EPR
        // needs to be set after the chains have been created for the first time
        // as now the binding provider won't be created until that time 
        //wire.getEndpointReference().getTargetEndpoint().setInterfaceContract(ref.getBindingProvider(binding).getBindingInterfaceContract());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.callbackID = in.readObject();
        this.convID = in.readObject();

        this.compositeActivator = CompositeContext.getCurrentCompositeActivator();

        // Get the target Component and Service from the URI
        final String uri = in.readUTF();
        final Component targetComponent = super.resolveComponentURI(uri);
        final ComponentService targetService = super.resolveServiceURI(uri, targetComponent);
        final InterfaceContract targetServiceIfaceContract = targetService.getInterfaceContract();

        // Re-create the resolved Endpoint
        this.resolvedEndpoint = new EndpointReferenceImpl(
                (RuntimeComponent) targetComponent, targetService, null, 
                targetServiceIfaceContract);

        // Copy the Java Interface from the Service
        final JavaInterface ji = (JavaInterface) targetServiceIfaceContract.getInterface();
        this.businessInterface = (Class<B>) ji.getJavaClass();
        
        // We need to re-create the callback wire. We need to do this on a clone of the Service
        // wire since we need to change some details on it.
        // FIXME: Is this the best way to do this?
        final RuntimeWire cbWire = ((RuntimeComponentService) targetService).getRuntimeWires().get(0);
        try {
            this.wire = (RuntimeWireImpl2) cbWire.clone();
        } catch (CloneNotSupportedException e) {
            throw new IOException(e.toString());
        }

        // Setup the reference on the cloned wire
        final RuntimeComponentReference ref = new RuntimeComponentReferenceImpl();
        ref.setComponent((RuntimeComponent) targetComponent);
        ref.setInterfaceContract(targetServiceIfaceContract);
        ((EndpointReferenceImpl) this.wire.getSource()).setContract(ref);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(this.callbackID);
        out.writeObject(this.convID);
        out.writeUTF(this.resolvedEndpoint.getURI());
    }
}
