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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import org.osoa.sca.CallableReference;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.ChainHolder;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.InvocationChain;

import org.apache.tuscany.core.wire.ProxyServiceExtension;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 *
 * @version $$Rev$$ $$Date$$
 */
@EagerInit
public class JDKProxyService extends ProxyServiceExtension {

    public JDKProxyService() {
        super(null);
    }

    @Constructor
    public JDKProxyService(@Reference WorkContext context) {
        super(context);
    }

    public <T> T createProxy(Class<T> interfaze, Wire wire) throws ProxyCreationException {
        assert interfaze != null;
        assert wire != null;
        JDKInvocationHandler handler = new JDKInvocationHandler(interfaze, wire, context);
        ClassLoader cl = interfaze.getClassLoader();
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public <T> T createProxy2(Class<T> interfaze, boolean conversational, Wire wire) throws ProxyCreationException {
        assert interfaze != null;
        assert wire != null;
        JDKInvocationHandler2 handler = new JDKInvocationHandler2(interfaze, conversational, wire, context);
        ClassLoader cl = interfaze.getClassLoader();
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public <T> T createProxy(Class<T> interfaze, Wire wire, Map<Method, ChainHolder> mapping)
        throws ProxyCreationException {
        assert interfaze != null;
        assert wire != null;
        assert mapping != null;
        JDKInvocationHandler handler = new JDKInvocationHandler(interfaze, wire, context);
        ClassLoader cl = interfaze.getClassLoader();
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public Object createCallbackProxy(Class<?> interfaze, List<Wire> wires) throws ProxyCreationException {
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wires, context);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public Object createCallbackProxy(Class<?> interfaze) throws ProxyCreationException {
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler2 handler = new JDKCallbackInvocationHandler2(context);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        InvocationHandler handler = Proxy.getInvocationHandler(target);
        if (handler instanceof JDKInvocationHandler) {
            // TODO return a ServiceReference
            throw new UnsupportedOperationException();
        } else if (handler instanceof JDKCallbackInvocationHandler) {
            // TODO return a CallbackReference
            throw new UnsupportedOperationException();
        } else {
            throw new IllegalArgumentException("Not a Tuscany SCA proxy");
        }
    }
}
