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
package org.apache.tuscany.core.builder;

import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.implementation.composite.CompositeReference;
import org.apache.tuscany.core.implementation.composite.CompositeService;
import org.apache.tuscany.core.wire.NonBlockingBridgingInterceptor;
import org.apache.tuscany.core.wire.OutboundAutowire;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;

/**
 * The default connector implmentation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorImpl implements Connector {

    private WirePostProcessorRegistry postProcessorRegistry;
    private WireService wireService;
    private WorkContext workContext;
    private WorkScheduler scheduler;

    public ConnectorImpl() {
    }

    @Constructor({"wireService", "processorRegistry", "scheduler", "workContext"})
    public ConnectorImpl(@Autowire WireService wireService,
                         @Autowire WirePostProcessorRegistry processorRegistry,
                         @Autowire WorkScheduler scheduler,
                         @Autowire WorkContext workContext) {
        this.postProcessorRegistry = processorRegistry;
        this.wireService = wireService;
        this.scheduler = scheduler;
        this.workContext = workContext;
    }

    public void connect(SCAObject source) throws WiringException {
        CompositeComponent parent = source.getParent();
        if (source instanceof AtomicComponent) {
            AtomicComponent sourceComponent = (AtomicComponent) source;
            // connect outbound wires for component references to their targets
            for (List<OutboundWire> referenceWires : sourceComponent.getOutboundWires().values()) {
                for (OutboundWire outboundWire : referenceWires) {
                    if (outboundWire instanceof OutboundAutowire) {
                        continue;
                    }
                    try {
                        SCAObject target;
                        if (sourceComponent.isSystem()) {
                            target = parent.getSystemChild(outboundWire.getTargetName().getPartName());
                        } else {
                            target = parent.getChild(outboundWire.getTargetName().getPartName());
                        }
                        connect(sourceComponent, outboundWire, target);
                    } catch (WiringException e) {
                        e.addContextName(source.getName());
                        e.addContextName(parent.getName());
                        throw e;
                    }
                }
            }
            // connect inbound wires
            for (InboundWire inboundWire : sourceComponent.getInboundWires().values()) {
                for (InboundInvocationChain chain : inboundWire.getInvocationChains().values()) {
                    Operation<?> operation = chain.getOperation();
                    String serviceName = inboundWire.getServiceName();
                    TargetInvoker invoker = sourceComponent.createTargetInvoker(serviceName, operation, null);
                    chain.setTargetInvoker(invoker);
                    chain.prepare();
                }
            }
        } else if (source instanceof Reference) {
            Reference reference = (Reference) source;
            InboundWire inboundWire = reference.getInboundWire();
            Map<Operation<?>, InboundInvocationChain> inboundChains = inboundWire.getInvocationChains();
            for (InboundInvocationChain chain : inboundChains.values()) {
                //TODO handle async
                // add target invoker on inbound side
                ServiceContract contract = inboundWire.getServiceContract();
                Operation operation = chain.getOperation();
                TargetInvoker invoker = reference.createTargetInvoker(contract, operation);
                chain.setTargetInvoker(invoker);
                chain.prepare();
            }
            OutboundWire outboundWire = reference.getOutboundWire();
            // connect the reference's inbound and outbound wires
            connect(inboundWire, outboundWire, true);

            if (reference instanceof CompositeReference) {
                // For a composite reference only, since its outbound wire comes
                // from its parent composite,
                // the corresponding target would not lie in its parent but
                // rather in its parent's parent
                parent = parent.getParent();
                assert parent != null : "Parent of parent was null";
                SCAObject target = parent.getChild(outboundWire.getTargetName().getPartName());
                connect((Component) parent, outboundWire, target);
            }
        } else if (source instanceof Service) {
            Service service = (Service) source;
            InboundWire inboundWire = service.getInboundWire();
            OutboundWire outboundWire = service.getOutboundWire();
            // For a composite reference only, since its outbound wire comes from its parent composite,
            // the corresponding target would not lie in its parent but rather in its parent's parent
//            if (source instanceof CompositeReference) {
//                parent = parent.getParent();
//                assert parent != null : "Parent of parent was null";
//            }
            SCAObject target;
            if (service.isSystem()) {
                target = parent.getSystemChild(outboundWire.getTargetName().getPartName());
            } else {
                target = parent.getChild(outboundWire.getTargetName().getPartName());
            }
            // connect the outbound service wire to the target
            connect(service, outboundWire, target);
            // NB: this connect must be done after the outbound service chain is connected to its target above
            if (!(source instanceof CompositeService)) {
                //REVIEW JFM: do we need this to be special for composites?
                connect(inboundWire, outboundWire, true);
            }
        }
    }

    public void connect(InboundWire sourceWire, OutboundWire targetWire, boolean optimizable)
        throws WiringException {
        Map<Operation<?>, OutboundInvocationChain> targetChains = targetWire.getInvocationChains();
        // perform optimization, if possible
        if (optimizable && sourceWire.getInvocationChains().isEmpty() && targetChains.isEmpty()) {
            sourceWire.setTargetWire(targetWire);
            if (postProcessorRegistry != null) {
                // run wire post-processors
                postProcessorRegistry.process(sourceWire, targetWire);
            }
            return;
        }
        for (InboundInvocationChain inboundChain : sourceWire.getInvocationChains().values()) {
            // match wire chains
            OutboundInvocationChain outboundChain = targetChains.get(inboundChain.getOperation());
            if (outboundChain == null) {
                // FIXME JFM    -------
                String serviceName = sourceWire.getServiceName();
                String sourceName = sourceWire.getContainer().getName();
                String refName = targetWire.getReferenceName();
                String targetName = targetWire.getContainer().getName();
                throw new IncompatibleInterfacesException("Incompatible source and target interfaces",
                    sourceName,
                    refName,
                    targetName,
                    serviceName);
            }
            connect(inboundChain, outboundChain);
        }
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(sourceWire, targetWire);
        }
    }

    /**
     * Connects the source outbound wire to a corresponding target inbound wire
     *
     * @param sourceWire  the source wire to connect
     * @param targetWire  the target wire to connect to
     * @param optimizable true if the wire connection can be optimized
     */
    public void connect(OutboundWire sourceWire, InboundWire targetWire, boolean optimizable)
        throws IncompatibleInterfacesException, IllegalCallbackException {
        SCAObject source = sourceWire.getContainer();
        SCAObject target = targetWire.getContainer();
        ServiceContract contract = sourceWire.getServiceContract();
        Map<Operation<?>, InboundInvocationChain> targetChains = targetWire.getInvocationChains();
        // perform optimization, if possible
        // REVIEW: (kentaminator@gmail.com) shouldn't this check whether the interceptors in the
        // source & target chains are marked as optimizable?  (and if so, optimize them away?)
        if (optimizable && sourceWire.getInvocationChains().isEmpty() && targetChains.isEmpty()) {
            sourceWire.setTargetWire(targetWire);
            if (postProcessorRegistry != null) {
                // run wire post-processors
                postProcessorRegistry.process(sourceWire, targetWire);
            }
            return;
        }
        // match outbound to inbound chains
        for (OutboundInvocationChain outboundChain : sourceWire.getInvocationChains().values()) {
            Operation<?> operation = outboundChain.getOperation();
            InboundInvocationChain inboundChain = targetChains.get(operation);
            if (inboundChain == null) {
                String sourceName = sourceWire.getContainer().getName();
                String refName = sourceWire.getReferenceName();
                String targetName = targetWire.getContainer().getName();
                String serviceName = targetWire.getServiceName();
                throw new IncompatibleInterfacesException("Incompatible interfaces",
                    sourceName,
                    refName,
                    targetName,
                    serviceName);
            }
            Operation<?> inboundOperation = inboundChain.getOperation();
            boolean isOneWayOperation = operation.isNonBlocking();
            boolean operationHasCallback = contract.getCallbackName() != null;
            if (isOneWayOperation && operationHasCallback) {
                String sourceName = sourceWire.getContainer().getName();
                String refName = sourceWire.getReferenceName();
                String targetName = targetWire.getContainer().getName();
                String serviceName = targetWire.getServiceName();
                throw new IllegalCallbackException("Operation cannot be marked one-way and have a callback",
                    inboundOperation.getName(),
                    sourceName,
                    refName,
                    targetName,
                    serviceName);
            }
            TargetInvoker invoker = null;
            if (target instanceof Component) {
                Component component = (Component) target;
                String portName = sourceWire.getTargetName().getPortName();
                invoker = component.createTargetInvoker(portName, inboundOperation, targetWire);
            } else if (target instanceof Reference) {
                Reference reference = (Reference) target;
                invoker = reference.createTargetInvoker(targetWire.getServiceContract(), inboundOperation);
            } else if (target instanceof CompositeService) {
                CompositeService compServ = (CompositeService) target;
                invoker = compServ.createTargetInvoker(targetWire.getServiceContract(), inboundChain.getOperation());
            }

            if (source instanceof Service && !(source instanceof CompositeService)) {
                // services are a special case: invoker must go on the inbound chain
                if (target instanceof Component && (isOneWayOperation || operationHasCallback)) {
                    // if the target is a component and the operation is non-blocking
                    connect(outboundChain, inboundChain, null, true);
                } else {
                    connect(outboundChain, inboundChain, null, false);
                }
                Service service = (Service) source;
                InboundInvocationChain chain = service.getInboundWire().getInvocationChains().get(operation);
                chain.setTargetInvoker(invoker);
            } else {
                if (target instanceof Component && (isOneWayOperation || operationHasCallback)) {
                    // if the target is a component and the operation is non-blocking
                    connect(outboundChain, inboundChain, invoker, true);
                } else {
                    connect(outboundChain, inboundChain, invoker, false);
                }
            }
        }

        // create source callback chains and connect them if target callback chains exist
        Map<Operation<?>, OutboundInvocationChain> sourceCallbackChains =
            targetWire.getSourceCallbackInvocationChains(source.getName());
        for (InboundInvocationChain inboundChain : sourceWire.getTargetCallbackInvocationChains().values()) {
            Operation<?> operation = inboundChain.getOperation();
            if (sourceCallbackChains != null && sourceCallbackChains.get(operation) != null) {
                String opName = operation.getName();
                String sourceName = sourceWire.getContainer().getName();
                String refName = sourceWire.getReferenceName();
                String targetName = targetWire.getContainer().getName();
                String serviceName = targetWire.getServiceName();
                throw new IllegalCallbackException("Source callback chain should not exist for operation",
                    opName,
                    sourceName,
                    refName,
                    targetName,
                    serviceName);
            }

            Operation targetOp =
                (Operation) targetWire.getServiceContract().getCallbackOperations().get(operation.getName());
            OutboundInvocationChain outboundChain = wireService.createOutboundChain(targetOp);
            targetWire.addSourceCallbackInvocationChain(source.getName(), targetOp, outboundChain);
            if (source instanceof Component) {
                Component component = (Component) source;
                TargetInvoker invoker = component.createTargetInvoker(null, operation, null);
                connect(outboundChain, inboundChain, invoker, false);
            } else if (source instanceof CompositeReference) {
                CompositeReference compRef = (CompositeReference) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker = compRef.createCallbackTargetInvoker(sourceContract, operation);
                connect(outboundChain, inboundChain, invoker, false);
            } else if (source instanceof Service) {
                Service service = (Service) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker = service.createCallbackTargetInvoker(sourceContract, operation);
                connect(outboundChain, inboundChain, invoker, false);
            }
        }
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(sourceWire, targetWire);
        }
    }

    /**
     * Connects a source to target chain
     *
     * @param sourceChain the source chain
     * @param targetChain the target chain
     * @param invoker     the invoker to place on the source chain for dispatching invocations
     * @param nonBlocking true if the operation is non-blocking
     */
    void connect(OutboundInvocationChain sourceChain,
                 InboundInvocationChain targetChain,
                 TargetInvoker invoker,
                 boolean nonBlocking) {
        Interceptor head = targetChain.getHeadInterceptor();
        assert head != null;
        if (nonBlocking) {
            sourceChain.setTargetInterceptor(new NonBlockingBridgingInterceptor(scheduler, workContext, head));
        } else {
            sourceChain.setTargetInterceptor(new SynchronousBridgingInterceptor(head));
        }
        sourceChain.prepare(); //FIXME prepare should be moved out
        sourceChain.setTargetInvoker(invoker);
    }


    /**
     * Connects an inbound source chain to an outbound target chain
     *
     * @param sourceChain
     * @param targetChain
     */
    void connect(InboundInvocationChain sourceChain, OutboundInvocationChain targetChain) {
        // invocations from inbound to outbound chains are always syncrhonius as they occur in services and references
        sourceChain.addInterceptor(new SynchronousBridgingInterceptor(targetChain.getHeadInterceptor()));
    }

    /**
     * Connects an component's outbound wire to its target in a composite.  Valid targets are either
     * <code>AtomicComponent</code>s contained in the composite, or <code>References</code> of the composite.
     *
     * @param sourceWire
     * @throws WiringException
     */
    private void connect(SCAObject source, OutboundWire sourceWire, SCAObject target) throws WiringException {
        assert sourceWire.getTargetName() != null;
        QualifiedName targetName = sourceWire.getTargetName();

        if (target instanceof AtomicComponent) {
            AtomicComponent targetComponent = (AtomicComponent) target;
            InboundWire targetWire = targetComponent.getInboundWire(targetName.getPortName());
            if (targetWire == null) {
                String sourceName = sourceWire.getContainer().getName();
                String sourceReference = sourceWire.getReferenceName();
                throw new TargetServiceNotFoundException("Target service not found",
                    sourceName,
                    sourceReference,
                    targetName.getPartName(),
                    targetName.getPortName());
            }
            checkIfWireable(sourceWire, targetWire);
            boolean optimizable = isOptimizable(source.getScope(), target.getScope());
            connect(sourceWire, targetWire, optimizable);
        } else if (target instanceof Reference) {
            InboundWire targetWire = ((Reference) target).getInboundWire();
            assert targetWire != null;
            checkIfWireable(sourceWire, targetWire);
            boolean optimizable = isOptimizable(source.getScope(), target.getScope());
            connect(sourceWire, targetWire, optimizable);
        } else if (target instanceof CompositeComponent) {
            CompositeComponent composite = (CompositeComponent) target;
            InboundWire targetWire = null;
            if (source.isSystem()) {
                for (Object child : composite.getSystemChildren()) {
                    if (child instanceof CompositeService) {
                        CompositeService compServ = (CompositeService) child;
                        targetWire = compServ.getInboundWire();
                        assert targetWire != null;
                        Class<?> sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
                        Class<?> targetInterface = targetWire.getServiceContract().getInterfaceClass();
                        if (sourceInterface.isAssignableFrom(targetInterface)) {
                            target = compServ;
                            break;
                        } else {
                            targetWire = null;
                        }
                    }
                }
            } else {
                for (Object child : composite.getChildren()) {
                    if (child instanceof CompositeService) {
                        CompositeService compServ = (CompositeService) child;
                        targetWire = compServ.getInboundWire();
                        assert targetWire != null;
                        Class<?> sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
                        Class<?> targetInterface = targetWire.getServiceContract().getInterfaceClass();
                        if (sourceInterface.isAssignableFrom(targetInterface)) {
                            target = compServ;
                            break;
                        } else {
                            targetWire = null;
                        }
                    }
                }
            }
            if (targetWire == null) {
                String sourceName = sourceWire.getContainer().getName();
                String sourceReference = sourceWire.getReferenceName();
                throw new TargetServiceNotFoundException("Target service not found",
                    sourceName,
                    sourceReference,
                    targetName.getPartName(),
                    targetName.getPortName());
            }
            boolean optimizable = isOptimizable(source.getScope(), target.getScope());
            connect(sourceWire, targetWire, optimizable);
        } else if (target == null) {
            String sourceName = sourceWire.getContainer().getName();
            String sourceReference = sourceWire.getReferenceName();
            throw new TargetServiceNotFoundException("Target service not found",
                sourceName,
                sourceReference,
                targetName.getPartName(),
                targetName.getPortName());
        } else {
            String sourceName = sourceWire.getContainer().getName();
            String sourceReference = sourceWire.getReferenceName();
            throw new InvalidTargetTypeException("Invalid target type",
                sourceName,
                sourceReference,
                targetName.getPartName(),
                targetName.getPortName());
        }
    }

    private void checkIfWireable(OutboundWire sourceWire, InboundWire targetWire)
        throws IncompatibleInterfacesException {
        if (wireService == null) {
            Class<?> sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
            Class<?> targetInterface = targetWire.getServiceContract().getInterfaceClass();
            if (!sourceInterface.isAssignableFrom(targetInterface)) {
                String sourceName = sourceWire.getContainer().getName();
                String refName = sourceWire.getReferenceName();
                String targetName = targetWire.getContainer().getName();
                String serviceName = targetWire.getServiceName();
                throw new IncompatibleInterfacesException("Incompatible interfaces",
                    sourceName,
                    refName,
                    targetName,
                    serviceName);
            }
        } else {
            try {
                ServiceContract sourceContract = sourceWire.getServiceContract();
                ServiceContract targetContract = targetWire.getServiceContract();
                wireService.checkCompatibility(sourceContract, targetContract, false);
            } catch (IncompatibleServiceContractException e) {
                String sourceName = sourceWire.getContainer().getName();
                String refName = sourceWire.getReferenceName();
                String targetName = targetWire.getContainer().getName();
                String serviceName = targetWire.getServiceName();
                throw new IncompatibleInterfacesException("Incompatible interfaces",
                    sourceName,
                    refName,
                    targetName,
                    serviceName, e);
            }
        }
    }

    private boolean isOptimizable(Scope pReferrer, Scope pReferee) {
        if (pReferrer == Scope.UNDEFINED || pReferee == Scope.UNDEFINED) {
            return false;
        }
        if (pReferee == pReferrer) {
            return true;
        } else if (pReferrer == Scope.STATELESS) {
            return true;
        } else if (pReferee == Scope.STATELESS) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SESSION) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.MODULE) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.MODULE) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.COMPOSITE && pReferee == Scope.MODULE) {
            // case where a service context points to a module scoped component
            return true;
        } else {
            return pReferrer == Scope.MODULE && pReferee == Scope.COMPOSITE;
        }
    }

}
