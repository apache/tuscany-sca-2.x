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
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 * 
 * @version $$Rev$$ $$Date: 2007-04-11 18:59:43 -0700 (Wed, 11 Apr
 *          2007) $$
 */
public class JDKProxyService implements ProxyFactory {
    protected InterfaceContractMapper contractMapper;
    private MessageFactory messageFactory;

    public JDKProxyService() {
        this(new MessageFactoryImpl(), new InterfaceContractMapperImpl());
    }

    public JDKProxyService(MessageFactory messageFactory, InterfaceContractMapper mapper) {
        this.contractMapper = mapper;
        this.messageFactory = messageFactory;
    }

    /** 
     * The original createProxy method assumes that the proxy doesn't want to 
     * share conversation state so sets the conversaton object to null
     */
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire) throws ProxyCreationException {
        return createProxy(interfaze, wire, null);
    }
    
    public <T> T createProxy(Class<T> interfaze, RuntimeWire wire, Conversation conversation) throws ProxyCreationException {
        assert interfaze != null;
        assert wire != null;
        JDKInvocationHandler handler = new JDKInvocationHandler(messageFactory, interfaze, wire);
        handler.setConversation(conversation);
        ClassLoader cl = interfaze.getClassLoader();
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[] {interfaze}, handler));
    }    

    public Object createCallbackProxy(Class<?> interfaze, List<RuntimeWire> wires) throws ProxyCreationException {
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
}
