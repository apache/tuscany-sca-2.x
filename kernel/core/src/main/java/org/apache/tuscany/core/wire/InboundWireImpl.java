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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Default implementation of an inbound wire
 *
 * @version $Rev$ $Date$
 */
public class InboundWireImpl implements InboundWire {

    private String serviceName;
    private ServiceContract serviceContract;
    private OutboundWire targetWire;
    private String callbackReferenceName;
    private Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
    private Map<Object, Map<Operation<?>, OutboundInvocationChain>> callbackSourceChainMaps =
        new HashMap<Object, Map<Operation<?>, OutboundInvocationChain>>();
    private SCAObject container;
    private Map<Object, Object> msgIdsToAddrs = new ConcurrentHashMap<Object, Object>();

    public Object getTargetService() throws TargetException {
        if (targetWire != null) {
            // optimized, no interceptors or handlers on either end
            return targetWire.getTargetService();
        }
        throw new TargetException("Target wire not optimized");
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public Map<Operation<?>, OutboundInvocationChain> getSourceCallbackInvocationChains(Object targetAddr) {
        return callbackSourceChainMaps.get(targetAddr);
    }

    public void addSourceCallbackInvocationChains(Object targetAddr,
                                                  Map<Operation<?>, OutboundInvocationChain> chains) {
        callbackSourceChainMaps.put(targetAddr, chains);
    }

    public void addSourceCallbackInvocationChain(Object targetAddr, Operation operation,
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
        for (InboundInvocationChain chain : chains.values()) {
            if (chain.getTargetInvoker() != null && !chain.getTargetInvoker().isOptimizable()) {
                return false;
            }
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
        }
        return true;
    }

    public SCAObject getContainer() {
        return container;
    }

    public void setContainer(SCAObject container) {
        this.container = container;
    }

    public void addMapping(Object messageId, Object fromAddress) {
        this.msgIdsToAddrs.put(messageId, fromAddress);
    }

    public Object retrieveMapping(Object messageId) {
        return this.msgIdsToAddrs.get(messageId);
    }

    public void removeMapping(Object messageId) {
        this.msgIdsToAddrs.remove(messageId);
    }
}
