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
import org.apache.tuscany.spi.component.ReferenceNotFoundException;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Binding;
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
import org.apache.tuscany.spi.wire.WireServiceExtension;

import org.apache.tuscany.core.implementation.composite.CompositeReference;
import org.apache.tuscany.core.implementation.composite.CompositeService;
import org.apache.tuscany.core.implementation.system.model.SystemBinding;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 *
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public class JDKWireService extends WireServiceExtension {

    public JDKWireService() {
        super(null, null);
    }

    @Constructor
    public JDKWireService(@Autowire WorkContext context, @Autowire PolicyBuilderRegistry policyRegistry) {
        super(context, policyRegistry);
    }

    @Init(eager = true)
    public void init() {
    }

    public <T> T createProxy(Class<T> interfaze, InboundWire wire) throws ProxyCreationException {
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(wire, context);
        ClassLoader cl = interfaze.getClassLoader();
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public Object createProxy(RuntimeWire wire) throws ProxyCreationException {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire inbound = (InboundWire) wire;
            Class<?> interfaze = wire.getServiceContract().getInterfaceClass();
            JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(inbound, context);
            ClassLoader cl = interfaze.getClassLoader();
            //FIXME
            return Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler);
        } else if (wire instanceof OutboundWire) {
            OutboundWire outbound = (OutboundWire) wire;
            JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(outbound, context);
            Class<?> interfaze = outbound.getServiceContract().getInterfaceClass();
            ClassLoader cl = interfaze.getClassLoader();
            return Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler);
        } else {
            throw new ProxyCreationException("Invalid wire type", wire.getClass().getName());
        }
    }

    public Object createCallbackProxy(ServiceContract<?> contract, InboundWire wire) throws ProxyCreationException {
        Class<?> interfaze = contract.getCallbackClass();
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wire, context);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public WireInvocationHandler createHandler(RuntimeWire wire) {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire inbound = (InboundWire) wire;
            return new JDKInboundInvocationHandler(inbound, context);
        } else if (wire instanceof OutboundWire) {
            OutboundWire outbound = (OutboundWire) wire;
            return new JDKOutboundInvocationHandler(outbound, context);
        } else {
            throw new ProxyCreationException("Invalid wire type", wire.getClass().getName());
        }
    }

    public WireInvocationHandler createCallbackHandler(InboundWire wire) {
        return new JDKCallbackInvocationHandler(wire, context);
    }

    public OutboundInvocationChain createOutboundChain(Operation<?> operation) {
        return new OutboundInvocationChainImpl(operation);
    }

    public InboundInvocationChain createInboundChain(Operation<?> operation) {
        return new InboundInvocationChainImpl(operation);
    }

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
        if (contract.getCallbackName() != null) {
            wire.setCallbackInterface(contract.getCallbackClass());
            for (Operation<?> operation : contract.getCallbackOperations().values()) {
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
        if (contract.getCallbackName() != null) {
            wire.setCallbackReferenceName(service.getCallbackReferenceName());
        }
        return wire;
    }

    public void createWires(Component component, ComponentDefinition<?> definition) {
        Implementation<?> implementation = definition.getImplementation();
        ComponentType<?, ?, ?> componentType = implementation.getComponentType();
        for (ServiceDefinition service : componentType.getServices().values()) {
            InboundWire inboundWire = createWire(service);
            inboundWire.setContainer(component);
            if (componentType instanceof CompositeComponentType<?, ?, ?>) {
                // If this is the case, then it means that component has already been returned
                // by CompositeBuilder and thus its children, in particular composite services,
                // have been registered
                CompositeComponent compositeComponent = (CompositeComponent) component;
                if (service instanceof BoundServiceDefinition) {
                    Binding binding = ((BoundServiceDefinition) service).getBinding();
                    if (binding instanceof SystemBinding) {
                        continue;
                    }
                }
                Service serviceChild = (Service) compositeComponent.getChild(service.getName());
                assert serviceChild != null;
                if (serviceChild instanceof CompositeService) {
                    serviceChild.setInboundWire(inboundWire);
                    // Notice that now the more immediate container of the wire is the composite service
                    inboundWire.setContainer(serviceChild);
                }
            }
            component.addInboundWire(inboundWire);
        }

        for (ReferenceTarget referenceTarget : definition.getReferenceTargets().values()) {
            Map<String, ? extends ReferenceDefinition> references = componentType.getReferences();
            ReferenceDefinition mappedReference = references.get(referenceTarget.getReferenceName());
            if (mappedReference == null) {
                String refName = referenceTarget.getReferenceName();
                ReferenceNotFoundException e = new ReferenceNotFoundException(refName);
                e.addContextName(refName);
                e.addContextName(definition.getName());
                throw e;
            }
            OutboundWire wire = createWire(referenceTarget, mappedReference);
            wire.setContainer(component);
            component.addOutboundWire(wire);
            if (componentType instanceof CompositeComponentType<?, ?, ?>) {
                // If this is the case, then it means that component has already been returned
                // by CompositeBuilder and thus its children, in particular composite references,
                // have been registered
                CompositeComponent compositeComponent = (CompositeComponent) component;
                Reference reference = (Reference) compositeComponent.getChild(referenceTarget.getReferenceName());
                assert reference != null;
                if (reference instanceof CompositeReference) {
                    reference.setOutboundWire(wire);
                    // Notice that now the more immediate container of the wire is the composite reference
                    wire.setContainer(reference);
                }
            }
        }
    }

    public void createWires(Reference reference, ServiceContract<?> contract) {
        InboundWire inboundWire = new InboundWireImpl();
        inboundWire.setServiceContract(contract);
        inboundWire.setContainer(reference);
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            inboundWire.addInvocationChain(operation, chain);
        }
        OutboundWire outboundWire = new OutboundWireImpl();

        // [rfeng] Check if the Reference has the binding contract
        ServiceContract<?> bindingContract = reference.getBindingServiceContract();
        if (bindingContract == null) {
            bindingContract = contract;
        }
        outboundWire.setServiceContract(bindingContract);
        outboundWire.setContainer(reference);
        for (Operation<?> operation : bindingContract.getOperations().values()) {
            OutboundInvocationChain chain = createOutboundChain(operation);
            chain.addInterceptor(new InvokerInterceptor());
            outboundWire.addInvocationChain(operation, chain);
        }

        // Notice that we skip inboundWire.setCallbackReferenceName
        // First, an inbound inboundWire's callbackReferenceName is only retrieved by JavaAtomicComponent
        // to create a callback injector based on the callback reference member; a composite reference
        // should not need to do that
        // Second, a reference definition does not have a callback reference name like a service
        // definition does
        reference.setInboundWire(inboundWire);
        reference.setOutboundWire(outboundWire);
    }

    public void createWires(Service service, String targetName, ServiceContract<?> contract) {
        InboundWire inboundWire = new InboundWireImpl();

        // [rfeng] Check if the Reference has the binding contract
        ServiceContract<?> bindingContract = service.getBindingServiceContract();
        if (bindingContract == null) {
            bindingContract = contract;
        }
        inboundWire.setServiceContract(bindingContract);
        inboundWire.setContainer(service);
        for (Operation<?> operation : bindingContract.getOperations().values()) {
            InboundInvocationChain inboundChain = createInboundChain(operation);
            inboundWire.addInvocationChain(operation, inboundChain);
        }

        OutboundWire outboundWire = new OutboundWireImpl();
        outboundWire.setServiceContract(contract);
        outboundWire.setTargetName(new QualifiedName(targetName));
        outboundWire.setContainer(service);

        for (Operation<?> operation : contract.getOperations().values()) {
            OutboundInvocationChain outboundChain = createOutboundChain(operation);
            outboundWire.addInvocationChain(operation, outboundChain);
        }

        // Add target callback chain to outbound wire, applicable to both bound and bindless services
        if (contract.getCallbackName() != null) {
            outboundWire.setCallbackInterface(contract.getCallbackClass());
            for (Operation<?> operation : contract.getCallbackOperations().values()) {
                InboundInvocationChain callbackTargetChain = createInboundChain(operation);
                // TODO handle policy
                //TODO statement below could be cleaner
                callbackTargetChain.addInterceptor(new InvokerInterceptor());
                outboundWire.addTargetCallbackInvocationChain(operation, callbackTargetChain);
            }
        }

        // Not clear in any case why this is done here and at the parent composite level as well
        // But for a composite service, make sure that the inbound wire comes from the parent
        if (!(service instanceof CompositeService)) {
            service.setInboundWire(inboundWire);
        }
        service.setOutboundWire(outboundWire);
    }


}
