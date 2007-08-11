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

package org.apache.tuscany.sca.implementation.java.invocation;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.tuscany.sca.core.invocation.JDKCallbackInvocationHandler;
import org.apache.tuscany.sca.core.invocation.JDKInvocationHandler;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.core.invocation.ProxyCreationException;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;

/**
 * The implementation of a wire service that uses cglib dynamic proxies
 * 
 * @version $Rev$ $Date$
 */
@SuppressWarnings("unused")
public class CglibProxyFactory implements ProxyFactory {
    private InterfaceContractMapper contractMapper;
    private MessageFactory messageFactory;

    public CglibProxyFactory() {
        this(new MessageFactoryImpl(), new InterfaceContractMapperImpl());
    }

    public CglibProxyFactory(MessageFactory messageFactory, InterfaceContractMapper mapper) {
        this.contractMapper = mapper;
        this.messageFactory = messageFactory;

    }

    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire) throws ProxyCreationException {
        return createProxy(interfaze, wire, null, null);
    }

    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire, Conversation conversation) throws ProxyCreationException {
        return createProxy(interfaze, wire, conversation, null);
    }
    
    /**
     * create the proxy with cglib. use the same JDKInvocationHandler as
     * JDKProxyService.
     */
    public <T> T createProxy(final Class<T> interfaze, final RuntimeWire wire, final Conversation conversation,
                             final EndpointReference endpoint) throws ProxyCreationException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaze);
        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
                throws Throwable {
                JDKInvocationHandler invocationHandler = new JDKInvocationHandler(messageFactory, interfaze, wire);
                invocationHandler.setConversation(conversation);
                invocationHandler.setEndpoint(endpoint);
                Object result = invocationHandler.invoke(proxy, method, args);
                return result;
            }
        });
        Object proxy = enhancer.create();
        return interfaze.cast(proxy);
    }

    /**
     * create the callback proxy with cglib. use the same
     * JDKCallbackInvocationHandler as JDKProxyService.
     */
    public Object createCallbackProxy(Class<?> interfaze, final List<RuntimeWire> wires) throws ProxyCreationException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaze);
        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
                throws Throwable {
                JDKCallbackInvocationHandler invocationHandler =
                    new JDKCallbackInvocationHandler(messageFactory, wires);
                Object result = invocationHandler.invoke(proxy, method, args);
                return result;
            }
        });
        Object proxy = enhancer.create();
        return interfaze.cast(proxy);
    }

    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#isProxyClass(java.lang.Class)
     */
    public boolean isProxyClass(Class<?> clazz) {
        return Factory.class.isAssignableFrom(clazz);
    }

}
