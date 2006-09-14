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

import org.apache.tuscany.core.implementation.composite.CompositeReference;
import org.apache.tuscany.core.implementation.composite.CompositeService;
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

    private Map<Method, InboundInvocationChain> createInboundMapping(InboundWire wire, Method[] methods)
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

    public Object createProxy(RuntimeWire wire) throws ProxyCreationException {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire inbound = (InboundWire) wire;
            Class<?> interfaze = wire.getServiceContract().getInterfaceClass();
            Method[] methods = interfaze.getMethods();
            Map<Method, InboundInvocationChain> chains = createInboundMapping(inbound, methods);
            JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(chains, context);
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
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public Object createCallbackProxy(ServiceContract<?> contract, InboundWire wire) throws ProxyCreationException {
        Class<?> interfaze = contract.getCallbackClass();
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(context, wire);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public WireInvocationHandler createHandler(RuntimeWire wire) {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire inbound = (InboundWire) wire;
            Method[] methods = inbound.getServiceContract().getInterfaceClass().getMethods();
            Map<Method, InboundInvocationChain> chains = createInboundMapping(inbound, methods);
            return new JDKInboundInvocationHandler(chains, context);
        } else if (wire instanceof OutboundWire) {
            OutboundWire outbound = (OutboundWire) wire;
            return new JDKOutboundInvocationHandler(outbound, context);
        } else {
            ProxyCreationException e = new ProxyCreationException("Invalid wire type");
            e.setIdentifier(wire.getClass().getName());
            throw e;
        }
    }

    public WireInvocationHandler createCallbackHandler(InboundWire wire) {
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
            if (componentType instanceof CompositeComponentType<?, ?, ?>) {
                // If this is the case, then it means that component has already been returned
                // by CompositeBuilder and thus its children, in particular composite services,
                // have been registered
                CompositeComponent compositeComponent = (CompositeComponent) component;
                Service serviceChild = (Service) compositeComponent.getChild(service.getName());
                assert serviceChild != null;
                if (serviceChild instanceof CompositeService) {
                    serviceChild.setInboundWire(inboundWire);
                    // Notice that now the more immediate container of the wire is the composite service
                    inboundWire.setContainerName(serviceChild.getName());
                }
            }
            component.addInboundWire(inboundWire);
        }

        for (ReferenceTarget referenceTarget : definition.getReferenceTargets().values()) {
            Map<String, ? extends ReferenceDefinition> references = componentType.getReferences();
            ReferenceDefinition mappedReference = references.get(referenceTarget.getReferenceName());
            OutboundWire wire = createWire(referenceTarget, mappedReference);
            wire.setContainerName(component.getName());
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
                    wire.setContainerName(reference.getName());
                }
            }
        }
    }

    public <T> void createWires(Reference reference, ServiceContract<?> contract) {
        InboundWire wire = new InboundWireImpl();
        wire.setServiceContract(contract);
        wire.setContainerName(reference.getName());
        for (Operation<?> operation : contract.getOperations().values()) {
            InboundInvocationChain chain = createInboundChain(operation);
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);
        }
        // Notice that we skip wire.setCallbackReferenceName
        // First, an inbound wire's callbackReferenceName is only retrieved by JavaAtomicComponent
        // to create a callback injector based on the callback reference member; a composite reference
        // should not need to do that
        // Second, a reference definition does not have a callback reference name like a service
        // definition does
        reference.setInboundWire(wire);
    }

    public void createWires(Service service, BoundServiceDefinition<?> def) {
        createWires(service, def.getTarget().getPath(), def.getServiceContract());
    }

    public void createWires(Service service, BindlessServiceDefinition def) {
        createWires(service, def.getTarget().getPath(), def.getServiceContract());
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
        Class<?> callbackInterface = contract.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackInterface(callbackInterface);
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
        Class<?> callbackInterface = contract.getCallbackClass();
        if (callbackInterface != null) {
            wire.setCallbackReferenceName(service.getCallbackReferenceName());
        }
        return wire;
    }

    private <T> void createWires(Service service, String targetName, ServiceContract<?> contract) {
        InboundWire inboundWire = new InboundWireImpl();
        OutboundWire outboundWire = new OutboundWireImpl();
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

        // Add target callback chain to outbound wire, applicable to both bound and bindless services
        Class<?> callbackInterface = contract.getCallbackClass();
        if (callbackInterface != null) {
            outboundWire.setCallbackInterface(callbackInterface);
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

    public boolean isWireable(ServiceContract<?> source, ServiceContract<?> target) {
        if (source == target) {
            // Shortcut for performance
            return true;
        }
        if (source.isRemotable() != target.isRemotable()) {
            return false;
        }
        if (source.getInteractionScope() != target.getInteractionScope()) {
            return false;
        }

        for (Operation<?> operation : source.getOperations().values()) {
            Operation<?> targetOperation = target.getOperations().get(operation.getName());
            if (targetOperation == null) {
                return false;
            }
            if (!isCompatibleWith(operation, targetOperation)) {
                return false;
            }
        }

        for (Operation<?> operation : source.getCallbackOperations().values()) {
            Operation<?> targetOperation = target.getCallbackOperations().get(operation.getName());
            if (targetOperation == null) {
                return false;
            }
            if (!isCompatibleWith(operation, targetOperation)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two operations for wiring compatibility as defined by the SCA assembly specification, namely: <ol>
     * <li>compatibility for the individual method is defined as compatibility of the signature, that is method name,
     * input types, and output types MUST BE the same. <li>the order of the input and output types also MUST BE the
     * same. <li>the set of Faults and Exceptions expected by the source MUST BE the same or be a superset of those
     * specified by the service. </ol>
     *
     * @param source the source operation to compare
     * @param target the target operation to compare
     * @return true if the two operations are compatibile
     */
    public boolean isCompatibleWith(Operation<?> source, Operation<?> target) {
        // FIXME:
        return source.equals(target);
    }


}
