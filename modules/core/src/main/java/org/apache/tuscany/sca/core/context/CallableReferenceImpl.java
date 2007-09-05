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
package org.apache.tuscany.sca.core.context;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URI;
import java.util.UUID;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.CompositeActivatorImpl;
import org.apache.tuscany.sca.core.assembly.EndpointReferenceImpl;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Base class for implementations of service and callback references.
 * 
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public class CallableReferenceImpl<B> implements CallableReference<B>, Externalizable, ObjectFactory<B> {
    protected transient CompositeActivator compositeActivator;
    protected transient ProxyFactory proxyFactory;

    protected transient Class<B> businessInterface;

    // if the wire targets a conversational service this holds the conversation state 
    protected transient ConversationImpl conversation;

    protected transient RuntimeComponent component;
    protected transient RuntimeComponentReference reference;
    protected transient Binding binding;

    protected String componentURI; //The URI of the owning component
    protected Object conversationID; // The conversationID should be serializable
    protected Object callbackID; // The callbackID should be serializable

    protected String scdl;

    protected CallableReferenceImpl() {
        super();
    }

    protected CallableReferenceImpl(Class<B> businessInterface,
                                    RuntimeComponent component,
                                    RuntimeComponentReference reference,
                                    Binding binding,
                                    ProxyFactory proxyFactory,
                                    CompositeActivator compositeActivator) {
        this.proxyFactory = proxyFactory;
        this.businessInterface = businessInterface;
        this.component = component;
        this.reference = reference;
        this.binding = binding;
        // FIXME: The SCA spec is not clear how we should handle multiplicty for CallableReference
        if (this.binding == null) {
            this.binding = this.reference.getBinding(SCABinding.class);
            if (this.binding == null) {
                this.binding = this.reference.getBindings().get(0);
            }
        }

        // FIXME: Should we normalize the componentName/serviceName URI into an absolute SCA URI in the SCA binding?
        // sca:component1/component11/component112/service1?
        String componentURI = component.getURI(); // The target will be relative to this base URI
        this.componentURI = componentURI;
        this.compositeActivator = compositeActivator;
        RuntimeWire wire = this.reference.getRuntimeWire(this.binding);
        init(wire);
    }

    public CallableReferenceImpl(Class<B> businessInterface, RuntimeWire wire, ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
        this.businessInterface = businessInterface;
        bind(wire);
    }

    public RuntimeWire getRuntimeWire() {
        if (reference != null) {
            return reference.getRuntimeWire(binding);
        } else {
            return null;
        }
    }

    protected void bind(RuntimeWire wire) {
        if (wire != null) {
            this.component = wire.getSource().getComponent();
            this.reference = (RuntimeComponentReference)wire.getSource().getContract();
            this.binding = wire.getSource().getBinding();
            this.componentURI = component.getURI();
            this.compositeActivator = ((ComponentContextImpl)component.getComponentContext()).getCompositeActivator();
            init(wire);
        }
    }

    protected void init(RuntimeWire wire) {
        EndpointReference target = wire.getTarget();

        // look to see if the target is conversational and if so create a conversation
        InterfaceContract contract = target.getInterfaceContract();
        if (contract == null) {
            contract = reference.getInterfaceContract();
        }
        Interface contractInterface = contract.getInterface();

        if (contractInterface != null && contractInterface.isConversational()) {
            conversation = new ConversationImpl();
        }

        if (contract.getCallbackInterface() != null) {
            this.callbackID = createCallbackID();
        }
    }

    public B getInstance() throws ObjectCreationException {
        try {
            resolve();
            return businessInterface.cast(proxyFactory.createProxy(this));
        } catch (Exception e) {
            throw new ObjectCreationException(e);
        }
    }

    public B getService() {
        try {
            return getInstance();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Class<B> getBusinessInterface() {
        return businessInterface;
    }

    public boolean isConversational() {
        return getConversation() != null;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public Object getCallbackID() {
        return callbackID;
    }

    public EndpointReference getEndpointReference() {
        return new EndpointReferenceImpl(component, reference, binding, reference.getInterfaceContract());
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.scdl = in.readUTF();
    }

    /**
     * @throws IOException
     */
    private synchronized void resolve() throws Exception {
        if (scdl != null && component == null && reference == null) {
            ComponentContextHelper componentContextHelper = ComponentContextHelper.getCurrentComponentContextHelper();
            if (componentContextHelper != null) {
                CompositeActivator currentActivator = ComponentContextHelper.getCurrentCompositeActivator();
                this.compositeActivator = currentActivator;
                Component c = componentContextHelper.fromXML(scdl);
                this.component = (RuntimeComponent)c;
                currentActivator.configureComponentContext(this.component);
                this.reference = (RuntimeComponentReference)c.getReferences().get(0);
                this.reference.setComponent(this.component);
                ReferenceParameters parameters = null;
                for (Object ext : reference.getExtensions()) {
                    if (ext instanceof ReferenceParameters) {
                        parameters = (ReferenceParameters)ext;
                        break;
                    }
                }
                if (parameters != null) {
                    this.callbackID = parameters.getCallbackID();
                    this.conversationID = parameters.getConversationID();
                    this.componentURI = parameters.getComponentURI();
                }
                URI uri = URI.create("/" + componentURI);
                for (Binding binding : reference.getBindings()) {
                    if (binding instanceof WireableBinding) {
                        String targetURI = uri.resolve(binding.getURI()).toString();
                        int index = targetURI.lastIndexOf('/');
                        String serviceName = targetURI.substring(index + 1);
                        targetURI = targetURI.substring(1, index);
                        Component targetComponet = compositeActivator.resolve(targetURI);
                        ComponentService targetService = null;
                        if (targetComponet != null) {
                            if ("".equals(serviceName)) {
                                targetService = ComponentContextHelper.getSingleService(targetComponet);
                            } else {
                                for (ComponentService service : targetComponet.getServices()) {
                                    if (service.getName().equals(serviceName)) {
                                        targetService = service;
                                        break;
                                    }
                                }
                            }
                        }
                        WireableBinding wireableBinding = (WireableBinding)binding;
                        wireableBinding.setTargetComponent(targetComponet);
                        wireableBinding.setTargetComponentService(targetService);
                        if (targetService != null) {
                            for (Binding serviceBinding : targetService.getBindings()) {
                                if (serviceBinding.getClass() == binding.getClass()) {
                                    wireableBinding.setTargetBinding(serviceBinding);
                                    break;
                                }
                            }
                        }
                    }
                }
                // FIXME: The SCA spec is not clear how we should handle multiplicty for CallableReference
                if (binding == null) {
                    binding = reference.getBinding(SCABinding.class);
                    if (binding == null) {
                        binding = reference.getBindings().get(0);
                    }
                }
                Interface i = reference.getInterfaceContract().getInterface();
                if (i instanceof JavaInterface) {
                    JavaInterface javaInterface = (JavaInterface)i;
                    if (javaInterface.isUnresolved()) {
                        javaInterface.setJavaClass(Thread.currentThread().getContextClassLoader()
                            .loadClass(javaInterface.getName()));
                        currentActivator.getJavaInterfaceFactory().createJavaInterface(javaInterface,
                                                                                       javaInterface.getJavaClass());
                    }
                    this.businessInterface = (Class<B>)javaInterface.getJavaClass();
                }
                this.proxyFactory = currentActivator.getProxyFactory();
            }
        }
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        try {
            ReferenceParameters parameters = new ReferenceParameters();
            parameters.setCallbackID(callbackID);
            parameters.setComponentURI(componentURI);
            parameters.setConversationID(conversationID);
            reference.getExtensions().add(parameters);
            String scdl =
                ((CompositeActivatorImpl)compositeActivator).getComponentContextHelper().toXML(component, reference);
            reference.getExtensions().remove(parameters);
            out.writeUTF(scdl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Create a callback id
     * 
     * @return the callback id
     */
    private String createCallbackID() {
        return UUID.randomUUID().toString();
    }

    public void attachCallbackID(Object callbackID) {
        this.callbackID = callbackID;
    }

    public void attachConversationID(Object conversationID) {
        conversation.setConversationID(conversationID);
    }

}
