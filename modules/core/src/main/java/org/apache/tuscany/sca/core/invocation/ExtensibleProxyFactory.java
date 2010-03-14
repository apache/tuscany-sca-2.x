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

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.Invocable;
import org.oasisopen.sca.ServiceReference;

/**
 * An extensible proxy factory.
 *
 * @version $Rev$ $Date$
 */
public class ExtensibleProxyFactory implements ProxyFactory {
    
    private ProxyFactoryExtensionPoint proxyFactories;

    public ExtensibleProxyFactory(ProxyFactoryExtensionPoint proxyFactories) {
        this.proxyFactories = proxyFactories;
    }
    
    public ExtensibleProxyFactory(ExtensionPointRegistry registry) {
        this.proxyFactories = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);
    }
    
    public static ExtensibleProxyFactory getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilities.getUtility(ExtensibleProxyFactory.class);
    }
    
    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#cast(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public <B, R extends ServiceReference<B>> R cast(B target) throws IllegalArgumentException {
        ProxyFactory interfaceFactory = proxyFactories.getInterfaceProxyFactory();
        ProxyFactory classFactory = proxyFactories.getClassProxyFactory();
        if (interfaceFactory.isProxyClass(target.getClass())) {
            return (R)interfaceFactory.cast(target);
        } else if (classFactory != null && classFactory.isProxyClass(target.getClass())) {
            return (R)classFactory.cast(target);
        } else {
            throw new IllegalArgumentException("The target is not a callable proxy");
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#createCallbackProxy(java.lang.Class,
     *      java.util.List)
     */
    public <T> T createCallbackProxy(Class<T> interfaze, List<? extends Invocable> wires) throws ProxyCreationException {
        ProxyFactory interfaceFactory = proxyFactories.getInterfaceProxyFactory();
        ProxyFactory classFactory = proxyFactories.getClassProxyFactory();
        if (interfaze.isInterface()) {
            return interfaceFactory.createCallbackProxy(interfaze, wires);
        } else {
            return classFactory.createCallbackProxy(interfaze, wires);
        }
    }

    public <T> T createProxy(ServiceReference<T> callableReference) throws ProxyCreationException {
        ProxyFactory interfaceFactory = proxyFactories.getInterfaceProxyFactory();
        ProxyFactory classFactory = proxyFactories.getClassProxyFactory();
        if (callableReference.getBusinessInterface().isInterface()) {
            return interfaceFactory.createProxy(callableReference);
        } else {
            return classFactory.createProxy(callableReference);
        }
    }

    public <T> T createCallbackProxy(ServiceReference<T> callbackReference) throws ProxyCreationException {
        ProxyFactory interfaceFactory = proxyFactories.getInterfaceProxyFactory();
        ProxyFactory classFactory = proxyFactories.getClassProxyFactory();
        if (callbackReference.getBusinessInterface().isInterface()) {
            return interfaceFactory.createCallbackProxy(callbackReference);
        } else {
            return classFactory.createCallbackProxy(callbackReference);
        }
    }

    /**
     * @see org.apache.tuscany.sca.core.invocation.ProxyFactory#createProxy(java.lang.Class,
     *      org.apache.tuscany.sca.runtime.Invocable)
     */
    public <T> T createProxy(Class<T> interfaze, Invocable wire) throws ProxyCreationException {
        ProxyFactory interfaceFactory = proxyFactories.getInterfaceProxyFactory();
        ProxyFactory classFactory = proxyFactories.getClassProxyFactory();
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
        ProxyFactory interfaceFactory = proxyFactories.getInterfaceProxyFactory();
        ProxyFactory classFactory = proxyFactories.getClassProxyFactory();
        return interfaceFactory.isProxyClass(clazz) || (classFactory != null && classFactory.isProxyClass(clazz));
    }

}
