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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.RuntimeWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public class JDKWireService implements WireService {

    private WorkContext context;
    //private PolicyBuilderRegistry policyRegistry;

    public JDKWireService() {
    }

    @Constructor({"workContext", "policyregisty"})
    public JDKWireService(@Autowire WorkContext context, @Autowire PolicyBuilderRegistry policyRegistry) {
        this.context = context;
        //this.policyRegistry = policyRegistry;
    }

    @Init(eager = true)
    public void init() {
    }


    public <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException {
        assert wire != null : "WireDefinition was null";
        if (wire instanceof InboundWire) {
            InboundWire<T> inbound = (InboundWire<T>) wire;
            JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(inbound.getInvocationChains());
            Class<T> interfaze = inbound.getBusinessInterface();
            ClassLoader cl = interfaze.getClassLoader();
            return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
        } else if (wire instanceof OutboundWire) {
            OutboundWire<T> outbound = (OutboundWire<T>) wire;
            JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(outbound);
            Class<T> interfaze = outbound.getBusinessInterface();
            ClassLoader cl = interfaze.getClassLoader();
            return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public <T> T createCallbackProxy(Class<T> interfaze) throws ProxyCreationException {
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(context);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public <T> WireInvocationHandler createHandler(RuntimeWire<T> wire) {
        assert wire != null : "WireDefinition was null";
        if (wire instanceof InboundWire) {
            InboundWire<T> inbound = (InboundWire<T>) wire;
            return new JDKInboundInvocationHandler(inbound.getInvocationChains());
        } else if (wire instanceof OutboundWire) {
            OutboundWire<T> outbound = (OutboundWire<T>) wire;
            return new JDKOutboundInvocationHandler(outbound);
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public WireInvocationHandler createCallbackHandler() {
        return new JDKCallbackInvocationHandler(context);
    }

    public OutboundWire createOutboundWire() {
        return new OutboundWireImpl();
    }

    public InboundWire createInboundWire() {
        return new InboundWireImpl();
    }

    public OutboundInvocationChain createOutboundChain(Method operation) {
        return new OutboundInvocationChainImpl(operation);
    }

    public InboundInvocationChain createInboundChain(Method operation) {
        return new InboundInvocationChainImpl(operation);
    }


    public void createWires(Component component, ComponentDefinition<?> definition) {
        Implementation<?> implementation = definition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        for (ServiceDefinition service : componentType.getServices().values()) {
            component.addInboundWire(createWire(service));
        }

        for (ReferenceTarget reference : definition.getReferenceTargets().values()) {
            Map<String, ? extends ReferenceDefinition> references = componentType.getReferences();
            ReferenceDefinition mappedReference = references.get(reference.getReferenceName());
            OutboundWire wire = createWire(reference, mappedReference);
            component.addOutboundWire(wire);
            if (componentType instanceof CompositeComponentType<?, ?, ?>) {
                // If this is the case, then it means that component has already been returned
                // by CompositeBuilder and thus its children, in particular composite references,
                // have been registered
                CompositeComponent compositeComponent = (CompositeComponent) component;
                Reference<?> bindlessReference = (Reference) compositeComponent.getChild(reference.getReferenceName());
                assert bindlessReference != null;
                bindlessReference.setOutboundWire(wire);
            }
        }
    }

    public <T> void createWires(Reference<T> reference) {
        InboundWire<T> wire = new InboundWireImpl<T>();
        Class<T> interfaze = reference.getInterface();
        wire.setBusinessInterface(interfaze);
        for (Method method : interfaze.getMethods()) {
            InboundInvocationChain chain = createInboundChain(method);
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(method, chain);
        }
        reference.setInboundWire(wire);
    }

    public void createWires(Service<?> service, BoundServiceDefinition<?> def) {
        createWires(service, def.getTarget().getPath());
    }

    public void createWires(Service<?> service, BindlessServiceDefinition def) {
        createWires(service, def.getTarget().getPath());
    }

    private <T> void createWires(Service<T> service, String targetName) {
        InboundWire<T> inboundWire = new InboundWireImpl<T>();
        OutboundWire<T> outboundWire = new OutboundWireImpl<T>();
        Class<T> interfaze = service.getInterface();
        inboundWire.setBusinessInterface(interfaze);
        outboundWire.setBusinessInterface(interfaze);
        outboundWire.setTargetName(new QualifiedName(targetName));
        for (Method method : interfaze.getMethods()) {
            InboundInvocationChain inboundChain = createInboundChain(method);
            inboundWire.addInvocationChain(method, inboundChain);
            OutboundInvocationChain outboundChain = createOutboundChain(method);
            outboundWire.addInvocationChain(method, outboundChain);
        }
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
    }


    @SuppressWarnings("unchecked")
    public OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def) {
        //TODO multiplicity
        if (reference.getTargets().size() != 1) {
            throw new UnsupportedOperationException();
        }
        Class<?> interfaze = def.getServiceContract().getInterfaceClass();
        OutboundWire wire = createOutboundWire();
        wire.setTargetName(new QualifiedName(reference.getTargets().get(0).toString()));
        wire.setBusinessInterface(interfaze);
        wire.setReferenceName(reference.getReferenceName());
        for (Method method : interfaze.getMethods()) {
            //TODO handle policy
            OutboundInvocationChain chain = createOutboundChain(method);
            wire.addInvocationChain(method, chain);
        }
        ServiceContract contract = def.getServiceContract();
        Class<?> callbackInterface = contract.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackInterface(callbackInterface);
            for (Method callbackMethod : callbackInterface.getMethods()) {
                InboundInvocationChain callbackTargetChain = createInboundChain(callbackMethod);
                OutboundInvocationChain callbackSourceChain = createOutboundChain(callbackMethod);
// TODO handle policy
//TODO statement below could be cleaner
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                wire.addTargetCallbackInvocationChain(callbackMethod, callbackTargetChain);
                wire.addSourceCallbackInvocationChain(callbackMethod, callbackSourceChain);
            }
        }
        return wire;
    }

    @SuppressWarnings("unchecked")
    public InboundWire createWire(ServiceDefinition service) {
        Class<?> interfaze = service.getServiceContract().getInterfaceClass();
        InboundWire wire = createInboundWire();
        wire.setBusinessInterface(interfaze);
        wire.setServiceName(service.getName());
        for (Method method : interfaze.getMethods()) {
            InboundInvocationChain chain = createInboundChain(method);
// TODO handle policy
//TODO statement below could be cleaner
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(method, chain);
        }
        ServiceContract contract = service.getServiceContract();
        Class<?> callbackInterface = contract.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackReferenceName(service.getCallbackReferenceName());
        }
        return wire;
    }

}
