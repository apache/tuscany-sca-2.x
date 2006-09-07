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
import java.util.HashMap;
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
import static org.apache.tuscany.spi.idl.java.JavaIDLUtils.findMethod;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Operation;
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

    private Map<Method, InboundInvocationChain> createInboundMapping(InboundWire<?> wire, Method[] methods)
        throws NoMethodForOperationException {
        Map<Method, InboundInvocationChain> chains = new HashMap<Method, InboundInvocationChain>();
        for (Map.Entry<Operation<?>, InboundInvocationChain> entry : wire.getInvocationChains().entrySet()) {
            Operation<?> operation = entry.getKey();
            InboundInvocationChain chain = entry.getValue();
            Method method = findMethod(operation, methods);
            if (method == null) {
                NoMethodForOperationException e = new NoMethodForOperationException();
                e.setIdentifier(operation.getName());
            }
            chains.put(method, chain);
        }
        return chains;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire<T> inbound = (InboundWire<T>) wire;
            Class<?> interfaze = wire.getServiceContract().getInterfaceClass();
            Method[] methods = interfaze.getMethods();
            Map<Method, InboundInvocationChain> chains = createInboundMapping(inbound, methods);
            JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains);
            ClassLoader cl = interfaze.getClassLoader();
            //FIXME
            return (T) Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler);
        } else if (wire instanceof OutboundWire) {
            OutboundWire<T> outbound = (OutboundWire<T>) wire;
            JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(outbound);
            Class<?> interfaze = outbound.getServiceContract().getInterfaceClass();
            ClassLoader cl = interfaze.getClassLoader();
            return (T) Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler);
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T createCallbackProxy(ServiceContract<?> contract, InboundWire<?> wire) throws ProxyCreationException {
        Class<T> interfaze = (Class<T>) contract.getCallbackClass();
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(context, wire);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public <T> WireInvocationHandler createHandler(RuntimeWire<T> wire) {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire<T> inbound = (InboundWire<T>) wire;
            Method[] methods = inbound.getServiceContract().getInterfaceClass().getMethods();
            Map<Method, InboundInvocationChain> chains = createInboundMapping(inbound, methods);
            return new JDKInboundInvocationHandler(chains);
        } else if (wire instanceof OutboundWire) {
            OutboundWire<T> outbound = (OutboundWire<T>) wire;
            return new JDKOutboundInvocationHandler(outbound);
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public WireInvocationHandler createCallbackHandler(InboundWire<?> wire) {
        return new JDKCallbackInvocationHandler(context, wire);
    }

    public OutboundInvocationChain createOutboundChain(Operation<?> operation) {
        return new OutboundInvocationChainImpl(operation);
    }

    public InboundInvocationChain createInboundChain(Operation<?> operation) {
        return new InboundInvocationChainImpl(operation);
    }


    public void createWires(Component component, ComponentDefinition<?> definition) {
        Implementation<?> implementation = definition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        for (ServiceDefinition service : componentType.getServices().values()) {
            InboundWire inboundWire = createWire(service);
            inboundWire.setContainerName(component.getName());
            component.addInboundWire(inboundWire);
        }

        for (ReferenceTarget reference : definition.getReferenceTargets().values()) {
            Map<String, ? extends ReferenceDefinition> references = componentType.getReferences();
            ReferenceDefinition mappedReference = references.get(reference.getReferenceName());
            OutboundWire wire = createWire(reference, mappedReference);
            wire.setContainerName(component.getName());
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

    public <T> void createWires(Reference<T> reference, ServiceContract<?> contract) {
        InboundWire<T> wire = new InboundWireImpl<T>();
        wire.setServiceContract(contract);
        wire.setContainerName(reference.getName());
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);
        }
        reference.setInboundWire(wire);
    }

    public void createWires(Service<?> service, BoundServiceDefinition<?> def) {
        createWires(service, def.getTarget().getPath(), def.getServiceContract());
    }

    public void createWires(Service<?> service, BindlessServiceDefinition def) {
        createWires(service, def.getTarget().getPath(), def.getServiceContract());
    }

    @SuppressWarnings("unchecked")
    public OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def) {
        //TODO multiplicity
        if (reference.getTargets().size() != 1) {
            throw new UnsupportedOperationException();
        }
        ServiceContract<?> contract = def.getServiceContract();
        OutboundWire wire = new OutboundWireImpl();
        QualifiedName qName = new QualifiedName(reference.getTargets().get(0).toString());
        wire.setTargetName(qName);
        wire.setServiceContract(contract);
        wire.setReferenceName(reference.getReferenceName());
        for (Operation<?> operation : contract.getOperations().values()) {
            //TODO handle policy
            OutboundInvocationChain chain = createOutboundChain(operation);
            wire.addInvocationChain(operation, chain);

        }
        Class<?> callbackInterface = contract.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackInterface(callbackInterface);
            for (Operation<?> operation : contract.getCallbacksOperations().values()) {
                InboundInvocationChain callbackTargetChain = createInboundChain(operation);
                // TODO handle policy
                //TODO statement below could be cleaner
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                wire.addTargetCallbackInvocationChain(operation, callbackTargetChain);
            }
        }
        return wire;
    }

    public InboundWire createWire(ServiceDefinition service) {
        InboundWire wire = new InboundWireImpl();
        ServiceContract<?> contract = service.getServiceContract();
        wire.setServiceContract(contract);
        wire.setServiceName(service.getName());
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            // TODO handle policy
            //TODO statement below could be cleaner
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);
        }
        Class<?> callbackInterface = contract.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackReferenceName(service.getCallbackReferenceName());
        }
        return wire;
    }

    private <T> void createWires(Service<T> service, String targetName, ServiceContract<?> contract) {
        InboundWire<T> inboundWire = new InboundWireImpl<T>();
        OutboundWire<T> outboundWire = new OutboundWireImpl<T>();
        inboundWire.setServiceContract(contract);
        inboundWire.setContainerName(service.getName());
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetName(new QualifiedName(targetName));
        outboundWire.setContainerName(service.getName());
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain inboundChain = createInboundChain(operation);
            inboundWire.addInvocationChain(operation, inboundChain);
            OutboundInvocationChain outboundChain = createOutboundChain(operation);
            outboundWire.addInvocationChain(operation, outboundChain);
        }
        service.setInboundWire(inboundWire);
        service.setOutboundWire(outboundWire);
    }

}
