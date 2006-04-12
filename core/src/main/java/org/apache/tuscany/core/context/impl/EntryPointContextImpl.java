/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context.impl;

import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.invocation.jdk.JDKInvocationHandler;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.MessageFactory;

import java.lang.reflect.InvocationHandler;

/**
 * The default implementation of an entry point context
 * 
 * @version $Rev$ $Date$
 */
public class EntryPointContextImpl extends AbstractContext implements EntryPointContext {

    private ProxyFactory<?> proxyFactory;

    private InvocationHandler invocationHandler;

    // a proxy implementing the service exposed by the entry point backed by the invocation handler
    private Object proxy;

    /**
     * Creates a new entry point
     * 
     * @param name the entry point name
     * @param proxyFactory the proxy factory containing the invocation chains for the entry point
     * @param messageFactory a factory for generating invocation messages
     * @throws org.apache.tuscany.core.context.ContextInitException if an error occurs creating the entry point
     */
    public EntryPointContextImpl(String name, ProxyFactory proxyFactory, MessageFactory messageFactory)
            throws ContextInitException {
        super(name);
        assert (proxyFactory != null) : "Proxy factory was null";
        assert (messageFactory != null) : "Message factory was null";
        this.proxyFactory = proxyFactory;
        invocationHandler = new JDKInvocationHandler(messageFactory, proxyFactory.getProxyConfiguration()
                .getInvocationConfigurations());
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        if (proxy == null) {
            try {
                proxy = proxyFactory.createProxy();
            } catch (ProxyCreationException e) {
                TargetException te = new TargetException(e);
                te.addContextName(getName());
                throw te;
            }
        }
        return proxy;
    }

    public void start() throws ContextInitException {
        lifecycleState = RUNNING;
    }

    public void stop() throws CoreRuntimeException {
        lifecycleState = STOPPED;
    }

     public Object getHandler() throws TargetException {
        return invocationHandler;
    }

}
