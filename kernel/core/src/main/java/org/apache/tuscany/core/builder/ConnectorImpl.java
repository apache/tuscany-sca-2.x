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

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.MissingWireTargetException;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.util.UriHelper;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.wire.NonBlockingBridgingInterceptor;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.apache.tuscany.core.wire.WireUtils;

/**
 * The default connector implmentation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorImpl implements Connector {
    private WireService wireService;
    private WirePostProcessorRegistry postProcessorRegistry;
    private ComponentManager componentManager;
    private WorkContext workContext;
    private WorkScheduler scheduler;

    public ConnectorImpl(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    @Constructor
    public ConnectorImpl(@Autowire WireService wireService,
                         @Autowire WirePostProcessorRegistry processorRegistry,
                         @Autowire ComponentManager componentManager,
                         @Autowire WorkScheduler scheduler,
                         @Autowire WorkContext workContext) {
        this.wireService = wireService;
        this.postProcessorRegistry = processorRegistry;
        this.componentManager = componentManager;
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

    public void connect(SCAObject source,
                        InboundWire sourceWire,
                        SCAObject target,
                        OutboundWire targetWire,
                        boolean optimizable)
        throws WiringException {
        Map<Operation<?>, OutboundInvocationChain> targetChains = targetWire.getInvocationChains();
        for (InboundInvocationChain inboundChain : sourceWire.getInvocationChains().values()) {
            // match invocation chains
            OutboundInvocationChain outboundChain = targetChains.get(inboundChain.getOperation());
            if (outboundChain == null) {
                throw new IncompatibleInterfacesException(sourceWire.getSourceUri(), targetWire.getTargetUri());
            }
            connect(inboundChain, outboundChain);
        }
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(source, sourceWire, target, targetWire);
        }
        if (optimizable && WireUtils.isOptimizable(source, sourceWire) && WireUtils.isOptimizable(targetWire)) {
            sourceWire.setOptimizable(true);
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
     * @deprecated
     */
    public void connect(SCAObject source,
                        OutboundWire sourceWire,
                        SCAObject target,
                        InboundWire targetWire,
                        boolean optimizable)
        throws WiringException {
        assert source != null;
        assert target != null;
        assert sourceWire.getTargetUri() != null;
        Map<Operation<?>, InboundInvocationChain> targetChains = targetWire.getInvocationChains();
        String portName = sourceWire.getTargetUri().getFragment();
        // match outbound to inbound chains
        for (OutboundInvocationChain outboundChain : sourceWire.getInvocationChains().values()) {
            Operation<?> operation = outboundChain.getOperation();
            InboundInvocationChain inboundChain = targetChains.get(operation);
            if (inboundChain == null) {
                throw new IncompatibleInterfacesException(sourceWire.getSourceUri(), targetWire.getSourceUri());
            }
            Operation<?> inboundOperation = inboundChain.getOperation();
            boolean isOneWayOperation = operation.isNonBlocking();
            TargetInvoker invoker;
            if (target instanceof Component) {
                Component component = (Component) target;
                try {
                    invoker = component.createTargetInvoker(portName, inboundOperation, targetWire);
                } catch (TargetInvokerCreationException e) {
                    URI sourceUri = sourceWire.getSourceUri();
                    URI targetUri = targetWire.getSourceUri();
                    throw new WireConnectException("Error connecting source and target", sourceUri, targetUri, e);
                }
            } else if (target instanceof ReferenceBinding) {
                ReferenceBinding referenceBinding = (ReferenceBinding) target;
                try {
                    invoker = referenceBinding.createTargetInvoker(targetWire.getServiceContract(), inboundOperation);
                } catch (TargetInvokerCreationException e) {
                    URI targetName = targetWire.getSourceUri();
                    throw new WireConnectException("Error processing inbound wire",
                        sourceWire.getSourceUri(),
                        targetName,
                        e);
                }
            } else if (target instanceof ServiceBinding) {
                ServiceBinding binding = (ServiceBinding) target;
                try {
                    invoker = binding.createTargetInvoker(targetWire.getServiceContract(), inboundChain.getOperation());
                } catch (TargetInvokerCreationException e) {
                    URI targetName = targetWire.getSourceUri();
                    throw new WireConnectException("Error processing inbound wire",
                        sourceWire.getSourceUri(),
                        targetName,
                        e);
                }
            } else {
                throw new AssertionError();
            }

            if (source instanceof ServiceBinding) {
                // services are a special case: invoker must go on the inbound and outbound chains
                if (target instanceof Component && isOneWayOperation) {
                    // if the target is a component and the operation is non-blocking
                    connect(outboundChain, inboundChain, invoker, true);
                } else {
                    connect(outboundChain, inboundChain, invoker, false);
                }
                ServiceBinding binding = (ServiceBinding) source;
                InboundInvocationChain chain = binding.getInboundWire().getInvocationChains().get(operation);
                chain.setTargetInvoker(invoker);
            } else {
                if (target instanceof Component && isOneWayOperation) {
                    // if the target is a component and the operation is non-blocking
                    connect(outboundChain, inboundChain, invoker, true);
                } else {
                    connect(outboundChain, inboundChain, invoker, false);
                }
            }
        }

        // create source callback chains and connect them if target callback chains exist
        //String sourceName = UriHelper.getBaseName(source.getUri());
        Map<Operation<?>, OutboundInvocationChain> sourceCallbackChains =
            targetWire.getSourceCallbackInvocationChains(source.getUri());
        for (InboundInvocationChain inboundChain : sourceWire.getTargetCallbackInvocationChains().values()) {
            Operation<?> operation = inboundChain.getOperation();
            if (sourceCallbackChains != null && sourceCallbackChains.get(operation) != null) {
                String opName = operation.getName();
                throw new IllegalCallbackException("Source callback chain should not exist for operation",
                    opName,
                    sourceWire.getSourceUri(),
                    targetWire.getSourceUri());
            }

            ServiceContract<?> targetContract = targetWire.getServiceContract();
            assert targetContract != null;
            String opName = operation.getName();
            assert opName != null;
            Operation targetOp = targetContract.getCallbackOperations().get(opName);
            OutboundInvocationChain outboundChain = wireService.createOutboundChain(targetOp);
            targetWire.addSourceCallbackInvocationChain(source.getUri(), targetOp, outboundChain);
            if (source instanceof Component) {
                Component component = (Component) source;
                TargetInvoker invoker;
                try {
                    invoker = component.createTargetInvoker(targetOp.getName(), operation, null);
                } catch (TargetInvokerCreationException e) {
                    URI sourceUri = sourceWire.getSourceUri();
                    URI targetUri = targetWire.getSourceUri();
                    throw new WireConnectException("Error connecting source and target", sourceUri, targetUri, e);
                }
                boolean isOneWayOperation = targetOp.isNonBlocking();
                if (target instanceof Component && isOneWayOperation) {
                    // if the target is a component and the operation is non-blocking
                    connect(outboundChain, inboundChain, invoker, true);
                } else {
                    connect(outboundChain, inboundChain, invoker, false);
                }
            } else if (source instanceof ReferenceBinding) {
                ReferenceBinding binding = (ReferenceBinding) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker;
                try {
                    invoker = binding.createTargetInvoker(sourceContract, operation);
                } catch (TargetInvokerCreationException e) {
                    URI sourceUri = sourceWire.getSourceUri();
                    URI targetUri = targetWire.getSourceUri();
                    throw new WireConnectException("Error connecting source and target", sourceUri, targetUri, e);
                }
                connect(outboundChain, inboundChain, invoker, false);
            } else if (source instanceof ServiceBinding) {
                ServiceBinding binding = (ServiceBinding) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker;
                try {
                    invoker = binding.createTargetInvoker(sourceContract, operation);
                } catch (TargetInvokerCreationException e) {
                    URI targetName = sourceWire.getSourceUri();
                    throw new WireConnectException("Error processing callback wire",
                        sourceWire.getSourceUri(),
                        targetName,
                        e);
                }
                connect(outboundChain, inboundChain, invoker, false);
            } else {
                throw new AssertionError();
            }
        }
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(source, sourceWire, target, targetWire);
        }
        // perform optimization, if possible
        if (optimizable && WireUtils.isOptimizable(sourceWire) && WireUtils.isOptimizable(target, targetWire)) {
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

    protected boolean assertWireable(OutboundWire sourceWire, InboundWire targetWire, boolean silent)
        throws IncompatibleInterfacesException {
        if (wireService == null) {
            Class<?> sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
            Class<?> targetInterface = targetWire.getServiceContract().getInterfaceClass();
            if (!sourceInterface.isAssignableFrom(targetInterface)) {
                if (!silent) {
                    throw new IncompatibleInterfacesException(sourceWire.getSourceUri(), targetWire.getSourceUri());
                } else {
                    return false;
                }
            }
        } else {
            try {
                ServiceContract sourceContract = sourceWire.getServiceContract();
                ServiceContract targetContract = targetWire.getServiceContract();
                wireService.checkCompatibility(sourceContract, targetContract, false, silent);
            } catch (IncompatibleServiceContractException e) {
                URI sourceUri = sourceWire.getSourceUri();
                URI targetUri = targetWire.getSourceUri();
                if (!silent) {
                    throw new IncompatibleInterfacesException(sourceUri, targetUri, e);
                } else {
                    return false;
                }
            }
        }
        return true;
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
        for (ServiceBinding binding : service.getServiceBindings()) {
            InboundWire inboundWire = binding.getInboundWire();
            OutboundWire outboundWire = binding.getOutboundWire();
            URI sourceUri = outboundWire.getSourceUri();
            URI targetUri = outboundWire.getTargetUri();
            String fragment = targetUri.getFragment();
            URI defragUri = UriHelper.getDefragmentedName(targetUri);
            Component targetComponent = componentManager.getComponent(defragUri);
            if (targetComponent == null) {
                throw new TargetComponentNotFoundException("Target not found", sourceUri, targetUri);
            }
            InboundWire targetWire = targetComponent.getTargetWire(fragment);
            if (targetWire == null) {
                throw new TargetServiceNotFoundException("Target not found", sourceUri, targetUri);
            }
            assertWireable(outboundWire, targetWire, false);
            boolean optimizable = isOptimizable(Scope.SYSTEM, targetComponent.getScope());
            connect(binding, outboundWire, targetComponent, targetWire, optimizable);
            connect(binding, inboundWire, targetComponent, outboundWire, true);
        }
    }

    private void handleReference(Reference reference) throws WiringException {
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
                    URI targetName = inboundWire.getSourceUri();
                    throw new WireConnectException("Error processing inbound wire",
                        reference.getUri(),
                        targetName,
                        e);
                }
                chain.setTargetInvoker(invoker);
                chain.prepare();
            }
            OutboundWire outboundWire = binding.getOutboundWire();
            // connect the reference's inbound and outbound wires
            connect(binding, inboundWire, binding, outboundWire, true);
        }
    }

    private void handleComponent(Component component) throws WiringException {
        // connect outbound wires for component references to their targets
        for (List<OutboundWire> referenceWires : component.getOutboundWires().values()) {
            for (OutboundWire outboundWire : referenceWires) {
                if (outboundWire.getTargetUri() == null) {
                    URI source = outboundWire.getSourceUri();
                    URI target = outboundWire.getTargetUri();
                    throw new MissingWireTargetException("Target name was null", source, target);
                }
                URI sourceUri = outboundWire.getSourceUri();
                URI targetUri = outboundWire.getTargetUri();
                String fragment = targetUri.getFragment();
                URI defragUri = UriHelper.getDefragmentedName(targetUri);
                Component targetComponent = componentManager.getComponent(defragUri);
                if (targetComponent == null) {
                    throw new TargetComponentNotFoundException("Target not found", sourceUri, targetUri);
                }
                InboundWire targetWire = null;
                if (fragment == null) {
                    // JFM TODO test
                    // find a suitable wire since no specific service was named
                    for (InboundWire wire : targetComponent.getInboundWires()) {
                        if (assertWireable(outboundWire, wire, true)) {
                            targetWire = wire;
                            break;
                        }
                    }
                    if (targetWire == null) {
                        throw new TargetServiceNotFoundException("Target not found", sourceUri, targetUri);
                    }
                } else {
                    targetWire = targetComponent.getTargetWire(fragment);
                    if (targetWire == null) {
                        throw new TargetServiceNotFoundException("Target not found", sourceUri, targetUri);
                    }
                    assertWireable(outboundWire, targetWire, false);
                }
                boolean optimizable = isOptimizable(component.getScope(), targetComponent.getScope());
                connect(component, outboundWire, targetComponent, targetWire, optimizable);
            }
        }
    }
}
