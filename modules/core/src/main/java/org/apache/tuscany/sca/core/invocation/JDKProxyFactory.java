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

import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 * 
 * @version $$Rev$$ $$Date: 2007-04-11 18:59:43 -0700 (Wed, 11 Apr
 *          2007) $$
 */
public class JDKProxyFactory implements ProxyFactory {
    protected InterfaceContractMapper contractMapper;
    private MessageFactory messageFactory;

    public JDKProxyFactory() {
        this(new MessageFactoryImpl(), new InterfaceContractMapperImpl());
    }

    public JDKProxyFactory(MessageFactory messageFactory, InterfaceContractMapper mapper) {
        this.contractMapper = mapper;
        this.messageFactory = messageFactory;
    }

    /** 
     * The original createProxy method assumes that the proxy doesn't want to 
     * share conversation state so sets the conversaton object to null
     */
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire) throws ProxyCreationException {
        return createProxy(interfaze, wire, null, null, null);
    }
    
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire, Conversation conversation) throws ProxyCreationException {
        return createProxy(interfaze, wire, conversation, null, null);
    }
    
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire, Conversation conversation, EndpointReference endpoint)
                             throws ProxyCreationException {
        return createProxy(interfaze, wire, conversation, endpoint, null);
    }
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire, Conversation conversation, EndpointReference endpoint,
                             Object callbackID) throws ProxyCreationException {
        assert interfaze != null;
        assert wire != null;
        JDKInvocationHandler handler = new JDKInvocationHandler(messageFactory, interfaze, wire);
        handler.setConversation(conversation);
        handler.setEndpoint(endpoint);
        handler.setCallbackID(callbackID);
        ClassLoader cl = interfaze.getClassLoader();
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[] {interfaze}, handler));
    }    

    public <T> T createCallbackProxy(Class<T> interfaze, List<RuntimeWire> wires) throws ProxyCreationException {
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(messageFactory, wires);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[] {interfaze}, handler));
    }

    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        throw new UnsupportedOperationException();

        //        InvocationHandler handler = Proxy.getInvocationHandler(target);
        //        if (handler instanceof JDKInvocationHandler) {
        //            // TODO return a ServiceReference 
        //            throw new UnsupportedOperationException();
        //        } else if (handler instanceof JDKCallbackInvocationHandler) {
        //            // TODO return a          CallbackReference 
        //            throw new UnsupportedOperationException();
        //        } else {
        //            throw new IllegalArgumentException("Not a Tuscany SCA proxy");
        //        }

    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#isProxyClass(java.lang.Class)
     */
    public boolean isProxyClass(Class<?> clazz) {
        return Proxy.isProxyClass(clazz);
    }
}
