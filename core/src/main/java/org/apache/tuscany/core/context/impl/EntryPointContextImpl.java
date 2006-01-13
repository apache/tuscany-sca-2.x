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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.core.binding.BindingChannel;
import org.apache.tuscany.core.binding.BindingHandler;
import org.apache.tuscany.core.binding.MessageContext;
import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.spi.ProxyCreationException;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;

/**
 * The default implementation of an entry point context
 * 
 * @version $Rev$ $Date$
 */
public class EntryPointContextImpl extends AbstractContext implements EntryPointContext {

    private BindingChannel channel = new BindingChannelImpl();

    private List<BindingHandler> bindingHandlers = new ArrayList();

    private MessageFactory messageFactory;

    private AggregateContext parentContext;

    private ProxyFactory targetFactory;

    private Object target;

    private InvocationHandler targetInvocationHandler;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public EntryPointContextImpl(String name, ProxyFactory targetFactory, AggregateContext parentContext,
            MessageFactory messageFctory, List<BindingHandler> bindingHandlers) throws ContextInitException {
        super(name);
        assert (targetFactory != null) : "Proxy factory was null";
        assert (messageFctory != null) : "Message factory was null";
        this.targetFactory = targetFactory;
        this.parentContext = parentContext;
        this.messageFactory = messageFctory;
        this.bindingHandlers.addAll(bindingHandlers);
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public Object getInstance(QualifiedName qName) throws TargetException {
        return channel;
    }

    public Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        return getInstance(qName);
    }

    public void start() throws ContextInitException {
        try {
            target = targetFactory.createProxy();
            if (Proxy.isProxyClass(target.getClass())) {
                // if this is a proxy, short-circuit proxy invocation
                targetInvocationHandler = Proxy.getInvocationHandler(target);
            }
            lifecycleState = RUNNING;
        } catch (ProxyCreationException e) {
            lifecycleState = ERROR;
            ContextInitException ce = new ContextInitException(e);
            ce.setIdentifier(getName());
            throw ce;
        }
    }

    public void stop() throws CoreRuntimeException {
        lifecycleState = STOPPED;
    }

    // ----------------------------------
    // Private classes
    // ----------------------------------

    /**
     * Initiates message processing through the entry point
     */
    private class BindingChannelImpl implements BindingChannel {

        public void send(MessageContext ctx) throws TargetException {
            Message msg = messageFactory.createMessage();
            ctx.setMessage(msg);
            for (Iterator iter = bindingHandlers.iterator(); iter.hasNext();) {
                BindingHandler handler = (BindingHandler) iter.next();
                if (!handler.process(ctx)) {
                    break;
                }
            }
            try {
                if (targetInvocationHandler != null) {
                    // short-circuit invocation
                    // TODO in JDKInvocationHandler, we should check to see if args[0] instanceof Message, and if so
                    // short-circuit message creation
                    targetInvocationHandler.invoke(target, ctx.getTargetMethod(), new Object[] { msg });
                } else {
                    ctx.getTargetMethod().invoke(target, new Object[] { msg });
                }
            } catch (Throwable e) {
                // handle client response and log error
            }
        }
    }

}
