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
package org.apache.tuscany.sca.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.HashMap;

import org.apache.tuscany.sca.core.invocation.SCAProxy;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 * 
 * @version $Rev$ $Date$
 */
public class JDKProxyFactory implements ProxyFactory {
    protected InterfaceContractMapper contractMapper;
    private MessageFactory messageFactory;

    public JDKProxyFactory(MessageFactory messageFactory, InterfaceContractMapper mapper) {
        this.contractMapper = mapper;
        this.messageFactory = messageFactory;
    }

    /** 
     * The original createProxy method assumes that the proxy doesn't want to 
     * share conversation state so sets the conversation object to null
     */
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire) throws ProxyCreationException {
        ServiceReference<T> serviceReference = new ServiceReferenceImpl(interfaze, wire, this);
        return createProxy(serviceReference);
    }
    
    public <T> T createProxy(CallableReference<T> callableReference) throws ProxyCreationException {
        assert callableReference != null;
        final Class<T> interfaze = callableReference.getBusinessInterface();
        InvocationHandler handler = new JDKInvocationHandler(messageFactory, callableReference);
        // Allow privileged access to class loader. Requires RuntimePermission in security policy.
        ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               return interfaze.getClassLoader();
            }
        });
        Object proxy = SCAProxy.newProxyInstance(cl, new Class[] {interfaze}, handler);
        ((CallableReferenceImpl)callableReference).setProxy(proxy);
        return interfaze.cast(proxy);
    }

    public <T> T createCallbackProxy(Class<T> interfaze, List<RuntimeWire> wires) throws ProxyCreationException {
        CallbackReferenceImpl<T> callbackReference = CallbackReferenceImpl.newInstance(interfaze, this, wires);
        return callbackReference != null ? createCallbackProxy(callbackReference) : null;
    }

    public <T> T createCallbackProxy(CallbackReferenceImpl<T> callbackReference) throws ProxyCreationException {
        assert callbackReference != null;
        Class<T> interfaze = callbackReference.getBusinessInterface();
        InvocationHandler handler = new JDKCallbackInvocationHandler(messageFactory, callbackReference);
        ClassLoader cl = interfaze.getClassLoader();
		Object proxy = SCAProxy.newProxyInstance(cl, new Class[] {interfaze}, handler);
		callbackReference.setProxy(proxy);
        return interfaze.cast(proxy);
    }

    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        InvocationHandler handler = SCAProxy.getInvocationHandler(target);
        if (handler instanceof JDKInvocationHandler) {
            return (R)((JDKInvocationHandler)handler).getCallableReference();
        } else {
            throw new IllegalArgumentException("The object is not a known proxy.");
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#isProxyClass(java.lang.Class)
     */
    public boolean isProxyClass(Class<?> clazz) {
        return SCAProxy.isProxyClass(clazz);
    }
}
