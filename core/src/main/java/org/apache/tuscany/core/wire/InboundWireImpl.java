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

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.MessageHandler;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.util.MethodHashMap;

/**
 * Default implementation of an inbound wire
 *
 * @version $Rev$ $Date$
 */
public class InboundWireImpl<T> implements InboundWire<T> {

    private String serviceName;
    private Class[] businessInterfaces;
    private OutboundWire<T> targetWire;
    private String callbackReferenceName;
    private Map<Method, InboundInvocationChain> chains = new MethodHashMap<InboundInvocationChain>();

    @SuppressWarnings("unchecked")
    public T getTargetService() throws TargetException {
        if (targetWire != null) {
            // optimized, no interceptors or handlers on either end
            return targetWire.getTargetService();
        }
        throw new TargetException("Target wire not optimized");
    }

    public void setBusinessInterface(Class interfaze) {
        businessInterfaces = new Class[]{interfaze};
    }

    @SuppressWarnings("unchecked")
    public Class<T> getBusinessInterface() {
        return businessInterfaces[0];
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    public Class[] getImplementedInterfaces() {
        return businessInterfaces;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<Method, InboundInvocationChain> getInvocationChains() {
        return chains;
    }

    public void addInvocationChains(Map<Method, InboundInvocationChain> chains) {
        this.chains.putAll(chains);
    }

    public void addInvocationChain(Method method, InboundInvocationChain chain) {
        chains.put(method, chain);
    }

    public void setTargetWire(OutboundWire<T> wire) {
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
            if (chain.getRequestHandlers() != null && !chain.getRequestHandlers().isEmpty()) {
                for (MessageHandler handler : chain.getRequestHandlers()) {
                    if (!handler.isOptimizable()) {
                        return false;
                    }
                }
            }
            if (chain.getResponseHandlers() != null && !chain.getResponseHandlers().isEmpty()) {
                for (MessageHandler handler : chain.getResponseHandlers()) {
                    if (!handler.isOptimizable()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
