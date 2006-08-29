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
package org.apache.tuscany.core.implementation.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.wire.OutboundAutowire;

/**
 * A specialization of <code>OutboundAutowire</code> that returns a direct reference to the target
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemOutboundAutowire<T> implements OutboundAutowire<T>, SystemOutboundWire<T> {
    private String referenceName;
    private ServiceContract serviceContract;
    private AutowireComponent<?> component;

    public SystemOutboundAutowire(String referenceName, Class<T> businessInterface, AutowireComponent<?> component) {
        this.referenceName = referenceName;
        this.component = component;
        serviceContract = new JavaServiceContract(businessInterface);
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return null;
    }

    public void setTargetName(QualifiedName targetName) {
    }

    public T getTargetService() throws TargetException {
        Class interfaze = serviceContract.getInterfaceClass();
        T service = (T) component.resolveInstance(interfaze);
        if (service == null) {
            TargetNotFoundException e = new TargetNotFoundException("Autowire target not found");
            e.setIdentifier(interfaze.getName());
            throw e;
        }
        return service;
    }

    @SuppressWarnings("unchecked")
    public void setCallbackInterface(Class<T> interfaze) {
        throw new UnsupportedOperationException();
    }

    public Class<T> getCallbackInterface() {
        throw new UnsupportedOperationException();
    }

    public void addCallbackInterface(Class<?> claz) {
        throw new UnsupportedOperationException();
    }

    public Class[] getImplementedCallbackInterfaces() {
        throw new UnsupportedOperationException();
    }

    public void setTargetWire(InboundWire<T> wire) {
        throw new UnsupportedOperationException();
    }

    public Map<Method, OutboundInvocationChain> getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, OutboundInvocationChain chains) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public Map<Method, InboundInvocationChain> getTargetCallbackInvocationChains() {
        throw new UnsupportedOperationException();
    }

    public void addTargetCallbackInvocationChains(Map<Method, InboundInvocationChain> chains) {
        throw new UnsupportedOperationException();
    }

    public void addTargetCallbackInvocationChain(Method method, InboundInvocationChain chain) {
        throw new UnsupportedOperationException();
    }

    public Map<Method, OutboundInvocationChain> getSourceCallbackInvocationChains() {
        return null;
    }

    public void addSourceCallbackInvocationChains(Map<Method, OutboundInvocationChain> chains) {

    }

    public void addSourceCallbackInvocationChain(Method method, OutboundInvocationChain chain) {

    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

    public boolean isOptimizable() {
        return true;
    }
}
