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
package org.apache.tuscany.core.wire;

import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Default implementation of an inbound wire
 *
 * @version $Rev$ $Date$
 */
public class InboundWireImpl implements InboundWire {
    private QName bindingType = LOCAL_BINDING;
    private URI uri;
    private ServiceContract serviceContract;
    private OutboundWire targetWire;
    private String callbackReferenceName;
    private Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
    private Map<URI, Map<Operation<?>, OutboundInvocationChain>> callbackSourceChainMaps =
        new HashMap<URI, Map<Operation<?>, OutboundInvocationChain>>();
    private SCAObject container;
    private AtomicComponent targetComponent;
    private boolean optimizable;

    /**
     * Creates a local inbound wire
     */
    public InboundWireImpl() {
    }

    /**
     * Creates an inbound wire for the given binding type
     *
     * @param bindingType the binding type
     */
    public InboundWireImpl(QName bindingType) {
        this.bindingType = bindingType;
    }

    public QName getBindingType() {
        return bindingType;
    }

    public Object getTargetService() throws TargetResolutionException {
        // JFM fixme hack
        if (targetWire == null && targetComponent != null) {
            return targetComponent.getTargetInstance();
        }
        assert targetWire != null;
        // optimized, no interceptors or handlers on either end
        return targetWire.getTargetService();
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI serviceName) {
        this.uri = serviceName;
    }

    public Map<Operation<?>, InboundInvocationChain> getInvocationChains() {
        return chains;
    }

    public void addInvocationChains(Map<Operation<?>, InboundInvocationChain> chains) {
        this.chains.putAll(chains);
    }

    public void addInvocationChain(Operation<?> operation, InboundInvocationChain chain) {
        chains.put(operation, chain);
    }

    public Map<Operation<?>, OutboundInvocationChain> getSourceCallbackInvocationChains(URI targetAddr) {
        return callbackSourceChainMaps.get(targetAddr);
    }

    public void addSourceCallbackInvocationChains(URI targetAddr,
                                                  Map<Operation<?>, OutboundInvocationChain> chains) {
        callbackSourceChainMaps.put(targetAddr, chains);
    }

    public void addSourceCallbackInvocationChain(URI targetAddr,
                                                 Operation operation,
                                                 OutboundInvocationChain chain) {
        Map<Operation<?>, OutboundInvocationChain> chains = callbackSourceChainMaps.get(targetAddr);
        if (chains == null) {
            chains = new HashMap<Operation<?>, OutboundInvocationChain>();
            callbackSourceChainMaps.put(targetAddr, chains);
        }
        chains.put(operation, chain);
    }

    public void setTargetWire(OutboundWire wire) {
        targetWire = wire;
    }

    public String getCallbackReferenceName() {
        return callbackReferenceName;
    }

    public void setCallbackReferenceName(String callbackReferenceName) {
        this.callbackReferenceName = callbackReferenceName;
    }

    public boolean isOptimizable() {
        return optimizable;
    }

    public void setOptimizable(boolean optimizable) {
        this.optimizable = optimizable;
    }

    public SCAObject getContainer() {
        return container;
    }

    public void setContainer(SCAObject container) {
        if (container instanceof AtomicComponent) {
            targetComponent = (AtomicComponent) container;
        }
        this.container = container;
    }
}
