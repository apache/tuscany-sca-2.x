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

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
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
    private Class<?>[] callbackInterfaces;
    private Map<Operation<?>, OutboundInvocationChain> chains = new HashMap<Operation<?>, OutboundInvocationChain>();
    private Map<Operation<?>, InboundInvocationChain> callbackTargetChains =
        new HashMap<Operation<?>, InboundInvocationChain>();
    private String referenceName;
    private QualifiedName targetName;
    private InboundWire targetWire;
    private SCAObject container;
    private boolean autowire;

    public QName getBindingType() {
        return bindingType;
    }

    public void setBindingType(QName bindingType) {
        this.bindingType = bindingType;
    }

    public Object getTargetService() throws TargetResolutionException {
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

    public void addInterface(Class<?> claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public void setCallbackInterface(Class<?> interfaze) {
        callbackInterfaces = new Class[]{interfaze};
    }

    public Class<?> getCallbackInterface() {
        return callbackInterfaces[0];
    }

    public void addCallbackInterface(Class<?> claz) {
        throw new UnsupportedOperationException("Additional callback interfaces not yet supported");
    }

    public Class[] getImplementedCallbackInterfaces() {
        return callbackInterfaces;
    }

    public void setTargetWire(InboundWire wire) {
        this.targetWire = wire;
    }

    public Map<Operation<?>, OutboundInvocationChain> getInvocationChains() {
        return chains;
    }

    public void addInvocationChains(Map<Operation<?>, OutboundInvocationChain> chains) {
        this.chains.putAll(chains);
    }

    public void addInvocationChain(Operation<?> operation, OutboundInvocationChain chain) {
        chains.put(operation, chain);
    }

    public Map<Operation<?>, InboundInvocationChain> getTargetCallbackInvocationChains() {
        return callbackTargetChains;
    }

    public void addTargetCallbackInvocationChains(Map<Operation<?>, InboundInvocationChain> chains) {
        callbackTargetChains.putAll(chains);
    }

    public void addTargetCallbackInvocationChain(Operation operation, InboundInvocationChain chain) {
        callbackTargetChains.put(operation, chain);
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return targetName;
    }

    public void setTargetName(QualifiedName targetName) {
        this.targetName = targetName;
    }

    public boolean isAutowire() {
        return autowire;
    }

    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
    }

    public boolean isOptimizable() {
        for (OutboundInvocationChain chain : chains.values()) {
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null && current != chain.getTargetInterceptor()) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
        }

        for (InboundInvocationChain chain : callbackTargetChains.values()) {
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
}
