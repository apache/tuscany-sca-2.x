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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Default implementation of an outbound wire
 *
 * @version $Rev$ $Date$
 */
public class OutboundWireImpl implements OutboundWire {
    private QName bindingType = LOCAL_BINDING;
    private ServiceContract serviceContract;
    private Map<Operation<?>, OutboundInvocationChain> chains = new HashMap<Operation<?>, OutboundInvocationChain>();
    private Map<Operation<?>, InboundInvocationChain> callbackTargetChains =
        new HashMap<Operation<?>, InboundInvocationChain>();
    private URI uri;
    private URI target;
    private InboundWire targetWire;
    private boolean optimizable;

    /**
     * Creates a local outbound wire
     */
    public OutboundWireImpl() {
    }

    /**
     * Creates an outbound wire for the given binding type
     *
     * @param bindingType the binding type
     */
    public OutboundWireImpl(QName bindingType) {
        this.bindingType = bindingType;
    }

    public void setOptimizable(boolean optimizable) {
        this.optimizable = optimizable;
    }

    public QName getBindingType() {
        return bindingType;
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public URI getSourceUri() {
        return uri;
    }

    public void setSourceUri(URI referenceUri) {
        this.uri = referenceUri;
    }

    public URI getTargetUri() {
        return target;
    }

    public void setTargetUri(URI target) {
        this.target = target;
    }

    public boolean isOptimizable() {
        return optimizable;
    }

    public Object getTargetService() throws TargetResolutionException {
        if (targetWire == null) {
            return null;
        }
        // optimized, no interceptors or handlers on either end
        return targetWire.getTargetService();
    }

    public void setTargetWire(InboundWire wire) {
        this.targetWire = wire;
    }

    public Map<Operation<?>, OutboundInvocationChain> getInvocationChains() {
        return chains;
    }

    public void addInvocationChain(Operation<?> operation, OutboundInvocationChain chain) {
        chains.put(operation, chain);
    }

    public Map<Operation<?>, InboundInvocationChain> getTargetCallbackInvocationChains() {
        return callbackTargetChains;
    }

    public void addTargetCallbackInvocationChain(Operation operation, InboundInvocationChain chain) {
        callbackTargetChains.put(operation, chain);
    }

}
