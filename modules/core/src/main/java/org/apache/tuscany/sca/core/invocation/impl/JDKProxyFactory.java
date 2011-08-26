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
package org.apache.tuscany.sca.core.invocation.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.tuscany.sca.common.java.collection.LRUCache;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.core.context.ServiceReferenceExt;
import org.apache.tuscany.sca.core.context.impl.CallbackServiceReferenceImpl;
import org.apache.tuscany.sca.core.context.impl.ServiceReferenceImpl;
import org.apache.tuscany.sca.core.invocation.ProxyCreationException;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.Invocable;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 * 
 * @version $Rev$ $Date$
 */
public class JDKProxyFactory implements ProxyFactory, LifeCycleListener {
    protected ExtensionPointRegistry registry;
    protected InterfaceContractMapper contractMapper;
    private MessageFactory messageFactory;

    public JDKProxyFactory(ExtensionPointRegistry registry,
                           MessageFactory messageFactory,
                           InterfaceContractMapper mapper) {
        this.registry = registry;
        this.contractMapper = mapper;
        this.messageFactory = messageFactory;
    }

    /** 
     * The original createProxy method assumes that the proxy doesn't want to 
     * share conversation state so sets the conversation object to null
     */
    public <T> T createProxy(final Class<T> interfaze, Invocable invocable) throws ProxyCreationException {
        if (invocable instanceof RuntimeEndpoint) {
            InvocationHandler handler;
            // TUSCANY-3659 - Always install a asynch handler regardless of whether ref is sync or async  
            //                needs tidying         
            //            if (isAsync(interfaze)) {
            handler = new AsyncJDKInvocationHandler(registry, messageFactory, interfaze, invocable);
            //            } else {
            //                handler = new JDKInvocationHandler(messageFactory, interfaze, invocable);
            //            }
            // Allow privileged access to class loader. Requires RuntimePermission in security policy.
            ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return interfaze.getClassLoader();
                }
            });
            T proxy = interfaze.cast(newProxyInstance(cl, new Class[] {interfaze}, handler));
            return proxy;
        }
        ServiceReference<T> serviceReference = new ServiceReferenceImpl<T>(interfaze, invocable, null);
        return createProxy(serviceReference);
    }

    public <T> T createProxy(ServiceReference<T> callableReference) throws ProxyCreationException {
        assert callableReference != null;
        final Class<T> interfaze = callableReference.getBusinessInterface();
        InvocationHandler handler;
        // TUSCANY-3659 - Always install a asynch handler regardless of whether ref is sync or async
        //                needs tidying
        //        if (isAsync(interfaze)) {
        handler = new AsyncJDKInvocationHandler(registry, messageFactory, callableReference);
        //        } else {
        //            handler = new JDKInvocationHandler(messageFactory, callableReference);
        //        }
        // Allow privileged access to class loader. Requires RuntimePermission in security policy.
        ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return interfaze.getClassLoader();
            }
        });
        T proxy = interfaze.cast(newProxyInstance(cl, new Class[] {interfaze}, handler));
        ((ServiceReferenceExt<T>)callableReference).setProxy(proxy);
        return proxy;
    }

    private boolean isAsync(Class<?> interfaze) {
        for (Method method : interfaze.getMethods()) {
            if (method.getName().endsWith("Async")) {
                if (method.getReturnType().isAssignableFrom(Future.class)) {
                    if (method.getParameterTypes().length > 0) {
                        if (method.getParameterTypes()[method.getParameterTypes().length - 1]
                            .isAssignableFrom(AsyncHandler.class)) {
                            return true;
                        }
                    }
                }
                if (method.getReturnType().isAssignableFrom(Response.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    public <T> T createCallbackProxy(Class<T> interfaze, List<? extends Invocable> wires) throws ProxyCreationException {
        ServiceReferenceImpl<T> callbackReference = null;
        try {
            callbackReference = new CallbackServiceReferenceImpl(interfaze, wires);
        } catch (ServiceRuntimeException e) {
            // [rfeng] In case that the call is not from a bidirectional interface, the field should be injected with null
            callbackReference = null;
        }
        return callbackReference != null ? createCallbackProxy(callbackReference) : null;
    }

    public <T> T createCallbackProxy(ServiceReference<T> callbackReference) throws ProxyCreationException {
        assert callbackReference != null;
        final Class<T> interfaze = callbackReference.getBusinessInterface();
        InvocationHandler handler = new JDKCallbackInvocationHandler(messageFactory, callbackReference);
        ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return interfaze.getClassLoader();
            }
        });
        T proxy = interfaze.cast(newProxyInstance(cl, new Class[] {interfaze}, handler));
        ((ServiceReferenceExt<T>)callbackReference).setProxy(proxy);
        return proxy;
    }

    public <B, R extends ServiceReference<B>> R cast(B target) throws IllegalArgumentException {
        InvocationHandler handler = Proxy.getInvocationHandler(target);
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
        return Proxy.isProxyClass(clazz);
    }

    // This is a cache containing the proxy class constructor for each business interface.
    // This improves performance compared to calling Proxy.newProxyInstance()
    // every time that a proxy is needed.
    private final LRUCache<Class<?>, Constructor<?>> cache = new LRUCache<Class<?>, Constructor<?>>(512);

    public Object newProxyInstance(ClassLoader classloader, Class<?> interfaces[], InvocationHandler invocationhandler)
        throws IllegalArgumentException {
        if (interfaces.length > 1) {
            // We only cache the proxy constructors with one single interface which the case in SCA where
            // one reference can have one interface
            return Proxy.newProxyInstance(classloader, interfaces, invocationhandler);
        }
        try {
            if (invocationhandler == null)
                throw new NullPointerException("InvocationHandler is null");
            // Lookup cached constructor.  aclass[0] is the reference's business interface.
            Constructor<?> proxyCTOR;
            synchronized (cache) {
                proxyCTOR = cache.get(interfaces[0]);
            }
            if (proxyCTOR == null) {
                Class<?> proxyClass = Proxy.getProxyClass(classloader, interfaces);
                proxyCTOR = proxyClass.getConstructor(InvocationHandler.class);
                synchronized (cache) {
                    cache.put(interfaces[0], proxyCTOR);
                }
            }
            return proxyCTOR.newInstance(invocationhandler);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void start() {
    }

    public void stop() {
        cache.clear();
    }
}
