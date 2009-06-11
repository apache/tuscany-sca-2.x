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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.UUID;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Endpoint2;
import org.apache.tuscany.sca.assembly.EndpointReference2;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BindingBuilderExtension;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.core.assembly.impl.CompositeActivatorImpl2;
import org.apache.tuscany.sca.core.assembly.impl.ReferenceParametersImpl;
import org.apache.tuscany.sca.core.context.CallableReferenceExt;
import org.apache.tuscany.sca.core.context.ComponentContextExt;
import org.apache.tuscany.sca.core.context.CompositeContext;
import org.apache.tuscany.sca.core.conversation.ConversationExt;
import org.apache.tuscany.sca.core.conversation.ConversationManager;
import org.apache.tuscany.sca.core.conversation.ConversationState;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.ReferenceParameters;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.Conversation;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Base class for implementations of service and callback references.
 * 
 * @version $Rev$ $Date$
 * @param <B> the type of the business interface
 */
public class CallableReferenceImpl<B> implements CallableReferenceExt<B> {
    static final long serialVersionUID = -521548304761848325L;
    protected transient CompositeActivator compositeActivator;
    protected transient ProxyFactory proxyFactory;
    protected transient Class<B> businessInterface;
    protected transient Object proxy;

    // if the wire targets a conversational service this holds the conversation state 
    protected transient ConversationManager conversationManager;
    protected transient ConversationExt conversation;
    protected transient Object conversationID;
    protected Object callbackID; // The callbackID should be serializable

    protected transient RuntimeComponent component;
    protected transient RuntimeComponentReference reference;
    protected transient EndpointReference2 endpointReference;

    protected String scdl;

    private transient RuntimeComponentReference clonedRef;
    private transient ReferenceParameters refParams;
    private transient XMLStreamReader xmlReader;
    
    private FactoryExtensionPoint modelFactories;
    protected RuntimeAssemblyFactory assemblyFactory;

    /*
     * Public constructor for Externalizable serialization/deserialization
     */
    public CallableReferenceImpl() {
        super();
    }

    /*
     * Public constructor for use by XMLStreamReader2CallableReference
     */
    public CallableReferenceImpl(XMLStreamReader xmlReader) throws Exception {
        this.xmlReader = xmlReader;
        resolve();
    }

    protected CallableReferenceImpl(Class<B> businessInterface,
                                    RuntimeComponent component,
                                    RuntimeComponentReference reference,
                                    EndpointReference2 endpointReference,
                                    ProxyFactory proxyFactory,
                                    CompositeActivator compositeActivator) {
        this.proxyFactory = proxyFactory;
        this.businessInterface = businessInterface;
        this.component = component;
        this.reference = reference;
        this.endpointReference = endpointReference;
        
        ExtensionPointRegistry registry = compositeActivator.getCompositeContext().getExtensionPointRegistry();
        this.modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);

        
        // FIXME: The SCA Specification is not clear how we should handle multiplicity 
        // for CallableReference
        if (this.endpointReference == null) {
            
            // TODO - EPR - If no endpoint reference specified assume the first one
            //        This will happen when a self reference is created in which case the 
            //        the reference should only have on endpointReference so use that 
            if (this.reference.getEndpointReferences().size() == 0){
                throw new ServiceRuntimeException("The reference " + reference.getName() + " in component " + 
                        component.getName() + " has no endpoint references");
            }
            
            if (this.reference.getEndpointReferences().size() > 1){
                throw new ServiceRuntimeException("The reference " + reference.getName() + " in component " + 
                        component.getName() + " has more than one endpoint reference");
            }
            
            this.endpointReference = this.reference.getEndpointReferences().get(0);
        }

        // FIXME: Should we normalize the componentName/serviceName URI into an absolute SCA URI in the SCA binding?
        // sca:component1/component11/component112/service1?
        this.compositeActivator = compositeActivator;
        this.conversationManager = this.compositeActivator.getCompositeContext().getConversationManager();
        initCallbackID();
    }

    public CallableReferenceImpl(Class<B> businessInterface, RuntimeWire wire, ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
        this.businessInterface = businessInterface;
        bind(wire);
    }

    public RuntimeWire getRuntimeWire() {
        try {
            resolve();
            if (endpointReference != null){
                return reference.getRuntimeWire(endpointReference);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    protected void bind(RuntimeWire wire) {
        if (wire != null) {
            this.component = (RuntimeComponent)wire.getEndpointReference().getComponent();
            this.reference = (RuntimeComponentReference)wire.getEndpointReference().getReference();
            this.endpointReference = wire.getEndpointReference();
            this.compositeActivator = ((ComponentContextExt)component.getComponentContext()).getCompositeActivator();
            this.conversationManager = this.compositeActivator.getCompositeContext().getConversationManager();
            initCallbackID();
        }
    }

    protected void initCallbackID() {
        if (reference.getInterfaceContract() != null) {
            if (reference.getInterfaceContract().getCallbackInterface() != null) {
                this.callbackID = createCallbackID();
            }
        }
    }

    public B getProxy() throws ObjectCreationException {
        try {
            if (proxy == null) {
                proxy = createProxy();
            }
            return businessInterface.cast(proxy);
        } catch (Exception e) {
            throw new ObjectCreationException(e);
        }
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    protected Object createProxy() throws Exception {
        return proxyFactory.createProxy(this);
    }

    public B getService() {
        try {
            resolve();
            return getProxy();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Class<B> getBusinessInterface() {
        try {
            resolve();
            return businessInterface;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public boolean isConversational() {
        try {
            resolve();
            return reference == null ? false : reference.getInterfaceContract().getInterface().isConversational();
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Conversation getConversation() {
        try {
            // resolve from XML just in case this CallableReference is the result of
            // passing a CallableReference as a parameter
            resolve();

            if (conversation == null || conversation.getState() == ConversationState.ENDED) {
                conversation = null;
            }
            return conversation;

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public Object getCallbackID() {
        try {
            resolve();
            return callbackID;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final boolean hasSCDL = in.readBoolean();
        if (hasSCDL) {
            this.scdl = in.readUTF();
        } else {
            this.scdl = null;
        }
    }

    /**
     * @throws IOException
     */
 // TODO - EPR all needs sorting out for endpoint references
    
    private synchronized void resolve() throws Exception {
        if ((scdl != null || xmlReader != null) && component == null && reference == null) {
            CompositeContext componentContextHelper = CompositeContext.getCurrentCompositeContext();
            if (componentContextHelper != null) {
                this.compositeActivator = CompositeContext.getCurrentCompositeActivator();
                this.conversationManager = componentContextHelper.getConversationManager();
                Component c;
                if (xmlReader != null) {
                    c = componentContextHelper.fromXML(xmlReader);
                    xmlReader = null; // OK to GC this now
                } else {
                    c = componentContextHelper.fromXML(scdl);
                    scdl = null; // OK to GC this now
                }
                this.component = (RuntimeComponent)c;
                compositeActivator.configureComponentContext(this.component);
                this.reference = (RuntimeComponentReference)c.getReferences().get(0);
                this.reference.setComponent(this.component);
                clonedRef = reference;
                ReferenceParameters parameters = null;
                for (Object ext : reference.getExtensions()) {
                    if (ext instanceof ReferenceParameters) {
                        parameters = (ReferenceParameters)ext;
                        break;
                    }
                }
                if (parameters != null) {
                    refParams = parameters;
                    this.callbackID = parameters.getCallbackID();
                    attachConversation(parameters.getConversationID());
                }

                // TODO - EPR all needs sorting out for endpoint references
                for (Binding binding : reference.getBindings()) {
                    if (binding instanceof OptimizableBinding) {
                        // Resolve the Component
                        final String bindingURI = binding.getURI();
                        final Component targetComponent = resolveComponentURI(bindingURI);

                        // Find the Service
                        ComponentService targetService = resolveServiceURI(bindingURI, targetComponent);

                        // if the target service is a promoted service then find the
                        // service it promotes
                        if ((targetService != null) && (targetService.getService() instanceof CompositeService)) {
                            CompositeService compositeService = (CompositeService)targetService.getService();
                            // Find the promoted component service
                            ComponentService promotedComponentService = getPromotedComponentService(compositeService);
                            if (promotedComponentService != null && !promotedComponentService.isUnresolved()) {
                                targetService = promotedComponentService;
                            }
                        }

                        OptimizableBinding optimizableBinding = (OptimizableBinding)binding;
                        optimizableBinding.setTargetComponent(targetComponent);
                        optimizableBinding.setTargetComponentService(targetService);
                        if (targetService != null) {
                            for (Binding serviceBinding : targetService.getBindings()) {
                                if (serviceBinding.getClass() == binding.getClass()) {
                                    optimizableBinding.setTargetBinding(serviceBinding);
                                    break;
                                }
                            }
                        }
                    }
                }
/*
                // FIXME: The SCA Specification is not clear how we should handle multiplicity 
                // for CallableReference
                if (binding == null) {
                    binding = reference.getBinding(SCABinding.class);
                    if (binding == null) {
                        binding = reference.getBindings().get(0);
                    }
                }
*/
                
                Interface i = reference.getInterfaceContract().getInterface();
                if (i instanceof JavaInterface) {
                    JavaInterface javaInterface = (JavaInterface)i;
                    if (javaInterface.isUnresolved()) {
                        // Allow privileged access to get ClassLoader. Requires RuntimePermission in
                        // security policy.
                        ClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                            public ClassLoader run() {
                                return Thread.currentThread().getContextClassLoader();
                            }
                        });
                        javaInterface.setJavaClass(classLoader.loadClass(javaInterface.getName()));
                        compositeActivator.getCompositeContext().getJavaInterfaceFactory()
                            .createJavaInterface(javaInterface, javaInterface.getJavaClass());
                        //FIXME: If the interface needs XSDs to be loaded (e.g., for static SDO),
                        // this needs to be done here.  We usually search for XSDs in the current
                        // contribution at resolve time.  Is it possible to locate the current
                        // contribution at runtime?
                    }
                    this.businessInterface = (Class<B>)javaInterface.getJavaClass();
                }
/*                
                if (binding instanceof BindingBuilderExtension) {
                    ((BindingBuilderExtension)binding).getBuilder().build(component, reference, binding, null);
                }
*/
                this.proxyFactory = compositeActivator.getCompositeContext().getProxyFactory();
            }
        } else {
            this.compositeActivator = CompositeContext.getCurrentCompositeActivator();
            if (this.compositeActivator != null) {
                this.proxyFactory = this.compositeActivator.getCompositeContext().getProxyFactory();
            }
        }
    }

    /**
     * Follow a service promotion chain down to the inner most (non composite)
     * component service.
     * 
     * @param topCompositeService
     * @return
     */
    private ComponentService getPromotedComponentService(CompositeService compositeService) {
        ComponentService componentService = compositeService.getPromotedService();
        if (componentService != null) {
            Service service = componentService.getService();
            if (componentService.getName() != null && service instanceof CompositeService) {

                // Continue to follow the service promotion chain
                return getPromotedComponentService((CompositeService)service);

            } else {

                // Found a non-composite service
                return componentService;
            }
        } else {

            // No promoted service
            return null;
        }
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        try {
            final String xml = toXMLString();
            if (xml == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                out.writeUTF(xml);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw new IOException(e.toString());
        }
    }

    public String toXMLString() throws IOException {
        if (reference != null) {
            if (clonedRef == null) {
                try {
                    clonedRef = (RuntimeComponentReference)reference.clone();
                } catch (CloneNotSupportedException e) {
                    // will not happen
                }
            }
            if (refParams == null) {
                refParams = new ReferenceParametersImpl();

                // remove any existing reference parameters from the clone                
                Object toRemove = null;
                for (Object extension : clonedRef.getExtensions()) {
                    if (extension instanceof ReferenceParameters) {
                        toRemove = extension;
                    }
                }

                if (toRemove != null) {
                    clonedRef.getExtensions().remove(toRemove);
                }

                // add the new reference parameter object
                clonedRef.getExtensions().add(refParams);
            }
            refParams.setCallbackID(callbackID);
            if (conversation != null) {
                refParams.setConversationID(conversation.getConversationID());
            }
            return ((CompositeActivatorImpl2)compositeActivator).getCompositeContext().toXML(component, clonedRef);
        } else {
            return scdl;
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
        this.conversationID = conversationID;
    }

    public void attachConversation(ConversationExt conversation) {
        this.conversation = conversation;
    }

    public void attachConversation(Object conversationID) {
        if (conversationID != null) {
            ConversationExt conversation = conversationManager.getConversation(conversationID);
            if (conversation == null) {
                conversation = conversationManager.startConversation(conversationID);
            }
            this.conversation = conversation;
        } else {
            this.conversation = null;
        }
    }

    protected ReferenceParameters getReferenceParameters() {
        ReferenceParameters parameters = new ReferenceParametersImpl();
        parameters.setCallbackID(callbackID);
        if (getConversation() != null) {
            parameters.setConversationID(conversation.getConversationID());
        }
        return parameters;
    }

    // TODO - EPR - needs sorting out for new endpoint references
    public EndpointReference2 getEndpointReference() {
        try {
            resolve();

            // Use the interface contract of the reference on the component type
            Reference componentTypeRef = reference.getReference();
            InterfaceContract sourceContract =
                componentTypeRef == null ? reference.getInterfaceContract() : componentTypeRef.getInterfaceContract();
            sourceContract = sourceContract.makeUnidirectional(false);
            
            EndpointReference2 epr = assemblyFactory.createEndpointReference();
            epr.setComponent(component);
            epr.setReference(reference);
            //epr.setBinding(binding);
            epr.setInterfaceContract(sourceContract);
            
            Endpoint2 endpoint = assemblyFactory.createEndpoint();
            epr.setTargetEndpoint(endpoint);
            
            return epr;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public XMLStreamReader getXMLReader() {
        return xmlReader;
    }

    /**
     * Resolves the specified URI to a Component using the compositeActivator.
     * There are two cases that we need to handle:
     * <ul>
     * <li>URI containing just Composite name(s) (i.e. no Service name specified)
     * <li>URI containing Composite name(s) and a Service Name
     * </ul>
     * 
     * @param componentURI The URI of the Component to resolve
     * @return The Component for the specified URI or null if not founds
     */
    protected Component resolveComponentURI(String componentURI) {
        // If the URI has come from a binding, it may well start with a '/'. We will need
        // to remove this so we can match it to the composite names.
        if (componentURI.startsWith("/")) {
            componentURI = componentURI.substring(1);
        }

        // First assume that we are dealing with a Component URI without a Service Name
        Component component = compositeActivator.resolve(componentURI);
        if (component != null) {
            return component;
        }

        // Perhaps we have a ComponentURI that has a ServiceName on the end of it
        final int index = componentURI.lastIndexOf('/');
        if (index > -1) {
            componentURI = componentURI.substring(0, index);
            return compositeActivator.resolve(componentURI);
        }

        // We could not resolve the Component URI
        return null;
    }

    /**
     * Examines the Services on the specified Component and returns the Service that matches the
     * specified Binding URI.
     * 
     * @param bindingURI The Binding URI to resolve on the Component
     * @param targetComponent The Component containing the Services
     * @return The Service with the specified serviceName or null if no such Service found.
     */
    protected ComponentService resolveServiceURI(String bindingURI, Component targetComponent) {

        ComponentService targetService = null;

        if (targetComponent != null) {
            if (bindingURI.startsWith("/")) {
                bindingURI = bindingURI.substring(1);
            }

            final String componentURI = targetComponent.getURI();
            final String serviceName;
            if (componentURI.equals(bindingURI)) {
                // No service specified
                serviceName = "";
            } else {
                // Get the Service name from the Binding URI
                serviceName = bindingURI.substring(componentURI.length() + 1);
            }

            if ("".equals(serviceName)) {
                targetService = CompositeContext.getSingleService(targetComponent);
            } else {
                for (ComponentService service : targetComponent.getServices()) {
                    if (service.getName().equals(serviceName)) {
                        targetService = service;
                        break;
                    }
                }
            }
        }

        return targetService;
    }
}
