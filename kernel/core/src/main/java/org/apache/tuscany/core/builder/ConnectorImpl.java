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
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.WirePostProcessorRegistry;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.implementation.composite.CompositeReference;
import org.apache.tuscany.core.implementation.composite.CompositeService;
import org.apache.tuscany.core.wire.BridgingInterceptor;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundAutowire;

/**
 * The default connector implmentation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorImpl implements Connector {

    private WirePostProcessorRegistry postProcessorRegistry;
    private WireService wireService;

    public ConnectorImpl() {
    }

    @Constructor({"wireService", "processorRegistry"})
    public ConnectorImpl(@Autowire WireService wireService, @Autowire WirePostProcessorRegistry processorRegistry) {
        this.postProcessorRegistry = processorRegistry;
        this.wireService = wireService;
    }

    public void connect(SCAObject source) {
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
                        connect(sourceComponent, outboundWire);
                    } catch (BuilderConfigException e) {
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
                    TargetInvoker invoker = sourceComponent.createTargetInvoker(serviceName, operation);
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
        } else if (source instanceof Service) {
            Service service = (Service) source;
            InboundWire inboundWire = service.getInboundWire();
            OutboundWire outboundWire = service.getOutboundWire();
            // connect the outbound service wire to the target
            connect(service, outboundWire);
            // NB: this connect must be done after the outbound service chain is connected to its target above
            if (!(source instanceof CompositeService)) {
                //REVIEW JFM: do we need this to be special for composites?
                connect(inboundWire, outboundWire, true);
            }
        }
    }

    public void connect(InboundWire sourceWire, OutboundWire targetWire, boolean optimizable)
        throws BuilderConfigException {
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(sourceWire, targetWire);
        }
        Map<Operation<?>, OutboundInvocationChain> targetChains = targetWire.getInvocationChains();
        // perform optimization, if possible
        if (optimizable && sourceWire.getInvocationChains().isEmpty() && targetChains.isEmpty()) {
            sourceWire.setTargetWire(targetWire);
            return;
        }
        for (InboundInvocationChain inboundChain : sourceWire.getInvocationChains().values()) {
            // match wire chains
            OutboundInvocationChain outboundChain = targetChains.get(inboundChain.getOperation());
            if (outboundChain == null) {
                BuilderConfigException e = new BuilderConfigException("Incompatible source and target interfaces");
                e.setIdentifier(sourceWire.getServiceName());
                throw e;
            }
            connect(inboundChain, outboundChain);
        }
    }

    /**
     * Connects the source outbound wire to a corresponding target inbound wire
     *
     * @param sourceWire  the source wire to connect
     * @param targetWire  the target wire to connect to
     * @param optimizable true if the wire connection can be optimized
     */
    void connect(OutboundWire sourceWire, InboundWire targetWire, boolean optimizable) {
        SCAObject source = sourceWire.getContainer();
        SCAObject target = targetWire.getContainer();
        ServiceContract contract = sourceWire.getServiceContract();
        if (postProcessorRegistry != null) {
            // run wire post-processors
            postProcessorRegistry.process(sourceWire, targetWire);
        }
        Map<Operation<?>, InboundInvocationChain> targetChains = targetWire.getInvocationChains();
        // perform optimization, if possible
        // REVIEW: (kentaminator@gmail.com) shouldn't this check whether the interceptors in the
        // source & target chains are marked as optimizable?  (and if so, optimize them away?)
        if (optimizable && sourceWire.getInvocationChains().isEmpty() && targetChains.isEmpty()) {
            sourceWire.setTargetWire(targetWire);
            return;
        }
        // match outbound to inbound chains
        for (OutboundInvocationChain outboundChain : sourceWire.getInvocationChains().values()) {
            Operation<?> operation = outboundChain.getOperation();
            InboundInvocationChain inboundChain = targetChains.get(operation);
            if (inboundChain == null) {
                BuilderConfigException e =
                    new BuilderConfigException("Incompatible source and target interfaces for reference");
                e.setIdentifier(sourceWire.getReferenceName());
                throw e;
            }
            boolean isOneWayOperation = operation.isNonBlocking();
            boolean operationHasCallback = contract.getCallbackName() != null;
            if (isOneWayOperation && operationHasCallback) {
                throw new ComponentRuntimeException("Operation cannot be marked one-way and have a callback");
            }
            TargetInvoker invoker = null;
            if (target instanceof Component) {
                Component component = (Component) target;
                if (isOneWayOperation || operationHasCallback) {
                    invoker = component.createAsyncTargetInvoker(targetWire, operation);
                } else {
                    Operation<?> inboundOperation = inboundChain.getOperation();
                    String portName = sourceWire.getTargetName().getPortName();
                    invoker = component.createTargetInvoker(portName, inboundOperation);
                }
            } else if (target instanceof Reference) {
                Reference reference = (Reference) target;
                if (!(reference instanceof CompositeReference) && operationHasCallback) {
                    // Notice that for bound references we only use async target invokers for callback operations
                    invoker = reference.createAsyncTargetInvoker(sourceWire, operation);
                } else {
                    ServiceContract targetContract = targetWire.getServiceContract();
                    Operation targetOperation = inboundChain.getOperation();
                    invoker = reference.createTargetInvoker(targetContract, targetOperation);
                }
            } else if (target instanceof CompositeService) {
                CompositeService compServ = (CompositeService) target;
                invoker = compServ.createTargetInvoker(targetWire.getServiceContract(), inboundChain.getOperation());
            }

            if (source instanceof Service && !(source instanceof CompositeService)) {
                // services are a special case: invoker must go on the inbound chain
                connect(outboundChain, inboundChain, null);
                Service service = (Service) source;
                InboundInvocationChain chain = service.getInboundWire().getInvocationChains().get(operation);
                chain.setTargetInvoker(invoker);
            } else {
                connect(outboundChain, inboundChain, invoker);
            }
        }

        // create source callback chains and connect them if target callback chains exist
        Map<Operation<?>, OutboundInvocationChain> sourceCallbackChains =
            targetWire.getSourceCallbackInvocationChains(source.getName());
        for (InboundInvocationChain inboundChain : sourceWire.getTargetCallbackInvocationChains().values()) {
            Operation<?> operation = inboundChain.getOperation();
            if (sourceCallbackChains != null && sourceCallbackChains.get(operation) != null) {
                String name = operation.getName();
                BuilderConfigException e =
                    new BuilderConfigException("Source callback chain should not exist for operation [" + name + "]");
                e.setIdentifier(sourceWire.getReferenceName());
                throw e;
            }
            OutboundInvocationChain outboundChain = wireService.createOutboundChain(operation);
            targetWire.addSourceCallbackInvocationChain(source.getName(), operation, outboundChain);
            if (source instanceof Component) {
                Component component = (Component) source;
                TargetInvoker invoker = component.createTargetInvoker(null, operation);
                connect(outboundChain, inboundChain, invoker);
            } else if (source instanceof CompositeReference) {
                CompositeReference compRef = (CompositeReference) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker = compRef.createCallbackTargetInvoker(sourceContract, operation);
                connect(outboundChain, inboundChain, invoker);
            } else if (source instanceof Service) {
                Service service = (Service) source;
                ServiceContract sourceContract = sourceWire.getServiceContract();
                TargetInvoker invoker = service.createCallbackTargetInvoker(sourceContract, operation);
                connect(outboundChain, inboundChain, invoker);
            }
        }
    }

    /**
     * Connects a source to target chain
     *
     * @param sourceChain the source chain
     * @param targetChain the target chain
     * @param invoker     the invoker to place on the source chain for dispatching invocations
     */
    void connect(OutboundInvocationChain sourceChain, InboundInvocationChain targetChain, TargetInvoker invoker) {
        Interceptor headInterceptor = targetChain.getHeadInterceptor();
        if (headInterceptor == null) {
            BuilderConfigException e = new BuilderConfigException("No interceptor for operation");
            e.setIdentifier(targetChain.getOperation().getName());
            throw e;
        }
        if (!(sourceChain.getTailInterceptor() instanceof InvokerInterceptor
            && headInterceptor instanceof InvokerInterceptor)) {
            // check that we do not have the case where the only interceptors are invokers since we just need one
            sourceChain.setTargetInterceptor(headInterceptor);
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
        // the are always interceptors so the connection algorithm is simple
        sourceChain.addInterceptor(new BridgingInterceptor(targetChain.getHeadInterceptor()));
    }

    /**
     * Connects an component's outbound wire to its target in a composite.  Valid targets are either
     * <code>AtomicComponent</code>s contained in the composite, or <code>References</code> of the composite.
     *
     * @param sourceWire
     * @throws BuilderConfigException
     */
    private void connect(SCAObject source, OutboundWire sourceWire) throws BuilderConfigException {
        assert sourceWire.getTargetName() != null : "Wire target name was null";
        QualifiedName targetName = sourceWire.getTargetName();
        CompositeComponent parent = source.getParent();
        assert parent != null : "Parent was null";
        // For a composite reference only, since its outbound wire comes from its parent composite,
        // the corresponding target would not lie in its parent but rather in its parent's parent
        if (source instanceof CompositeReference) {
            parent = parent.getParent();
            assert parent != null : "Parent of parent was null";
        }
        SCAObject target = parent.getChild(targetName.getPartName());
        if (target == null) {
            String refName = sourceWire.getReferenceName();
            BuilderConfigException e = new BuilderConfigException("Target not found for reference " + refName);
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }

        if (target instanceof AtomicComponent) {
            AtomicComponent targetComponent = (AtomicComponent) target;
            InboundWire targetWire = targetComponent.getInboundWire(targetName.getPortName());
            if (targetWire == null) {
                String refName = sourceWire.getReferenceName();
                BuilderConfigException e = new BuilderConfigException("No target service for reference " + refName);
                e.setIdentifier(targetName.getPortName());
                throw e;
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
            if (targetWire == null) {
                throw new BuilderConfigException("No target composite service in composite");
            }
            boolean optimizable = isOptimizable(source.getScope(), target.getScope());
            connect(sourceWire, targetWire, optimizable);
        } else {
            String name = sourceWire.getReferenceName();
            BuilderConfigException e = new BuilderConfigException("Invalid target type for reference " + name);
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }
    }

    private void checkIfWireable(OutboundWire sourceWire, InboundWire targetWire) {
        if (wireService == null) {
            Class<?> sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
            Class<?> targetInterface = targetWire.getServiceContract().getInterfaceClass();
            if (!sourceInterface.isAssignableFrom(targetInterface)) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
        } else if (!wireService.isWireable(sourceWire.getServiceContract(), targetWire.getServiceContract())) {
            throw new BuilderConfigException("Incompatible source and target interfaces");
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
