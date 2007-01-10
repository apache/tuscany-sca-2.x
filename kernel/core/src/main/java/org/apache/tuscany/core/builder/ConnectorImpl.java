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
import org.apache.tuscany.spi.builder.MissingWireTargetException;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
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

import org.apache.tuscany.core.wire.LoopBackWire;
import org.apache.tuscany.core.wire.NonBlockingBridgingInterceptor;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.apache.tuscany.core.wire.WireUtils;

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

    @Constructor
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
        if (source instanceof Component) {
            handleComponent((Component) source);
        } else if (source instanceof Reference) {
            handleReference((Reference) source);
        } else if (source instanceof Service) {
            handleService((Service) source);
        } else {
            throw new AssertionError("Invalid source type");
        }
    }

    public void connect(InboundWire sourceWire, OutboundWire targetWire, boolean optimizable)
        throws WiringException {
        Map<Operation<?>, OutboundInvocationChain> targetChains = targetWire.getInvocationChains();
        if (sourceWire.getContainer() != null && sourceWire.getContainer().isSystem()) {
            sourceWire.setTargetWire(targetWire);
            // system services do not need to have their chains processed, return
            return;
        }
        for (InboundInvocationChain inboundChain : sourceWire.getInvocationChains().values()) {
            // match invocation chains
            OutboundInvocationChain outboundChain = targetChains.get(inboundChain.getOperation());
            if (outboundChain == null) {
                throw new IncompatibleInterfacesException(sourceWire, targetWire);
            }
            connect(inboundChain, outboundChain);
        }
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(sourceWire, targetWire);
        }
        // perform optimization, if possible. Atomic component's do not have outbound target wires, but keep the check
        // perform optimization, if possible. Note that optimizations on stateless targets are not performed if they
        // receive destroy events since a destruction notification must be given through a proxy
        if (optimizable
            && WireUtils.isOptimizable(sourceWire)
            && WireUtils.isOptimizable(targetWire)
            && targetWire.getContainer() instanceof AtomicComponent
            && targetWire.getContainer().getScope() == Scope.STATELESS
            && !((AtomicComponent) targetWire.getContainer()).isDestroyable()) {
            sourceWire.setTargetWire(targetWire);
        }
    }

    /**
     * Connects the source outbound wire to a corresponding target inbound wire
     *
     * @param sourceWire  the source wire to connect
     * @param targetWire  the target wire to connect to
     * @param optimizable true if the wire connection can be optimized
     * @throws WiringException
     */
    public void connect(OutboundWire sourceWire, InboundWire targetWire, boolean optimizable)
        throws WiringException {
        SCAObject source = sourceWire.getContainer();
        assert source != null;
        SCAObject target = targetWire.getContainer();
        assert target != null;
        Map<Operation<?>, InboundInvocationChain> targetChains = targetWire.getInvocationChains();
        if (sourceWire.getContainer() != null && sourceWire.getContainer().isSystem()) {
            sourceWire.setTargetWire(targetWire);
            // system services do not need to have their chains processed, return
            return;
        }
        ServiceContract contract = sourceWire.getServiceContract();
        // match outbound to inbound chains
        for (OutboundInvocationChain outboundChain : sourceWire.getInvocationChains().values()) {
            Operation<?> operation = outboundChain.getOperation();
            InboundInvocationChain inboundChain = targetChains.get(operation);
            if (inboundChain == null) {
                throw new IncompatibleInterfacesException(sourceWire, targetWire);
            }
            Operation<?> inboundOperation = inboundChain.getOperation();
            boolean isOneWayOperation = operation.isNonBlocking();
            boolean operationHasCallback = contract.getCallbackName() != null;
            if (isOneWayOperation && operationHasCallback) {
                throw new IllegalCallbackException("Operation cannot be marked one-way and have a callback",
                    inboundOperation.getName(),
                    sourceWire,
                    targetWire);
            }
            TargetInvoker invoker;
            if (target instanceof Component) {
                Component component = (Component) target;
                QualifiedName wireTargetName = sourceWire.getTargetName();
                String portName = null;
                if (wireTargetName != null) {
                    portName = wireTargetName.getPortName();
                }
                try {
                    invoker = component.createTargetInvoker(portName, inboundOperation, targetWire);
                } catch (TargetInvokerCreationException e) {
                    throw new WireConnectException("Error connecting source and target", sourceWire, targetWire, e);
                }
            } else if (target instanceof ReferenceBinding) {
                ReferenceBinding referenceBinding = (ReferenceBinding) target;
                try {
                    invoker = referenceBinding.createTargetInvoker(targetWire.getServiceContract(), inboundOperation);
                } catch (TargetInvokerCreationException e) {
                    String targetName = targetWire.getContainer().getName();
                    throw new WireConnectException("Error processing inbound wire", null, null, targetName, null, e);
                }
            } else if (target instanceof ServiceBinding) {
                ServiceBinding binding = (ServiceBinding) target;
                try {
                    invoker = binding.createTargetInvoker(targetWire.getServiceContract(), inboundChain.getOperation());
                } catch (TargetInvokerCreationException e) {
                    String targetName = targetWire.getContainer().getName();
                    throw new WireConnectException("Error processing inbound wire", null, null, targetName, null, e);
                }
            } else {
                throw new AssertionError();
            }

            if (source instanceof ServiceBinding) {
                // services are a special case: invoker must go on the inbound and outbound chains
                if (target instanceof Component && (isOneWayOperation || operationHasCallback)) {
                    // if the target is a component and the operation is non-blocking
                    connect(outboundChain, inboundChain, invoker, true);
                } else {
                    connect(outboundChain, inboundChain, invoker, false);
                }
                ServiceBinding binding = (ServiceBinding) source;
                InboundInvocationChain chain = binding.getInboundWire().getInvocationChains().get(operation);
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
                throw new IllegalCallbackException("Source callback chain should not exist for operation",
                    opName,
                    sourceWire,
                    targetWire);
            }

            ServiceContract<?> targetContract = targetWire.getServiceContract();
            assert targetContract != null;
            String opName = operation.getName();
            assert opName != null;
            Operation targetOp = targetContract.getCallbackOperations().get(opName);
            OutboundInvocationChain outboundChain = wireService.createOutboundChain(targetOp);
            targetWire.addSourceCallbackInvocationChain(source.getName(), targetOp, outboundChain);
            if (source instanceof Component) {
                Component component = (Component) source;
                TargetInvoker invoker;
                try {
                    invoker = component.createTargetInvoker(targetOp.getName(), operation, null);
                } catch (TargetInvokerCreationException e) {
                    throw new WireConnectException("Error connecting source and target",
                        sourceWire,
                        targetWire,
                        e);
                }
                connect(outboundChain, inboundChain, invoker, false);
            } else if (source instanceof ReferenceBinding) {
                ReferenceBinding binding = (ReferenceBinding) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker;
                try {
                    invoker = binding.createTargetInvoker(sourceContract, operation);
                } catch (TargetInvokerCreationException e) {
                    throw new WireConnectException("Error connecting source and target",
                        sourceWire,
                        targetWire,
                        e);
                }
                connect(outboundChain, inboundChain, invoker, false);
            } else if (source instanceof ServiceBinding) {
                ServiceBinding binding = (ServiceBinding) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker;
                try {
                    invoker = binding.createTargetInvoker(sourceContract, operation);
                } catch (TargetInvokerCreationException e) {
                    String targetName = sourceWire.getContainer().getName();
                    throw new WireConnectException("Error processing callback wire",
                        null,
                        null,
                        targetName,
                        null,
                        e);
                }
                connect(outboundChain, inboundChain, invoker, false);
            } else {
                throw new AssertionError();
            }
        }
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(sourceWire, targetWire);
        }
        // perform optimization, if possible. Note that optimizations on stateless targets are not performed if they
        // receive destroy events since a destruction notification must be given through a proxy
        if (optimizable
            && WireUtils.isOptimizable(sourceWire)
            && WireUtils.isOptimizable(targetWire)
            && targetWire.getContainer() instanceof AtomicComponent
            && targetWire.getContainer().getScope() == Scope.STATELESS
            && !((AtomicComponent) targetWire.getContainer()).isDestroyable()) {
            sourceWire.setOptimizable(true);
            sourceWire.setTargetWire(targetWire);
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
    protected void connect(OutboundInvocationChain sourceChain,
                           InboundInvocationChain targetChain,
                           TargetInvoker invoker,
                           boolean nonBlocking) throws WireConnectException {
        Interceptor head = targetChain.getHeadInterceptor();
        if (head == null) {
            throw new WireConnectException("Inbound chain must contain at least one interceptor");
        }
        if (nonBlocking) {
            sourceChain.setTargetInterceptor(new NonBlockingBridgingInterceptor(scheduler, workContext, head));
        } else {
            sourceChain.setTargetInterceptor(new SynchronousBridgingInterceptor(head));
        }
        sourceChain.prepare();
        sourceChain.setTargetInvoker(invoker);
    }


    /**
     * Connects an inbound source chain to an outbound target chain
     *
     * @param sourceChain the source chain to connect
     * @param targetChain the target chain to connect
     */
    protected void connect(InboundInvocationChain sourceChain, OutboundInvocationChain targetChain)
        throws WireConnectException {
        Interceptor head = targetChain.getHeadInterceptor();
        if (head == null) {
            throw new WireConnectException("Outbound chain must contain at least one interceptor");
        }
        // invocations from inbound to outbound chains are always synchronous as they occur in services and references
        sourceChain.addInterceptor(new SynchronousBridgingInterceptor(head));
    }

    /**
     * Connects an outbound wire to its target in a composite.
     *
     * @param sourceWire the source wire to connect
     * @throws WiringException
     */
    protected void connect(SCAObject source, OutboundWire sourceWire, SCAObject target) throws WiringException {
        assert sourceWire.getTargetName() != null;
        QualifiedName targetName = sourceWire.getTargetName();
        if (target instanceof Component) {
            connect(source, sourceWire, (Component) target);
        } else if (target instanceof Reference) {
            connect(source, sourceWire, (Reference) target);
        } else if (target instanceof Service) {
            connect(source, sourceWire, (Service) target);
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

    protected void connect(SCAObject source, OutboundWire sourceWire, Reference target) throws WiringException {
        assert sourceWire.getTargetName() != null;
        QualifiedName targetName = sourceWire.getTargetName();
        InboundWire targetWire = null;
        for (ReferenceBinding binding : target.getReferenceBindings()) {
            InboundWire candidate = binding.getInboundWire();
            if (sourceWire.getBindingType().equals(candidate.getBindingType())) {
                targetWire = candidate;
                break;
            }
        }
        if (targetWire == null) {
            if (target.getReferenceBindings().size() > 0 && source instanceof Component) {
                // TODO create a pluggable algorithm for selecting the binding type
                targetWire = target.getReferenceBindings().get(0).getInboundWire();
            }
            if (targetWire == null) {
                throw new NoCompatibleBindingsException(source.getName(),
                    targetName.getPartName(),
                    targetName.getPortName());
            }
        }
        checkIfWireable(sourceWire, targetWire);
        boolean optimizable = isOptimizable(source.getScope(), target.getScope());
        connect(sourceWire, targetWire, optimizable);
    }

    protected void connect(SCAObject source, OutboundWire sourceWire, Service target) throws WiringException {
        assert sourceWire.getTargetName() != null;
        QualifiedName targetName = sourceWire.getTargetName();
        InboundWire targetWire = null;
        for (ServiceBinding binding : target.getServiceBindings()) {
            InboundWire candidate = binding.getInboundWire();
            if (sourceWire.getBindingType().equals(candidate.getBindingType())) {
                targetWire = candidate;
                break;
            }
        }
        if (targetWire == null) {
            throw new NoCompatibleBindingsException(source.getName(),
                targetName.getPartName(),
                targetName.getPortName());
        }
        checkIfWireable(sourceWire, targetWire);
        boolean optimizable = isOptimizable(source.getScope(), target.getScope());
        connect(sourceWire, targetWire, optimizable);
    }

    protected void connect(SCAObject source, OutboundWire sourceWire, Component target)
        throws WiringException {
        assert sourceWire.getTargetName() != null;
        QualifiedName targetName = sourceWire.getTargetName();
        InboundWire targetWire;
        // FIXME JFM should we move getInboundSystemWire up to Component?
        if (target instanceof CompositeComponent && source.isSystem()) {
            targetWire = ((CompositeComponent) target).getInboundSystemWire(targetName.getPortName());
        } else {
            targetWire = target.getInboundWire(targetName.getPortName());
        }
        if (targetWire == null) {
            String sourceName = sourceWire.getContainer().getName();
            String sourceReference = sourceWire.getReferenceName();
            throw new TargetServiceNotFoundException("Target service does not exist or is not configured with a "
                + "local binding",
                sourceName,
                sourceReference,
                targetName.getPartName(),
                targetName.getPortName());
        }
        checkIfWireable(sourceWire, targetWire);
        boolean optimizable = isOptimizable(source.getScope(), target.getScope());
        connect(sourceWire, targetWire, optimizable);
    }

    protected void autowire(OutboundWire outboundWire, CompositeComponent parent)
        throws WiringException {
        // JFM FIXME test coverage for this method
        InboundWire targetWire;
        try {
            Class interfaze = outboundWire.getServiceContract().getInterfaceClass();
            if (CompositeComponent.class.equals(interfaze)) {
                JavaServiceContract contract = new JavaServiceContract(CompositeComponent.class);
                targetWire = new LoopBackWire();
                targetWire.setServiceContract(contract);
                targetWire.setContainer(parent);
                outboundWire.setTargetWire(targetWire);
                return;
            }
            if (outboundWire.getContainer().isSystem()) {
                targetWire = parent.resolveSystemAutowire(interfaze);
            } else {
                targetWire = parent.resolveAutowire(interfaze);
            }
        } catch (TargetResolutionException e) {
            String sourceReference = outboundWire.getReferenceName();
            String sourceName = outboundWire.getContainer().getName();
            throw new WireConnectException("Error resolving autowire target",
                sourceName,
                sourceReference,
                null,
                null,
                e);
        }
        if (targetWire == null) {
            // autowire may return null if it is optional. The client must decide if an error should be thrown
            return;
        }
        Scope sourceScope = outboundWire.getContainer().getScope();
        Scope targetScope = targetWire.getContainer().getScope();
        boolean optimizable = isOptimizable(sourceScope, targetScope);
        connect(outboundWire, targetWire, optimizable);
    }

    protected void checkIfWireable(OutboundWire sourceWire, InboundWire targetWire)
        throws IncompatibleInterfacesException {
        if (wireService == null) {
            Class<?> sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
            Class<?> targetInterface = targetWire.getServiceContract().getInterfaceClass();
            if (!sourceInterface.isAssignableFrom(targetInterface)) {
                throw new IncompatibleInterfacesException(sourceWire, targetWire);
            }
        } else {
            try {
                ServiceContract sourceContract = sourceWire.getServiceContract();
                ServiceContract targetContract = targetWire.getServiceContract();
                wireService.checkCompatibility(sourceContract, targetContract, false);
            } catch (IncompatibleServiceContractException e) {
                throw new IncompatibleInterfacesException(sourceWire, targetWire, e);
            }
        }
    }

    protected boolean isOptimizable(Scope pReferrer, Scope pReferee) {
        if (pReferrer == Scope.UNDEFINED
            || pReferee == Scope.UNDEFINED
            || pReferrer == Scope.CONVERSATION
            || pReferee == Scope.CONVERSATION) {
            return false;
        }
        if (pReferee == pReferrer) {
            return true;
        } else if (pReferrer == Scope.STATELESS) {
            return true;
        } else if (pReferee == Scope.STATELESS) {
            return false;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SESSION) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SYSTEM) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.SYSTEM) {
            return true;
        } else //noinspection SimplifiableIfStatement
            if (pReferrer == Scope.SYSTEM && pReferee == Scope.COMPOSITE) {
                // case where a service context points to a composite scoped component
                return true;
            } else {
                return pReferrer == Scope.COMPOSITE && pReferee == Scope.SYSTEM;
            }
    }

    /**
     * Connects wires from a service to a target
     *
     * @param service the service
     * @throws WiringException if an exception connecting the service wires is encountered
     */
    private void handleService(Service service) throws WiringException {
        CompositeComponent parent = service.getParent();
        assert parent != null;
        for (ServiceBinding binding : service.getServiceBindings()) {
            InboundWire inboundWire = binding.getInboundWire();
            OutboundWire outboundWire = binding.getOutboundWire();
            // For a composite reference only, since its outbound wire comes from its parent composite,
            // the corresponding target would not lie in its parent but rather in its parent's parent
            SCAObject target;
            if (service.isSystem()) {
                target = parent.getSystemChild(outboundWire.getTargetName().getPartName());
            } else {
                target = parent.getChild(outboundWire.getTargetName().getPartName());
            }
            // connect the outbound service wire to the target
            connect(binding, outboundWire, target);
            // NB: this connect must be done after the outbound service chain is connected to its target above
            connect(inboundWire, outboundWire, true);
        }
    }

    private void handleReference(Reference reference) throws WiringException {
        CompositeComponent parent = reference.getParent();
        assert parent != null;
        for (ReferenceBinding binding : reference.getReferenceBindings()) {
            InboundWire inboundWire = binding.getInboundWire();
            Map<Operation<?>, InboundInvocationChain> inboundChains = inboundWire.getInvocationChains();
            for (InboundInvocationChain chain : inboundChains.values()) {
                // add target invoker on inbound side
                ServiceContract contract = inboundWire.getServiceContract();
                Operation operation = chain.getOperation();
                TargetInvoker invoker;
                try {
                    invoker = binding.createTargetInvoker(contract, operation);
                } catch (TargetInvokerCreationException e) {
                    String targetName = inboundWire.getContainer().getName();
                    throw new WireConnectException("Error processing inbound wire",
                        null,
                        null,
                        targetName,
                        null,
                        e);
                }
                chain.setTargetInvoker(invoker);
                chain.prepare();
            }
            OutboundWire outboundWire = binding.getOutboundWire();
            // connect the reference's inbound and outbound wires
            connect(inboundWire, outboundWire, true);
        }
    }

    private void handleComponent(Component component) throws WiringException {
        CompositeComponent parent = component.getParent();
        assert parent != null;
        // connect outbound wires for component references to their targets
        for (List<OutboundWire> referenceWires : component.getOutboundWires().values()) {
            for (OutboundWire outboundWire : referenceWires) {
                try {
                    if (component.isSystem()) {
                        if (outboundWire.isAutowire()) {
                            autowire(outboundWire, parent);

                        } else {
                            SCAObject target = parent.getSystemChild(outboundWire.getTargetName().getPartName());
                            connect(component, outboundWire, target);
                        }
                    } else {
                        if (outboundWire.isAutowire()) {
                            autowire(outboundWire, parent);
                        } else {
                            if (outboundWire.getTargetName() == null) {
                                String referenceName = outboundWire.getReferenceName();
                                throw new MissingWireTargetException("Target name was null", referenceName);
                            }
                            SCAObject target = parent.getChild(outboundWire.getTargetName().getPartName());
                            connect(component, outboundWire, target);
                        }
                    }
                } catch (WiringException e) {
                    e.addContextName(component.getName());
                    e.addContextName(parent.getName());
                    throw e;
                }
            }
        }
    }
}
