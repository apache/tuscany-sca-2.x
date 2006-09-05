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

import java.util.ArrayList;
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
import org.apache.tuscany.spi.wire.MessageChannel;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.wire.BridgingInterceptor;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.MessageChannelImpl;
import org.apache.tuscany.core.wire.MessageDispatcher;
import org.apache.tuscany.core.wire.OutboundAutowire;

/**
 * The default connector implmentation
 *
 * @version $$Rev$$ $$Date$$
 */
public class ConnectorImpl implements Connector {

    private WirePostProcessorRegistry postProcessorRegistry;

    public ConnectorImpl() {
    }

    @Constructor
    public ConnectorImpl(@Autowire WirePostProcessorRegistry postProcessorRegistry) {
        this.postProcessorRegistry = postProcessorRegistry;
    }

    public <T> void connect(SCAObject<T> source) {
        CompositeComponent parent = source.getParent();
        if (source instanceof AtomicComponent) {
            AtomicComponent<T> sourceComponent = (AtomicComponent<T>) source;
            for (List<OutboundWire> referenceWires : sourceComponent.getOutboundWires().values()) {
                for (OutboundWire<T> outboundWire : referenceWires) {
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
            for (InboundWire<T> inboundWire : sourceComponent.getInboundWires().values()) {
                for (InboundInvocationChain chain : inboundWire.getInvocationChains().values()) {
                    Operation<?> operation = chain.getOperation();
                    TargetInvoker invoker = sourceComponent.createTargetInvoker(null, operation);
                    chain.setTargetInvoker(invoker);
                    chain.prepare();
                }
            }
        } else if (source instanceof Reference) {
            Reference<?> reference = (Reference) source;
            InboundWire<?> wire = reference.getInboundWire();
            Map<Operation<?>, InboundInvocationChain> chains = wire.getInvocationChains();
            // for references, no need to have an outbound wire
            for (InboundInvocationChain chain : chains.values()) {
                //TODO handle async
                TargetInvoker invoker = reference.createTargetInvoker(wire.getServiceContract(), chain.getOperation());
                chain.setTargetInvoker(invoker);
                chain.prepare();
            }
        } else if (source instanceof Service) {
            Service<T> service = (Service<T>) source;
            InboundWire<T> inboundWire = service.getInboundWire();
            OutboundWire<T> outboundWire = service.getOutboundWire();
            // connect the outbound service wire to the target
            connect(service, outboundWire);
            // services have inbound and outbound wires
            // NB: this connect must be done after the outbound service chain is connected to its target above
            connect(inboundWire, outboundWire, true);
        }
    }

    /**
     * Connects an inbound wire to a specific outbound wire
     *
     * @param sourceWire  the inbound wire to connect
     * @param targetWire  the outbound wire to connect to
     * @param optimizable true if the connection can be ooptimized
     * @throws BuilderConfigException
     */
    public <T> void connect(InboundWire<T> sourceWire,
                            OutboundWire<T> targetWire,
                            boolean optimizable) throws BuilderConfigException {
        if (postProcessorRegistry != null){
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

    public <T> void connect(SCAObject<?> source,
                            SCAObject<?> target,
                            OutboundWire<T> sourceWire,
                            InboundWire<T> targetWire,
                            boolean optimizable) {
        if (postProcessorRegistry != null){
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
        ServiceContract contract = sourceWire.getServiceContract();
        for (OutboundInvocationChain outboundChain : sourceWire.getInvocationChains().values()) {
            // match wire chains
            Operation<?> operation = outboundChain.getOperation();
            InboundInvocationChain inboundChain = targetChains.get(operation);
            if (inboundChain == null) {
                BuilderConfigException e =
                    new BuilderConfigException("Incompatible source and target chain interfaces for reference");
                e.setIdentifier(sourceWire.getReferenceName());
                throw e;
            }
            TargetInvoker invoker = null;
            if (target instanceof Component) {
                Component component = (Component) target;

                // FIXME should not relay on annotations
                boolean isOneWayOperation = operation.isNonBlocking();
                boolean operationHasCallback = contract.getCallbackName() != null;
                if (isOneWayOperation && operationHasCallback) {
                    throw new ComponentRuntimeException("Operation cannot be marked one-way and have a callback");
                }
                if (isOneWayOperation || operationHasCallback) {
                    invoker = component.createAsyncTargetInvoker(sourceWire, operation);
                } else {
                    Operation<?> inboundOperation = inboundChain.getOperation();
                    invoker = component.createTargetInvoker(null, inboundOperation);
                }
            } else if (target instanceof Reference) {
                Reference reference = (Reference) target;
                invoker = reference.createTargetInvoker(targetWire.getServiceContract(), inboundChain.getOperation());
            }
            if (source instanceof Service) {
                // services are a special case: invoker must go on the inbound chain
                connect(outboundChain, inboundChain, null);
                Service<?> service = (Service) source;
                InboundInvocationChain chain = service.getInboundWire().getInvocationChains().get(operation);
                chain.setTargetInvoker(invoker);
            } else {
                connect(outboundChain, inboundChain, invoker);
            }
        }

        // connect callback wires if they exist
        for (OutboundInvocationChain outboundChain : sourceWire.getSourceCallbackInvocationChains().values()) {
            // match wire chains
            Map<Operation<?>, InboundInvocationChain> chains = sourceWire.getTargetCallbackInvocationChains();
            InboundInvocationChain inboundChain = chains.get(outboundChain.getOperation());
            if (inboundChain == null) {
                BuilderConfigException e =
                    new BuilderConfigException("Incompatible source and target interfaces for reference");
                e.setIdentifier(sourceWire.getReferenceName());
                throw e;
            }
            if (source instanceof Component) {
                Component component = (Component) source;
                Operation<?> operation = outboundChain.getOperation();
                boolean isOneWayOperation = operation.isNonBlocking();
                boolean operationHasCallback = contract.getCallbackName() != null;
                if (isOneWayOperation && operationHasCallback) {
                    throw new ComponentRuntimeException("Operation cannot be marked one-way and have a callback");
                }
                TargetInvoker invoker;
                if (isOneWayOperation || operationHasCallback) {
                    invoker = component.createAsyncTargetInvoker(sourceWire, operation);
                } else {
                    Operation<?> inboundOperation = inboundChain.getOperation();
                    invoker = component.createTargetInvoker(null, inboundOperation);
                }
                connect(outboundChain, inboundChain, invoker);
            } else if (target instanceof Service) {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void connect(OutboundInvocationChain sourceChain,
                        InboundInvocationChain targetChain,
                        TargetInvoker invoker) {
        // if handlers are configured, add them
        if (targetChain.getRequestHandlers() != null || targetChain.getResponseHandlers() != null) {
            if (targetChain.getRequestHandlers() == null) {
                // the target may not have request handlers, so bridge it on the source
                if (targetChain.getHeadInterceptor() != null) {
                    List<MessageHandler> handlers = new ArrayList<MessageHandler>();
                    handlers.add(new MessageDispatcher(targetChain.getHeadInterceptor()));
                    MessageChannel channel = new MessageChannelImpl(handlers);
                    sourceChain.setTargetRequestChannel(channel);
                } else {
                    BuilderConfigException e = new BuilderConfigException("Service chain must have an interceptor");
                    e.setIdentifier(targetChain.getOperation().getName());
                    throw e;
                }
            } else {
                sourceChain.setTargetRequestChannel(new MessageChannelImpl(targetChain.getRequestHandlers()));
            }
            sourceChain.setTargetResponseChannel(new MessageChannelImpl(targetChain.getResponseHandlers()));
        } else {
            // no handlers, just connect interceptors
            if (targetChain.getHeadInterceptor() == null) {
                BuilderConfigException e = new BuilderConfigException("No chain handler or interceptor for operation");
                e.setIdentifier(targetChain.getOperation().getName());
                throw e;
            }
            if (!(sourceChain.getTailInterceptor() instanceof InvokerInterceptor && targetChain
                .getHeadInterceptor() instanceof InvokerInterceptor)) {
                // check that we do not have the case where the only interceptors are invokers since we just need one
                sourceChain.setTargetInterceptor(targetChain.getHeadInterceptor());
            }
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
    public void connect(InboundInvocationChain sourceChain, OutboundInvocationChain targetChain) {
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
    @SuppressWarnings("unchecked")
    private <T> void connect(SCAObject<T> source,
                             OutboundWire<T> sourceWire) throws BuilderConfigException {
        assert sourceWire.getTargetName() != null : "Wire target name was null";
        QualifiedName targetName = sourceWire.getTargetName();
        CompositeComponent<?> parent = source.getParent();
        SCAObject<?> target = parent.getChild(targetName.getPartName());
        if (target == null) {
            String refName = sourceWire.getReferenceName();
            BuilderConfigException e = new BuilderConfigException("Target not found for reference " + refName);
            e.setIdentifier(targetName.getQualifiedName());
            throw e;
        }

        if (target instanceof AtomicComponent) {
            AtomicComponent<?> targetComponent = (AtomicComponent<?>) target;
            InboundWire<T> targetWire = targetComponent.getInboundWire(targetName.getPortName());
            if (targetWire == null) {
                String refName = sourceWire.getReferenceName();
                BuilderConfigException e = new BuilderConfigException("No target service for reference " + refName);
                e.setIdentifier(targetName.getPortName());
                throw e;
            }
            Class sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
            Class targetInterface = targetWire.getServiceContract().getInterfaceClass();
            if (!sourceInterface.isAssignableFrom(targetInterface)) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            boolean optimizable = isOptimizable(source.getScope(), target.getScope());
            connect(source, target, sourceWire, targetWire, optimizable);
        } else if (target instanceof Reference) {
            InboundWire<T> targetWire = ((Reference) target).getInboundWire();
            assert targetWire != null;
            Class sourceInterface = sourceWire.getServiceContract().getInterfaceClass();
            Class targetInterface = targetWire.getServiceContract().getInterfaceClass();
            if (!sourceInterface.isAssignableFrom(targetInterface)) {
                throw new BuilderConfigException("Incompatible source and target interfaces");
            }
            boolean optimizable = isOptimizable(source.getScope(), target.getScope());
            connect(source, target, sourceWire, targetWire, optimizable);
        } else {
            String name = sourceWire.getReferenceName();
            BuilderConfigException e = new BuilderConfigException("Invalid target type for reference " + name);
            e.setIdentifier(targetName.getQualifiedName());
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
