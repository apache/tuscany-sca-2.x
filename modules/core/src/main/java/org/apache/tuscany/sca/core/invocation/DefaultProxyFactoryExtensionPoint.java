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

import java.util.List;

import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;

public class DefaultProxyFactoryExtensionPoint implements ProxyFactoryExtensionPoint {
    private InterfaceContractMapper interfaceContractMapper;
    private MessageFactory messageFactory;
    
    private ProxyFactory interfaceFactory;
    private ProxyFactory classFactory;

    public DefaultProxyFactoryExtensionPoint() {
        this(new MessageFactoryImpl(), new InterfaceContractMapperImpl());
    }

    public DefaultProxyFactoryExtensionPoint(MessageFactory messageFactory, InterfaceContractMapper mapper) {
        this.interfaceContractMapper = mapper;
        this.messageFactory = messageFactory;
        interfaceFactory = new JDKProxyService(messageFactory, mapper);
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint#getClassProxyFactory()
     */
    public ProxyFactory getClassProxyFactory() {
        return classFactory;
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint#getInterfaceProxyFactory()
     */
    public ProxyFactory getInterfaceProxyFactory() {
        return interfaceFactory;
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint#setClassProxyFactory(org.apache.tuscany.sca.core.invocation.ProxyFactory)
     */
    public void setClassProxyFactory(ProxyFactory factory) {
        this.classFactory = factory;

    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint#setInterfaceProxyFactory(org.apache.tuscany.sca.core.invocation.ProxyFactory)
     */
    public void setInterfaceProxyFactory(ProxyFactory factory) {
        this.interfaceFactory = factory;

    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#cast(java.lang.Object)
     */
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        if (interfaceFactory.isProxyClass(target.getClass())) {
            return (R) interfaceFactory.cast(target);
        } else if (classFactory != null && classFactory.isProxyClass(target.getClass())) {
            return (R) classFactory.cast(target);
        } else {
            throw new IllegalArgumentException("The target is not a callable proxy");
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#createCallbackProxy(java.lang.Class,
     *      java.util.List)
     */
    public Object createCallbackProxy(Class<?> interfaze, List<RuntimeWire> wires) throws ProxyCreationException {
        if (interfaze.isInterface()) {
            return interfaceFactory.createCallbackProxy(interfaze, wires);
        } else {
            return classFactory.createCallbackProxy(interfaze, wires);
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#createProxy(java.lang.Class,
     *      org.apache.tuscany.sca.runtime.RuntimeWire,
     *      org.osoa.sca.Conversation)
     */
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire, Conversation conversation)
        throws ProxyCreationException {
        if (interfaze.isInterface()) {
            return interfaceFactory.createProxy(interfaze, wire, conversation);
        } else {
            return classFactory.createProxy(interfaze, wire, conversation);
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#createProxy(java.lang.Class,
     *      org.apache.tuscany.sca.runtime.RuntimeWire,
     *      org.osoa.sca.Conversation)
     */
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire, Conversation conversation, EndpointReference endpoint)
        throws ProxyCreationException {
        if (interfaze.isInterface()) {
            return interfaceFactory.createProxy(interfaze, wire, conversation, endpoint);
        } else {
            return classFactory.createProxy(interfaze, wire, conversation, endpoint);
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#createProxy(java.lang.Class,
     *      org.apache.tuscany.sca.runtime.RuntimeWire)
     */
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire) throws ProxyCreationException {
        if (interfaze.isInterface()) {
            return interfaceFactory.createProxy(interfaze, wire);
        } else {
            return classFactory.createProxy(interfaze, wire);
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#isProxyClass(java.lang.Class)
     */
    public boolean isProxyClass(Class<?> clazz) {
        return interfaceFactory.isProxyClass(clazz) || (classFactory != null && classFactory.isProxyClass(clazz));
    }

    /**
     * @return the interfaceContractMapper
     */
    public InterfaceContractMapper getInterfaceContractMapper() {
        return interfaceContractMapper;
    }

    /**
     * @return the messageFactory
     */
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

}
