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
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeComponentReferenceImpl;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeWireImpl;
import org.apache.tuscany.sca.core.context.CompositeContext;
import org.apache.tuscany.sca.core.context.impl.CallableReferenceImpl;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Message;
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
    private Endpoint resolvedEndpoint;
	private Object convID;

    public static CallbackReferenceImpl newInstance(Class interfaze,
                                                    ProxyFactory proxyFactory,
                                                    List<RuntimeWire> wires) {
        if (ThreadMessageContext.getMessageContext().getFrom().getCallbackEndpoint() != null) {
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
            throw new RuntimeException("No callback binding found for " + msgContext.getTo().toString());
        }
        resolvedEndpoint = msgContext.getFrom().getCallbackEndpoint();
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

    protected Endpoint getResolvedEndpoint() {
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

    // TODO - EPR - why static?
    private static RuntimeComponentReference bind(RuntimeComponentReference reference,
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
        this.resolvedEndpoint = assemblyFactory.createEndpoint();
        this.resolvedEndpoint.setComponent(targetComponent);
        this.resolvedEndpoint.setService(targetService);
        this.resolvedEndpoint.setInterfaceContract(targetServiceIfaceContract);

        // Copy the Java Interface from the Service
        final JavaInterface ji = (JavaInterface) targetServiceIfaceContract.getInterface();
        this.businessInterface = (Class<B>) ji.getJavaClass();
        
        // We need to re-create the callback wire. We need to do this on a clone of the Service
        // wire since we need to change some details on it.
        // FIXME: Is this the best way to do this?
        final RuntimeWire cbWire = ((RuntimeComponentService) targetService).getRuntimeWires().get(0);
        try {
            this.wire = (RuntimeWireImpl) cbWire.clone();
        } catch (CloneNotSupportedException e) {
            throw new IOException(e.toString());
        }

        // TODO - EPR - This doesn't sound right to me. 
        // Setup the reference on the cloned wire
        final RuntimeComponentReference ref = new RuntimeComponentReferenceImpl();
        ref.setComponent((RuntimeComponent) targetComponent);
        ref.setInterfaceContract(targetServiceIfaceContract);
        ((EndpointReference) this.wire.getEndpointReference()).setReference(ref);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(this.callbackID);
        out.writeObject(this.convID);
        
        // TODO - EPR - What to do about URI?
        out.writeUTF(this.resolvedEndpoint.getBinding().getURI());
    }
}
